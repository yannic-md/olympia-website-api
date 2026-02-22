package de.olympia.main.service;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.stream.Collectors;

import de.olympia.main.dto.CountryResponse;
import de.olympia.main.dto.CreateCountryRequest;
import de.olympia.main.dto.UpdateCountryRequest;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
import de.olympia.main.repository.ResultRepository;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final AthleteRepository athleteRepository;
    private final ResultRepository resultRepository;
    private final CacheManager cacheManager;

    /**
     * Get all countries from the database
     *
     * @return List of all countries as CountryResponse DTOs
     */
    @Transactional(readOnly = true)
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific country by ID
     *
     * @param id The ID of the country to retrieve
     * @return CountryResponse DTO with country information
     * @throws RuntimeException if country not found
     */
    @Transactional(readOnly = true)
    public CountryResponse getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        return toResponse(country);
    }

    /**
     * Create a new country
     *
     * @param request CreateCountryRequest with country data (code, name)
     * @return CountryResponse DTO with created country information
     * @throws IllegalArgumentException if validation fails or country code already exists
     */
    @Transactional
    public CountryResponse createCountry(CreateCountryRequest request) {
        validateRequest(request.getCode(), request.getName());

        // Check if country code already exists
        if (countryRepository.findByCode(request.getCode()).isPresent()) {
            throw new IllegalArgumentException("Country with code '" + request.getCode() + "' already exists");
        }

        Country country = new Country();
        country.setCode(request.getCode());
        country.setName(request.getName());

        Country savedCountry = countryRepository.save(country);
        evictLeaderboardCacheAfterCommit();
        return toResponse(savedCountry);
    }

    /**
     * Update an existing country (partial update)
     * Only provided fields will be updated
     *
     * @param id The ID of the country to update
     * @param request UpdateCountryRequest with fields to update
     * @return CountryResponse DTO with updated country information
     * @throws RuntimeException if country not found
     * @throws IllegalArgumentException if new country code is already in use
     */
    @Transactional
    public CountryResponse updateCountry(Long id, UpdateCountryRequest request) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));

        if (request.getCode() != null && !request.getCode().isEmpty()) {
            // Check if new code is already used by another country
            countryRepository.findByCode(request.getCode())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new IllegalArgumentException("Country code '" + request.getCode() + "' is already in use");
                        }
                    });
            country.setCode(request.getCode());
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            country.setName(request.getName());
        }

        Country updatedCountry = countryRepository.save(country);
        evictLeaderboardCacheAfterCommit();
        return toResponse(updatedCountry);
    }

    /**
     * Delete a country by ID
     * This will also delete all results for athletes of that country,
     * then cascade delete all associated athletes
     *
     * @param id The ID of the country to delete
     * @throws RuntimeException if country not found
     */
    @Transactional
    public void deleteCountry(Long id) {
        if (!countryRepository.existsById(id)) {
            throw new RuntimeException("Country not found with id: " + id);
        }

        // Delete all results for athletes belonging to this country
        List<Athlete> athletes = athleteRepository.findByCountryId(id);
        for (Athlete athlete : athletes) {
            resultRepository.deleteAll(resultRepository.findByAthleteId(athlete.getId()));
        }

        // Delete all athletes of this country
        athleteRepository.deleteAll(athletes);

        countryRepository.deleteById(id);
        evictLeaderboardCacheAfterCommit();
    }

    /**
     * Validate country request data
     *
     * @param code Country code to validate (max 8 characters)
     * @param name Country name to validate (max 150 characters)
     * @throws IllegalArgumentException if validation fails
     */
    private void validateRequest(String code, String name) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Country name is required");
        }
        if (code.length() > 8) {
            throw new IllegalArgumentException("Country code must not exceed 8 characters");
        }
        if (name.length() > 150) {
            throw new IllegalArgumentException("Country name must not exceed 150 characters");
        }
    }

    /**
     * Convert Country entity to CountryResponse DTO
     *
     * @param country The country entity to convert
     * @return CountryResponse DTO with country information
     */
    private CountryResponse toResponse(Country country) {
        CountryResponse response = new CountryResponse();
        response.setId(country.getId());
        response.setCode(country.getCode());
        response.setName(country.getName());
        return response;
    }

    /**
     * Evicts the leaderboard cache after the current transaction is committed.
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
