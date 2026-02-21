package de.olympia.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import de.olympia.main.dto.LeaderboardEntryResponse;
import de.olympia.main.entity.Result;
import de.olympia.main.repository.ResultRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class LeaderboardService {

    private final ResultRepository resultRepository;
    private final TranslationService translationService;

    /**
     * Get all results for the leaderboard
     * This method is cached to reduce database queries
     * Cache is evicted after 5 minutes or when results are updated
     *
     * @param lang Language code for translations (en, de, fr). Defaults to en.
     * @return List of all results as LeaderboardEntryResponse DTOs, sorted by rank
     */
    @Cacheable(value = "leaderboard", key = "'all_' + #lang")
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getAllResults(String lang) {
        String normalizedLang = translationService.normalizeLang(lang);
        log.info("Fetching all results from database for leaderboard (lang={})", normalizedLang);

        List<Result> results = resultRepository.findAllByOrderByRankAsc();

        return results.stream()
                .map(r -> toLeaderboardEntry(r, normalizedLang))
                .collect(Collectors.toList());
    }

    /**
     * Get only medal winners for the leaderboard
     * This method is cached to reduce database queries
     * Sorted by medal type: GOLD -> SILVER -> BRONZE
     *
     * @param lang Language code for translations (en, de, fr). Defaults to en.
     * @return List of medal winners as LeaderboardEntryResponse DTOs, sorted by medal type
     */
    @Cacheable(value = "leaderboard", key = "'medals_' + #lang")
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getMedalWinners(String lang) {
        String normalizedLang = translationService.normalizeLang(lang);
        log.info("Fetching medal winners from database for leaderboard (lang={})", normalizedLang);

        List<Result> results = resultRepository.findByMedalIsNotNull();

        // Sort by medal type: GOLD (1), SILVER (2), BRONZE (3)
        return results.stream()
                .sorted(Comparator.comparing(r -> getMedalSortOrder(r.getMedal())))
                .map(r -> toLeaderboardEntry(r, normalizedLang))
                .collect(Collectors.toList());
    }

    /**
     * Get sort order for medal type
     *
     * @param medal The medal to get sort order for
     * @return Sort order (1=GOLD, 2=SILVER, 3=BRONZE, 4=null)
     */
    private int getMedalSortOrder(Result.Medal medal) {
        if (medal == null) return 4;
        return switch (medal) {
            case GOLD -> 1;
            case SILVER -> 2;
            case BRONZE -> 3;
        };
    }

    /**
     * Convert Result entity to LeaderboardEntryResponse DTO
     *
     * @param result The result entity to convert
     * @param lang   The language code for translations
     * @return LeaderboardEntryResponse DTO with translated result, athlete, and country information
     */
    private LeaderboardEntryResponse toLeaderboardEntry(Result result, String lang) {
        LeaderboardEntryResponse entry = new LeaderboardEntryResponse();
        entry.setResultId(result.getId());
        entry.setRank(result.getRank());
        entry.setTimeOrPoints(result.getTimeOrPoints());
        entry.setScoreType(result.getScoreType() != null ? result.getScoreType().name().toUpperCase() : null);
        entry.setMedal(result.getMedal() != null ? result.getMedal().name().toUpperCase() : null);

        if (result.getSports() != null) {
            entry.setSportName(translationService.translateSport(result.getSports().getName(), lang));
        }

        if (result.getAthlete() != null) {
            entry.setAthleteName(
                result.getAthlete().getFirstName() + " " + result.getAthlete().getLastName()
            );

            if (result.getAthlete().getCountry() != null) {
                entry.setCountryCode(result.getAthlete().getCountry().getCode());
                entry.setCountryName(translationService.translateCountry(
                        result.getAthlete().getCountry().getName(), lang));
            }
        }

        return entry;
    }
}
