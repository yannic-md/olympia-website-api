package de.olympia.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import de.olympia.main.dto.AthleteResponse;
import de.olympia.main.dto.CreateAthleteRequest;
import de.olympia.main.dto.UpdateAthleteRequest;
import de.olympia.main.service.AthleteService;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteController {

    private final AthleteService athleteService;

    /**
     * Create new athlete - only ADMIN and JUDGE roles
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> createAthlete(@RequestBody CreateAthleteRequest request) {
        try {
            AthleteResponse athlete = athleteService.createAthlete(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(athlete);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update athlete - only ADMIN and JUDGE roles
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> updateAthlete(@PathVariable Long id, @RequestBody UpdateAthleteRequest request) {
        try {
            AthleteResponse athlete = athleteService.updateAthlete(id, request);
            return ResponseEntity.ok(athlete);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete athlete - only ADMIN and JUDGE roles
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> deleteAthlete(@PathVariable Long id) {
        try {
            athleteService.deleteAthlete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

