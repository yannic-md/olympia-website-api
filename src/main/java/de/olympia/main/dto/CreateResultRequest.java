package de.olympia.main.dto;

import de.olympia.main.entity.Result;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateResultRequest {
    private Long athleteId;
    private Long sportId;
    private Result.Medal medal;
    private String timeOrPoints;
    private Result.ScoreType scoreType;
    private Integer rank;
}

