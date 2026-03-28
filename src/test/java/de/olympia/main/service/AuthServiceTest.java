package de.olympia.main.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.olympia.main.dto.LoginRequest;
import de.olympia.main.dto.LoginResponse;
import de.olympia.main.dto.RegisterRequest;
import de.olympia.main.entity.User;
import de.olympia.main.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPasswordHash("hashedpassword");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.JUDGE);
    }

    // ================== LOGIN TESTS ==================

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password", "hashedpassword")).thenReturn(true);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("JUDGE", response.getRole());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password", "hashedpassword");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setUsername("unknown");
        request.setPassword("password");

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("Should throw exception with invalid password")
    void testLoginInvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", "hashedpassword")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    // ================== REGISTER TESTS ==================

    @Test
    @DisplayName("Should register new user successfully")
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setEmail("new@example.com");

        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");
        newUser.setRole(User.Role.JUDGE);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded123");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("newuser", response.getUsername());
        assertEquals("JUDGE", response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testRegisterDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setEmail("duplicate@example.com");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("Should assign JUDGE role to new user")
    void testRegisterAssignsJudgeRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("judge_user");
        request.setPassword("password123");
        request.setEmail("judge@example.com");

        when(userRepository.findByUsername("judge_user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded123");

        User capturedUser = new User();
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            capturedUser.setId(2L);
            capturedUser.setUsername(user.getUsername());
            capturedUser.setRole(user.getRole());
            return capturedUser;
        });

        authService.register(request);

        assertEquals(User.Role.JUDGE, capturedUser.getRole());
    }

    @Test
    @DisplayName("Should encode password on register")
    void testRegisterEncodesPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("plainpassword");
        request.setEmail("new@example.com");

        User newUser = new User();
        newUser.setId(2L);

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainpassword")).thenReturn("encrypted");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        authService.register(request);

        verify(passwordEncoder, times(1)).encode("plainpassword");
    }
}

