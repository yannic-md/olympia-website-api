package de.olympia.main.importer.parser;

import de.olympia.main.importer.dto.AthleteImportDto;
import de.olympia.main.importer.dto.CountryImportDto;
import de.olympia.main.importer.dto.ResultImportDto;
import de.olympia.main.importer.exception.InvalidImportDataException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelParser {

    /**
     * Parse countries from Excel file
     */
    public List<CountryImportDto> parseCountries(MultipartFile file) throws IOException {
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
     * Parse athletes from Excel file
     */
    public List<AthleteImportDto> parseAthletes(MultipartFile file) throws IOException {
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
     * Parse results from Excel file
     */
    public List<ResultImportDto> parseResults(MultipartFile file) throws IOException {
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
                Integer rank = getCellValueAsInteger(row.getCell(2), rowNum, "rank");
                String timeOrPoints = getCellValueAsOptionalString(row.getCell(3));
                String scoreType = getCellValueAsOptionalString(row.getCell(4));
                String medal = getCellValueAsOptionalString(row.getCell(5));

                results.add(new ResultImportDto(
                    athleteFirstName.trim(),
                    athleteLastName.trim(),
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
}

