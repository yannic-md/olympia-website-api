package de.olympia.main.repository;

import static org.junit.jupiter.api.Assertions.*;

import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.entity.Result;
import de.olympia.main.entity.Sports;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayName("Result Repository Tests")
public class ResultRepositoryTest {

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private SportsRepository sportsRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Athlete testAthlete;
    private Sports testSport;
    private Result testResult;

    @BeforeEach
    void setUp() {
        Country country = new Country();
        country.setCode("DE_TEST");
        country.setName("Germany");
        countryRepository.save(country);

        testAthlete = new Athlete();
        testAthlete.setFirstName("John");
        testAthlete.setLastName("Doe");
        testAthlete.setCountry(country);
        athleteRepository.save(testAthlete);

        testSport = new Sports();
        testSport.setName("Swimming");
        sportsRepository.save(testSport);

        testResult = new Result();
        testResult.setAthlete(testAthlete);
        testResult.setSports(testSport);
        testResult.setMedal(Result.Medal.GOLD);
        testResult.setScoreType(Result.ScoreType.TIME);
        testResult.setTimeOrPoints("12.34");
        testResult.setRank(1);
        resultRepository.save(testResult);
    }

    @Test
    @DisplayName("Should find results by athlete ID")
    void testFindByAthleteId() {
        List<Result> results = resultRepository.findByAthleteId(testAthlete.getId());
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testAthlete.getId(), results.get(0).getAthlete().getId());
    }

    @Test
    @DisplayName("Should find results by athlete and medal")
    void testFindByAthleteIdAndMedal() {
        List<Result> results = resultRepository.findByAthleteIdAndMedal(
                testAthlete.getId(), Result.Medal.GOLD);
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(Result.Medal.GOLD, results.get(0).getMedal());
    }

    @Test
    @DisplayName("Should find result by sport and athlete")
    void testFindBySportsIdAndAthleteId() {
        Optional<Result> result = resultRepository.findBySportsIdAndAthleteId(
                testSport.getId(), testAthlete.getId());
        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("Should find result by sport and medal")
    void testFindBySportsIdAndMedal() {
        Optional<Result> result = resultRepository.findBySportsIdAndMedal(
                testSport.getId(), Result.Medal.GOLD);
        assertTrue(result.isPresent());
        assertEquals(Result.Medal.GOLD, result.get().getMedal());
    }

    @Test
    @DisplayName("Should save and retrieve result")
    void testSaveResult() {
        Result newResult = new Result();
        newResult.setAthlete(testAthlete);
        newResult.setSports(testSport);
        newResult.setMedal(Result.Medal.SILVER);
        newResult.setScoreType(Result.ScoreType.TIME);
        newResult.setTimeOrPoints("13.50");
        newResult.setRank(2);

        Result saved = resultRepository.save(newResult);

        assertNotNull(saved.getId());
        assertEquals("13.50", saved.getTimeOrPoints());
        assertEquals(Result.Medal.SILVER, saved.getMedal());
    }
}



