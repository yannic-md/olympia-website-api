package de.olympia.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.CreateResultRequest;
import de.olympia.main.dto.ResultResponse;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Result;
import de.olympia.main.entity.Sports;
import de.olympia.main.entity.User;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.ResultRepository;
import de.olympia.main.repository.SportsRepository;
import de.olympia.main.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("ResultService Tests")
public class ResultServiceTest {

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private SportsRepository sportsRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private ResultService resultService;

    private Athlete testAthlete;
    private Sports testSport;
    private Result testResult;
    private User testUser;

    @BeforeEach
    void setUp() {
        testAthlete = new Athlete();
        testAthlete.setId(1L);
        testAthlete.setFirstName("John");
        testAthlete.setLastName("Doe");

        testSport = new Sports();
        testSport.setId(1L);
        testSport.setName("Swimming");

        testResult = new Result();
        testResult.setId(1L);
        testResult.setAthlete(testAthlete);
        testResult.setSports(testSport);
        testResult.setMedal(Result.Medal.GOLD);
        testResult.setScoreType(Result.ScoreType.TIME);
        testResult.setTimeOrPoints("12.34");
        testResult.setRank(1);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        TransactionSynchronizationManager.initSynchronization();
    }

    // ================== UPSERT RESULT TESTS ==================

    @Test
    @DisplayName("Should create new result successfully")
    void testUpsertResultCreate() {
        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(1L);
        request.setSportId(1L);
        request.setMedal(Result.Medal.GOLD);
        request.setScoreType(Result.ScoreType.TIME);
        request.setTimeOrPoints("12.34");

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(sportsRepository.findById(1L)).thenReturn(Optional.of(testSport));
        when(resultRepository.findBySportsIdAndMedal(1L, Result.Medal.GOLD)).thenReturn(Optional.empty());
        when(resultRepository.findBySportsIdAndAthleteId(1L, 1L)).thenReturn(Optional.empty());
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        ResultResponse response = resultService.upsertResult(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getAthleteFirstName());
        verify(resultRepository, times(1)).save(any(Result.class));
    }

    @Test
    @DisplayName("Should upsert existing result with same medal")
    void testUpsertResultUpdateByMedal() {
        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(1L);
        request.setSportId(1L);
        request.setMedal(Result.Medal.GOLD);
        request.setScoreType(Result.ScoreType.TIME);
        request.setTimeOrPoints("11.50");

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(sportsRepository.findById(1L)).thenReturn(Optional.of(testSport));
        when(resultRepository.findBySportsIdAndMedal(1L, Result.Medal.GOLD))
                .thenReturn(Optional.of(testResult));
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        ResultResponse response = resultService.upsertResult(request);

        assertNotNull(response);
        verify(resultRepository, times(1)).save(any(Result.class));
    }

