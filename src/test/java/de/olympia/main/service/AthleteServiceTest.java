package de.olympia.main.service;

import de.olympia.main.dto.AthleteResponse;
import de.olympia.main.dto.CreateAthleteRequest;
import de.olympia.main.dto.UpdateAthleteRequest;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AthleteService Unit Tests")
class AthleteServiceTest {

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private AthleteService athleteService;

    private Country testCountry;
    private Athlete testAthlete;
    private CreateAthleteRequest createRequest;
    private UpdateAthleteRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Test Country
        testCountry = new Country();
        testCountry.setId(1L);
        testCountry.setCode("DE");
        testCountry.setName("Germany");

        // Test Athlete
        testAthlete = new Athlete();
        testAthlete.setId(1L);
        testAthlete.setFirstName("Max");
        testAthlete.setLastName("Müller");
        testAthlete.setCountry(testCountry);
        testAthlete.setCreatedAt(LocalDateTime.now());

        // Create Request
        createRequest = new CreateAthleteRequest();
        createRequest.setFirstName("Anna");
        createRequest.setLastName("Schmidt");
        createRequest.setCountryId(1L);

        // Update Request
        updateRequest = new UpdateAthleteRequest();
        updateRequest.setFirstName("Anna");
        updateRequest.setLastName("Neumann");
    }

    // ===== READ OPERATIONS =====

    @Test
    @DisplayName("Should retrieve all athletes successfully")
    void testGetAllAthletes_Success() {
        // Arrange
        Athlete athlete1 = createTestAthlete(1L, "Max", "Müller");
        Athlete athlete2 = createTestAthlete(2L, "Anna", "Schmidt");
        when(athleteRepository.findAll()).thenReturn(Arrays.asList(athlete1, athlete2));

        // Act
        List<AthleteResponse> result = athleteService.getAllAthletes();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Max", result.get(0).getFirstName());
        assertEquals("Anna", result.get(1).getFirstName());
        verify(athleteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no athletes exist")
    void testGetAllAthletes_EmptyList() {
        // Arrange
        when(athleteRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<AthleteResponse> result = athleteService.getAllAthletes();

        // Assert
        assertTrue(result.isEmpty());
        verify(athleteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve athlete by ID successfully")
    void testGetAthleteById_Success() {
        // Arrange
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));

        // Act
        AthleteResponse result = athleteService.getAthleteById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Max", result.getFirstName());
        assertEquals("Müller", result.getLastName());
        verify(athleteRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when athlete not found by ID")
    void testGetAthleteById_NotFound() {
        // Arrange
        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> athleteService.getAthleteById(999L));
        assertTrue(exception.getMessage().contains("Athlete not found"));
        verify(athleteRepository, times(1)).findById(999L);
    }

    // ===== CREATE OPERATIONS =====

    @Test
    @DisplayName("Should create athlete successfully with country")
    void testCreateAthlete_Success() {
        // Arrange
        Athlete savedAthlete = new Athlete();
        savedAthlete.setId(5L);
        savedAthlete.setFirstName("Anna");
        savedAthlete.setLastName("Schmidt");
        savedAthlete.setCountry(testCountry);
        savedAthlete.setCreatedAt(LocalDateTime.now());

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(savedAthlete);

        // Act
        AthleteResponse result = athleteService.createAthlete(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Anna", result.getFirstName());
        assertEquals("Schmidt", result.getLastName());
        verify(athleteRepository, times(1)).save(any(Athlete.class));
        verify(countryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create athlete without country")
    void testCreateAthlete_WithoutCountry() {
        // Arrange
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("Bob");
        request.setLastName("Miller");
        request.setCountryId(null);

        Athlete savedAthlete = new Athlete();
        savedAthlete.setId(10L);
        savedAthlete.setFirstName("Bob");
        savedAthlete.setLastName("Miller");
        savedAthlete.setCountry(null);
        savedAthlete.setCreatedAt(LocalDateTime.now());

        when(athleteRepository.save(any(Athlete.class))).thenReturn(savedAthlete);

        // Act
        AthleteResponse result = athleteService.createAthlete(request);

        // Assert
        assertNotNull(result);
        assertEquals("Bob", result.getFirstName());
        assertNull(result.getCountry());
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should throw exception when first name is empty")
    void testCreateAthlete_EmptyFirstName() {
        // Arrange
        createRequest.setFirstName("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> athleteService.createAthlete(createRequest));
        assertTrue(exception.getMessage().contains("First name is required"));
    }

    @Test
    @DisplayName("Should throw exception when last name is null")
    void testCreateAthlete_NullLastName() {
        // Arrange
        createRequest.setLastName(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> athleteService.createAthlete(createRequest));
        assertTrue(exception.getMessage().contains("Last name is required"));
    }

    @Test
    @DisplayName("Should throw exception when country not found")
    void testCreateAthlete_CountryNotFound() {
        // Arrange
        createRequest.setCountryId(999L);
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> athleteService.createAthlete(createRequest));
        assertTrue(exception.getMessage().contains("Country not found"));
        verify(countryRepository, times(1)).findById(999L);
    }

    // ===== UPDATE OPERATIONS =====

    @Test
    @DisplayName("Should update athlete successfully")
    void testUpdateAthlete_Success() {
        // Arrange
        Athlete updatedAthlete = createTestAthlete(1L, "Anna", "Neumann");
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(updatedAthlete);

        // Act
        AthleteResponse result = athleteService.updateAthlete(1L, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Anna", result.getFirstName());
        assertEquals("Neumann", result.getLastName());
        verify(athleteRepository, times(1)).findById(1L);
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should update only first name")
    void testUpdateAthlete_PartialUpdate_FirstNameOnly() {
        // Arrange
        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setFirstName("NewName");
        request.setLastName(null);
        request.setCountryId(null);

        Athlete updatedAthlete = createTestAthlete(1L, "NewName", "Müller");
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(updatedAthlete);

        // Act
        AthleteResponse result = athleteService.updateAthlete(1L, request);

        // Assert
        assertEquals("NewName", result.getFirstName());
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should update country successfully")
    void testUpdateAthlete_ChangeCountry() {
        // Arrange
        Country newCountry = new Country();
        newCountry.setId(2L);
        newCountry.setCode("FR");
        newCountry.setName("France");

        updateRequest.setCountryId(2L);

        Athlete updatedAthlete = createTestAthlete(1L, "Anna", "Neumann");
        updatedAthlete.setCountry(newCountry);

        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(countryRepository.findById(2L)).thenReturn(Optional.of(newCountry));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(updatedAthlete);

        // Act
        AthleteResponse result = athleteService.updateAthlete(1L, updateRequest);

        // Assert
        assertNotNull(result);
        verify(countryRepository, times(1)).findById(2L);
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent athlete")
    void testUpdateAthlete_NotFound() {
        // Arrange
        when(athleteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> athleteService.updateAthlete(999L, updateRequest));
        assertTrue(exception.getMessage().contains("Athlete not found"));
    }

    @Test
    @DisplayName("Should throw exception when updating to non-existent country")
    void testUpdateAthlete_CountryNotFound() {
        // Arrange
        updateRequest.setCountryId(999L);
        when(athleteRepository.findById(1L)).thenReturn(Optional.of(testAthlete));
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> athleteService.updateAthlete(1L, updateRequest));
        assertTrue(exception.getMessage().contains("Country not found"));
    }

    // ===== DELETE OPERATIONS =====

    @Test
    @DisplayName("Should delete athlete successfully")
    void testDeleteAthlete_Success() {
        // Arrange
        when(athleteRepository.existsById(1L)).thenReturn(true);

        // Act
        athleteService.deleteAthlete(1L);

        // Assert
        verify(athleteRepository, times(1)).existsById(1L);
        verify(athleteRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent athlete")
    void testDeleteAthlete_NotFound() {
        // Arrange
        when(athleteRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> athleteService.deleteAthlete(999L));
        assertTrue(exception.getMessage().contains("Athlete not found"));
        verify(athleteRepository, never()).deleteById(anyLong());
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Should handle athlete with whitespace in names")
    void testCreateAthlete_WhitespaceHandling() {
        // Arrange
        createRequest.setFirstName("  Max  ");
        createRequest.setLastName("  Müller  ");

        Athlete savedAthlete = createTestAthlete(15L, "  Max  ", "  Müller  ");
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(savedAthlete);

        // Act
        AthleteResponse result = athleteService.createAthlete(createRequest);

        // Assert
        assertNotNull(result);
        verify(athleteRepository, times(1)).save(any(Athlete.class));
    }

    @Test
    @DisplayName("Should handle special characters in names")
    void testCreateAthlete_SpecialCharacters() {
        // Arrange
        createRequest.setFirstName("José-Luis");
        createRequest.setLastName("O'Brien");

        Athlete savedAthlete = createTestAthlete(20L, "José-Luis", "O'Brien");
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(athleteRepository.save(any(Athlete.class))).thenReturn(savedAthlete);

        // Act
        AthleteResponse result = athleteService.createAthlete(createRequest);

        // Assert
        assertNotNull(result);
        assertEquals("José-Luis", result.getFirstName());
    }

    @Test
    @DisplayName("Should validate empty string as null")
    void testCreateAthlete_EmptyStringAsNull() {
        // Arrange
        createRequest.setFirstName("Max");
        createRequest.setLastName("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> athleteService.createAthlete(createRequest));
        assertTrue(exception.getMessage().contains("Last name is required"));
    }

    // Helper methods
    private Athlete createTestAthlete(Long id, String firstName, String lastName) {
        Athlete athlete = new Athlete();
        athlete.setId(id);
        athlete.setFirstName(firstName);
        athlete.setLastName(lastName);
        athlete.setCountry(testCountry);
        athlete.setCreatedAt(LocalDateTime.now());
        return athlete;
    }
}



