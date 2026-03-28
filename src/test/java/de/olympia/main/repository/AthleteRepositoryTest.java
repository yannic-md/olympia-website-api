package de.olympia.main.repository;

import static org.junit.jupiter.api.Assertions.*;

import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayName("Athlete Repository Tests")
public class AthleteRepositoryTest {

    @Autowired
    private AthleteRepository athleteRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Athlete testAthlete;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setCode("DE_TEST");
        testCountry.setName("Germany");
        countryRepository.save(testCountry);

        testAthlete = new Athlete();
        testAthlete.setFirstName("John");
        testAthlete.setLastName("Doe");
        testAthlete.setCountry(testCountry);
        athleteRepository.save(testAthlete);
    }

    @Test
    @DisplayName("Should save and retrieve athlete")
    void testSaveAthlete() {
        assertNotNull(testAthlete.getId());
        assertEquals("John", testAthlete.getFirstName());
        assertEquals("Doe", testAthlete.getLastName());
    }

    @Test
    @DisplayName("Should find athlete by ID")
    void testFindById() {
        Optional<Athlete> found = athleteRepository.findById(testAthlete.getId());
        assertTrue(found.isPresent());
        assertEquals("John", found.get().getFirstName());
    }

    @Test
    @DisplayName("Should find athletes by country ID")
    void testFindByCountryId() {
        List<Athlete> athletes = athleteRepository.findByCountryId(testCountry.getId());
        assertNotNull(athletes);
        assertEquals(1, athletes.size());
        assertEquals("John", athletes.get(0).getFirstName());
    }

    @Test
    @DisplayName("Should find athlete by first and last name")
    void testFindByFirstNameAndLastName() {
        Optional<Athlete> found = athleteRepository.findByFirstNameAndLastName("John", "Doe");
        assertTrue(found.isPresent());
        assertEquals(testAthlete.getId(), found.get().getId());
    }

    @Test
    @DisplayName("Should update athlete")
    void testUpdateAthlete() {
        testAthlete.setFirstName("Jane");
        Athlete updated = athleteRepository.save(testAthlete);
        assertEquals("Jane", updated.getFirstName());
    }

    @Test
    @DisplayName("Should delete athlete")
    void testDeleteAthlete() {
        Long id = testAthlete.getId();
        athleteRepository.deleteById(id);
        Optional<Athlete> found = athleteRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should return empty list for non-existent country")
    void testFindByCountryIdNotFound() {
        List<Athlete> athletes = athleteRepository.findByCountryId(999L);
        assertNotNull(athletes);
        assertTrue(athletes.isEmpty());
    }

    @Test
    @DisplayName("Should count athletes")
    void testCountAthletes() {
        long count = athleteRepository.count();
        assertTrue(count > 0);
    }
}



