package de.olympia.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     *
     * @param lang Language code for translations: "en" (default), "de", "fr"
     */
    @GetMapping
    public ResponseEntity<List<LeaderboardEntryResponse>> getAllResults(
            @RequestParam(name = "lang", defaultValue = "en") String lang) {
        List<LeaderboardEntryResponse> results = leaderboardService.getAllResults(lang);
        return ResponseEntity.ok(results);
    }

    /**
     * Get only medal winners for the leaderboard
     * Public endpoint - no authentication required
     *
     * @param lang Language code for translations: "en" (default), "de", "fr"
     */
    @GetMapping("/medals")
    public ResponseEntity<List<LeaderboardEntryResponse>> getMedalWinners(
            @RequestParam(name = "lang", defaultValue = "en") String lang) {
        List<LeaderboardEntryResponse> results = leaderboardService.getMedalWinners(lang);
        return ResponseEntity.ok(results);
    }
}

