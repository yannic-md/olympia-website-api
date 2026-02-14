package de.olympia.main.controller;

import de.olympia.main.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.olympia.main.dto.LoginRequest;
import de.olympia.main.dto.LoginResponse;
import de.olympia.main.dto.RegisterRequest;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return ResponseEntity.status(201).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(new LoginResponse(null, null, null, e.getMessage()));
        }
    }
}
