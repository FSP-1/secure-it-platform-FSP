package com.fsp.secureit.config;

import com.fsp.secureit.entity.User;
import com.fsp.secureit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String username = System.getenv("SECUREIT_ADMIN_USERNAME");
            String email = System.getenv("SECUREIT_ADMIN_EMAIL");
            String password = System.getenv("SECUREIT_ADMIN_PASSWORD");

            if (username == null || email == null || password == null) {
                System.out.println(
                        "Variables de administrador no configuradas. "
                        + "No se creará el usuario ADMIN."
                );
                return;
            }

            if (userRepository.existsByUsername(username)) {
                return;
            }

            User admin = new User();

            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole("ADMIN");
            admin.setEnabled(true);

            userRepository.save(admin);

            System.out.println("Usuario ADMIN creado correctamente.");
        };
    }
}