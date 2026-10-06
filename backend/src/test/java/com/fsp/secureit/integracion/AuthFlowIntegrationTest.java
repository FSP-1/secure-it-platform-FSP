package com.fsp.secureit.integracion;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Flyway flyway;

    @BeforeEach
    void cleanTestDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void userCanRegisterAndLoginWithBcryptPassword() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "integrationuser",
                          "email": "integrationuser@example.com",
                          "password": "TestPassword123!"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("USER"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "integrationuser",
                          "password": "TestPassword123!"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("integrationuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    @Test
    void logoutInvalidatesSessionAndProtectsEndpoints() throws Exception {
        MockHttpSession session = loginAs(
                "logoutuser",
                "logoutuser@example.com",
                "TestPassword123!");

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().is3xxRedirection());

        assertTrue(session.isInvalid());

        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void duplicateUsernameIsRejected() throws Exception {
        String request = """
                {
                  "username": "duplicateuser",
                  "email": "first@example.com",
                  "password": "TestPassword123!"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.replace("first@example.com", "second@example.com")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El nombre de usuario ya existe"));
    }

    @Test
    void invalidRegistrationIsRejected() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "",
                          "email": "invalid-email",
                          "password": "123"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de validación"));
    }

    @Test
    void unauthenticatedUserCannotAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void regularUserCannotAccessAdminEndpoint() throws Exception {
        MockHttpSession session = loginAs("normaluser", "normaluser@example.com", "TestPassword123!");

        mockMvc.perform(get("/api/admin/dashboard").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanAccessAdminEndpoint() throws Exception {
        MockHttpSession session = loginAs("adminuser", "adminuser@example.com", "AdminPassword123!");
        flyway.getConfiguration().getDataSource().getConnection().prepareStatement(
                "UPDATE users SET role = 'ADMIN' WHERE username = 'adminuser'")
                .executeUpdate();

        session = login("adminuser", "AdminPassword123!");

        mockMvc.perform(get("/api/admin/dashboard").session(session))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().string("Área de administración"));
    }

    private MockHttpSession loginAs(String username, String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "%s",
                          "email": "%s",
                          "password": "%s"
                        }
                        """.formatted(username, email, password)))
                .andExpect(status().isCreated());
        return login(username, password);
    }

    private MockHttpSession login(String username, String password) throws Exception {
        return (MockHttpSession) mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "%s",
                          "password": "%s"
                        }
                        """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn().getRequest().getSession();
    }
}