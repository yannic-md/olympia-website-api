package de.olympia.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import de.olympia.main.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // Find results by athlete
    List<Result> findByAthleteId(Long athleteId);

    // Find results by athlete and a specific medal type
    List<Result> findByAthleteIdAndMedal(Long athleteId, Result.Medal medal);

    /**
     * Fetch all results with athlete and sport eagerly joined in a single query.
     * Uses LEFT JOIN for country so athletes without an assigned country are included.
     * Used by V2 API to avoid N+1 issues when building the full leaderboard.
     */
    @Query("SELECT r FROM Result r JOIN FETCH r.athlete a LEFT JOIN FETCH a.country JOIN FETCH r.sports")
    List<Result> findAllWithAthleteAndSport();
}

