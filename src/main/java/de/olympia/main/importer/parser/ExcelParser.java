package de.olympia.main.importer.parser;

import de.olympia.main.dto.AthleteImportDto;
import de.olympia.main.dto.CountryImportDto;
import de.olympia.main.dto.ResultImportDto;
import de.olympia.main.importer.exception.InvalidImportDataException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ExcelParser {

    /**
     * Parse countries from Excel or CSV file
     */
    public List<CountryImportDto> parseCountries(MultipartFile file) throws IOException {
        if (isCsvFile(file)) {
            return parseCountriesCsv(file);
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
                String nameEn = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;
                String nameDe = row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null;
                String nameFr = row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null;

                CountryImportDto dto = new CountryImportDto(code.trim(), name.trim(), nameEn, nameDe, nameFr);
                countries.add(dto);
            }
        }

        return countries;
    }

    /**
     * Parse athletes from Excel or CSV file
     */
    public List<AthleteImportDto> parseAthletes(MultipartFile file) throws IOException {
        if (isCsvFile(file)) {
            return parseAthletesCsv(file);
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
            return parseResultsCsv(file);
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
                String timeOrPoints = row.getCell(4) != null ? getCellValueAsStringOptional(row.getCell(4), rowNum) : null;
                String scoreType = row.getCell(5) != null ? getCellValueAsStringOptional(row.getCell(5), rowNum) : null;
                String medal = row.getCell(6) != null ? getCellValueAsStringOptional(row.getCell(6), rowNum) : null;

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

    private List<CountryImportDto> parseCountriesCsv(MultipartFile file) throws IOException {
        List<CountryImportDto> countries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                 .builder()
                 .setHeader()
                 .setSkipHeaderRecord(true)
                 .setTrim(true)
                 .build()
                 .parse(reader)) {

            for (CSVRecord record : parser) {
                int rowNum = (int) record.getRecordNumber() + 1;
                if (isCsvRecordEmpty(record)) {
                    continue;
                }

                String code = getCsvValueByName(record, "code", rowNum, true);
                String name = getCsvValueByName(record, "name", rowNum, true);
                String nameEn = getCsvValueByName(record, "nameEn", rowNum, false);
                String nameDe = getCsvValueByName(record, "nameDe", rowNum, false);
                String nameFr = getCsvValueByName(record, "nameFr", rowNum, false);
                countries.add(new CountryImportDto(code, name, nameEn, nameDe, nameFr));
            }
        }

        return countries;
    }

    private List<AthleteImportDto> parseAthletesCsv(MultipartFile file) throws IOException {
        List<AthleteImportDto> athletes = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                 .builder()
                 .setHeader()
                 .setSkipHeaderRecord(true)
                 .setTrim(true)
                 .build()
                 .parse(reader)) {

            for (CSVRecord record : parser) {
                int rowNum = (int) record.getRecordNumber() + 1;
                if (isCsvRecordEmpty(record)) {
                    continue;
                }

                String firstName = getCsvValueByName(record, "firstName", rowNum, true);
                String lastName = getCsvValueByName(record, "lastName", rowNum, true);
                String countryCode = getCsvValueByName(record, "countryCode", rowNum, false);
                athletes.add(new AthleteImportDto(firstName, lastName, countryCode));
            }
        }

        return athletes;
    }

    private List<ResultImportDto> parseResultsCsv(MultipartFile file) throws IOException {
        List<ResultImportDto> results = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                 .builder()
                 .setHeader()
                 .setSkipHeaderRecord(true)
                 .setTrim(true)
                 .build()
                 .parse(reader)) {

            for (CSVRecord record : parser) {
                int rowNum = (int) record.getRecordNumber() + 1;
                if (isCsvRecordEmpty(record)) {
                    continue;
                }

                String athleteFirstName = getCsvValueByName(record, "athleteFirstName", rowNum, true);
                String athleteLastName = getCsvValueByName(record, "athleteLastName", rowNum, true);
                String sport = getCsvValueByName(record, "sport", rowNum, true);
                Integer rank = getCsvIntegerValueByName(record, "rank", rowNum, true);
                String timeOrPoints = getCsvValueByName(record, "timeOrPoints", rowNum, false);
                String scoreType = getCsvValueByName(record, "scoreType", rowNum, false);
                String medal = getCsvValueByName(record, "medal", rowNum, false);

                results.add(new ResultImportDto(
                    athleteFirstName,
                    athleteLastName,
                    sport,
                    rank,
                    timeOrPoints,
                    scoreType,
                    medal
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
                "Only .xlsx, .xls and .csv files are supported. Got: " + filename,
                "UNSUPPORTED_FORMAT"
            );
        }
    }

    private boolean isCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase(Locale.ROOT).endsWith(".csv");
    }

    private boolean isCsvRecordEmpty(CSVRecord record) {
        for (int i = 0; i < record.size(); i++) {
            String value = record.get(i);
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getCsvValueByName(CSVRecord record, String columnName, int rowNum, boolean required) {
        String value = null;
        try {
            value = record.get(columnName);
        } catch (IllegalArgumentException e) {
            if (required) {
                throw new InvalidImportDataException(
                    "Required column '" + columnName + "' not found in CSV header",
                    "MISSING_REQUIRED_FIELD",
                    rowNum,
                    columnName
                );
            }
            return null;
        }

        if (value != null) {
            value = value.trim();
        }

        if (required && (value == null || value.isEmpty())) {
            throw new InvalidImportDataException(
                "Required field is empty",
                "MISSING_REQUIRED_FIELD",
                rowNum,
                columnName
            );
        }

        return (value == null || value.isEmpty()) ? null : value;
    }

    private Integer getCsvIntegerValueByName(CSVRecord record, String columnName, int rowNum, boolean required) {
        String value = getCsvValueByName(record, columnName, rowNum, required);
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidImportDataException(
                "Invalid integer value: " + value,
                "INVALID_NUMBER_FORMAT",
                rowNum,
                columnName
            );
        }
    }

    private String getCsvValue(CSVRecord record, int index, int rowNum, String fieldName, boolean required) {
        String value = index < record.size() ? record.get(index) : null;
        if (value != null) {
            value = value.trim();
        }

        if (required && (value == null || value.isEmpty())) {
            throw new InvalidImportDataException(
                "Required field is empty",
                "MISSING_REQUIRED_FIELD",
                rowNum,
                fieldName
            );
        }

        return (value == null || value.isEmpty()) ? null : value;
    }

    private Integer getCsvIntegerValue(CSVRecord record, int index, int rowNum, String fieldName, boolean required) {
        String value = getCsvValue(record, index, rowNum, fieldName, required);
        if (value == null) {
            return null;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidImportDataException(
                "Invalid integer value: " + value,
                "INVALID_NUMBER_FORMAT",
                rowNum,
                fieldName
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
     * Get cell value as string
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
     * Get cell value as string (optional - returns null if cell is blank or null)
     */
    private String getCellValueAsStringOptional(Cell cell, int rowNum) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }

        if (cell.getCellType() == CellType.STRING) {
            String value = cell.getStringCellValue();
            return value != null && !value.isEmpty() ? value : null;
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }

        return null;
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
}

