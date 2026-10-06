package com.fsp.secureit.unitario.login;

import com.fsp.secureit.entity.User;
import com.fsp.secureit.repository.UserRepository;
import com.fsp.secureit.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

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
    void loginReturnsEnabledUserWithValidPassword() {
        User user = user("loginuser", "encoded-password", true);
        when(userRepository.findByUsername("loginuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);

        User result = authService.login("loginuser", "Password123!");

        assertEquals(user, result);
    }

    @Test
    void loginRejectsUnknownUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login("unknown", "Password123!"));

        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
    }

    @Test
    void loginRejectsInvalidPassword() {
        User user = user("loginuser", "encoded-password", true);
        when(userRepository.findByUsername("loginuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "encoded-password")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login("loginuser", "WrongPassword"));

        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
    }

    @Test
    void loginRejectsDisabledUser() {
        User user = user("disableduser", "encoded-password", false);
        when(userRepository.findByUsername("disableduser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login("disableduser", "Password123!"));

        assertEquals("Usuario deshabilitado", exception.getMessage());
    }

    private User user(String username, String password, boolean enabled) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword(password);
        user.setRole("USER");
        user.setEnabled(enabled);
        return user;
    }
}