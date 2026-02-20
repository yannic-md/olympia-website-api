package de.olympia.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import de.olympia.main.entity.Athlete;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    List<Athlete> findByCountryId(Long countryId);
    List<Athlete> findByLastNameContainingIgnoreCase(String lastName);
    Optional<Athlete> findByFirstNameAndLastName(String firstName, String lastName);
}

