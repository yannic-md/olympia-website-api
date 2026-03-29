package de.olympia.main.repository;

import static org.junit.jupiter.api.Assertions.*;

import de.olympia.main.entity.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayName("User Repository Tests")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser_test");
        testUser.setPasswordHash("hashedpassword");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.JUDGE);
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("Should save and retrieve user")
    void testSaveUser() {
        assertNotNull(testUser.getId());
        assertEquals("testuser", testUser.getUsername());
        assertEquals("test@example.com", testUser.getEmail());
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void testFindByUsernameNotFound() {
        Optional<User> found = userRepository.findByUsername("nonexistent");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should update user")
    void testUpdateUser() {
        testUser.setEmail("newemail@example.com");
        User updated = userRepository.save(testUser);
        assertEquals("newemail@example.com", updated.getEmail());
    }

    @Test
    @DisplayName("Should delete user")
    void testDeleteUser() {
        Long id = testUser.getId();
        userRepository.deleteById(id);
        Optional<User> found = userRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should have correct role")
    void testUserRole() {
        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals(User.Role.JUDGE, found.get().getRole());
    }
}



