package de.olympia.main.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.LoginRequest;
import de.olympia.main.dto.LoginResponse;
import de.olympia.main.dto.RegisterRequest;
import de.olympia.main.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthController authController;

    private LoginResponse testLoginResponse;

    @BeforeEach
    void setUp() {
        testLoginResponse = new LoginResponse(1L, "testuser", "JUDGE", "Login successful");
    }

    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(testLoginResponse);

        ResponseEntity<LoginResponse> response = authController.login(request, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        verify(authService, times(1)).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Should return 401 on login failure")
    void testLoginFailure() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("wrong");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("User not found"));

        ResponseEntity<LoginResponse> response = authController.login(request, null, null);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        LoginResponse registerResponse = new LoginResponse(2L, "newuser", "JUDGE", "Registration successful");

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(registerResponse);

        ResponseEntity<LoginResponse> response = authController.register(request, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("newuser", response.getBody().getUsername());
        verify(authService, times(1)).register(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("Should return 400 on register failure")
    void testRegisterFailure() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setEmail("test@example.com");

        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new RuntimeException("Username already exists"));

        ResponseEntity<LoginResponse> response = authController.register(request, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should logout successfully")
    void testLogoutSuccess() {
        ResponseEntity<?> response = authController.logout(null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}


