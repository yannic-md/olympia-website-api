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
}

