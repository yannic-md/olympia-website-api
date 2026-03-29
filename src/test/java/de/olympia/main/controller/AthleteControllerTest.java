package de.olympia.main.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.AthleteResponse;
import de.olympia.main.dto.CreateAthleteRequest;
import de.olympia.main.dto.UpdateAthleteRequest;
import de.olympia.main.entity.Result;
import de.olympia.main.service.AthleteService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
@DisplayName("AthleteController Tests")
public class AthleteControllerTest {

    @Mock
    private AthleteService athleteService;

    @InjectMocks
    private AthleteController athleteController;

    private AthleteResponse testAthleteResponse;

    @BeforeEach
    void setUp() {
        testAthleteResponse = new AthleteResponse();
        testAthleteResponse.setId(1L);
        testAthleteResponse.setFirstName("John");
        testAthleteResponse.setLastName("Doe");
        testAthleteResponse.setSport("Swimming");
        testAthleteResponse.setScoreType(Result.ScoreType.TIME);
        testAthleteResponse.setMedals(new AthleteResponse.MedalsDto(2, 1, 0, 3));
        testAthleteResponse.setCreatedAt(LocalDateTime.now());
    }

    // ================== CREATE ATHLETE TESTS ==================

    @Test
    @DisplayName("Should create athlete successfully")
    void testCreateAthleteSuccess() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        when(athleteService.createAthlete(any(CreateAthleteRequest.class)))
                .thenReturn(testAthleteResponse);

        ResponseEntity<?> response = athleteController.createAthlete(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testAthleteResponse, response.getBody());
        verify(athleteService, times(1)).createAthlete(any(CreateAthleteRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid athlete")
    void testCreateAthleteInvalid() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName(null);
        request.setLastName("Doe");

        when(athleteService.createAthlete(any(CreateAthleteRequest.class)))
                .thenThrow(new IllegalArgumentException("First name is required"));

        ResponseEntity<?> response = athleteController.createAthlete(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return internal error when service fails")
    void testCreateAthleteServiceError() {
        CreateAthleteRequest request = new CreateAthleteRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        when(athleteService.createAthlete(any(CreateAthleteRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = athleteController.createAthlete(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ================== UPDATE ATHLETE TESTS ==================

    @Test
    @DisplayName("Should update athlete successfully")
    void testUpdateAthleteSuccess() {
        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setFirstName("Jane");

        when(athleteService.updateAthlete(1L, request))
                .thenReturn(testAthleteResponse);

        ResponseEntity<?> response = athleteController.updateAthlete(1L, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testAthleteResponse, response.getBody());
        verify(athleteService, times(1)).updateAthlete(1L, request);
    }

    @Test
    @DisplayName("Should return bad request for invalid update")
    void testUpdateAthleteInvalid() {
        UpdateAthleteRequest request = new UpdateAthleteRequest();

        when(athleteService.updateAthlete(1L, request))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = athleteController.updateAthlete(1L, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return not found when athlete doesn't exist")
    void testUpdateAthleteNotFound() {
        UpdateAthleteRequest request = new UpdateAthleteRequest();
        request.setFirstName("Jane");

        when(athleteService.updateAthlete(999L, request))
                .thenThrow(new RuntimeException("Athlete not found"));

        ResponseEntity<?> response = athleteController.updateAthlete(999L, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // ================== DELETE ATHLETE TESTS ==================

    @Test
    @DisplayName("Should delete athlete successfully")
    void testDeleteAthleteSuccess() {
        doNothing().when(athleteService).deleteAthlete(1L);

        ResponseEntity<?> response = athleteController.deleteAthlete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(athleteService, times(1)).deleteAthlete(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent athlete")
    void testDeleteAthleteNotFound() {
        doThrow(new RuntimeException("Athlete not found"))
                .when(athleteService).deleteAthlete(999L);

        ResponseEntity<?> response = athleteController.deleteAthlete(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

