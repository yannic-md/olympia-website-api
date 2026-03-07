package de.olympia.main.service;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import de.olympia.main.dto.CreateResultRequest;
import de.olympia.main.dto.ResultResponse;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Result;
import de.olympia.main.entity.Sports;
import de.olympia.main.entity.User;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.ResultRepository;
import de.olympia.main.repository.SportsRepository;
import de.olympia.main.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final AthleteRepository athleteRepository;
    private final SportsRepository sportsRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    /**
     * Creates or updates a result for a given athlete + sport combination.
     * If a result row for the same (sport, athlete) pair already exists it is
     * overwritten (upsert), otherwise a new row is inserted.
     * {@code created_by} is resolved from the current security context.
     * {@code rank} is derived automatically from the medal (GOLD=1, SILVER=2, BRONZE=3).
     *
     * @param request The incoming result payload.
     * @return {@link ResultResponse} DTO of the persisted result.
     * @throws RuntimeException when the referenced athlete or sport cannot be found.
     */
    @Transactional
    public ResultResponse upsertResult(CreateResultRequest request) {
        Athlete athlete = athleteRepository.findById(request.getAthleteId())
                .orElseThrow(() -> new RuntimeException("Athlete not found: " + request.getAthleteId()));

        Sports sport = sportsRepository.findById(request.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found: " + request.getSportId()));

        // Upsert priority:
        // 1. Re-use the existing row for this medal slot (may belong to a different athlete).
        //    This guarantees at most one Gold / Silver / Bronze per sport.
        // 2. If no medal slot exists yet, check whether the same athlete already has a row
        //    for this sport (e.g. a previous result without a medal) and update that row.
        // 3. Otherwise insert a fresh row.
        Result result = resultRepository.findBySportsIdAndMedal(sport.getId(), request.getMedal())
                .or(() -> resultRepository.findBySportsIdAndAthleteId(sport.getId(), athlete.getId()))
                .orElseGet(Result::new);

        result.setAthlete(athlete);
        result.setSports(sport);
        result.setMedal(request.getMedal());

        Result.ScoreType scoreType = request.getScoreType() != null ? request.getScoreType()
                : (sport.getScoreType() != null ? Result.ScoreType.valueOf(sport.getScoreType().name()) : null);
        result.setScoreType(scoreType);
        result.setTimeOrPoints(formatTimeOrPoints(request.getTimeOrPoints(), scoreType));

        // Derive rank from medal — overrides any client-supplied value
        result.setRank(medalToRank(request.getMedal()));

        // Resolve created_by from the authenticated user — only set on INSERT, not UPDATE
        if (result.getId() == null) {
            resolveCurrentUser().ifPresent(result::setCreatedBy);
        }

        Result saved = resultRepository.save(result);
        evictLeaderboardCacheAfterCommit();
        return toResponse(saved);
    }

    /**
     * Deletes a result row by its primary key.
     *
     * @param id The result ID to delete.
     * @throws RuntimeException when the result cannot be found.
     */
    @Transactional
    public void deleteResult(Long id) {
        if (!resultRepository.existsById(id)) {
            throw new RuntimeException("Result not found: " + id);
        }
        resultRepository.deleteById(id);
        evictLeaderboardCacheAfterCommit();
    }

    /**
     * Maps a {@link Result} entity to a {@link ResultResponse} DTO.
     *
     * @param result The entity to map.
     * @return The mapped DTO.
     */
    private ResultResponse toResponse(Result result) {
        return new ResultResponse(
                result.getId(),
                result.getAthlete().getId(),
                result.getAthlete().getFirstName(),
                result.getAthlete().getLastName(),
                result.getSports() != null ? result.getSports().getId() : null,
                result.getSports() != null ? result.getSports().getName() : null,
                result.getMedal() != null ? result.getMedal().name() : null,
                result.getTimeOrPoints(),
                result.getScoreType() != null ? result.getScoreType().name() : null,
                result.getRank()
        );
    }

    /**
     * Appends the appropriate unit suffix to a raw score value.
     * TIME values are stored unchanged; PTS gets " pts", WINS gets " wins".
     * Already-suffixed values are not double-suffixed.
     *
     * @param value     The raw input string.
     * @param scoreType The score type determining the suffix.
     * @return The formatted string, or the original value when no suffix applies.
     */
    private String formatTimeOrPoints(String value, Result.ScoreType scoreType) {
        if (value == null || value.isEmpty()) return value;
        if (scoreType == Result.ScoreType.PTS && !value.endsWith(" pts")) {
            return value + " pts";
        }
        if (scoreType == Result.ScoreType.WINS && !value.endsWith(" wins")) {
            return value + " wins";
        }
        return value;
    }

    /**
     * Schedules leaderboard cache eviction after the current transaction commits.
     * This prevents a race condition where the frontend reloads data before DB
     * changes are visible.
     */
    private void evictLeaderboardCacheAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String cacheName : List.of("v2Athletes", "v2Countries", "v2Sports", "v2Leaderboard")) {
                    var cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                    }
                }
            }
        });
    }

    /**
     * Maps a medal enum value to its conventional rank position.
     * GOLD → 1, SILVER → 2, BRONZE → 3, null → null.
     */
    private Integer medalToRank(Result.Medal medal) {
        if (medal == null) return null;
        return switch (medal) {
            case GOLD   -> 1;
            case SILVER -> 2;
            case BRONZE -> 3;
        };
    }

    /**
     * Resolves the currently authenticated {@link User} entity from the security context.
     * Returns an empty Optional when no principal is available or the username cannot be found.
     */
    private java.util.Optional<User> resolveCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return java.util.Optional.empty();
        return userRepository.findByUsername(auth.getName());
    }
}
