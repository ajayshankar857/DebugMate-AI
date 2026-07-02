package com.debugmate.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class DebugMateApplication {
    public static void main(String[] args) {
        SpringApplication.run(DebugMateApplication.class, args);
    }
}
