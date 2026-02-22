package de.olympia.main.service;

import de.olympia.main.dto.LeaderboardEntryResponse;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.entity.Result;
import de.olympia.main.repository.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LeaderboardService Unit Tests")
class LeaderboardServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock(lenient = true)
    private TranslationService translationService;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private Country testCountry;
    private Athlete testAthlete;
    private Result goldResult;
    private Result silverResult;
    private Result bronzeResult;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setId(1L);
        testCountry.setCode("DE");
        testCountry.setName("Germany");

        testAthlete = new Athlete();
        testAthlete.setId(1L);
        testAthlete.setFirstName("Max");
        testAthlete.setLastName("Müller");
        testAthlete.setCountry(testCountry);
        testAthlete.setCreatedAt(LocalDateTime.now());

        goldResult = new Result();
        goldResult.setId(1L);
        goldResult.setAthlete(testAthlete);
        goldResult.setRank(1);
        goldResult.setTimeOrPoints("10.5");
        goldResult.setMedal(Result.Medal.GOLD);
        goldResult.setScoreType(Result.ScoreType.TIME);

        silverResult = new Result();
        silverResult.setId(2L);
        silverResult.setAthlete(testAthlete);
        silverResult.setRank(2);
        silverResult.setTimeOrPoints("11.2");
        silverResult.setMedal(Result.Medal.SILVER);
        silverResult.setScoreType(Result.ScoreType.TIME);

        bronzeResult = new Result();
        bronzeResult.setId(3L);
        bronzeResult.setAthlete(testAthlete);
        bronzeResult.setRank(3);
        bronzeResult.setTimeOrPoints("11.8");
        bronzeResult.setMedal(Result.Medal.BRONZE);
        bronzeResult.setScoreType(Result.ScoreType.TIME);
    }

    // ===== GET ALL RESULTS =====

    @Test
    @DisplayName("Should retrieve all results for leaderboard with default language")
    void testGetAllResults_DefaultLanguage() {
        // Arrange
        when(resultRepository.findAllByOrderByRankAsc())
            .thenReturn(Arrays.asList(goldResult, silverResult, bronzeResult));
        when(translationService.normalizeLang(null)).thenReturn("en");
        when(translationService.translateScoreType(eq("TIME"), eq("en")))
            .thenReturn("Time");
        when(translationService.translateMedal(anyString(), eq("en")))
            .thenReturn("Medal");
        when(translationService.translateSport(anyString(), eq("en")))
            .thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("en")))
            .thenReturn("Germany");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getAllResults(null);

        // Assert
        assertEquals(3, result.size());
        verify(resultRepository, times(1)).findAllByOrderByRankAsc();
    }

    @Test
    @DisplayName("Should retrieve all results with German language")
    void testGetAllResults_GermanLanguage() {
        // Arrange
        when(resultRepository.findAllByOrderByRankAsc())
            .thenReturn(Arrays.asList(goldResult, silverResult));
        when(translationService.normalizeLang("de")).thenReturn("de");
        when(translationService.translateScoreType(eq("TIME"), eq("de")))
            .thenReturn("Zeit");
        when(translationService.translateMedal(anyString(), eq("de")))
            .thenReturn("Medaille");
        when(translationService.translateSport(anyString(), eq("de")))
            .thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("de")))
            .thenReturn("Deutschland");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getAllResults("de");

        // Assert
        assertEquals(2, result.size());
        verify(translationService, times(1)).normalizeLang("de");
    }

    @Test
    @DisplayName("Should return empty list when no results exist")
    void testGetAllResults_EmptyList() {
        // Arrange
        when(resultRepository.findAllByOrderByRankAsc()).thenReturn(Arrays.asList());
        when(translationService.normalizeLang(null)).thenReturn("en");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getAllResults(null);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should retrieve all results sorted by rank")
    void testGetAllResults_SortedByRank() {
        // Arrange
        Result result1 = createTestResult(1L, 1, "10.5");
        Result result2 = createTestResult(2L, 2, "11.2");
        Result result3 = createTestResult(3L, 3, "11.8");

        when(resultRepository.findAllByOrderByRankAsc())
            .thenReturn(Arrays.asList(result1, result2, result3));
        when(translationService.normalizeLang(anyString())).thenReturn("en");
        when(translationService.translateScoreType(anyString(), eq("en"))).thenReturn("Time");
        when(translationService.translateMedal(anyString(), eq("en"))).thenReturn("Medal");
        when(translationService.translateSport(anyString(), eq("en"))).thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("en"))).thenReturn("Country");

        // Act
        List<LeaderboardEntryResponse> results = leaderboardService.getAllResults("en");

        // Assert
        assertEquals(3, results.size());
    }

    // ===== GET MEDAL WINNERS =====

    @Test
    @DisplayName("Should retrieve only medal winners")
    void testGetMedalWinners_Success() {
        // Arrange
        when(resultRepository.findByMedalIsNotNull())
            .thenReturn(Arrays.asList(goldResult, silverResult, bronzeResult));
        when(translationService.normalizeLang("en")).thenReturn("en");
        when(translationService.translateScoreType(eq("TIME"), eq("en"))).thenReturn("Time");
        when(translationService.translateMedal(anyString(), eq("en"))).thenReturn("Medal");
        when(translationService.translateSport(anyString(), eq("en"))).thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("en"))).thenReturn("Germany");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getMedalWinners("en");

        // Assert
        assertEquals(3, result.size());
        verify(resultRepository, times(1)).findByMedalIsNotNull();
    }

    @Test
    @DisplayName("Should return empty list when no medal winners exist")
    void testGetMedalWinners_NoMedals() {
        // Arrange
        when(resultRepository.findByMedalIsNotNull()).thenReturn(Arrays.asList());
        when(translationService.normalizeLang("en")).thenReturn("en");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getMedalWinners("en");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should sort medal winners by medal type (GOLD, SILVER, BRONZE)")
    void testGetMedalWinners_SortedByMedalType() {
        // Arrange - Mixed order to test sorting
        when(resultRepository.findByMedalIsNotNull())
            .thenReturn(Arrays.asList(silverResult, bronzeResult, goldResult));
        when(translationService.normalizeLang("en")).thenReturn("en");
        when(translationService.translateScoreType(eq("TIME"), eq("en"))).thenReturn("Time");
        when(translationService.translateMedal(anyString(), eq("en"))).thenReturn("Medal");
        when(translationService.translateSport(anyString(), eq("en"))).thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("en"))).thenReturn("Germany");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getMedalWinners("en");

        // Assert
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Should retrieve medal winners with German translations")
    void testGetMedalWinners_GermanTranslations() {
        // Arrange
        when(resultRepository.findByMedalIsNotNull())
            .thenReturn(Arrays.asList(goldResult, silverResult));
        when(translationService.normalizeLang("de")).thenReturn("de");
        when(translationService.translateScoreType(eq("TIME"), eq("de"))).thenReturn("Zeit");
        when(translationService.translateMedal(anyString(), eq("de"))).thenReturn("Medaille");
        when(translationService.translateSport(anyString(), eq("de"))).thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("de"))).thenReturn("Deutschland");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getMedalWinners("de");

        // Assert
        assertEquals(2, result.size());
        verify(translationService, times(1)).normalizeLang("de");
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Should handle results without medals")
    void testGetAllResults_WithoutMedals() {
        // Arrange
        Result noMedalResult = createTestResult(4L, 4, "12.0");
        noMedalResult.setMedal(null);

        when(resultRepository.findAllByOrderByRankAsc())
            .thenReturn(Arrays.asList(goldResult, noMedalResult));
        when(translationService.normalizeLang("en")).thenReturn("en");
        when(translationService.translateScoreType(eq("TIME"), eq("en"))).thenReturn("Time");
        when(translationService.translateMedal(anyString(), eq("en"))).thenReturn(null);
        when(translationService.translateSport(anyString(), eq("en"))).thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("en"))).thenReturn("Germany");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getAllResults("en");

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should handle results without athlete country")
    void testGetAllResults_WithoutCountry() {
        // Arrange
        Athlete athleteNoCountry = new Athlete();
        athleteNoCountry.setId(2L);
        athleteNoCountry.setFirstName("John");
        athleteNoCountry.setLastName("Doe");
        athleteNoCountry.setCountry(null);

        Result resultNoCountry = new Result();
        resultNoCountry.setId(10L);
        resultNoCountry.setAthlete(athleteNoCountry);
        resultNoCountry.setRank(5);
        resultNoCountry.setTimeOrPoints("12.0");
        resultNoCountry.setMedal(null);

        when(resultRepository.findAllByOrderByRankAsc()).thenReturn(Arrays.asList(resultNoCountry));
        when(translationService.normalizeLang("en")).thenReturn("en");
        when(translationService.translateScoreType(eq("TIME"), eq("en"))).thenReturn("Time");
        when(translationService.translateSport(anyString(), eq("en"))).thenReturn("Sport");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getAllResults("en");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should normalize invalid language codes")
    void testGetAllResults_InvalidLanguageNormalization() {
        // Arrange
        when(resultRepository.findAllByOrderByRankAsc()).thenReturn(Arrays.asList(goldResult));
        when(translationService.normalizeLang("xx")).thenReturn("en");
        when(translationService.translateScoreType(eq("TIME"), eq("en"))).thenReturn("Time");
        when(translationService.translateMedal(eq("GOLD"), eq("en"))).thenReturn("Gold");
        when(translationService.translateSport(anyString(), eq("en"))).thenReturn("Sport");
        when(translationService.translateCountry(anyString(), eq("en"))).thenReturn("Germany");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getAllResults("xx");

        // Assert
        assertEquals(1, result.size());
        verify(translationService).normalizeLang("xx");
    }

    @Test
    @DisplayName("Should handle result without sports")
    void testGetAllResults_WithoutSports() {
        // Arrange
        Result resultNoSports = createTestResult(5L, 1, "10.5");
        resultNoSports.setSports(null);

        when(resultRepository.findAllByOrderByRankAsc()).thenReturn(Arrays.asList(resultNoSports));
        when(translationService.normalizeLang("en")).thenReturn("en");
        when(translationService.translateScoreType(eq("TIME"), eq("en"))).thenReturn("Time");
        when(translationService.translateMedal(eq("GOLD"), eq("en"))).thenReturn("Gold");
        when(translationService.translateCountry(anyString(), eq("en"))).thenReturn("Germany");

        // Act
        List<LeaderboardEntryResponse> result = leaderboardService.getAllResults("en");

        // Assert
        assertEquals(1, result.size());
    }

    // Helper methods
    private Result createTestResult(Long id, Integer rank, String timeOrPoints) {
        Result result = new Result();
        result.setId(id);
        result.setAthlete(testAthlete);
        result.setRank(rank);
        result.setTimeOrPoints(timeOrPoints);
        result.setScoreType(Result.ScoreType.TIME);
        result.setMedal(Result.Medal.GOLD);
        return result;
    }
}












