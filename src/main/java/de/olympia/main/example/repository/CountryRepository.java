package de.olympia.main.example.repository;

import de.olympia.main.example.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
