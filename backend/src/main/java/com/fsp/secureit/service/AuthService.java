package com.fsp.secureit.service;

import com.fsp.secureit.dto.RegisterRequest;
import com.fsp.secureit.entity.User;
import com.fsp.secureit.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("El correo electrónico ya existe");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Nunca guardar contraseñas en texto plano
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // El usuario nunca puede elegir su propio rol
        user.setRole("USER");

        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User login(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Usuario o contraseña incorrectos"
                        )
                );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException(
                    "Usuario o contraseña incorrectos"
            );
        }

        if (!user.isEnabled()) {
            throw new IllegalArgumentException(
                    "Usuario deshabilitado"
            );
        }

        return user;
    }
}