package de.olympia.main.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2CountryResponse {

    private Long id;
    private String code;
    private String name;

    /** Aggregated medal counts for all athletes of this country. */
    private MedalSummary medals;

    /** Rank among all countries by total medal count (GOLD > SILVER > BRONZE). */
    private Integer leaderboardRank;

    /** All athletes belonging to this country with their medal summary. */
    private List<AthleteRef> athletes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedalSummary {
        private int gold;
        private int silver;
        private int bronze;
        private int total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AthleteRef {
        private Long id;
        private String firstName;
        private String lastName;
        private MedalSummary medals;
    }
}

