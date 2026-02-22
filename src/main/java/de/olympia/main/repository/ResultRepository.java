package de.olympia.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import de.olympia.main.entity.Result;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // Find all results sorted by rank (use Sort parameter for nulls handling)
    List<Result> findAllByOrderByRankAsc();

    // Find results by athlete
    List<Result> findByAthleteId(Long athleteId);

    // Find results by athlete and a specific medal type
    List<Result> findByAthleteIdAndMedal(Long athleteId, Result.Medal medal);

    // Find results by sport
    List<Result> findBySportsId(Long sportsId);

    // Find all medal winners (medal is not null)
    List<Result> findByMedalIsNotNull();

    // Find the most recent result for a given sport that has a scoreType set
    @Query("SELECT r FROM Result r WHERE r.sports.id = :sportsId AND r.scoreType IS NOT NULL ORDER BY r.id DESC")
    List<Result> findTopBySportsIdAndScoreTypeNotNull(@Param("sportsId") Long sportsId);
}

