package de.olympia.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import de.olympia.main.dto.CountryResponse;
import de.olympia.main.dto.CreateCountryRequest;
import de.olympia.main.dto.UpdateCountryRequest;
import de.olympia.main.service.CountryService;

@RestController
@RequestMapping("/api/countries")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;

    /**
     * Get all countries - accessible to all authenticated users
     */
    @GetMapping
    public ResponseEntity<List<CountryResponse>> getAllCountries() {
        List<CountryResponse> countries = countryService.getAllCountries();
        return ResponseEntity.ok(countries);
    }

    /**
     * Get country by ID - accessible to all authenticated users
     */
    @GetMapping("/{id}")
    public ResponseEntity<CountryResponse> getCountryById(@PathVariable Long id) {
        try {
            CountryResponse country = countryService.getCountryById(id);
            return ResponseEntity.ok(country);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new country - only ADMIN and JUDGE roles
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> createCountry(@RequestBody CreateCountryRequest request) {
        try {
            CountryResponse country = countryService.createCountry(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(country);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Update country - only ADMIN and JUDGE roles
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> updateCountry(@PathVariable Long id, @RequestBody UpdateCountryRequest request) {
        try {
            CountryResponse country = countryService.updateCountry(id, request);
            return ResponseEntity.ok(country);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete country - only ADMIN and JUDGE roles
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'JUDGE')")
    public ResponseEntity<?> deleteCountry(@PathVariable Long id) {
        try {
            countryService.deleteCountry(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

