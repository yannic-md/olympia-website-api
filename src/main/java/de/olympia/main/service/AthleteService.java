package de.olympia.main.service;

import de.olympia.main.dto.AthleteResponse;
import de.olympia.main.dto.CreateAthleteRequest;
import de.olympia.main.dto.UpdateAthleteRequest;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final CountryRepository countryRepository;

    @Transactional(readOnly = true)
    public List<AthleteResponse> getAllAthletes() {
        return athleteRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AthleteResponse getAthleteById(Long id) {
        Athlete athlete = athleteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + id));
        return toResponse(athlete);
    }

    @Transactional
    public AthleteResponse createAthlete(CreateAthleteRequest request) {
        validateRequest(request.getFirstName(), request.getLastName(), request.getGender());

        Athlete athlete = new Athlete();
        athlete.setFirstName(request.getFirstName());
        athlete.setLastName(request.getLastName());

        if (request.getCountryId() != null) {
            Country country = countryRepository.findById(request.getCountryId())
                    .orElseThrow(() -> new RuntimeException("Country not found with id: " + request.getCountryId()));
            athlete.setCountry(country);
        }

        if (request.getGender() != null && !request.getGender().isEmpty()) {
            athlete.setGender(Athlete.Gender.valueOf(request.getGender()));
        }

        Athlete savedAthlete = athleteRepository.save(athlete);
        return toResponse(savedAthlete);
    }

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

        if (request.getGender() != null && !request.getGender().isEmpty()) {
            athlete.setGender(Athlete.Gender.valueOf(request.getGender()));
        }

        Athlete updatedAthlete = athleteRepository.save(athlete);
        return toResponse(updatedAthlete);
    }

    @Transactional
    public void deleteAthlete(Long id) {
        if (!athleteRepository.existsById(id)) {
            throw new RuntimeException("Athlete not found with id: " + id);
        }
        athleteRepository.deleteById(id);
    }

    private void validateRequest(String firstName, String lastName, String gender) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (gender != null && !gender.isEmpty()) {
            try {
                Athlete.Gender.valueOf(gender);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid gender. Must be M, F, or D");
            }
        }
    }

    private AthleteResponse toResponse(Athlete athlete) {
        AthleteResponse response = new AthleteResponse();
        response.setId(athlete.getId());
        response.setFirstName(athlete.getFirstName());
        response.setLastName(athlete.getLastName());
        response.setGender(athlete.getGender() != null ? athlete.getGender().name() : null);
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
}

