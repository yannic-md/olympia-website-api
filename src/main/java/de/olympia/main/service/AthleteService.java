package de.olympia.main.service;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

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
     * Get all athletes from the database
     *
     * @return List of all athletes as AthleteResponse DTOs
     */
    @Transactional(readOnly = true)
    public List<AthleteResponse> getAllAthletes() {
        return athleteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific athlete by ID
     *
     * @param id The ID of the athlete to retrieve
     * @return AthleteResponse DTO with athlete information
     * @throws RuntimeException if athlete not found
     */
    @Transactional(readOnly = true)
    public AthleteResponse getAthleteById(Long id) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + id));
        return toResponse(athlete);
    }

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
                    !bestTimeSet ? request.getBestTime() : null);
            resultRepository.save(r);
            bestTimeSet = true;
        }
        for (int i = 0; i < silver; i++) {
            Result r = buildResult(athlete, sport, Result.Medal.SILVER,
                    !bestTimeSet ? request.getBestTime() : null);
            resultRepository.save(r);
            bestTimeSet = true;
        }
        for (int i = 0; i < bronze; i++) {
            Result r = buildResult(athlete, sport, Result.Medal.BRONZE,
                    !bestTimeSet ? request.getBestTime() : null);
            resultRepository.save(r);
            bestTimeSet = true;
        }

        // If no medal results were created but bestTime is provided, create a result without medal
        if (!bestTimeSet && request.getBestTime() != null && !request.getBestTime().isEmpty()) {
            Result r = buildResult(athlete, sport, null, request.getBestTime());
            resultRepository.save(r);
        }
    }

    /**
     * Builds a new Result entity with the given parameters.
     */
    private Result buildResult(Athlete athlete, Sports sport, Result.Medal medal, String timeOrPoints) {
        Result r = new Result();
        r.setAthlete(athlete);
        r.setSports(sport);
        r.setMedal(medal);
        if (timeOrPoints != null && !timeOrPoints.isEmpty()) {
            r.setTimeOrPoints(timeOrPoints);
        }
        return r;
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
     * Compares current medal counts with requested counts and adjusts individual result entries accordingly.
     *
     * @param athleteId The ID of the athlete whose results to update
     * @param request The update request containing new medal counts and best time
     */
    private void updateAthleteResults(Long athleteId, UpdateAthleteRequest request) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + athleteId));
        List<Result> results = resultRepository.findByAthleteId(athleteId);

        // Update best time on the first result if provided
        if (request.getBestTime() != null) {
            if (results.isEmpty()) {
                // Create a new result for the time/points
                Result r = buildResult(athlete, null, null,
                        request.getBestTime().isEmpty() ? null : request.getBestTime());
                resultRepository.save(r);
                results = resultRepository.findByAthleteId(athleteId);
            } else {
                results.get(0).setTimeOrPoints(request.getBestTime().isEmpty() ? null : request.getBestTime());
                resultRepository.save(results.get(0));
            }
        }

        // Count current medals
        int currentGold = 0, currentSilver = 0, currentBronze = 0;
        for (Result r : results) {
            if (r.getMedal() == null) continue;
            switch (r.getMedal()) {
                case GOLD -> currentGold++;
                case SILVER -> currentSilver++;
                case BRONZE -> currentBronze++;
            }
        }

        int targetGold = request.getGoldMedals() != null ? request.getGoldMedals() : currentGold;
        int targetSilver = request.getSilverMedals() != null ? request.getSilverMedals() : currentSilver;
        int targetBronze = request.getBronzeMedals() != null ? request.getBronzeMedals() : currentBronze;

        // Update medals on existing results, create new ones if needed
        updateMedalType(athlete, results, Result.Medal.GOLD, currentGold, targetGold);
        updateMedalType(athlete, results, Result.Medal.SILVER, currentSilver, targetSilver);
        updateMedalType(athlete, results, Result.Medal.BRONZE, currentBronze, targetBronze);
    }

    /**
     * Adjusts the number of a specific medal type across an athlete's results.
     * If the target count is lower, removes medals from results. If higher, assigns medals to
     * results that currently have no medal, or creates new result entries.
     *
     * @param results The list of the athlete's results
     * @param medalType The medal type to adjust (GOLD, SILVER, BRONZE)
     * @param current The current count of this medal type
     * @param target The desired count of this medal type
     */
    private void updateMedalType(Athlete athlete, List<Result> results, Result.Medal medalType, int current, int target) {
        if (target == current) return;

        if (target < current) {
            // Remove excess medals
            int toRemove = current - target;
            for (Result r : results) {
                if (toRemove <= 0) break;
                if (r.getMedal() == medalType) {
                    r.setMedal(null);
                    resultRepository.save(r);
                    toRemove--;
                }
            }
        } else {
            // Add missing medals to results without a medal
            int toAdd = target - current;
            for (Result r : results) {
                if (toAdd <= 0) break;
                if (r.getMedal() == null) {
                    r.setMedal(medalType);
                    resultRepository.save(r);
                    toAdd--;
                }
            }
            // If there are still medals to add, create new result entries
            for (int i = 0; i < toAdd; i++) {
                Result r = buildResult(athlete, null, medalType, null);
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
                var cache = cacheManager.getCache("leaderboard");
                if (cache != null) {
                    cache.clear();
                }
            }
        });
    }
}

