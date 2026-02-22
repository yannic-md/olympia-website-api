package de.olympia.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {
    private Long resultId;
    private Integer rank;
    private Long athleteId;
    private String athleteName;
    private Long countryId;
    private String countryCode;
    private String countryName;
    private String timeOrPoints;
    private String scoreType;
    private String medal;
    private String sportName;
    private String sportRawName;
}

