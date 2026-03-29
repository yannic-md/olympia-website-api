package de.olympia.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.AthleteResponse;
import de.olympia.main.dto.CreateAthleteRequest;
import de.olympia.main.dto.UpdateAthleteRequest;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("AthleteService Tests")
public class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private SportsRepository sportsRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private AthleteService athleteService;

    private Country testCountry;
    private Athlete testAthlete;
    private Sports testSport;

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
        testAthlete.setFirstName("Max");
        testAthlete.setLastName("Mustermann");
        testAthlete.setCountry(testCountry);
        testAthlete.setCreatedAt(LocalDateTime.now());

        testSport = new Sports();
        testSport.setId(1L);
        testSport.setName("Swimming");

        // Mock TransactionSynchronizationManager
        TransactionSynchronizationManager.initSynchronization();
    }

    // ================== CREATE ATHLETE TESTS ==================

    @Test
    @DisplayName("Should create athlete with required fields only")
    void testCreateAthleteMinimal() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(anyLong())).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.createAthlete(request);

        assertNotNull(response);
        assertEquals("Max", response.getFirstName());
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should create athlete with country")
    void testCreateAthleteWithCountry() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setCountryId(1L);

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(anyLong())).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.createAthlete(request);

        assertNotNull(response);
        verify(countryRepository, times(1)).findById(1L);
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should create athlete with medals")
    void testCreateAthleteWithMedals() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setGoldMedals(2);
        request.setSilverMedals(1);
        request.setBronzeMedals(0);
        request.setBestTime("12.34");
        request.setSport("Swimming");
        request.setScoreType(Result.ScoreType.TIME);

        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(sportsRepository.findByNameIgnoreCase("Swimming")).thenReturn(Optional.of(testSport));
        when(resultRepository.findByAthleteId(anyLong())).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.createAthlete(request);

        assertNotNull(response);
        // Verify save was called for gold medals (2), silver (1), and possibly bestTime
        verify(resultRepository, atLeastOnce()).save(any(Result.class));
    }

    @Test
    @DisplayName("Should create athlete with PTS score type")
    void testCreateAthleteWithPtsScoreType() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setGoldMedals(1);
        request.setBestTime("150");
        request.setScoreType(Result.ScoreType.PTS);

        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(anyLong())).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.createAthlete(request);

        assertNotNull(response);
        verify(resultRepository, atLeastOnce()).save(any(Result.class));
    }

    @Test
    @DisplayName("Should throw exception when firstName is null")
    void testCreateAthleteNullFirstName() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName(null);
        request.setLastName("Doe");

        assertThrows(IllegalArgumentException.class, () -> athleteService.createAthlete(request));
    }

    @Test
    @DisplayName("Should throw exception when lastName is empty")
    void testCreateAthleteEmptyLastName() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("");

        assertThrows(IllegalArgumentException.class, () -> athleteService.createAthlete(request));
    }

    @Test
    @DisplayName("Should throw exception when country not found")
    void testCreateAthleteCountryNotFound() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setCountryId(999L);

        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> athleteService.createAthlete(request));
    }

    @Test
    @DisplayName("Should create athlete with bestTime only (no medals)")
    void testCreateAthleteWithBestTimeOnly() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setBestTime("12.34");
        request.setScoreType(Result.ScoreType.TIME);

        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(anyLong())).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.createAthlete(request);

        assertNotNull(response);
        verify(resultRepository, times(1)).save(any(Result.class));
    }

    // ================== UPDATE ATHLETE TESTS ==================

    @Test
    @DisplayName("Should update athlete first name")
    void testUpdateAthleteFirstName() {
        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setFirstName("Jane");

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(anyLong())).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.updateAthlete(1L, request);

        assertNotNull(response);
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should update athlete country")
    void testUpdateAthleteCountry() {
        Country newCountry = new Country();
        newCountry.setId(2L);
        newCountry.setCode("FR");

        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setCountryId(2L);

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(countryRepository.findById(2L)).thenReturn(Optional.of(newCountry));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(anyLong())).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.updateAthlete(1L, request);

        assertNotNull(response);
        verify(countryRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Should update athlete medals")
    void testUpdateAthleteMedals() {
        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setGoldMedals(3);
        request.setSilverMedals(2);
        request.setBronzeMedals(1);
        request.setBestTime("11.50");
        request.setScoreType(Result.ScoreType.TIME);

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(1L)).thenReturn(new ArrayList<>());
        when(resultRepository.findByAthleteIdAndMedal(1L, Result.Medal.GOLD)).thenReturn(new ArrayList<>());
        when(resultRepository.findByAthleteIdAndMedal(1L, Result.Medal.SILVER)).thenReturn(new ArrayList<>());
        when(resultRepository.findByAthleteIdAndMedal(1L, Result.Medal.BRONZE)).thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.updateAthlete(1L, request);

        assertNotNull(response);
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should throw exception when athlete not found on update")
    void testUpdateAthleteNotFound() {
        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setFirstName("Jane");

        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> athleteService.updateAthlete(999L, request));
    }

    @Test
    @DisplayName("Should throw exception when country not found on update")
    void testUpdateAthleteCountryNotFound() {
        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setCountryId(999L);

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> athleteService.updateAthlete(1L, request));
    }

    @Test
    @DisplayName("Should reduce medal count by deleting excess results")
    void testUpdateAthleteReduceMedals() {
        Result goldResult = new Result();
        goldResult.setId(1L);
        goldResult.setMedal(Result.Medal.GOLD);

        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setGoldMedals(0); // Reduce from 1 to 0

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(testAthlete);
        when(resultRepository.findByAthleteId(1L)).thenReturn(new ArrayList<>());
        when(resultRepository.findByAthleteIdAndMedal(1L, Result.Medal.GOLD))
                .thenReturn(List.of(goldResult));
        when(resultRepository.findByAthleteIdAndMedal(1L, Result.Medal.SILVER))
                .thenReturn(new ArrayList<>());
        when(resultRepository.findByAthleteIdAndMedal(1L, Result.Medal.BRONZE))
                .thenReturn(new ArrayList<>());

        AthleteResponse response = athleteService.updateAthlete(1L, request);

        assertNotNull(response);
        verify(resultRepository, times(1)).deleteAll(anyList());
    }

    // ================== DELETE ATHLETE TESTS ==================

    @Test
    @DisplayName("Should delete athlete successfully")
    void testDeleteAthleteSuccess() {
        when(athleteRepository.existsById(1L)).thenReturn(true);

        athleteService.deleteAthlete(1L);

        verify(athleteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent athlete")
    void testDeleteAthleteNotFound() {
        when(athleteRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> athleteService.deleteAthlete(999L));
    }

    // ================== FORMAT TIMEORPOINTS TESTS ==================

    @Test
    @DisplayName("Should format bestTime with pts suffix")
    void testFormatTimeOrPointsWithPts() {
        String result = (String) ReflectionTestUtils.invokeMethod(athleteService,
                "formatTimeOrPoints", "150", Result.ScoreType.PTS);
        assertEquals("150 pts", result);
    }

    @Test
    @DisplayName("Should format bestTime with wins suffix")
    void testFormatTimeOrPointsWithWins() {
        String result = (String) ReflectionTestUtils.invokeMethod(athleteService,
                "formatTimeOrPoints", "5", Result.ScoreType.WINS);
        assertEquals("5 wins", result);
    }

    @Test
    @DisplayName("Should not add suffix for TIME score type")
    void testFormatTimeOrPointsWithTime() {
        String result = (String) ReflectionTestUtils.invokeMethod(athleteService,
                "formatTimeOrPoints", "12.34", Result.ScoreType.TIME);
        assertEquals("12.34", result);
    }

    @Test
    @DisplayName("Should handle null bestTime")
    void testFormatTimeOrPointsNull() {
        String result = (String) ReflectionTestUtils.invokeMethod(athleteService,
                "formatTimeOrPoints", null, Result.ScoreType.PTS);
        assertNull(result);
    }

    @Test
    @DisplayName("Should be idempotent - not double-suffix")
    void testFormatTimeOrPointsIdempotent() {
        String result = (String) ReflectionTestUtils.invokeMethod(athleteService,
                "formatTimeOrPoints", "150 pts", Result.ScoreType.PTS);
        assertEquals("150 pts", result);
    }

    // ================== TORESPONSE TESTS ==================

    @Test
    @DisplayName("Should convert athlete to response")
    void testToResponse() {
        Result result = new Result();
        result.setSports(testSport);
        result.setScoreType(Result.ScoreType.TIME);
        result.setMedal(Result.Medal.GOLD);
        result.setTimeOrPoints("12.34");

        when(resultRepository.findByAthleteId(1L)).thenReturn(List.of(result));

        AthleteResponse response = (AthleteResponse) ReflectionTestUtils.invokeMethod(athleteService,
                "toResponse", testAthlete);

        assertNotNull(response);
        assertEquals("Max", response.getFirstName());
        assertEquals("Mustermann", response.getLastName());
        assertEquals("Swimming", response.getSport());
        assertEquals(Result.ScoreType.TIME, response.getScoreType());
        assertEquals(1, response.getMedals().getGold());
    }

    @Test
    @DisplayName("Should handle athlete with no results in response")
    void testToResponseNoResults() {
        when(resultRepository.findByAthleteId(1L)).thenReturn(new ArrayList<>());

        AthleteResponse response = (AthleteResponse) ReflectionTestUtils.invokeMethod(athleteService,
                "toResponse", testAthlete);

        assertNotNull(response);
        assertNull(response.getSport());
        assertEquals(0, response.getMedals().getTotal());
    }

    @Test
    @DisplayName("Should aggregate medal counts correctly")
    void testToResponseMedalAggregation() {
        Result gold1 = new Result();
        gold1.setMedal(Result.Medal.GOLD);
        Result gold2 = new Result();
        gold2.setMedal(Result.Medal.GOLD);
        Result silver1 = new Result();
        silver1.setMedal(Result.Medal.SILVER);

        when(resultRepository.findByAthleteId(1L)).thenReturn(List.of(gold1, gold2, silver1));

        AthleteResponse response = (AthleteResponse) ReflectionTestUtils.invokeMethod(athleteService,
                "toResponse", testAthlete);

        assertEquals(2, response.getMedals().getGold());
        assertEquals(1, response.getMedals().getSilver());
        assertEquals(0, response.getMedals().getBronze());
        assertEquals(3, response.getMedals().getTotal());
    }
}

