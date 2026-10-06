package com.fsp.secureit.unitario.registro;

import com.fsp.secureit.dto.RegisterRequest;
import com.fsp.secureit.entity.User;
import com.fsp.secureit.repository.UserRepository;
import com.fsp.secureit.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceRegistrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder);
    }

    @Test
    void registerCreatesEnabledUserWithUserRoleAndEncodedPassword() {
        RegisterRequest request = request("newuser", "newuser@example.com", "PlainPassword123!");
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = authService.register(request);

        assertEquals("newuser", savedUser.getUsername());
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals("USER", savedUser.getRole());
        assertTrue(savedUser.isEnabled());
        assertEquals("encoded-password", savedUser.getPassword());
        assertNotEquals(request.getPassword(), savedUser.getPassword());
        verify(passwordEncoder).encode(request.getPassword());
    }

    @Test
    void registerRejectsDuplicateUsername() {
        RegisterRequest request = request("existing", "new@example.com", "Password123!");
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("El nombre de usuario ya existe", exception.getMessage());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        RegisterRequest request = request("newuser", "existing@example.com", "Password123!");
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("El correo electrónico ya existe", exception.getMessage());
    }

    private RegisterRequest request(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        return request;
    }
}