package de.olympia.main.controller;

import de.olympia.main.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for administrative operations
 * All endpoints require ADMIN role
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Reset the database to initial state
     * Deletes all data except the admin user
     *
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> resetDatabase() {
        try {
            adminService.resetDatabase();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Database reset successful. All data cleared except admin user.");
            response.put("status", "success");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Database reset failed: " + e.getMessage());
            errorResponse.put("status", "error");

            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}

