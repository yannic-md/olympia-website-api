package de.olympia.main.service;

import de.olympia.main.dto.CountryResponse;
import de.olympia.main.dto.CreateCountryRequest;
import de.olympia.main.dto.UpdateCountryRequest;
import de.olympia.main.entity.Country;
import de.olympia.main.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CountryResponse getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        return toResponse(country);
    }

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
        return toResponse(savedCountry);
    }

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
        return toResponse(updatedCountry);
    }

    @Transactional
    public void deleteCountry(Long id) {
        if (!countryRepository.existsById(id)) {
            throw new RuntimeException("Country not found with id: " + id);
        }
        countryRepository.deleteById(id);
    }

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

    private CountryResponse toResponse(Country country) {
        CountryResponse response = new CountryResponse();
        response.setId(country.getId());
        response.setCode(country.getCode());
        response.setName(country.getName());
        return response;
    }
}

