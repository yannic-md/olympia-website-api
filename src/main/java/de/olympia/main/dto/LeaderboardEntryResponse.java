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
    private String athleteName;
    private String countryCode;
    private String countryName;
    private String timeOrPoints;
    private String medal;
    private Long eventId;
}

