package de.olympia.main.repository;

import static org.junit.jupiter.api.Assertions.*;

import de.olympia.main.entity.Sports;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayName("Sports Repository Tests")
public class SportsRepositoryTest {

    @Autowired
    private SportsRepository sportsRepository;

    private Sports testSport;

    @BeforeEach
    void setUp() {
        testSport = new Sports();
        testSport.setName("Swimming_Test");
        testSport.setScoreType(Sports.ScoreType.TIME);
        sportsRepository.save(testSport);
    }

    @Test
    @DisplayName("Should save and retrieve sport")
    void testSaveSport() {
        assertNotNull(testSport.getId());
        assertEquals("Swimming", testSport.getName());
        assertEquals(Sports.ScoreType.TIME, testSport.getScoreType());
    }

    @Test
    @DisplayName("Should find sport by ID")
    void testFindById() {
        Optional<Sports> found = sportsRepository.findById(testSport.getId());
        assertTrue(found.isPresent());
        assertEquals("Swimming", found.get().getName());
    }

    @Test
    @DisplayName("Should find sport by name ignoring case")
    void testFindByNameIgnoreCase() {
        Optional<Sports> found = sportsRepository.findByNameIgnoreCase("swimming");
        assertTrue(found.isPresent());
        assertEquals("Swimming", found.get().getName());
    }

    @Test
    @DisplayName("Should find sport with uppercase")
    void testFindByNameIgnoreCaseUppercase() {
        Optional<Sports> found = sportsRepository.findByNameIgnoreCase("SWIMMING");
        assertTrue(found.isPresent());
        assertEquals("Swimming", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when sport not found")
    void testFindByNameNotFound() {
        Optional<Sports> found = sportsRepository.findByNameIgnoreCase("Unknown");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should update sport")
    void testUpdateSport() {
        testSport.setScoreType(Sports.ScoreType.PTS);
        Sports updated = sportsRepository.save(testSport);
        assertEquals(Sports.ScoreType.PTS, updated.getScoreType());
    }

    @Test
    @DisplayName("Should delete sport")
    void testDeleteSport() {
        Long id = testSport.getId();
        sportsRepository.deleteById(id);
        Optional<Sports> found = sportsRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should count sports")
    void testCountSports() {
        long count = sportsRepository.count();
        assertTrue(count > 0);
    }
}








