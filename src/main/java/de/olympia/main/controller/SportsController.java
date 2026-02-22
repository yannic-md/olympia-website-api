package de.olympia.main.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import de.olympia.main.dto.SportResponse;
import de.olympia.main.entity.Sports;
import de.olympia.main.repository.ResultRepository;
import de.olympia.main.repository.SportsRepository;
import de.olympia.main.service.TranslationService;

@RestController
@RequestMapping("/api/public/sports")
@RequiredArgsConstructor
public class SportsController {

    private final SportsRepository sportsRepository;
    private final ResultRepository resultRepository;
    private final TranslationService translationService;

    /**
     * Returns all sports with their translated name, raw DB name and scoreType.
     * The scoreType is derived from the most recent result that has one set for that sport.
     * Public endpoint — no authentication required.
     *
     * @param lang Language code for translation (en, de, fr). Defaults to "en".
     * @return List of all sports as SportResponse DTOs, sorted by translated name.
     */
    @GetMapping
    public ResponseEntity<List<SportResponse>> getAllSports(@RequestParam(defaultValue = "en") String lang) {
        String normalizedLang = translationService.normalizeLang(lang);

        List<SportResponse> sports = sportsRepository.findAll().stream()
                .map(s -> toSportResponse(s, normalizedLang))
                .sorted(Comparator.comparing(SportResponse::getName))
                .toList();

        return ResponseEntity.ok(sports);
    }

    /**
     * Converts a Sports entity to a SportResponse DTO.
     * Derives scoreType from the most recent result that has one set for this sport.
     */
    private SportResponse toSportResponse(Sports sports, String lang) {
        String translatedName = translationService.translateSport(sports.getName(), lang);

        // Derive scoreType from the most recent result; fall back to TIME if none found
        String scoreType = resultRepository.findTopBySportsIdAndScoreTypeNotNull(sports.getId())
                .stream()
                .findFirst()
                .map(r -> r.getScoreType().name())
                .orElse("TIME");

        return new SportResponse(translatedName, sports.getName(), scoreType);
    }
}



