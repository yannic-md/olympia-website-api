package de.olympia.main.example.service;

import de.olympia.main.example.entity.CountryMedalEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MedalService {

    private final List<CountryMedalEntity> countries = new ArrayList<>();

    public MedalService() {
        countries.add(new CountryMedalEntity(1L, "USA", "United States", Map.of("GOLD", 10, "SILVER", 5, "BRONZE", 3)));
        countries.add(new CountryMedalEntity(2L, "GER", "Germany", Map.of("GOLD", 6, "SILVER", 7, "BRONZE", 4)));
        countries.add(new CountryMedalEntity(3L, "FRA", "France", Map.of("GOLD", 3, "SILVER", 2, "BRONZE", 6)));
    }

    public List<CountryMedalEntity> getAllCountryMedals() {
        return List.copyOf(countries);
    }

    public Optional<CountryMedalEntity> findByCode(String code) {
        return countries.stream()
                .filter(c -> c.getCode().equalsIgnoreCase(code))
                .findFirst();
    }
}
