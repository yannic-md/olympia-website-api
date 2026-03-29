package de.olympia.main.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryImportDto {

    @NotBlank(message = "Country code is required")
    private String code;

    @NotBlank(message = "Country name is required")
    private String name;

    private String nameEn;
    private String nameDe;
    private String nameFr;
}

