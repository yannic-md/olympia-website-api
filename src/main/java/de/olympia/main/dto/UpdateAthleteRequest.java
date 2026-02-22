package de.olympia.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAthleteRequest {
    private String firstName;
    private String lastName;
    private Long countryId;
    private Integer goldMedals;
    private Integer silverMedals;
    private Integer bronzeMedals;
    private String bestTime;
}

