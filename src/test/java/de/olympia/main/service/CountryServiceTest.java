package de.olympia.main.service;

import de.olympia.main.dto.CountryResponse;
import de.olympia.main.dto.CreateCountryRequest;
import de.olympia.main.dto.UpdateCountryRequest;
import de.olympia.main.entity.Country;
import de.olympia.main.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryService Unit Tests")
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setId(1L);
        testCountry.setCode("DE");
        testCountry.setName("Germany");
    }

    // ===== READ OPERATIONS =====

    @Test
    @DisplayName("Should retrieve all countries successfully")
    void testGetAllCountries_Success() {
        Country country1 = createTestCountry(1L, "DE", "Germany");
        Country country2 = createTestCountry(2L, "FR", "France");
        when(countryRepository.findAll()).thenReturn(Arrays.asList(country1, country2));

        List<CountryResponse> result = countryService.getAllCountries();

        assertEquals(2, result.size());
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve empty list when no countries exist")
    void testGetAllCountries_EmptyList() {
        when(countryRepository.findAll()).thenReturn(Arrays.asList());

        List<CountryResponse> result = countryService.getAllCountries();

        assertTrue(result.isEmpty());
        verify(countryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve country by ID")
    void testGetCountryById_Success() {
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));

        CountryResponse result = countryService.getCountryById(1L);

        assertNotNull(result);
        assertEquals("Germany", result.getName());
        assertEquals("DE", result.getCode());
        verify(countryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw when country not found by ID")
    void testGetCountryById_NotFound() {
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> countryService.getCountryById(999L));
        assertTrue(exception.getMessage().contains("Country not found"));
    }

    // ===== CREATE OPERATIONS =====

    @Test
    @DisplayName("Should create country successfully")
    void testCreateCountry_Success() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("FR");
        request.setName("France");

        Country savedCountry = createTestCountry(2L, "FR", "France");
        when(countryRepository.findByCode("FR")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        CountryResponse result = countryService.createCountry(request);

        assertNotNull(result);
        assertEquals("France", result.getName());
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw when country code already exists")
    void testCreateCountry_DuplicateCode() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("DE");
        request.setName("Germany");

        when(countryRepository.findByCode("DE")).thenReturn(Optional.of(testCountry));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> countryService.createCountry(request));
        assertTrue(exception.getMessage().contains("already exists"));
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw when country code is empty")
    void testCreateCountry_EmptyCode() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("");
        request.setName("Country");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> countryService.createCountry(request));
        assertTrue(exception.getMessage().contains("Country code is required"));
    }

    @Test
    @DisplayName("Should throw when country name is null")
    void testCreateCountry_NullName() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("XX");
        request.setName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> countryService.createCountry(request));
        assertTrue(exception.getMessage().contains("Country name is required"));
    }

    @Test
    @DisplayName("Should throw when country code exceeds max length")
    void testCreateCountry_CodeTooLong() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("TOOLONGCODE");
        request.setName("Country");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> countryService.createCountry(request));
        assertTrue(exception.getMessage().contains("must not exceed 8 characters"));
    }

    @Test
    @DisplayName("Should throw when country name exceeds max length")
    void testCreateCountry_NameTooLong() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("XX");
        request.setName("A".repeat(151));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> countryService.createCountry(request));
        assertTrue(exception.getMessage().contains("must not exceed 150 characters"));
    }

    // ===== UPDATE OPERATIONS =====

    @Test
    @DisplayName("Should update country successfully")
    void testUpdateCountry_Success() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode("DE");
        request.setName("German Republic");

        Country updatedCountry = createTestCountry(1L, "DE", "German Republic");
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.findByCode("DE")).thenReturn(Optional.of(testCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(updatedCountry);

        CountryResponse result = countryService.updateCountry(1L, request);

        assertNotNull(result);
        assertEquals("German Republic", result.getName());
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should update only name, keep code unchanged")
    void testUpdateCountry_PartialUpdate() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode(null);
        request.setName("German Republic");

        Country updatedCountry = createTestCountry(1L, "DE", "German Republic");
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(updatedCountry);

        CountryResponse result = countryService.updateCountry(1L, request);

        assertNotNull(result);
        assertEquals("DE", result.getCode());
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw when updating to duplicate code")
    void testUpdateCountry_DuplicateCode() {
        Country existingCountry = createTestCountry(2L, "FR", "France");
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode("FR");
        request.setName("New Name");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.findByCode("FR")).thenReturn(Optional.of(existingCountry));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> countryService.updateCountry(1L, request));
        assertTrue(exception.getMessage().contains("already in use"));
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw when updating non-existent country")
    void testUpdateCountry_NotFound() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setName("New Name");

        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> countryService.updateCountry(999L, request));
        assertTrue(exception.getMessage().contains("Country not found"));
    }

    // ===== DELETE OPERATIONS =====

    @Test
    @DisplayName("Should delete country successfully")
    void testDeleteCountry_Success() {
        when(countryRepository.existsById(1L)).thenReturn(true);

        countryService.deleteCountry(1L);

        verify(countryRepository, times(1)).existsById(1L);
        verify(countryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw when deleting non-existent country")
    void testDeleteCountry_NotFound() {
        when(countryRepository.existsById(999L)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> countryService.deleteCountry(999L));
        assertTrue(exception.getMessage().contains("Country not found"));
        verify(countryRepository, never()).deleteById(anyLong());
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Should handle special characters in country names")
    void testCreateCountry_SpecialCharacters() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("CV");
        request.setName("Côte d'Ivoire");

        Country savedCountry = createTestCountry(10L, "CV", "Côte d'Ivoire");
        when(countryRepository.findByCode("CV")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        CountryResponse result = countryService.createCountry(request);

        assertNotNull(result);
        assertEquals("Côte d'Ivoire", result.getName());
    }

    @Test
    @DisplayName("Should validate empty string as null")
    void testCreateCountry_EmptyStringAsNull() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("XX");
        request.setName("  ");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> countryService.createCountry(request));
        assertTrue(exception.getMessage().contains("Country name is required"));
    }

    // Helper method
    private Country createTestCountry(Long id, String code, String name) {
        Country country = new Country();
        country.setId(id);
        country.setCode(code);
        country.setName(name);
        return country;
    }
}

