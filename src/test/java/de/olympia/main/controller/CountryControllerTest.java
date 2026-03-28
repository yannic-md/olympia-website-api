package de.olympia.main.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.CountryResponse;
import de.olympia.main.dto.CreateCountryRequest;
import de.olympia.main.dto.UpdateCountryRequest;
import de.olympia.main.service.CountryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("CountryController Tests")
public class CountryControllerTest {

    @Mock
    private CountryService countryService;

    @InjectMocks
    private CountryController countryController;

    private CountryResponse testCountryResponse;

    @BeforeEach
    void setUp() {
        testCountryResponse = new CountryResponse();
        testCountryResponse.setId(1L);
        testCountryResponse.setCode("DE");
        testCountryResponse.setName("Germany");
        testCountryResponse.setNameEn("Germany");
        testCountryResponse.setNameDe("Deutschland");
        testCountryResponse.setNameFr("Allemagne");
    }

    // ================== CREATE COUNTRY TESTS ==================

    @Test
    @DisplayName("Should create country successfully")
    void testCreateCountrySuccess() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("FR");
        request.setName("France");

        when(countryService.createCountry(any(CreateCountryRequest.class)))
                .thenReturn(testCountryResponse);

        ResponseEntity<?> response = countryController.createCountry(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testCountryResponse, response.getBody());
        verify(countryService, times(1)).createCountry(any(CreateCountryRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for duplicate code")
    void testCreateCountryDuplicateCode() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("DE");
        request.setName("Germany");

        when(countryService.createCountry(any(CreateCountryRequest.class)))
                .thenThrow(new IllegalArgumentException("Country with code 'DE' already exists"));

        ResponseEntity<?> response = countryController.createCountry(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return internal error when service fails")
    void testCreateCountryServiceError() {
        CreateCountryRequest request = new CreateCountryRequest();
        request.setCode("FR");
        request.setName("France");

        when(countryService.createCountry(any(CreateCountryRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = countryController.createCountry(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ================== UPDATE COUNTRY TESTS ==================

    @Test
    @DisplayName("Should update country successfully")
    void testUpdateCountrySuccess() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setName("New Germany");

        when(countryService.updateCountry(1L, request))
                .thenReturn(testCountryResponse);

        ResponseEntity<?> response = countryController.updateCountry(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testCountryResponse, response.getBody());
        verify(countryService, times(1)).updateCountry(1L, request);
    }

    @Test
    @DisplayName("Should return bad request for duplicate code on update")
    void testUpdateCountryDuplicateCode() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setCode("FR");

        when(countryService.updateCountry(1L, request))
                .thenThrow(new IllegalArgumentException("Country code 'FR' is already in use"));

        ResponseEntity<?> response = countryController.updateCountry(1L, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return not found when country doesn't exist")
    void testUpdateCountryNotFound() {
        UpdateCountryRequest request = new UpdateCountryRequest();
        request.setName("New Name");

        when(countryService.updateCountry(999L, request))
                .thenThrow(new RuntimeException("Country not found"));

        ResponseEntity<?> response = countryController.updateCountry(999L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ================== DELETE COUNTRY TESTS ==================

    @Test
    @DisplayName("Should delete country successfully")
    void testDeleteCountrySuccess() {
        doNothing().when(countryService).deleteCountry(1L);

        ResponseEntity<?> response = countryController.deleteCountry(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(countryService, times(1)).deleteCountry(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent country")
    void testDeleteCountryNotFound() {
        doThrow(new RuntimeException("Country not found"))
                .when(countryService).deleteCountry(999L);

        ResponseEntity<?> response = countryController.deleteCountry(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

