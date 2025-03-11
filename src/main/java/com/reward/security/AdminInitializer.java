package com.reward.security;

import com.reward.entity.User;
import com.reward.entity.Role;
import com.reward.repository.UserRepository;
import com.reward.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.Optional;
import java.util.UUID;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        return args -> createAdminUser(userRepository, roleRepository, passwordEncoder);
    }

    @Transactional
    public void createAdminUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        boolean adminExists = userRepository.existsByRoleName("ADMIN");
        if (adminExists) {
            System.out.println("Admin user already exists.");
            return;
        }

        Optional<Role> adminRole = roleRepository.findByRoleName("ADMIN");
        if (adminRole.isEmpty()) {
            System.out.println("ADMIN role not found. Please create roles first.");
            return;
        }

        User admin = User.builder()
                .id(UUID.randomUUID())
                .name("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin@123"))
                .role(adminRole.get())
                .build();

        userRepository.save(admin);
        System.out.println("Admin user created: admin@example.com / admin@123");
    }
}
