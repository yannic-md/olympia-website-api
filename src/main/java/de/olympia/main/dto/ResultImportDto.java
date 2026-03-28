package de.olympia.main.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultImportDto {

    @NotNull(message = "Athlete first name is required")
    private String athleteFirstName;

    @NotNull(message = "Athlete last name is required")
    private String athleteLastName;

    private String sport; // Name des Sports (z.B. "Alpine Skiing")

    @Positive(message = "Rank must be a positive number")
    private Integer rank;

    private String timeOrPoints;

    private String scoreType; // PTS, WINS, TIME

    private String medal; // GOLD, SILVER, BRONZE
}

