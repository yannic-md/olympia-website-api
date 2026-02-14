package de.olympia.main.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.olympia.main.entity.User;
import de.olympia.main.repository.UserRepository;
import de.olympia.main.dto.LoginRequest;
import de.olympia.main.dto.LoginResponse;
import de.olympia.main.dto.RegisterRequest;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Ungültige Anmeldedaten");
        }

        return new LoginResponse(user.getId(), user.getUsername(), user.getRole().toString(), "Login erfolgreich");
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Benutzername existiert bereits");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(User.Role.JUDGE);

        User savedUser = userRepository.save(user);
        return new LoginResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getRole().toString(), "Registrierung erfolgreich");
    }

    public LoginResponse adminLogin(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Ungültige Anmeldedaten");
        }

        if (user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Zugriff verweigert: Nur Administratoren können sich hier anmelden");
        }

        return new LoginResponse(user.getId(), user.getUsername(), user.getRole().toString(), "Admin-Login erfolgreich");
    }
}
