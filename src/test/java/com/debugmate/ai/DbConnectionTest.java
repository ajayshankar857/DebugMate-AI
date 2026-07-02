package com.debugmate.ai;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionTest {
    @Test
    public void testPasswords() {
        String[] passwords = {
            "", "root", "admin", "password", "1234", "123456", "12345678", "mysql", "root123", "root@123", "mysql123", "ajays", "ajay"
        };
        for (String password : passwords) {
            String url = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            try (Connection conn = DriverManager.getConnection(url, "root", password)) {
                System.out.println("=========================================");
                System.out.println("SUCCESS! Working MySQL password is: \"" + password + "\"");
                System.out.println("=========================================");
                return;
            } catch (SQLException e) {
                // Ignore and try next
            }
        }
        System.out.println("=========================================");
        System.out.println("FAILED to find working password.");
        System.out.println("=========================================");
    }
}
