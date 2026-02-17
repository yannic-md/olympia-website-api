package de.olympia.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import de.olympia.main.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // Find all results sorted by rank (use Sort parameter for nulls handling)
    List<Result> findAllByOrderByRankAsc();

    // Find results by athlete
    List<Result> findByAthleteId(Long athleteId);

    // Find results by event
    List<Result> findByEventId(Long eventId);

    // Find all medal winners (medal is not null)
    List<Result> findByMedalIsNotNull();
}

