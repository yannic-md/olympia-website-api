package de.olympia.main.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2AthleteResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private CountryRef country;

    /** Aggregated medal counts across all sports. */
    private MedalSummary medals;

    /** Rank among all athletes by total medal count (GOLD > SILVER > BRONZE). */
    private Integer leaderboardRank;

    /** One entry per sport the athlete has participated in. */
    private List<SportResult> results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryRef {
        private Long id;
        private String code;
        private String name;
    }

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
    public static class SportResult {
        private Long sportId;
        private String sportName;
        private String sportRawName;
        /** Score type of this sport (PTS, WINS, TIME). */
        private String scoreType;
        /** Best result value (time, points, wins). */
        private String result;
        /** Rank achieved in this sport. */
        private Integer rank;
        /** Medal received, null if none. */
        private String medal;
    }
}

