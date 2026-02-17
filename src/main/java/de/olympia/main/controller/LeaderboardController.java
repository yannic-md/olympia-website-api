package de.olympia.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import de.olympia.main.dto.LeaderboardEntryResponse;
import de.olympia.main.service.LeaderboardService;

@RestController
@RequestMapping("/api/public/leaderboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow all origins for public API
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    /**
     * Get all tournament results for the leaderboard
     * Public endpoint - no authentication required
     * Cached on backend and includes Cache-Control headers for frontend caching
     */
    @GetMapping
    public ResponseEntity<List<LeaderboardEntryResponse>> getAllResults() {
        List<LeaderboardEntryResponse> results = leaderboardService.getAllResults();

        // Add Cache-Control header to enable frontend caching
        // Cache for 5 minutes (300 seconds)
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES).cachePublic())
                .body(results);
    }

    /**
     * Get only medal winners for the leaderboard
     * Public endpoint - no authentication required
     * Cached on backend and includes Cache-Control headers for frontend caching
     */
    @GetMapping("/medals")
    public ResponseEntity<List<LeaderboardEntryResponse>> getMedalWinners() {
        List<LeaderboardEntryResponse> results = leaderboardService.getMedalWinners();

        // Add Cache-Control header to enable frontend caching
        // Cache for 5 minutes (300 seconds)
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES).cachePublic())
                .body(results);
    }
}

