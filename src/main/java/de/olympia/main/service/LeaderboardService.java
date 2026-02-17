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

    /**
     * Get all results for the leaderboard
     * This method is cached to reduce database queries
     * Cache is evicted after 5 minutes or when results are updated
     *
     * @return List of all results as LeaderboardEntryResponse DTOs, sorted by rank
     */
    @Cacheable(value = "leaderboard", key = "'all'")
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getAllResults() {
        log.info("Fetching all results from database for leaderboard");

        List<Result> results = resultRepository.findAllByOrderByRankAsc();

        return results.stream()
                .map(this::toLeaderboardEntry)
                .collect(Collectors.toList());
    }

    /**
     * Get only medal winners for the leaderboard
     * This method is cached to reduce database queries
     * Sorted by medal type: GOLD -> SILVER -> BRONZE
     *
     * @return List of medal winners as LeaderboardEntryResponse DTOs, sorted by medal type
     */
    @Cacheable(value = "leaderboard", key = "'medals'")
    @Transactional(readOnly = true)
    public List<LeaderboardEntryResponse> getMedalWinners() {
        log.info("Fetching medal winners from database for leaderboard");

        List<Result> results = resultRepository.findByMedalIsNotNull();

        // Sort by medal type: GOLD (1), SILVER (2), BRONZE (3)
        return results.stream()
                .sorted(Comparator.comparing(r -> getMedalSortOrder(r.getMedal())))
                .map(this::toLeaderboardEntry)
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
     * @return LeaderboardEntryResponse DTO with result, athlete, and country information
     */
    private LeaderboardEntryResponse toLeaderboardEntry(Result result) {
        LeaderboardEntryResponse entry = new LeaderboardEntryResponse();
        entry.setResultId(result.getId());
        entry.setRank(result.getRank());
        entry.setTimeOrPoints(result.getTimeOrPoints());
        entry.setMedal(result.getMedal() != null ? result.getMedal().name() : null);
        entry.setEventId(result.getEventId());

        // Athlete information
        if (result.getAthlete() != null) {
            entry.setAthleteName(
                result.getAthlete().getFirstName() + " " + result.getAthlete().getLastName()
            );

            // Country information
            if (result.getAthlete().getCountry() != null) {
                entry.setCountryCode(result.getAthlete().getCountry().getCode());
                entry.setCountryName(result.getAthlete().getCountry().getName());
            }
        }

        return entry;
    }
}
