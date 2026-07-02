package com.debugmate.ai.config;

import com.debugmate.ai.entity.ERole;
import com.debugmate.ai.entity.Role;
import com.debugmate.ai.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        seedRoles();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            log.info("Database roles are empty. Seeding ERole values...");
            
            Role userRole = Role.builder().name(ERole.ROLE_USER).build();
            Role adminRole = Role.builder().name(ERole.ROLE_ADMIN).build();
            
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
            
            log.info("Roles seeded successfully: ROLE_USER, ROLE_ADMIN");
        } else {
            log.info("Roles already seeded, skipping seeder.");
        }
    }
}
