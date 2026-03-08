package de.olympia.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse {
    private Long id;
    private Long athleteId;
    private String athleteFirstName;
    private String athleteLastName;
    private Long sportId;
    private String sportRawName;
    private String medal;
    private String timeOrPoints;
    private String scoreType;
    private Integer rank;
}

