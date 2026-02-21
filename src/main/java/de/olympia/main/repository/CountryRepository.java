package de.olympia.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import de.olympia.main.entity.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByCode(String code);
    Optional<Country> findByCodeIgnoreCase(String code);
}

