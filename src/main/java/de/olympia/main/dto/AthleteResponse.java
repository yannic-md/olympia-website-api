package de.olympia.main.dto;

import de.olympia.main.entity.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private MedalsDto medals;
    private List<ResultDto> results;
    private LocalDateTime createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryDto {
        private Long id;
        private String code;
        private String name;
        private String nameEn;
        private String nameDe;
        private String nameFr;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MedalsDto {
        private int gold;
        private int silver;
        private int bronze;
        private int total;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDto {
        private Long sportId;
        private String sportName;
        private String sportRawName;
        private String scoreType;
        private String result;
        private Integer rank;
        private String medal;
    }
}