    @Test
    @DisplayName("Should throw exception when athlete not found")
    void testUpsertResultAthleteNotFound() {
        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(999L);
        request.setSportId(1L);
        request.setMedal(Result.Medal.GOLD);

        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resultService.upsertResult(request));
    }

    @Test
    @DisplayName("Should throw exception when sport not found")
    void testUpsertResultSportNotFound() {
        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(1L);
        request.setSportId(999L);
        request.setMedal(Result.Medal.GOLD);

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(sportsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> resultService.upsertResult(request));
    }

    @Test
    @DisplayName("Should set rank based on medal type (GOLD=1)")
    void testUpsertResultRankGold() {
        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(1L);
        request.setSportId(1L);
        request.setMedal(Result.Medal.GOLD);
        request.setScoreType(Result.ScoreType.TIME);
        request.setTimeOrPoints("12.34");

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(sportsRepository.findById(1L)).thenReturn(Optional.of(testSport));
        when(resultRepository.findBySportsIdAndMedal(1L, Result.Medal.GOLD)).thenReturn(Optional.empty());
        when(resultRepository.findBySportsIdAndAthleteId(1L, 1L)).thenReturn(Optional.empty());
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        resultService.upsertResult(request);

        // Gold should map to rank 1
        Integer rank = (Integer) ReflectionTestUtils.invokeMethod(resultService,
                "medalToRank", Result.Medal.GOLD);
        assertEquals(1, rank);
    }

    @Test
    @DisplayName("Should set rank based on medal type (SILVER=2)")
    void testUpsertResultRankSilver() {
        Integer rank = (Integer) ReflectionTestUtils.invokeMethod(resultService,
                "medalToRank", Result.Medal.SILVER);
        assertEquals(2, rank);
    }

    @Test
    @DisplayName("Should set rank based on medal type (BRONZE=3)")
    void testUpsertResultRankBronze() {
        Integer rank = (Integer) ReflectionTestUtils.invokeMethod(resultService,
                "medalToRank", Result.Medal.BRONZE);
        assertEquals(3, rank);
    }

    @Test
    @DisplayName("Should set rank to null for non-medal results")
    void testUpsertResultRankNull() {
        Integer rank = (Integer) ReflectionTestUtils.invokeMethod(resultService,
                "medalToRank", null);
        assertNull(rank);
    }

    @Test
    @DisplayName("Should strip pts suffix from timeOrPoints")
    void testUpsertResultStripPtsSuffix() {
        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(1L);
        request.setSportId(1L);
        request.setMedal(Result.Medal.GOLD);
        request.setScoreType(Result.ScoreType.PTS);
        request.setTimeOrPoints("150 pts");

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(sportsRepository.findById(1L)).thenReturn(Optional.of(testSport));
        when(resultRepository.findBySportsIdAndMedal(1L, Result.Medal.GOLD)).thenReturn(Optional.empty());
        when(resultRepository.findBySportsIdAndAthleteId(1L, 1L)).thenReturn(Optional.empty());
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        resultService.upsertResult(request);

        // Suffix should be stripped
        String stripped = (String) ReflectionTestUtils.invokeMethod(resultService,
                "stripSuffix", "150 pts");
        assertEquals("150", stripped);
    }

    @Test
    @DisplayName("Should strip wins suffix from timeOrPoints")
    void testUpsertResultStripWinsSuffix() {
        String stripped = (String) ReflectionTestUtils.invokeMethod(resultService,
                "stripSuffix", "5 wins");
        assertEquals("5", stripped);
    }

    @Test
    @DisplayName("Should handle null timeOrPoints")
    void testUpsertResultNullTimeOrPoints() {
        String stripped = (String) ReflectionTestUtils.invokeMethod(resultService,
                "stripSuffix", null);
        assertNull(stripped);
    }

    // ================== DELETE RESULT TESTS ==================

    @Test
    @DisplayName("Should delete result successfully")
    void testDeleteResultSuccess() {
        when(resultRepository.existsById(1L)).thenReturn(true);

        resultService.deleteResult(1L);

        verify(resultRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent result")
    void testDeleteResultNotFound() {
        when(resultRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> resultService.deleteResult(999L));
    }

    // ================== TORESPONSE TESTS ==================

    @Test
    @DisplayName("Should convert result to response")
    void testToResponse() {
        ResultResponse response = (ResultResponse) ReflectionTestUtils.invokeMethod(resultService,
                "toResponse", testResult);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getAthleteFirstName());
        assertEquals("Doe", response.getAthleteLastName());
        assertEquals("Swimming", response.getSportRawName());
        assertEquals("GOLD", response.getMedal());
        assertEquals("12.34", response.getTimeOrPoints());
        assertEquals(1, response.getRank());
    }

    @Test
    @DisplayName("Should handle null sport in response")
    void testToResponseNullSport() {
        Result resultWithoutSport = new Result();
        resultWithoutSport.setId(1L);
        resultWithoutSport.setAthlete(testAthlete);
        resultWithoutSport.setSports(null);

        ResultResponse response = (ResultResponse) ReflectionTestUtils.invokeMethod(resultService,
                "toResponse", resultWithoutSport);

        assertNotNull(response);
        assertNull(response.getSportId());
        assertNull(response.getSportRawName());
    }

    // ================== RESOLVE CURRENT USER TESTS ==================

    @Test
    @DisplayName("Should resolve current user from security context")
    void testResolveCurrentUser() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("testuser");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        java.util.Optional<User> result = (java.util.Optional<User>) ReflectionTestUtils.invokeMethod(resultService,
                "resolveCurrentUser");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty optional when no authentication")
    void testResolveCurrentUserNoAuth() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);

        java.util.Optional<User> result = (java.util.Optional<User>) ReflectionTestUtils.invokeMethod(resultService,
                "resolveCurrentUser");

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should inherit scoreType from sport when not provided")
    void testUpsertResultInheritScoreTypeFromSport() {
        Sports sportWithType = new Sports();
        sportWithType.setId(1L);
        sportWithType.setName("Athletics");
        sportWithType.setScoreType(Sports.ScoreType.TIME);

        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(1L);
        request.setSportId(1L);
        request.setMedal(Result.Medal.GOLD);
        request.setScoreType(null); // Not provided
        request.setTimeOrPoints("60.5");

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(sportsRepository.findById(1L)).thenReturn(Optional.of(sportWithType));
        when(resultRepository.findBySportsIdAndMedal(1L, Result.Medal.GOLD)).thenReturn(Optional.empty());
        when(resultRepository.findBySportsIdAndAthleteId(1L, 1L)).thenReturn(Optional.empty());
        when(resultRepository.save(any(Result.class))).thenReturn(testResult);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        resultService.upsertResult(request);

        verify(resultRepository, times(1)).save(any(Result.class));
    }
}




