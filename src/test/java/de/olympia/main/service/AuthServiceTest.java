package de.olympia.main.service;

import de.olympia.main.dto.LoginRequest;
import de.olympia.main.dto.LoginResponse;
import de.olympia.main.dto.RegisterRequest;
import de.olympia.main.entity.User;
import de.olympia.main.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testJudgeUser;
    private User testAdminUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // Test Judge User
        testJudgeUser = new User();
        testJudgeUser.setId(1L);
        testJudgeUser.setUsername("judge1");
        testJudgeUser.setPasswordHash("hashedPassword123");
        testJudgeUser.setRole(User.Role.JUDGE);
        testJudgeUser.setEmail("judge@example.com");
        testJudgeUser.setCreatedAt(LocalDateTime.now());

        // Test Admin User
        testAdminUser = new User();
        testAdminUser.setId(2L);
        testAdminUser.setUsername("admin");
        testAdminUser.setPasswordHash("hashedAdminPassword");
        testAdminUser.setRole(User.Role.ADMIN);
        testAdminUser.setEmail("admin@example.com");
        testAdminUser.setCreatedAt(LocalDateTime.now());

        // Login Request
        loginRequest = new LoginRequest();
        loginRequest.setUsername("judge1");
        loginRequest.setPassword("plainPassword123");

        // Register Request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newjudge");
        registerRequest.setPassword("newPassword123");
        registerRequest.setEmail("newjudge@example.com");
    }

    // ===== LOGIN TESTS =====

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void testLogin_Success() {
        // Arrange
        when(userRepository.findByUsername("judge1")).thenReturn(Optional.of(testJudgeUser));
        when(passwordEncoder.matches("plainPassword123", "hashedPassword123")).thenReturn(true);

        // Act
        LoginResponse result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("judge1", result.getUsername());
        assertEquals("JUDGE", result.getRole());
        assertEquals("Login successful", result.getMessage());
        verify(userRepository, times(1)).findByUsername("judge1");
        verify(passwordEncoder, times(1)).matches("plainPassword123", "hashedPassword123");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testLogin_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.login(new LoginRequest("nonexistent", "password")));
        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findByUsername("nonexistent");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when password is invalid")
    void testLogin_InvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("judge1")).thenReturn(Optional.of(testJudgeUser));
        when(passwordEncoder.matches("wrongPassword", "hashedPassword123")).thenReturn(false);

        LoginRequest wrongPasswordRequest = new LoginRequest();
        wrongPasswordRequest.setUsername("judge1");
        wrongPasswordRequest.setPassword("wrongPassword");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.login(wrongPasswordRequest));
        assertTrue(exception.getMessage().contains("Invalid credentials"));
        verify(passwordEncoder, times(1)).matches("wrongPassword", "hashedPassword123");
    }

    @Test
    @DisplayName("Should handle case sensitive usernames")
    void testLogin_CaseSensitiveUsername() {
        // Arrange - username with different case not found
        when(userRepository.findByUsername("JUDGE1")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(new LoginRequest("JUDGE1", "password"));
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }

    // ===== REGISTRATION TESTS =====

    @Test
    @DisplayName("Should register new user successfully")
    void testRegister_Success() {
        // Arrange
        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("newjudge");
        savedUser.setPasswordHash("encodedPassword");
        savedUser.setEmail("newjudge@example.com");
        savedUser.setRole(User.Role.JUDGE);
        savedUser.setCreatedAt(LocalDateTime.now());

        when(userRepository.findByUsername("newjudge")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        LoginResponse result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("newjudge", result.getUsername());
        assertEquals("JUDGE", result.getRole());
        assertEquals("Registration successful", result.getMessage());
        verify(userRepository, times(1)).findByUsername("newjudge");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void testRegister_DuplicateUsername() {
        // Arrange
        when(userRepository.findByUsername("newjudge")).thenReturn(Optional.of(testJudgeUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.register(registerRequest));
        assertTrue(exception.getMessage().contains("Username already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should create new user with JUDGE role")
    void testRegister_CreatesJudgeRole() {
        // Arrange
        when(userRepository.findByUsername("newjudge")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("newjudge");
        savedUser.setRole(User.Role.JUDGE);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        LoginResponse result = authService.register(registerRequest);

        // Assert
        assertEquals("JUDGE", result.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should encode password during registration")
    void testRegister_PasswordEncoded() {
        // Arrange
        when(userRepository.findByUsername("newjudge")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(10L);
        savedUser.setUsername("newjudge");
        savedUser.setPasswordHash("encodedPassword");
        savedUser.setRole(User.Role.JUDGE);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(passwordEncoder, times(1)).encode("newPassword123");
    }

    // ===== ADMIN LOGIN TESTS =====

    @Test
    @DisplayName("Should admin login successfully with correct credentials")
    void testAdminLogin_Success() {
        // Arrange
        LoginRequest adminRequest = new LoginRequest();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("adminPassword");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testAdminUser));
        when(passwordEncoder.matches("adminPassword", "hashedAdminPassword")).thenReturn(true);

        // Act
        LoginResponse result = authService.adminLogin(adminRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("admin", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        assertEquals("Admin login successful", result.getMessage());
        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Should throw exception when admin user not found")
    void testAdminLogin_UserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.adminLogin(new LoginRequest("nonexistent", "password"));
        });
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    @DisplayName("Should throw exception when non-admin tries to admin login")
    void testAdminLogin_NonAdminUser() {
        // Arrange
        LoginRequest loginReq = new LoginRequest();
        loginReq.setUsername("judge1");
        loginReq.setPassword("plainPassword123");

        when(userRepository.findByUsername("judge1")).thenReturn(Optional.of(testJudgeUser));
        when(passwordEncoder.matches("plainPassword123", "hashedPassword123")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.adminLogin(loginReq));
        assertTrue(exception.getMessage().contains("Access denied"));
        assertTrue(exception.getMessage().contains("administrators"));
    }

    @Test
    @DisplayName("Should throw exception when admin password is invalid")
    void testAdminLogin_InvalidPassword() {
        // Arrange
        LoginRequest adminRequest = new LoginRequest();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("wrongPassword");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testAdminUser));
        when(passwordEncoder.matches("wrongPassword", "hashedAdminPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.adminLogin(adminRequest));
        assertTrue(exception.getMessage().contains("Invalid credentials"));
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Should handle empty username")
    void testLogin_EmptyUsername() {
        // Arrange
        LoginRequest emptyUserRequest = new LoginRequest();
        emptyUserRequest.setUsername("");
        emptyUserRequest.setPassword("password");

        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.login(emptyUserRequest));
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testRegister_SpecialCharactersInUsername() {
        // Arrange
        RegisterRequest specialRequest = new RegisterRequest();
        specialRequest.setUsername("user_123-abc");
        specialRequest.setPassword("password123");
        specialRequest.setEmail("user@example.com");

        when(userRepository.findByUsername("user_123-abc")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(15L);
        savedUser.setUsername("user_123-abc");
        savedUser.setRole(User.Role.JUDGE);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        LoginResponse result = authService.register(specialRequest);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void testRegister_SpecialCharactersInEmail() {
        // Arrange
        RegisterRequest emailRequest = new RegisterRequest();
        emailRequest.setUsername("newuser");
        emailRequest.setPassword("password");
        emailRequest.setEmail("user+tag@example.co.uk");

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        User savedUser = new User();
        savedUser.setId(16L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("user+tag@example.co.uk");
        savedUser.setRole(User.Role.JUDGE);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        LoginResponse result = authService.register(emailRequest);

        // Assert
        assertNotNull(result);
    }
}





