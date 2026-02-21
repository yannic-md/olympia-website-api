package de.olympia.main.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.olympia.main.dto.LoginRequest;
import de.olympia.main.dto.LoginResponse;
import de.olympia.main.dto.RegisterRequest;
import de.olympia.main.entity.User;
import de.olympia.main.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate a user with username and password
     *
     * @param request Login credentials
     * @return LoginResponse with user information
     * @throws RuntimeException if user not found or password is invalid
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        return new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getRole().toString(),
            "Login successful"
        );
    }

    /**
     * Register a new user with JUDGE role
     *
     * @param request Registration data (username, password, email)
     * @return LoginResponse with new user information
     * @throws RuntimeException if username already exists
     */
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(User.Role.JUDGE);

        User savedUser = userRepository.save(user);

        return new LoginResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getRole().toString(),
            "Registration successful"
        );
    }

    /**
     * Authenticate an admin user
     * Only users with ADMIN role can login through this endpoint
     *
     * @param request Login credentials
     * @return LoginResponse with admin user information
     * @throws RuntimeException if user not found, password invalid, or user is not an admin
     */
    public LoginResponse adminLogin(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Access denied: Only administrators can login here");
        }

        return new LoginResponse(
            user.getId(),
            user.getUsername(),
            user.getRole().toString(),
            "Admin login successful"
        );
    }
}
