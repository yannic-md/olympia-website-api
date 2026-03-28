package de.olympia.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.CountryResponse;
import de.olympia.main.dto.CreateCountryRequest;
import de.olympia.main.dto.UpdateCountryRequest;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
import de.olympia.main.repository.ResultRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryService Tests")
public class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private AthleteRepository athleteRepository;

    @Mock
    private ResultRepository resultRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CountryService countryService;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setId(1L);
        testCountry.setCode("DE");
        testCountry.setName("Germany");
        testCountry.setNameEn("Germany");
        testCountry.setNameDe("Deutschland");
        testCountry.setNameFr("Allemagne");

        TransactionSynchronizationManager.initSynchronization();
    }

    // ================== CREATE COUNTRY TESTS ==================

    @Test
    @DisplayName("Should create country successfully")
    void testCreateCountrySuccess() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("FR");
        request.setName("France");
        request.setNameEn("France");
        request.setNameDe("Frankreich");
        request.setNameFr("France");

        when(countryRepository.findByCode("FR")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        CountryResponse response = countryService.createCountry(request);

        assertNotNull(response);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when code already exists")
    void testCreateCountryDuplicateCode() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("DE");
        request.setName("Germany");

        when(countryRepository.findByCode("DE")).thenReturn(Optional.of(testCountry));

        assertThrows(IllegalArgumentException.class, () -> countryService.createCountry(request));
    }

    @Test
    @DisplayName("Should throw exception when code is null")
    void testCreateCountryNullCode() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode(null);
        request.setName("Germany");

        assertThrows(IllegalArgumentException.class, () -> countryService.createCountry(request));
    }

    @Test
    @DisplayName("Should throw exception when name is empty")
    void testCreateCountryEmptyName() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("DE");
        request.setName("");

        assertThrows(IllegalArgumentException.class, () -> countryService.createCountry(request));
    }

    @Test
    @DisplayName("Should create country with translations")
    void testCreateCountryWithTranslations() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("ES");
        request.setName("Spain");
        request.setNameEn("Spain");
        request.setNameDe("Spanien");
        request.setNameFr("Espagne");

        when(countryRepository.findByCode("ES")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        CountryResponse response = countryService.createCountry(request);

        assertNotNull(response);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    // ================== UPDATE COUNTRY TESTS ==================

    @Test
    @DisplayName("Should update country code")
    void testUpdateCountryCode() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode("FR");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.findByCode("FR")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        CountryResponse response = countryService.updateCountry(1L, request);

        assertNotNull(response);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should update country name")
    void testUpdateCountryName() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setName("New Germany");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        CountryResponse response = countryService.updateCountry(1L, request);

        assertNotNull(response);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should update country translations")
    void testUpdateCountryTranslations() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setNameEn("Germany English");
        request.setNameDe("Deutschland Deutsch");
        request.setNameFr("Allemagne Français");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        CountryResponse response = countryService.updateCountry(1L, request);

        assertNotNull(response);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating to duplicate code")
    void testUpdateCountryDuplicateCode() {
        Country otherCountry = new Country();
        otherCountry.setId(2L);
        otherCountry.setCode("FR");

        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode("FR");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.findByCode("FR")).thenReturn(Optional.of(otherCountry));

        assertThrows(IllegalArgumentException.class, () -> countryService.updateCountry(1L, request));
    }

    @Test
    @DisplayName("Should allow updating own code")
    void testUpdateCountrySameCode() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode("DE");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.findByCode("DE")).thenReturn(Optional.of(testCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        CountryResponse response = countryService.updateCountry(1L, request);

        assertNotNull(response);
    }

    @Test
    @DisplayName("Should throw exception when country not found")
    void testUpdateCountryNotFound() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode("FR");

        when(countryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> countryService.updateCountry(999L, request));
    }

    @Test
    @DisplayName("Should clear translation fields")
    void testUpdateCountryClearTranslations() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setNameEn("");
        request.setNameDe("");
        request.setNameFr("");

        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);

        CountryResponse response = countryService.updateCountry(1L, request);

        assertNotNull(response);
    }

    // ================== DELETE COUNTRY TESTS ==================

    @Test
    @DisplayName("Should delete country successfully")
    void testDeleteCountrySuccess() {
        when(countryRepository.existsById(1L)).thenReturn(true);
        when(athleteRepository.findByCountryId(1L)).thenReturn(new ArrayList<>());

        countryService.deleteCountry(1L);

        verify(countryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent country")
    void testDeleteCountryNotFound() {
        when(countryRepository.existsById(999L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> countryService.deleteCountry(999L));
    }

    @Test
    @DisplayName("Should throw exception when country has athletes")
    void testDeleteCountryWithAthletes() {
        Athlete athlete = new Athlete();
        athlete.setId(1L);
        athlete.setCountry(testCountry);

        when(countryRepository.existsById(1L)).thenReturn(true);
        when(athleteRepository.findByCountryId(1L)).thenReturn(List.of(athlete));

        assertThrows(RuntimeException.class, () -> countryService.deleteCountry(1L));
        verify(countryRepository, never()).deleteById(1L);
    }
}

