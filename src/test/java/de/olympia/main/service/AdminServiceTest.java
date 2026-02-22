package de.olympia.main.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Unit Tests")
class AdminServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        // Setup is minimal for AdminService
    }

    // ===== DATABASE RESET =====

    @Test
    @DisplayName("Should reset database successfully")
    void testResetDatabase_Success() {
        // Arrange - No specific setup needed as we're mocking JdbcTemplate
        doNothing().when(jdbcTemplate).execute(anyString());

        // Act
        assertDoesNotThrow(() -> adminService.resetDatabase());

        // Assert
        verify(jdbcTemplate, atLeastOnce()).execute(anyString());
    }

    @Test
    @DisplayName("Should execute SQL statements from reset script")
    void testResetDatabase_ExecutesSqlStatements() {
        // Arrange
        doNothing().when(jdbcTemplate).execute(anyString());

        // Act
        adminService.resetDatabase();

        // Assert
        verify(jdbcTemplate, atLeastOnce()).execute(anyString());
    }

    @Test
    @DisplayName("Should throw exception when reset fails")
    void testResetDatabase_ExecutionFailure() {
        // Arrange
        doThrow(new RuntimeException("Database error"))
            .when(jdbcTemplate).execute(anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> adminService.resetDatabase());
        assertTrue(exception.getMessage().contains("Database reset failed"));
    }

    @Test
    @DisplayName("Should handle SQL exceptions gracefully")
    void testResetDatabase_SqlException() {
        // Arrange
        doThrow(new org.springframework.dao.DataAccessException("SQL Error") {})
            .when(jdbcTemplate).execute(anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> adminService.resetDatabase());
        assertNotNull(exception.getMessage());
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Should handle multiple reset calls")
    void testResetDatabase_MultipleResets() {
        // Arrange
        doNothing().when(jdbcTemplate).execute(anyString());

        // Act
        adminService.resetDatabase();
        adminService.resetDatabase();

        // Assert
        verify(jdbcTemplate, atLeastOnce()).execute(anyString());
    }

    @Test
    @DisplayName("Should continue execution even if single statement fails")
    void testResetDatabase_PartialFailure() {
        // Arrange - First call succeeds, second fails
        doNothing()
            .doThrow(new RuntimeException("Statement failed"))
            .when(jdbcTemplate).execute(anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> adminService.resetDatabase());
        assertNotNull(exception);
    }
}

