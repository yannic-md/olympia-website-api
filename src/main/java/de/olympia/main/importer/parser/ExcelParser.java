package de.olympia.main.importer.parser;

import de.olympia.main.importer.dto.AthleteImportDto;
import de.olympia.main.importer.dto.CountryImportDto;
import de.olympia.main.importer.dto.ResultImportDto;
import de.olympia.main.importer.exception.InvalidImportDataException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelParser {

    /**
     * Parse countries from Excel or CSV file
     */
    public List<CountryImportDto> parseCountries(MultipartFile file) throws IOException {
        if (isCsvFile(file)) {
            return parseCountriesFromCsv(file);
        }
        List<CountryImportDto> countries = new ArrayList<>();

        try (Workbook workbook = getWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new InvalidImportDataException("No sheets found in Excel file", "EMPTY_SHEET");
            }

            int rowNum = 0;
            for (Row row : sheet) {
                rowNum++;

                // Skip header row
                if (rowNum == 1) {
                    continue;
                }

                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }

                String code = getCellValueAsString(row.getCell(0), rowNum, "code");
                String name = getCellValueAsString(row.getCell(1), rowNum, "name");

                countries.add(new CountryImportDto(code.trim(), name.trim()));
            }
        }

        return countries;
    }

    /**
     * Parse athletes from Excel or CSV file
     */
    public List<AthleteImportDto> parseAthletes(MultipartFile file) throws IOException {
        if (isCsvFile(file)) {
            return parseAthletesFromCsv(file);
        }
        List<AthleteImportDto> athletes = new ArrayList<>();

        try (Workbook workbook = getWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new InvalidImportDataException("No sheets found in Excel file", "EMPTY_SHEET");
            }

            int rowNum = 0;
            for (Row row : sheet) {
                rowNum++;

                // Skip header row
                if (rowNum == 1) {
                    continue;
                }

                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }

                String firstName = getCellValueAsString(row.getCell(0), rowNum, "firstName");
                String lastName = getCellValueAsString(row.getCell(1), rowNum, "lastName");
                String countryCode = getCellValueAsString(row.getCell(2), rowNum, "countryCode");

                athletes.add(new AthleteImportDto(
                    firstName.trim(),
                    lastName.trim(),
                    countryCode != null ? countryCode.trim() : null
                ));
            }
        }

        return athletes;
    }

    /**
     * Parse results from Excel or CSV file
     */
    public List<ResultImportDto> parseResults(MultipartFile file) throws IOException {
        if (isCsvFile(file)) {
            return parseResultsFromCsv(file);
        }
        List<ResultImportDto> results = new ArrayList<>();

        try (Workbook workbook = getWorkbook(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new InvalidImportDataException("No sheets found in Excel file", "EMPTY_SHEET");
            }

            int rowNum = 0;
            for (Row row : sheet) {
                rowNum++;

                // Skip header row
                if (rowNum == 1) {
                    continue;
                }

                // Skip empty rows
                if (isRowEmpty(row)) {
                    continue;
                }

                String athleteFirstName = getCellValueAsString(row.getCell(0), rowNum, "athleteFirstName");
                String athleteLastName = getCellValueAsString(row.getCell(1), rowNum, "athleteLastName");
                String sport = getCellValueAsString(row.getCell(2), rowNum, "sport");
                Integer rank = getCellValueAsInteger(row.getCell(3), rowNum, "rank");
                String timeOrPoints = getCellValueAsOptionalString(row.getCell(4));
                String scoreType = getCellValueAsOptionalString(row.getCell(5));
                String medal = getCellValueAsOptionalString(row.getCell(6));

                results.add(new ResultImportDto(
                    athleteFirstName.trim(),
                    athleteLastName.trim(),
                    sport.trim(),
                    rank,
                    timeOrPoints != null ? timeOrPoints.trim() : null,
                    scoreType != null ? scoreType.trim() : null,
                    medal != null ? medal.trim() : null
                ));
            }
        }

        return results;
    }

    /**
     * Get workbook from file (supports both .xlsx and .xls)
     */
    private Workbook getWorkbook(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new InvalidImportDataException("File has no name", "INVALID_FILE");
        }

        if (filename.endsWith(".xlsx")) {
            return new XSSFWorkbook(file.getInputStream());
        } else if (filename.endsWith(".xls")) {
            return new HSSFWorkbook(file.getInputStream());
        } else {
            throw new InvalidImportDataException(
                "Only .xlsx and .xls files are supported. Got: " + filename,
                "UNSUPPORTED_FORMAT"
            );
        }
    }

    /**
     * Check if a row is empty
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get cell value as string (required field)
     */
    private String getCellValueAsString(Cell cell, int rowNum, String fieldName) {
        if (cell == null) {
            throw new InvalidImportDataException(
                "Required field is empty",
                "MISSING_REQUIRED_FIELD",
                rowNum,
                fieldName
            );
        }

        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }

        throw new InvalidImportDataException(
            "Invalid cell type for field: " + fieldName,
            "INVALID_CELL_TYPE",
            rowNum,
            fieldName
        );
    }

    /**
     * Get cell value as optional string (nullable field)
     */
    private String getCellValueAsOptionalString(Cell cell) {
        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.BLANK) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue();
            return value != null && !value.isEmpty() ? value : null;
        } else if (cell.getCellType() == CellType.NUMERIC) {
            // Handle numeric cells (e.g., times stored as numbers)
            return String.valueOf(cell.getNumericCellValue());
        }

        // For other types, try to get string representation
        try {
            String value = cell.toString();
            return value != null && !value.isEmpty() ? value : null;
        } catch (Exception e) {
            // If conversion fails, return null instead of throwing exception for optional fields
            return null;
        }
    }

    /**
     * Get cell value as integer
     */
    private Integer getCellValueAsInteger(Cell cell, int rowNum, String fieldName) {
        if (cell == null) {
            throw new InvalidImportDataException(
                "Required field is empty",
                "MISSING_REQUIRED_FIELD",
                rowNum,
                fieldName
            );
        }

        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue());
            } catch (NumberFormatException e) {
                throw new InvalidImportDataException(
                    "Invalid integer value: " + cell.getStringCellValue(),
                    "INVALID_NUMBER_FORMAT",
                    rowNum,
                    fieldName
                );
            }
        }

        throw new InvalidImportDataException(
            "Invalid cell type for numeric field: " + fieldName,
            "INVALID_CELL_TYPE",
            rowNum,
            fieldName
        );
    }

    // ---------------------------------------------------------------
    // CSV parsing helpers
    // ---------------------------------------------------------------

    private boolean isCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase().endsWith(".csv");
    }

    private CSVFormat buildCsvFormat() {
        return CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .build();
    }

    /**
     * Parse countries from CSV file.
     * Expected columns (case-insensitive): code, name
     */
    private List<CountryImportDto> parseCountriesFromCsv(MultipartFile file) throws IOException {
        List<CountryImportDto> countries = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, buildCsvFormat())) {

            int rowNum = 2; // row 1 = header
            for (CSVRecord record : csvParser) {
                String code = getRequiredCsvValue(record, "code", rowNum);
                String name = getRequiredCsvValue(record, "name", rowNum);
                countries.add(new CountryImportDto(code, name));
                rowNum++;
            }
        }
        return countries;
    }

    /**
     * Parse athletes from CSV file.
     * Expected columns (case-insensitive): firstName, lastName, countryCode
     */
    private List<AthleteImportDto> parseAthletesFromCsv(MultipartFile file) throws IOException {
        List<AthleteImportDto> athletes = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, buildCsvFormat())) {

            int rowNum = 2;
            for (CSVRecord record : csvParser) {
                String firstName = getRequiredCsvValue(record, "firstName", rowNum);
                String lastName = getRequiredCsvValue(record, "lastName", rowNum);
                String countryCode = getOptionalCsvValue(record, "countryCode");
                athletes.add(new AthleteImportDto(firstName, lastName, countryCode));
                rowNum++;
            }
        }
        return athletes;
    }

    /**
     * Parse results from CSV file.
     * Expected columns (case-insensitive):
     *   athleteFirstName, athleteLastName, sport, rank, timeOrPoints, scoreType, medal
     */
    private List<ResultImportDto> parseResultsFromCsv(MultipartFile file) throws IOException {
        List<ResultImportDto> results = new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, buildCsvFormat())) {

            int rowNum = 2;
            for (CSVRecord record : csvParser) {
                String athleteFirstName = getRequiredCsvValue(record, "athleteFirstName", rowNum);
                String athleteLastName  = getRequiredCsvValue(record, "athleteLastName", rowNum);
                String sport = getRequiredCsvValue(record, "sport", rowNum);
                String rankStr = getRequiredCsvValue(record, "rank", rowNum);
                Integer rank;
                try {
                    rank = Integer.parseInt(rankStr);
                } catch (NumberFormatException e) {
                    throw new InvalidImportDataException(
                        "Invalid integer value for rank: " + rankStr,
                        "INVALID_NUMBER_FORMAT",
                        rowNum,
                        "rank"
                    );
                }
                String timeOrPoints = getOptionalCsvValue(record, "timeOrPoints");
                String scoreType    = getOptionalCsvValue(record, "scoreType");
                String medal        = getOptionalCsvValue(record, "medal");
                results.add(new ResultImportDto(athleteFirstName, athleteLastName, sport, rank, timeOrPoints, scoreType, medal));
                rowNum++;
            }
        }
        return results;
    }

    private String getRequiredCsvValue(CSVRecord record, String columnName, int rowNum) {
        if (!record.isMapped(columnName)) {
            throw new InvalidImportDataException(
                "Required column not found: " + columnName,
                "MISSING_COLUMN",
                rowNum,
                columnName
            );
        }
        String value = record.get(columnName).trim();
        if (value.isEmpty()) {
            throw new InvalidImportDataException(
                "Required field is empty: " + columnName,
                "MISSING_REQUIRED_FIELD",
                rowNum,
                columnName
            );
        }
        return value;
    }

    private String getOptionalCsvValue(CSVRecord record, String columnName) {
        if (!record.isMapped(columnName)) {
            return null;
        }
        String value = record.get(columnName).trim();
        return value.isEmpty() ? null : value;
    }
}

