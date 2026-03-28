package de.olympia.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
     * Resets the database by executing the reset SQL script from resources.
     * 
     * This is a destructive administrative operation that:
     * 1. Loads the reset SQL script from db/reset_data.sql resource file
     * 2. Parses it into individual statements (split by semicolon)
     * 3. Executes each statement in sequence via JdbcTemplate
     * 4. Skips comment lines and empty statements
     * 5. Clears all related caches to prevent stale data display
     * 
     * WARNING: This operation deletes all data from the database except the admin user.
     * It should only be called by authenticated administrators via a protected endpoint.
     * All changes are immediately committed and cannot be rolled back via API.
     *
     * @throws RuntimeException if the reset operation fails (with wrapped cause exception)
     * 
     * Side effects: Evicts all leaderboard, athlete, country, and sport caches
     */
    @CacheEvict(value = {"v2Leaderboard", "v2Countries", "v2Athletes", "v2Sports"}, allEntries = true)
    @Transactional
    public void resetDatabase() {
        try {
            log.warn("Starting database reset operation...");

            // Load the reset SQL script from the classpath resources
            // The script is typically located in src/main/resources/db/reset_data.sql
            ClassPathResource resource = new ClassPathResource("db/reset_data.sql");

            // Read the entire SQL script from the resource stream
            String sqlScript;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                sqlScript = reader.lines().collect(Collectors.joining("\n"));
            }

            // Split the script into individual SQL statements (delimited by semicolons)
            String[] statements = sqlScript.split(";");

            // Execute each statement individually
            for (String statement : statements) {
                // Trim whitespace and skip comments and empty statements
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

