package de.olympia.main.service;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

import de.olympia.main.dto.AthleteResponse;
import de.olympia.main.dto.CreateAthleteRequest;
import de.olympia.main.dto.UpdateAthleteRequest;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.entity.Result;
import de.olympia.main.entity.Sports;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
import de.olympia.main.repository.ResultRepository;
import de.olympia.main.repository.SportsRepository;

@Service
@RequiredArgsConstructor
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final CountryRepository countryRepository;
    private final ResultRepository resultRepository;
    private final SportsRepository sportsRepository;
    private final CacheManager cacheManager;

    /**
     * Create a new athlete
     *
     * @param request CreateAthleteRequest with athlete data (firstName, lastName, countryId)
     * @return AthleteResponse DTO with created athlete information
     * @throws IllegalArgumentException if validation fails (missing required fields)
     * @throws RuntimeException if country not found
     */
    @Transactional
    public AthleteResponse createAthlete(CreateAthleteRequest request) {
        validateRequest(request.getFirstName(), request.getLastName());

        Athlete athlete = new Athlete();
        athlete.setFirstName(request.getFirstName());
        athlete.setLastName(request.getLastName());

        if (request.getCountryId() != null) {
            Country country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new RuntimeException("Country not found with id: " + request.getCountryId()));
            athlete.setCountry(country);
        }

        Athlete savedAthlete = athleteRepository.save(athlete);

        // Create result entries for medals and bestTime
        createAthleteResults(savedAthlete, request);

        evictLeaderboardCacheAfterCommit();
        return toResponse(savedAthlete);
    }

    /**
     * Creates Result entries for a newly created athlete based on the request data.
     * Creates one Result per medal and an additional one for bestTime if no medal result exists yet.
     */
    private void createAthleteResults(Athlete athlete, CreateAthleteRequest request) {
        Sports sport = null;
        if (request.getSport() != null && !request.getSport().isEmpty()) {
            sport = sportsRepository.findByNameIgnoreCase(request.getSport()).orElse(null);
        }

        int gold = request.getGoldMedals() != null ? request.getGoldMedals() : 0;
        int silver = request.getSilverMedals() != null ? request.getSilverMedals() : 0;
        int bronze = request.getBronzeMedals() != null ? request.getBronzeMedals() : 0;
        boolean bestTimeSet = false;

        for (int i = 0; i < gold; i++) {
            Result r = buildResult(athlete, sport, Result.Medal.GOLD,
                    !bestTimeSet ? request.getBestTime() : null, request.getScoreType());
            resultRepository.save(r);
            bestTimeSet = true;
        }
        for (int i = 0; i < silver; i++) {
            Result r = buildResult(athlete, sport, Result.Medal.SILVER,
                    !bestTimeSet ? request.getBestTime() : null, request.getScoreType());
            resultRepository.save(r);
            bestTimeSet = true;
        }
        for (int i = 0; i < bronze; i++) {
            Result r = buildResult(athlete, sport, Result.Medal.BRONZE,
                    !bestTimeSet ? request.getBestTime() : null, request.getScoreType());
            resultRepository.save(r);
            bestTimeSet = true;
        }

        // If no medal results were created but bestTime is provided, create a result without medal
        if (!bestTimeSet && request.getBestTime() != null && !request.getBestTime().isEmpty()) {
            Result r = buildResult(athlete, sport, null, request.getBestTime(), request.getScoreType());
            resultRepository.save(r);
        }
    }

    /**
     * Builds a new Result entity with the given parameters.
     * Appends the appropriate suffix ("pts", "wins") to timeOrPoints based on scoreType
     * so the leaderboard display can translate it correctly.
     */
    private Result buildResult(Athlete athlete, Sports sport, Result.Medal medal, String timeOrPoints, Result.ScoreType scoreType) {
        Result r = new Result();
        r.setAthlete(athlete);
        r.setSports(sport);
        r.setMedal(medal);
        r.setScoreType(scoreType);
        if (timeOrPoints != null && !timeOrPoints.isEmpty()) {
            r.setTimeOrPoints(formatTimeOrPoints(timeOrPoints, scoreType));
        }
        return r;
    }

    /**
     * Appends the correct unit suffix to a score value based on its type.
     * TIME values are stored as-is. PTS gets " pts" appended, WINS gets " wins".
     * Already-suffixed values are not double-suffixed.
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
     * Update an existing athlete (partial update)
     * Only provided fields will be updated
     *
     * @param id The ID of the athlete to update
     * @param request UpdateAthleteRequest with fields to update
     * @return AthleteResponse DTO with updated athlete information
     * @throws RuntimeException if athlete or country not found
     */
    @Transactional
    public AthleteResponse updateAthlete(Long id, UpdateAthleteRequest request) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + id));

        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            athlete.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            athlete.setLastName(request.getLastName());
        }

        if (request.getCountryId() != null) {
            Country country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new RuntimeException("Country not found with id: " + request.getCountryId()));
            athlete.setCountry(country);
        }

        // Update results (medals & time/points) if provided
        if (request.getGoldMedals() != null || request.getSilverMedals() != null
                || request.getBronzeMedals() != null || request.getBestTime() != null) {
            updateAthleteResults(id, request);
        }

        Athlete updatedAthlete = athleteRepository.save(athlete);
        evictLeaderboardCacheAfterCommit();
        return toResponse(updatedAthlete);
    }

    /**
     * Updates the results of an athlete based on the requested medal counts and best time.
     * Excess medal results are DELETED (not nulled) to prevent orphaned null-medal rows.
     *
     * @param athleteId The ID of the athlete whose results to update
     * @param request The update request containing new medal counts, best time and scoreType
     */
    private void updateAthleteResults(Long athleteId, UpdateAthleteRequest request) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + athleteId));

        // Resolve the Sports entity: prefer the value from the request, fall back to the last result
        Sports sport = null;
        String sportName = request.getSport();
        if (sportName == null || sportName.isEmpty()) {
            sportName = resultRepository.findByAthleteId(athleteId).stream()
                    .filter(r -> r.getSports() != null)
                    .reduce((first, second) -> second)
                    .map(r -> r.getSports().getName())
                    .orElse(null);
        }
        if (sportName != null && !sportName.isEmpty()) {
            sport = sportsRepository.findByNameIgnoreCase(sportName).orElse(null);
        }

        int currentGold   = resultRepository.findByAthleteIdAndMedal(athleteId, Result.Medal.GOLD).size();
        int currentSilver = resultRepository.findByAthleteIdAndMedal(athleteId, Result.Medal.SILVER).size();
        int currentBronze = resultRepository.findByAthleteIdAndMedal(athleteId, Result.Medal.BRONZE).size();

        int targetGold   = request.getGoldMedals()   != null ? request.getGoldMedals()   : currentGold;
        int targetSilver = request.getSilverMedals()  != null ? request.getSilverMedals()  : currentSilver;
        int targetBronze = request.getBronzeMedals()  != null ? request.getBronzeMedals()  : currentBronze;

        updateMedalType(athlete, sport, Result.Medal.GOLD,   currentGold,   targetGold,   request.getBestTime(), request.getScoreType());
        updateMedalType(athlete, sport, Result.Medal.SILVER, currentSilver, targetSilver, request.getBestTime(), request.getScoreType());
        updateMedalType(athlete, sport, Result.Medal.BRONZE, currentBronze, targetBronze, request.getBestTime(), request.getScoreType());

        // Always update sport, scoreType and bestTime on ALL existing medal results
        // This ensures event_id is set even when the medal count did not change
        final Sports resolvedSport = sport;
        final Result.ScoreType resolvedScoreType = request.getScoreType();
        List<Result> allResults = resultRepository.findByAthleteId(athleteId);
        for (Result r : allResults) {
            boolean changed = false;

            if (resolvedSport != null && !resolvedSport.equals(r.getSports())) {
                r.setSports(resolvedSport);
                changed = true;
            }
            if (resolvedScoreType != null && !resolvedScoreType.equals(r.getScoreType())) {
                r.setScoreType(resolvedScoreType);
                changed = true;
            }
            if (request.getBestTime() != null && !request.getBestTime().isEmpty()) {
                // Use the effective scoreType: from request if provided, else from the (possibly just updated) result
                Result.ScoreType effectiveScoreType = resolvedScoreType != null ? resolvedScoreType : r.getScoreType();
                r.setTimeOrPoints(formatTimeOrPoints(request.getBestTime(), effectiveScoreType));
                changed = true;
            }

            if (changed) {
                resultRepository.save(r);
            }
        }
    }

    /**
     * Adjusts the number of a specific medal type for an athlete.
     * Deletes surplus rows — prevents null-medal entries from accumulating.
     * Creates new result rows when the target exceeds the current count.
     *
     * @param athlete   The athlete entity
     * @param sport     The sports entity (may be null)
     * @param medalType The medal type to adjust
     * @param current   Current count of this medal type
     * @param target    Desired count of this medal type
     * @param bestTime  The time/points value to set on new results
     * @param scoreType The score type to set on new results
     */
    private void updateMedalType(Athlete athlete, Sports sport, Result.Medal medalType,
                                  int current, int target, String bestTime, Result.ScoreType scoreType) {
        if (target == current) return;

        if (target < current) {
            // Delete excess medal results — fixes the null-medal orphan problem
            List<Result> existing = resultRepository.findByAthleteIdAndMedal(athlete.getId(), medalType);
            int toDelete = current - target;
            List<Result> forDeletion = existing.subList(0, toDelete);
            resultRepository.deleteAll(forDeletion);
        } else {
            // Create missing result rows
            int toAdd = target - current;
            for (int i = 0; i < toAdd; i++) {
                Result r = buildResult(athlete, sport, medalType,
                        bestTime != null && !bestTime.isEmpty() ? bestTime : null, scoreType);
                resultRepository.save(r);
            }
        }
    }

    /**
     * Delete an athlete by ID
     *
     * @param id The ID of the athlete to delete
     * @throws RuntimeException if athlete not found
     */
    @Transactional
    public void deleteAthlete(Long id) {
        if (!athleteRepository.existsById(id)) {
            throw new RuntimeException("Athlete not found with id: " + id);
        }
        athleteRepository.deleteById(id);
        evictLeaderboardCacheAfterCommit();
    }

    /**
     * Validate athlete request data
     *
     * @param firstName First name to validate
     * @param lastName Last name to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRequest(String firstName, String lastName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
    }

    /**
     * Convert Athlete entity to AthleteResponse DTO
     *
     * @param athlete The athlete entity to convert
     * @return AthleteResponse DTO with athlete information
     */
    private AthleteResponse toResponse(Athlete athlete) {
        AthleteResponse response = new AthleteResponse();
        response.setId(athlete.getId());
        response.setFirstName(athlete.getFirstName());
        response.setLastName(athlete.getLastName());
        response.setCreatedAt(athlete.getCreatedAt());

        // Derive sport and scoreType from the athlete's most recent result
        resultRepository.findByAthleteId(athlete.getId()).stream()
                .filter(r -> r.getSports() != null)
                .reduce((first, second) -> second) // last element
                .ifPresent(r -> {
                    response.setSport(r.getSports().getName());
                    response.setScoreType(r.getScoreType());
                });

        if (athlete.getCountry() != null) {
            AthleteResponse.CountryDto countryDto = new AthleteResponse.CountryDto();
            countryDto.setId(athlete.getCountry().getId());
            countryDto.setCode(athlete.getCountry().getCode());
            countryDto.setName(athlete.getCountry().getName());
            response.setCountry(countryDto);
        }

        return response;
    }

    /**
     * Evicts the leaderboard cache after the current transaction is committed.
     * This prevents a race condition where the frontend reloads data before DB changes are visible.
     */
    private void evictLeaderboardCacheAfterCommit() {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (String cacheName : List.of("leaderboard", "v2Athletes", "v2Countries", "v2Sports", "v2Leaderboard")) {
                    var cache = cacheManager.getCache(cacheName);
                    if (cache != null) {
                        cache.clear();
                    }
                }
            }
        });
    }
}

