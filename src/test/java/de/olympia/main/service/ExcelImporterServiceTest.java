package de.olympia.main.service;

import de.olympia.main.importer.dto.CountryImportDto;
import de.olympia.main.importer.entity.ImportDetail;
import de.olympia.main.importer.entity.ImportError;
import de.olympia.main.importer.entity.ImportLog;
import de.olympia.main.importer.exception.InvalidImportDataException;
import de.olympia.main.importer.parser.ExcelParser;
import de.olympia.main.importer.repository.ImportDetailRepository;
import de.olympia.main.importer.repository.ImportErrorRepository;
import de.olympia.main.importer.repository.ImportLogRepository;
import de.olympia.main.importer.service.ExcelImporterService;
import de.olympia.main.entity.Country;
import de.olympia.main.repository.CountryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExcelImporterService Unit Tests")
class ExcelImporterServiceTest {

    @Mock
    private ExcelParser excelParser;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private ImportLogRepository importLogRepository;

    @Mock
    private ImportErrorRepository importErrorRepository;

    @Mock
    private ImportDetailRepository importDetailRepository;

    @Mock
    private MultipartFile multipartFile;

    @Mock
    private jakarta.validation.Validator validator;

    @InjectMocks
    private ExcelImporterService excelImporterService;

    private CountryImportDto testCountryDto;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        testCountryDto = new CountryImportDto();
        testCountryDto.setCode("DE");
        testCountryDto.setName("Germany");

        testCountry = new Country();
        testCountry.setId(1L);
        testCountry.setCode("DE");
        testCountry.setName("Germany");

