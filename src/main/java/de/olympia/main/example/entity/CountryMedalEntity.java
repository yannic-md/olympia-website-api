package de.olympia.main.example.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CountryMedalEntity {
    private Long id;
    private String code;
    private String name;
    private Map<String, Integer> medals;

    public CountryMedalEntity() {}

    public CountryMedalEntity(Long id, String code, String name, Map<String, Integer> medals) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.medals = medals;
    }

}
