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
     * Creates initial Result entries for a newly created athlete based on the request data.
     * 
     * This method:
     * 1. Resolves the sport entity by name (case-insensitive), may be null
     * 2. Creates one Result row per medal (gold, silver, bronze) with the given sport
     * 3. Applies bestTime only to the FIRST result created (bestTimeSet flag ensures this)
     * 4. If medals are created but no bestTime is available, creates one additional result without medal
     * 5. If no medals are created but bestTime is provided, creates a single result with bestTime
     *
     * The bestTime is only stored once to avoid redundant duplication across multiple results.
     * This is an optimization to keep the database lean for athletes with multiple medals.
     *
     * @param athlete The newly saved athlete entity to create results for
     * @param request The creation request containing medal counts, bestTime, sport name, and scoreType
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

        // Create gold medal results
        for (int i = 0; i < gold; i++) {
            Result r = buildResult(athlete, sport, Result.Medal.GOLD,
                    !bestTimeSet ? request.getBestTime() : null, request.getScoreType());
            resultRepository.save(r);
            bestTimeSet = true;
        }
        // Create silver medal results
        for (int i = 0; i < silver; i++) {
            Result r = buildResult(athlete, sport, Result.Medal.SILVER,
                    !bestTimeSet ? request.getBestTime() : null, request.getScoreType());
            resultRepository.save(r);
            bestTimeSet = true;
        }
        // Create bronze medal results
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
     * 
     * This factory method creates a Result with:
     * - Athlete and Sport references set
     * - Medal type assigned
     * - Score type assigned
     * - timeOrPoints value formatted with appropriate unit suffix (via formatTimeOrPoints)
     *
     * The scoreType determines how bestTime will be formatted:
     * - PTS: appends " pts" suffix
     * - WINS: appends " wins" suffix
     * - TIME or null: no suffix added
     *
     * @param athlete The athlete to associate with this result
     * @param sport The sport to associate with this result (may be null)
     * @param medal The medal type (GOLD, SILVER, BRONZE, or null for non-medal results)
     * @param timeOrPoints The raw time/score value to store (formatted before persistence)
     * @param scoreType The scoring method (PTS, WINS, TIME)
     * @return A new Result entity (not yet persisted to database)
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
     * Appends the correct unit suffix to a score value based on its scoreType.
     * 
     * Suffix mapping:
     * - ScoreType.TIME: No suffix (value stored as-is, e.g., "12.34" or "1:23:45")
     * - ScoreType.PTS: Appends " pts" suffix (e.g., "150 pts")
     * - ScoreType.WINS: Appends " wins" suffix (e.g., "5 wins")
     *
     * Idempotent: Already-suffixed values are not double-suffixed.
     * This prevents issues when formatTimeOrPoints is called multiple times on same value.
     *
     * The suffix is used by the leaderboard display to provide proper i18n
     * translations and formatting without storing locale-specific information in the database.
     *
     * @param value The raw numeric or time string value (may be null or empty)
     * @param scoreType The score type that determines the suffix (PTS, WINS, TIME, or null)
     * @return The value with appropriate suffix appended, or original value if null/empty/TIME type
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
     * 
     * This complex method handles:
     * 1. Resolving the sport from request or falling back to most recent result
     * 2. Comparing current vs. target medal counts for each medal type
     * 3. DELETING excess medal results when target < current (prevents null-medal orphans)
     * 4. CREATING new medal results when target > current
     * 5. UPDATING ALL existing medal results with new sport, scoreType, and bestTime
     * 6. Properly formatting bestTime with unit suffixes based on scoreType
     *
     * The logic ensures:
     * - No accumulation of null-medal rows from decreased medal counts
     * - All medal results are kept in sync with the same sport and scoreType
     * - bestTime is applied consistently across all medal results
     * - event_id (sports) is always set even when medal count doesn't change
     *
     * @param athleteId The ID of the athlete whose results to update
     * @param request The update request containing new medal counts, best time and scoreType
     * @throws RuntimeException if athlete not found
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

        // Get current medal counts for each type
        int currentGold   = resultRepository.findByAthleteIdAndMedal(athleteId, Result.Medal.GOLD).size();
        int currentSilver = resultRepository.findByAthleteIdAndMedal(athleteId, Result.Medal.SILVER).size();
        int currentBronze = resultRepository.findByAthleteIdAndMedal(athleteId, Result.Medal.BRONZE).size();

        // Get target medal counts from request, defaulting to current if not provided
        int targetGold   = request.getGoldMedals()   != null ? request.getGoldMedals()   : currentGold;
        int targetSilver = request.getSilverMedals()  != null ? request.getSilverMedals()  : currentSilver;
        int targetBronze = request.getBronzeMedals()  != null ? request.getBronzeMedals()  : currentBronze;

        // Update each medal type to match target counts
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

            // Update sport if a new one was provided or resolved from results
            if (resolvedSport != null && !resolvedSport.equals(r.getSports())) {
                r.setSports(resolvedSport);
                changed = true;
            }
            // Update scoreType if provided
            if (resolvedScoreType != null && !resolvedScoreType.equals(r.getScoreType())) {
                r.setScoreType(resolvedScoreType);
                changed = true;
            }
            // Update bestTime with proper formatting based on scoreType
            if (request.getBestTime() != null && !request.getBestTime().isEmpty()) {
                // Use the effective scoreType: from request if provided, else from the (possibly just updated) result
                Result.ScoreType effectiveScoreType = resolvedScoreType != null ? resolvedScoreType : r.getScoreType();
                r.setTimeOrPoints(formatTimeOrPoints(request.getBestTime(), effectiveScoreType));
                changed = true;
            }

            // Only save if something actually changed
            if (changed) {
                resultRepository.save(r);
            }
        }
    }

    /**
     * Adjusts the number of a specific medal type for an athlete.
     * 
     * This method handles two scenarios:
     * 1. REDUCTION (target < current): Deletes the excess medal result rows.
     *    This fixes the null-medal orphan problem where reducing medal counts would
     *    leave behind rows with null medals from UPSERT logic.
     * 2. INCREASE (target > current): Creates new result rows with the given sport and bestTime.
     *
     * Key aspects:
     * - Deletion happens FIRST to clean up excess rows before creating new ones
     * - New results are created with the resolved sport (may be null)
     * - bestTime is passed through and formatted according to scoreType
     * - Does nothing if target == current (consistent state maintained)
     *
     * @param athlete   The athlete entity to adjust medals for
     * @param sport     The sports entity to set on new results (may be null if not yet resolved)
     * @param medalType The medal type to adjust (GOLD, SILVER, or BRONZE)
     * @param current   Current count of this medal type
     * @param target    Desired count of this medal type
     * @param bestTime  The time/points value to set on new results (applied via formatTimeOrPoints)
     * @param scoreType The score type to set on new results (PTS, WINS, or TIME)
     */
    private void updateMedalType(Athlete athlete, Sports sport, Result.Medal medalType,
                                  int current, int target, String bestTime, Result.ScoreType scoreType) {
        if (target == current) return; // No change needed

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
     * Converts an Athlete entity to an AthleteResponse DTO for API responses.
     * 
     * This method:
     * 1. Maps basic athlete properties (id, firstName, lastName, createdAt)
     * 2. Derives sport name and scoreType from the athlete's most recent Result
     * 3. Aggregates medal counts (gold, silver, bronze) from all results
     * 4. Filters results to only include those with an associated sport
     * 5. Builds per-sport result DTOs with rank, time/points, and medal info
     * 6. Converts country reference with all translated names
     *
     * The sport and scoreType are derived from the LAST (most recent) result
     * to represent the athlete's current/primary sport.
     *
     * @param athlete The athlete entity to convert (must have id and names set)
     * @return An AthleteResponse DTO ready for JSON serialization in API responses
     */
    private AthleteResponse toResponse(Athlete athlete) {
        AthleteResponse response = new AthleteResponse();
        response.setId(athlete.getId());
        response.setFirstName(athlete.getFirstName());
        response.setLastName(athlete.getLastName());
        response.setCreatedAt(athlete.getCreatedAt());

        // Fetch all results for this athlete
        List<Result> results = resultRepository.findByAthleteId(athlete.getId());

        // Derive sport and scoreType from the athlete's most recent result
        results.stream()
                .filter(r -> r.getSports() != null)
                .reduce((first, second) -> second)  // Get last element
                .ifPresent(r -> {
                    response.setSport(r.getSports().getName());
                    response.setScoreType(r.getScoreType());
                });

        // Aggregate medal counts from all results
        int gold   = (int) results.stream().filter(r -> r.getMedal() == Result.Medal.GOLD).count();
        int silver = (int) results.stream().filter(r -> r.getMedal() == Result.Medal.SILVER).count();
        int bronze = (int) results.stream().filter(r -> r.getMedal() == Result.Medal.BRONZE).count();
        response.setMedals(new AthleteResponse.MedalsDto(gold, silver, bronze, gold + silver + bronze));

        // Build per-sport result DTOs (only for results with associated sport)
        List<AthleteResponse.ResultDto> resultDtos = results.stream()
                .filter(r -> r.getSports() != null)
                .map(r -> new AthleteResponse.ResultDto(
                        r.getSports().getId(),
                        r.getSports().getName(),
                        r.getSports().getName(),
                        r.getScoreType() != null ? r.getScoreType().name() : null,
                        r.getTimeOrPoints(),
                        r.getRank(),
                        r.getMedal() != null ? r.getMedal().name() : null
                ))
                .collect(java.util.stream.Collectors.toList());
        response.setResults(resultDtos);

        // Convert country reference with translations
        if (athlete.getCountry() != null) {
            Country c = athlete.getCountry();
            AthleteResponse.CountryDto countryDto = new AthleteResponse.CountryDto();
            countryDto.setId(c.getId());
            countryDto.setCode(c.getCode());
            countryDto.setName(c.getName());
            countryDto.setNameEn(c.getNameEn());
            countryDto.setNameDe(c.getNameDe());
            countryDto.setNameFr(c.getNameFr());
            response.setCountry(countryDto);
        }

        return response;
    }

    /**
     * Schedules leaderboard cache eviction after the current transaction commits.
     * 
     * This prevents a race condition where the frontend might:
     * 1. Receive the API response (before commit)
     * 2. Reload data from cache (before DB changes are visible)
     * 3. See stale data
     *
     * By using TransactionSynchronizationManager, cache eviction happens AFTER the
     * database transaction is fully committed, ensuring DB consistency.
     *
     * Clears caches: leaderboard, v2Athletes, v2Countries, v2Sports, v2Leaderboard
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

