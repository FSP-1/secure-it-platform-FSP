package com.fsp.secureit.api;

import com.fsp.secureit.controller.AuthController;
import com.fsp.secureit.dto.RegisterRequest;
import com.fsp.secureit.entity.User;
import com.fsp.secureit.exception.GlobalExceptionHandler;
import com.fsp.secureit.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerApiTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService, authenticationManager);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void registerReturnsCreatedUser() throws Exception {
        User user = user(1L, "apiuser", "apiuser@example.com", "USER");
        when(authService.register(any(RegisterRequest.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "username": "apiuser",
                          "email": "apiuser@example.com",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("apiuser"))
                .andExpect(jsonPath("$.email").value("apiuser@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void registerReturnsBadRequestForInvalidPayload() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
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
    void registerReturnsConflictForDuplicateUser() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new IllegalArgumentException("El nombre de usuario ya existe"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "username": "existing",
                          "email": "existing@example.com",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("El nombre de usuario ya existe"));
    }

    @Test
    void loginReturnsAuthenticatedUser() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "apiuser", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        mockMvc.perform(post("/api/auth/login")
                .contentType(APPLICATION_JSON)
                .content("""
                        {
                          "username": "apiuser",
                          "password": "Password123!"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login correcto"))
                .andExpect(jsonPath("$.username").value("apiuser"))
                .andExpect(jsonPath("$.role").value("ROLE_USER"));
    }

    private User user(Long id, String username, String email, String role) {
        User user = new User();
        ReflectionTestUtils.setField(user, "id", id);
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        return user;
    }
}