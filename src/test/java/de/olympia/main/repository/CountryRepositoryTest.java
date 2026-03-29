package de.olympia.main.repository;

import static org.junit.jupiter.api.Assertions.*;

import de.olympia.main.entity.Country;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@DisplayName("Country Repository Tests")
public class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountry = new Country();
        testCountry.setCode("DE_TEST");
        testCountry.setName("Germany");
        testCountry.setNameEn("Germany");
        testCountry.setNameDe("Deutschland");
        testCountry.setNameFr("Allemagne");
        countryRepository.save(testCountry);
    }

    @Test
    @DisplayName("Should save and retrieve country")
    void testSaveCountry() {
        assertNotNull(testCountry.getId());
        assertEquals("DE", testCountry.getCode());
        assertEquals("Germany", testCountry.getName());
    }

    @Test
    @DisplayName("Should find country by ID")
    void testFindById() {
        Optional<Country> found = countryRepository.findById(testCountry.getId());
        assertTrue(found.isPresent());
        assertEquals("DE", found.get().getCode());
    }

    @Test
    @DisplayName("Should find country by code")
    void testFindByCode() {
        Optional<Country> found = countryRepository.findByCode("DE");
        assertTrue(found.isPresent());
        assertEquals("Germany", found.get().getName());
    }

    @Test
    @DisplayName("Should return empty when code not found")
    void testFindByCodeNotFound() {
        Optional<Country> found = countryRepository.findByCode("XX");
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should update country")
    void testUpdateCountry() {
        testCountry.setName("Deutschland");
        Country updated = countryRepository.save(testCountry);
        assertEquals("Deutschland", updated.getName());
    }

    @Test
    @DisplayName("Should delete country")
    void testDeleteCountry() {
        Long id = testCountry.getId();
        countryRepository.deleteById(id);
        Optional<Country> found = countryRepository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should store translations")
    void testCountryTranslations() {
        Optional<Country> found = countryRepository.findByCode("DE");
        assertTrue(found.isPresent());
        Country country = found.get();
        assertEquals("Germany", country.getNameEn());
        assertEquals("Deutschland", country.getNameDe());
        assertEquals("Allemagne", country.getNameFr());
    }

    @Test
    @DisplayName("Should count countries")
    void testCountCountries() {
        long count = countryRepository.count();
        assertTrue(count > 0);
    }
}



