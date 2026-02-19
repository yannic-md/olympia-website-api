package de.olympia.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * Service for administrative operations
 * Provides database reset functionality and other admin-only operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Reset the database by executing the reset SQL script
     * This will delete all data except the admin user
     *
     * @throws RuntimeException if the reset operation fails
     */
    @Transactional
    public void resetDatabase() {
        try {
            log.warn("Starting database reset operation...");

            // Load the reset SQL script from resources
            ClassPathResource resource = new ClassPathResource("db/reset_data.sql");

            String sqlScript;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                sqlScript = reader.lines().collect(Collectors.joining("\n"));
            }

            // Split the script into individual statements and execute them
            String[] statements = sqlScript.split(";");

            for (String statement : statements) {
                String trimmedStatement = statement.trim();
                if (!trimmedStatement.isEmpty() && !trimmedStatement.startsWith("--")) {
                    jdbcTemplate.execute(trimmedStatement);
                }
            }

            log.warn("Database reset completed successfully. All data cleared except admin user.");

        } catch (Exception e) {
            log.error("Failed to reset database", e);
            throw new RuntimeException("Database reset failed: " + e.getMessage(), e);
        }
    }
}

