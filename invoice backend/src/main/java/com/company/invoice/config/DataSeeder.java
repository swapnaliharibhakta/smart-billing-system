package com.company.invoice.config;

import com.company.invoice.entity.User;
import com.company.invoice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (!repo.existsByEmail("admin@company.com")) {
                User u = new User();
                u.setName("Admin");
                u.setEmail("admin@company.com");
                u.setPasswordHash(encoder.encode("Admin@123"));
                u.setRole(User.Role.ADMIN);
                u.setEnabled(true);
                repo.save(u);
            }
        };
    }
}