        when(multipartFile.getOriginalFilename()).thenReturn("test.xlsx");
    }

    // ===== IMPORT COUNTRIES =====

    @Test
    @DisplayName("Should import countries successfully")
    void testImportCountries_Success() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Arrays.asList(testCountryDto));
        when(countryRepository.findByCodeIgnoreCase("DE"))
            .thenReturn(java.util.Optional.empty());

        Country savedCountry = new Country();
        savedCountry.setId(1L);
        savedCountry.setCode("DE");
        savedCountry.setName("Germany");
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(ImportLog.ImportStatus.COMPLETED, result.getStatus());
        assertEquals(1, result.getTotalRecords());
        verify(excelParser, times(1)).parseCountries(multipartFile);
        verify(countryRepository, times(1)).save(any(Country.class));
    }

    @Test
    @DisplayName("Should handle duplicate country codes during import")
    void testImportCountries_DuplicateCode() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Arrays.asList(testCountryDto));
        when(countryRepository.findByCodeIgnoreCase("DE"))
            .thenReturn(java.util.Optional.of(testCountry));

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        importLog.setFailedRecords(0);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(ImportLog.ImportStatus.COMPLETED, result.getStatus());
        verify(importDetailRepository, times(1)).save(any(ImportDetail.class));
    }

    @Test
    @DisplayName("Should handle file read errors during import")
    void testImportCountries_FileReadError() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenThrow(new IOException("File read error"));

        ImportLog importLog = new ImportLog();
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(ImportLog.ImportStatus.FAILED, result.getStatus());
        assertTrue(result.getErrorMessage().contains("Error reading file"));
    }

    @Test
    @DisplayName("Should handle invalid import data during country import")
    void testImportCountries_InvalidData() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenThrow(new InvalidImportDataException("Invalid data", "VALIDATION_ERROR", 2, "code"));

        ImportLog importLog = new ImportLog();
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertEquals(ImportLog.ImportStatus.FAILED, result.getStatus());
        verify(importErrorRepository, times(1)).save(any(ImportError.class));
    }

    @Test
    @DisplayName("Should import multiple countries successfully")
    void testImportCountries_MultipleRecords() throws IOException {
        // Arrange
        CountryImportDto dto2 = new CountryImportDto();
        dto2.setCode("FR");
        dto2.setName("France");

        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Arrays.asList(testCountryDto, dto2));
        when(countryRepository.findByCodeIgnoreCase(anyString()))
            .thenReturn(java.util.Optional.empty());

        Country savedCountry1 = new Country();
        savedCountry1.setId(1L);
        savedCountry1.setCode("DE");

        Country savedCountry2 = new Country();
        savedCountry2.setId(2L);
        savedCountry2.setCode("FR");

        when(countryRepository.save(any(Country.class)))
            .thenReturn(savedCountry1)
            .thenReturn(savedCountry2);

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        importLog.setSuccessfulRecords(0);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertEquals(2, result.getTotalRecords());
        assertEquals(ImportLog.ImportStatus.COMPLETED, result.getStatus());
        verify(countryRepository, times(2)).save(any(Country.class));
    }

    // ===== EDGE CASES =====

    @Test
    @DisplayName("Should handle empty import file")
    void testImportCountries_EmptyFile() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Collections.emptyList());

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertEquals(0, result.getTotalRecords());
        assertEquals(ImportLog.ImportStatus.COMPLETED, result.getStatus());
    }

    @Test
    @DisplayName("Should record import details for successful imports")
    void testImportCountries_RecordsImportDetails() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Arrays.asList(testCountryDto));
        when(countryRepository.findByCodeIgnoreCase("DE"))
            .thenReturn(java.util.Optional.empty());

        Country savedCountry = new Country();
        savedCountry.setId(10L);
        savedCountry.setCode("DE");
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        verify(importDetailRepository, times(1)).save(any(ImportDetail.class));
    }

    @Test
    @DisplayName("Should handle unexpected errors gracefully")
    void testImportCountries_UnexpectedError() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenThrow(new RuntimeException("Unexpected error"));

        ImportLog importLog = new ImportLog();
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertEquals(ImportLog.ImportStatus.FAILED, result.getStatus());
        assertTrue(result.getErrorMessage().contains("Unexpected error"));
    }

    @Test
    @DisplayName("Should set correct import type for countries")
    void testImportCountries_SetCorrectImportType() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Collections.emptyList());

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertEquals("COUNTRIES", result.getImportType());
        assertEquals(1L, result.getImportedBy());
    }

    @Test
    @DisplayName("Should handle null user ID during import")
    void testImportCountries_NullUserId() throws IOException {
        // Arrange
        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Arrays.asList(testCountryDto));
        when(countryRepository.findByCodeIgnoreCase("DE"))
            .thenReturn(java.util.Optional.empty());

        Country savedCountry = new Country();
        savedCountry.setId(1L);
        when(countryRepository.save(any(Country.class))).thenReturn(savedCountry);

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, null);

        // Assert
        assertNotNull(result);
        assertNull(result.getImportedBy());
    }

    @Test
    @DisplayName("Should track successful and failed records separately")
    void testImportCountries_TrackingMetrics() throws IOException {
        // Arrange
        CountryImportDto success = new CountryImportDto();
        success.setCode("DE");
        success.setName("Germany");

        CountryImportDto duplicate = new CountryImportDto();
        duplicate.setCode("FR");
        duplicate.setName("France");

        when(excelParser.parseCountries(multipartFile))
            .thenReturn(Arrays.asList(success, duplicate));

        when(countryRepository.findByCodeIgnoreCase("DE"))
            .thenReturn(java.util.Optional.empty());
        when(countryRepository.findByCodeIgnoreCase("FR"))
            .thenReturn(java.util.Optional.of(testCountry));

        Country saved = new Country();
        saved.setId(1L);
        when(countryRepository.save(any(Country.class))).thenReturn(saved);

        ImportLog importLog = new ImportLog();
        importLog.setId(1L);
        importLog.setSuccessfulRecords(0);
        importLog.setFailedRecords(0);
        when(importLogRepository.save(any(ImportLog.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ImportLog result = excelImporterService.importCountries(multipartFile, 1L);

        // Assert
        assertEquals(2, result.getTotalRecords());
    }
}


