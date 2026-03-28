package de.olympia.main.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import de.olympia.main.dto.CreateResultRequest;
import de.olympia.main.dto.ResultResponse;
import de.olympia.main.entity.Result;
import de.olympia.main.service.ResultService;

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
@DisplayName("ResultController Tests")
public class ResultControllerTest {

    @Mock
    private ResultService resultService;

    @InjectMocks
    private ResultController resultController;

    private ResultResponse testResultResponse;

    @BeforeEach
    void setUp() {
        testResultResponse = new ResultResponse(
                1L, 1L, "John", "Doe", 1L, "Swimming", "GOLD", "12.34", "TIME", 1
        );
    }

    @Test
    @DisplayName("Should create result successfully")
    void testUpsertResultSuccess() {
        CreateResultRequest request = new CreateResultRequest();
        request.setAthleteId(1L);
        request.setSportId(1L);
        request.setMedal(Result.Medal.GOLD);
        request.setScoreType(Result.ScoreType.TIME);
        request.setTimeOrPoints("12.34");

        when(resultService.upsertResult(any(CreateResultRequest.class)))
                .thenReturn(testResultResponse);

        ResponseEntity<?> response = resultController.upsertResult(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testResultResponse, response.getBody());
        verify(resultService, times(1)).upsertResult(any(CreateResultRequest.class));
    }

    @Test
    @DisplayName("Should return bad request for invalid request")
    void testUpsertResultInvalid() {
        CreateResultRequest request = new CreateResultRequest();

        when(resultService.upsertResult(any(CreateResultRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        ResponseEntity<?> response = resultController.upsertResult(request);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should delete result successfully")
    void testDeleteResultSuccess() {
        doNothing().when(resultService).deleteResult(1L);
        ResponseEntity<?> response = resultController.deleteResult(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(resultService, times(1)).deleteResult(1L);
    }

    @Test
    @DisplayName("Should return not found when deleting non-existent result")
    void testDeleteResultNotFound() {
        doThrow(new RuntimeException("Result not found: 999"))
                .when(resultService).deleteResult(999L);
        ResponseEntity<?> response = resultController.deleteResult(999L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

