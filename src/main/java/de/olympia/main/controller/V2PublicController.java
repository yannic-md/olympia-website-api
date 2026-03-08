package de.olympia.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import de.olympia.main.dto.v2.V2AthleteResponse;
import de.olympia.main.dto.v2.V2CountryResponse;
import de.olympia.main.dto.v2.V2LeaderboardResponse;
import de.olympia.main.dto.v2.V2SportResponse;
import de.olympia.main.service.V2PublicService;

/**
 * Public V2 read-only endpoints.
 */
@RestController
@RequestMapping("/api/v2/public")
@RequiredArgsConstructor
public class V2PublicController {

    private final V2PublicService v2PublicService;

    /**
     * Returns all athletes with medal summaries, leaderboard rank and
     * per-sport result sub-list.
     *
     * @param lang ISO language code: en (default), de, fr
     */
    @GetMapping("/athletes")
    public ResponseEntity<List<V2AthleteResponse>> getAthletes(@RequestParam(defaultValue = "en") String lang) {
        return ResponseEntity.ok(v2PublicService.getAthletes(lang));
    }

    /**
     * Returns all countries with aggregated medal counts, leaderboard rank
     * and their athlete sub-list.
     *
     * @param lang ISO language code: en (default), de, fr
     */
    @GetMapping("/countries")
    public ResponseEntity<List<V2CountryResponse>> getCountries(@RequestParam(defaultValue = "en") String lang) {
        return ResponseEntity.ok(v2PublicService.getCountries(lang));
    }

    /**
     * Returns all sports with their participant list sorted by rank.
     *
     * @param lang ISO language code: en (default), de, fr
     */
    @GetMapping("/sports")
    public ResponseEntity<List<V2SportResponse>> getSports(@RequestParam(defaultValue = "en") String lang) {
        return ResponseEntity.ok(v2PublicService.getSports(lang));
    }

    /**
     * Returns a combined leaderboard payload containing sports, athletes and
     * countries in a single response. Intended for the frontend to populate
     * all views without additional requests.
     *
     * @param lang ISO language code: en (default), de, fr
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<V2LeaderboardResponse> getLeaderboard(
            @RequestParam(defaultValue = "en") String lang) {
        return ResponseEntity.ok(v2PublicService.getLeaderboard(lang));
    }
}

