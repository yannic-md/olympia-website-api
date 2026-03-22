package de.olympia.main.importer.parser;

import de.olympia.main.importer.dto.ResultImportDto;
import de.olympia.main.importer.exception.InvalidImportDataException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExcelParserTest {

    private ExcelParser excelParser;

    @BeforeEach
    void setUp() {
        excelParser = new ExcelParser();
    }

    @Test
    void testParseResults_WithValidData_ShouldReturnResults() throws IOException {
        // Create test Excel file with valid data
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("athleteFirstName");
        headerRow.createCell(1).setCellValue("athleteLastName");
        headerRow.createCell(2).setCellValue("sport");
        headerRow.createCell(3).setCellValue("rank");
        headerRow.createCell(4).setCellValue("timeOrPoints");
        headerRow.createCell(5).setCellValue("scoreType");
        headerRow.createCell(6).setCellValue("medal");
        
        // Create data row with string values
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Katie");
        dataRow.createCell(1).setCellValue("Ledecky");
        dataRow.createCell(2).setCellValue("Speed Skating");
        dataRow.createCell(3).setCellValue(1);  // numeric rank
        dataRow.createCell(4).setCellValue("3:59.34");  // string time
        dataRow.createCell(5).setCellValue("TIME");
        dataRow.createCell(6).setCellValue("GOLD");
        
        workbook.write(baos);
        workbook.close();
        
        // Convert to MultipartFile
        MultipartFile file = new MockMultipartFile(
            "file",
            "test.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            baos.toByteArray()
        );
        
        // Parse results
        List<ResultImportDto> results = excelParser.parseResults(file);
        
        // Verify
        assertNotNull(results);
        assertEquals(1, results.size());
        
        ResultImportDto result = results.get(0);
        assertEquals("Katie", result.getAthleteFirstName());
        assertEquals("Ledecky", result.getAthleteLastName());
        assertEquals("Speed Skating", result.getSport());
        assertEquals(1, result.getRank());
        assertEquals("3:59.34", result.getTimeOrPoints());
        assertEquals("TIME", result.getScoreType());
        assertEquals("GOLD", result.getMedal());
    }

    @Test
    void testParseResults_WithNumericTime_ShouldConvertToString() throws IOException {
        // Create test Excel file with numeric time value (common Excel issue)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("athleteFirstName");
        headerRow.createCell(1).setCellValue("athleteLastName");
        headerRow.createCell(2).setCellValue("sport");
        headerRow.createCell(3).setCellValue("rank");
        headerRow.createCell(4).setCellValue("timeOrPoints");
        headerRow.createCell(5).setCellValue("scoreType");
        headerRow.createCell(6).setCellValue("medal");
        
        // Create data row with numeric time (this was causing the original bug)
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Max");
        dataRow.createCell(1).setCellValue("Mustermann");
        dataRow.createCell(2).setCellValue("Alpine Skiing");
        dataRow.createCell(3).setCellValue(2);
        dataRow.createCell(4).setCellValue(4.016666);  // numeric instead of string!
        dataRow.createCell(5).setCellValue("TIME");
        dataRow.createCell(6).setCellValue("SILVER");
        
        workbook.write(baos);
        workbook.close();
        
        // Convert to MultipartFile
        MultipartFile file = new MockMultipartFile(
            "file",
            "test.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            baos.toByteArray()
        );
        
        // Parse results - this should NOT throw exception
        List<ResultImportDto> results = excelParser.parseResults(file);
        
        // Verify
        assertNotNull(results);
        assertEquals(1, results.size());
        
        ResultImportDto result = results.get(0);
        assertEquals("Max", result.getAthleteFirstName());
        assertEquals("Mustermann", result.getAthleteLastName());
        assertEquals("Alpine Skiing", result.getSport());
        assertEquals(2, result.getRank());
        // The numeric value should be converted to string
        assertNotNull(result.getTimeOrPoints());
        assertTrue(result.getTimeOrPoints().contains("4.016666") || result.getTimeOrPoints().contains("4,016666"));
        assertEquals("TIME", result.getScoreType());
        assertEquals("SILVER", result.getMedal());
    }

    @Test
    void testParseResults_WithEmptyOptionalFields_ShouldReturnNull() throws IOException {
        // Create test Excel file with empty optional fields
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("athleteFirstName");
        headerRow.createCell(1).setCellValue("athleteLastName");
        headerRow.createCell(2).setCellValue("sport");
        headerRow.createCell(3).setCellValue("rank");
        headerRow.createCell(4).setCellValue("timeOrPoints");
        headerRow.createCell(5).setCellValue("scoreType");
        headerRow.createCell(6).setCellValue("medal");
        
        // Create data row without optional fields
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("John");
        dataRow.createCell(1).setCellValue("Doe");
        dataRow.createCell(2).setCellValue("Figure Skating");
        dataRow.createCell(3).setCellValue(3);
        // Leave cells 4, 5, 6 empty - they should be null
        
        workbook.write(baos);
        workbook.close();
        
        // Convert to MultipartFile
        MultipartFile file = new MockMultipartFile(
            "file",
            "test.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            baos.toByteArray()
        );
        
        // Parse results
        List<ResultImportDto> results = excelParser.parseResults(file);
        
        // Verify
        assertNotNull(results);
        assertEquals(1, results.size());
        
        ResultImportDto result = results.get(0);
        assertEquals("John", result.getAthleteFirstName());
        assertEquals("Doe", result.getAthleteLastName());
        assertEquals("Figure Skating", result.getSport());
        assertEquals(3, result.getRank());
        assertNull(result.getTimeOrPoints());  // Should be null, not throw exception
        assertNull(result.getScoreType());
        assertNull(result.getMedal());
    }

    @Test
    void testParseResults_WithMissingRequiredField_ShouldThrowException() throws IOException {
        // Create test Excel file with missing required field
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("athleteFirstName");
        headerRow.createCell(1).setCellValue("athleteLastName");
        headerRow.createCell(2).setCellValue("sport");
        headerRow.createCell(3).setCellValue("rank");
        headerRow.createCell(4).setCellValue("timeOrPoints");
        headerRow.createCell(5).setCellValue("scoreType");
        headerRow.createCell(6).setCellValue("medal");
        
        // Create data row missing lastName
        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Jane");
        // Missing lastName - should throw exception
        dataRow.createCell(2).setCellValue("Biathlon");
        dataRow.createCell(3).setCellValue(1);
        
        workbook.write(baos);
        workbook.close();
        
        // Convert to MultipartFile
        MultipartFile file = new MockMultipartFile(
            "file",
            "test.xlsx",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            baos.toByteArray()
        );
        
        // Parse results - should throw InvalidImportDataException
        assertThrows(InvalidImportDataException.class, () -> {
            excelParser.parseResults(file);
        });
    }
}

