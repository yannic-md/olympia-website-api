package de.olympia.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.v2.V2AthleteResponse;
import de.olympia.main.dto.v2.V2CountryResponse;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.entity.Result;
import de.olympia.main.entity.Sports;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
import de.olympia.main.repository.ResultRepository;
import de.olympia.main.repository.SportsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("V2PublicService Tests")
public class V2PublicServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private SportsRepository sportsRepository;

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private V2PublicService v2PublicService;

    private Athlete testAthlete;
    private Country testCountry;
    private Sports testSport;
    private Result testResult;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setId(1L);
        testCountry.setCode("DE");
        testCountry.setName("Germany");
        testCountry.setNameEn("Germany");
        testCountry.setNameDe("Deutschland");
        testCountry.setNameFr("Allemagne");

        testAthlete = new Athlete();
        testAthlete.setId(1L);
        testAthlete.setFirstName("John");
        testAthlete.setLastName("Doe");
        testAthlete.setCountry(testCountry);

        testSport = new Sports();
        testSport.setId(1L);
        testSport.setName("Swimming");
        testSport.setScoreType(Sports.ScoreType.TIME);

        testResult = new Result();
        testResult.setId(1L);
        testResult.setAthlete(testAthlete);
        testResult.setSports(testSport);
        testResult.setMedal(Result.Medal.GOLD);
        testResult.setScoreType(Result.ScoreType.TIME);
        testResult.setTimeOrPoints("12.34");
        testResult.setRank(1);
    }

    // ================== GET ATHLETES TESTS ==================

    @Test
    @DisplayName("Should get athletes with en language")
    void testGetAthletesEn() {
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));
        when(athleteRepository.findAll()).thenReturn(List.of(testAthlete));

        List<V2AthleteResponse> athletes = v2PublicService.getAthletes("en");

        assertNotNull(athletes);
        assertTrue(athletes.size() > 0);
    }

    @Test
    @DisplayName("Should get athletes with de language")
    void testGetAthletesDe() {
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));
        when(athleteRepository.findAll()).thenReturn(List.of(testAthlete));

        List<V2AthleteResponse> athletes = v2PublicService.getAthletes("de");

        assertNotNull(athletes);
    }

    @Test
    @DisplayName("Should get athletes with fr language")
    void testGetAthletesFr() {
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));
        when(athleteRepository.findAll()).thenReturn(List.of(testAthlete));

        List<V2AthleteResponse> athletes = v2PublicService.getAthletes("fr");

        assertNotNull(athletes);
    }

    @Test
    @DisplayName("Should return empty list when no athletes")
    void testGetAthletesEmpty() {
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(new ArrayList<>());
        when(athleteRepository.findAll()).thenReturn(new ArrayList<>());

        List<V2AthleteResponse> athletes = v2PublicService.getAthletes("en");

        assertNotNull(athletes);
        assertTrue(athletes.isEmpty());
    }

    // ================== GET COUNTRIES TESTS ==================

    @Test
    @DisplayName("Should get countries with en language")
    void testGetCountriesEn() {
        when(countryRepository.findAll()).thenReturn(List.of(testCountry));
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));

        List<V2CountryResponse> countries = v2PublicService.getCountries("en");

        assertNotNull(countries);
    }

    @Test
    @DisplayName("Should get countries with de language")
    void testGetCountriesDe() {
        when(countryRepository.findAll()).thenReturn(List.of(testCountry));
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));

        List<V2CountryResponse> countries = v2PublicService.getCountries("de");

        assertNotNull(countries);
    }

    @Test
    @DisplayName("Should return empty list when no countries")
    void testGetCountriesEmpty() {
        when(countryRepository.findAll()).thenReturn(new ArrayList<>());
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(new ArrayList<>());

        List<V2CountryResponse> countries = v2PublicService.getCountries("en");

        assertNotNull(countries);
        assertTrue(countries.isEmpty());
    }

    // ================== GET SPORTS TESTS ==================

    @Test
    @DisplayName("Should get sports with en language")
    void testGetSportsEn() {
        when(sportsRepository.findAll()).thenReturn(List.of(testSport));
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));

        var sports = v2PublicService.getSports("en");

        assertNotNull(sports);
    }

    @Test
    @DisplayName("Should return empty list when no sports")
    void testGetSportsEmpty() {
        when(sportsRepository.findAll()).thenReturn(new ArrayList<>());
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(new ArrayList<>());

        var sports = v2PublicService.getSports("en");

        assertNotNull(sports);
        assertTrue(sports.isEmpty());
    }

    // ================== GET LEADERBOARD TESTS ==================

    @Test
    @DisplayName("Should get leaderboard with en language")
    void testGetLeaderboardEn() {
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));
        when(athleteRepository.findAll()).thenReturn(List.of(testAthlete));

        var leaderboard = v2PublicService.getLeaderboard("en");

        assertNotNull(leaderboard);
    }

    @Test
    @DisplayName("Should get leaderboard with de language")
    void testGetLeaderboardDe() {
        when(resultRepository.findAllWithAthleteAndSport()).thenReturn(List.of(testResult));
        when(athleteRepository.findAll()).thenReturn(List.of(testAthlete));

        var leaderboard = v2PublicService.getLeaderboard("de");

        assertNotNull(leaderboard);
    }
}


