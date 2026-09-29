package com.fsp.secureit;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecureitApplicationTests {

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
	void registerUserSuccessfully() throws Exception {

		String username = "testuser";
		String email = "testuser@example.com";

		String request = """
				{
				    "username": "%s",
				    "email": "%s",
				    "password": "TestPassword123!"
				}
				""".formatted(username, email);

		mockMvc.perform(
				post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.username").value(username))
				.andExpect(jsonPath("$.email").value(email))
				.andExpect(jsonPath("$.role").value("USER"));
	}

	@Test
	void registerUserWithInvalidDataReturnsBadRequest() throws Exception {

		String request = """
				{
				    "username": "",
				    "email": "correo-invalido",
				    "password": "123"
				}
				""";

		mockMvc.perform(
				post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Error de validación"));
	}

	@Test
	void registerDuplicateUsernameReturnsConflict() throws Exception {

		String firstRequest = """
				{
					"username": "duplicateuser",
					"email": "first@example.com",
					"password": "TestPassword123!"
				}
				""";

		mockMvc.perform(
				post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(firstRequest))
				.andExpect(status().isCreated());

		String secondRequest = """
				{
					"username": "duplicateuser",
					"email": "second@example.com",
					"password": "TestPassword123!"
				}
				""";

		mockMvc.perform(
				post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(secondRequest))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error")
						.value("El nombre de usuario ya existe"));
	}
}