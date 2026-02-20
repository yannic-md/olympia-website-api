package de.olympia.main.repository;

import de.olympia.main.entity.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AthleteRepository extends JpaRepository<Athlete, Long> {
    Optional<Athlete> findByFirstNameAndLastName(String firstName, String lastName);
}

