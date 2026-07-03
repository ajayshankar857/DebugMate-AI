package com.debugmate.ai.config;

import com.debugmate.ai.entity.ERole;
import com.debugmate.ai.entity.Role;
import com.debugmate.ai.entity.BugKnowledge;
import com.debugmate.ai.repository.RoleRepository;
import com.debugmate.ai.repository.BugKnowledgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final BugKnowledgeRepository bugKnowledgeRepository;

    @Override
    public void run(String... args) {
        if (roleRepository.count() == 0) {
            log.info("Database roles are empty. Seeding ERole values...");
            Arrays.stream(ERole.values()).forEach(role -> {
                roleRepository.save(Role.builder().name(role).build());
            });
            log.info("Roles seeded successfully: ROLE_USER, ROLE_ADMIN");
        } else {
            log.info("Roles already seeded, skipping seeder.");
        }

        if (bugKnowledgeRepository.count() == 0) {
            log.info("Seeding Bug Knowledge Base...");
            bugKnowledgeRepository.save(BugKnowledge.builder()
                .language("Java")
                .title("NullPointerException (NPE)")
                .category("Runtime Error")
                .description("Occurs when trying to use a null object reference. This is the most common error in Java.")
                .exampleCode("String s = null;\ns.length();")
                .solution("Check for null before accessing methods or properties, or use Optional.")
                .bestPractice("Use Optional<T> for return types that might be empty. Use Objects.requireNonNull().")
                .severity("CRITICAL")
                .build());
            
            bugKnowledgeRepository.save(BugKnowledge.builder()
                .language("JavaScript")
                .title("TypeError: Cannot read properties of undefined")
                .category("Runtime Error")
                .description("Occurs when accessing a property on an undefined or null variable.")
                .exampleCode("let obj = undefined;\nconsole.log(obj.name);")
                .solution("Use optional chaining (?.) or check for undefined before access.")
                .bestPractice("Always initialize variables and use TypeScript for compile-time safety.")
                .severity("HIGH")
                .build());
                
            log.info("Bug Knowledge Base seeded successfully.");
        }
    }
}
