package de.olympia.main.dto;

import de.olympia.main.entity.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AthleteResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private CountryDto country;
    private String sport;
    private Result.ScoreType scoreType;
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryDto {
        private Long id;
        private String code;
        private String name;
    }
}

