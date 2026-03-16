package de.olympia.main.importer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultImportDto {

    @NotBlank(message = "Athlete first name is required")
    private String athleteFirstName;

    @NotBlank(message = "Athlete last name is required")
    private String athleteLastName;

    @NotBlank(message = "Sport is required")
    private String sport;

    @Positive(message = "Rank must be a positive number")
    private Integer rank;

    private String timeOrPoints;

    private String scoreType; // PTS, WINS, TIME

    private String medal; // GOLD, SILVER, BRONZE
}

