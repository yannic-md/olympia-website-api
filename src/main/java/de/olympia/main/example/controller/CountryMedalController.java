package de.olympia.main.example.controller;

import de.olympia.main.example.entity.CountryMedalEntity;
import de.olympia.main.example.service.MedalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/example")
public class CountryMedalController {

    private final MedalService medalService;

    public CountryMedalController(MedalService medalService) {
        this.medalService = medalService;
    }

    @GetMapping("/countries/medals")
    public List<CountryMedalEntity> getAll() {
        return medalService.getAllCountryMedals();
    }

    @GetMapping("/countries/{code}/medals")
    public ResponseEntity<CountryMedalEntity> getByCode(@PathVariable String code) {
        return medalService.findByCode(code)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
