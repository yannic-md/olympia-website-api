package de.olympia.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import de.olympia.main.dto.CreateResultRequest;
import de.olympia.main.dto.ResultResponse;
import de.olympia.main.service.ResultService;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    /**
     * Creates or updates a result for a given athlete + sport combination.
     * When a result row for the same (sport, athlete) pair already exists it
     * is overwritten (upsert).
     * Restricted to ADMIN and JUDGE roles.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> upsertResult(@RequestBody CreateResultRequest request) {
        try {
            ResultResponse result = resultService.upsertResult(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Deletes a result by its ID.
     * Restricted to ADMIN and JUDGE roles.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> deleteResult(@PathVariable Long id) {
        try {
            resultService.deleteResult(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

