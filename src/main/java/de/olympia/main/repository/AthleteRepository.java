package de.olympia.main.repository;

import de.olympia.main.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    List<Athlete> findByCountryId(Long countryId);
    List<Athlete> findByLastNameContainingIgnoreCase(String lastName);
}

