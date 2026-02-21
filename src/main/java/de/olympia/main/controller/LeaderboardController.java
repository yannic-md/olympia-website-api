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
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    /**
     * Get all tournament results for the leaderboard
     * Public endpoint - no authentication required
     * Cached on backend and includes Cache-Control headers for frontend caching
     *
     * @param lang Language code for translations: "en" (default), "de", "fr"
     */
    @GetMapping
    public ResponseEntity<List<LeaderboardEntryResponse>> getAllResults(
            @RequestParam(name = "lang", defaultValue = "en") String lang) {
        List<LeaderboardEntryResponse> results = leaderboardService.getAllResults(lang);

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
     *
     * @param lang Language code for translations: "en" (default), "de", "fr"
     */
    @GetMapping("/medals")
    public ResponseEntity<List<LeaderboardEntryResponse>> getMedalWinners(
            @RequestParam(name = "lang", defaultValue = "en") String lang) {
        List<LeaderboardEntryResponse> results = leaderboardService.getMedalWinners(lang);

        // Add Cache-Control header to enable frontend caching
        // Cache for 5 minutes (300 seconds)
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES).cachePublic())
                .body(results);
    }
}

