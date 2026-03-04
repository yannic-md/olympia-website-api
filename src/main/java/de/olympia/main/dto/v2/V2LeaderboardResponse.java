package de.olympia.main.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Combined leaderboard response containing all entities in a single payload,
 * allowing the frontend to build all views without additional requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2LeaderboardResponse {

    /** All sports with their competition results, sorted by sport name. */
    private List<V2SportResponse> sports;

    /** All athletes with aggregated medals and leaderboard rank. */
    private List<V2AthleteResponse> athletes;

    /** All countries with aggregated medals and leaderboard rank. */
    private List<V2CountryResponse> countries;
}

