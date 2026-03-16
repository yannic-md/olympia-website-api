package de.olympia.main.importer.parser;

import de.olympia.main.importer.dto.AthleteImportDto;
import de.olympia.main.importer.dto.CountryImportDto;
import de.olympia.main.importer.dto.ResultImportDto;
import de.olympia.main.importer.exception.InvalidImportDataException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class CsvParser {

    /**
     * Parse countries from CSV file.
     * Expected columns: code, name
     */
    public List<CountryImportDto> parseCountries(MultipartFile file) throws IOException {
        List<CountryImportDto> countries = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .setIgnoreEmptyLines(true)
                     .build()
                     .parse(reader)) {

            int rowNum = 1;
            for (CSVRecord record : csvParser) {
                rowNum++;
                String code = getRequiredField(record, "code", rowNum);
                String name = getRequiredField(record, "name", rowNum);
                countries.add(new CountryImportDto(code, name));
            }
        }

        return countries;
    }

    /**
     * Parse athletes from CSV file.
     * Expected columns: firstName, lastName, countryCode
     */
    public List<AthleteImportDto> parseAthletes(MultipartFile file) throws IOException {
        List<AthleteImportDto> athletes = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .setIgnoreEmptyLines(true)
                     .build()
                     .parse(reader)) {

            int rowNum = 1;
            for (CSVRecord record : csvParser) {
                rowNum++;
                String firstName = getRequiredField(record, "firstName", rowNum);
                String lastName = getRequiredField(record, "lastName", rowNum);
                String countryCode = getOptionalField(record, "countryCode");
                athletes.add(new AthleteImportDto(firstName, lastName, countryCode));
            }
        }

        return athletes;
    }

    /**
     * Parse results from CSV file.
     * Expected columns: athleteFirstName, athleteLastName, rank, timeOrPoints, scoreType, medal
     */
    public List<ResultImportDto> parseResults(MultipartFile file) throws IOException {
        List<ResultImportDto> results = new ArrayList<>();

        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
             CSVParser csvParser = CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .setIgnoreEmptyLines(true)
                     .build()
                     .parse(reader)) {

            int rowNum = 1;
            for (CSVRecord record : csvParser) {
                rowNum++;
                String athleteFirstName = getRequiredField(record, "athleteFirstName", rowNum);
                String athleteLastName = getRequiredField(record, "athleteLastName", rowNum);
                String rankStr = getRequiredField(record, "rank", rowNum);
                Integer rank;
                try {
                    rank = Integer.parseInt(rankStr);
                } catch (NumberFormatException e) {
                    throw new InvalidImportDataException(
                            "Invalid integer value: " + rankStr,
                            "INVALID_NUMBER_FORMAT",
                            rowNum,
                            "rank"
                    );
                }
                String timeOrPoints = getOptionalField(record, "timeOrPoints");
                String scoreType = getOptionalField(record, "scoreType");
                String medal = getOptionalField(record, "medal");
                results.add(new ResultImportDto(athleteFirstName, athleteLastName, rank, timeOrPoints, scoreType, medal));
            }
        }

        return results;
    }

    private String getRequiredField(CSVRecord record, String headerName, int rowNum) {
        if (!record.isMapped(headerName)) {
            throw new InvalidImportDataException(
                    "Required column not found: " + headerName,
                    "MISSING_COLUMN",
                    rowNum,
                    headerName
            );
        }
        String value = record.get(headerName);
        if (value == null || value.isBlank()) {
            throw new InvalidImportDataException(
                    "Required field is empty",
                    "MISSING_REQUIRED_FIELD",
                    rowNum,
                    headerName
            );
        }
        return value;
    }

    private String getOptionalField(CSVRecord record, String headerName) {
        if (!record.isMapped(headerName)) {
            return null;
        }
        String value = record.get(headerName);
        return (value == null || value.isBlank()) ? null : value;
    }
}
