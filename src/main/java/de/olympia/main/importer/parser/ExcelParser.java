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
     * Parst Längerdaten aus einer Excel oder CSV Datei.
     * 
     * Diese Methode verarbeitet die Upload-Datei und extrahiert Längerdaten. Je nach
     * Dateityp (CSV oder Excel) wird ein entsprechender Parser aufgerufen. Die Datei
     * wird zeilenweise gelesen und jede Zeile wird validiert. Leerzeilen werden übersprungen,
     * die Header-Zeile wird ignoriert.
     * 
     * Spaltenformat (Excel/CSV):
     * - Spalte 0/1: code (erforderlich) - ISO-Code des Landes
     * - Spalte 1: name (erforderlich) - Name des Landes
     * - Spalte 2: nameEn (optional) - Name in Englisch
     * - Spalte 3: nameDe (optional) - Name in Deutsch
     * - Spalte 4: nameFr (optional) - Name in Französisch
     * 
     * @param file die zu parsende Datei (Excel .xlsx/.xls oder CSV)
     * @return Liste von CountryImportDto Objekten mit den geparsten Längerdaten
     * @throws IOException wenn die Datei nicht gelesen werden kann
     * @throws InvalidImportDataException wenn die Datei ungültig ist oder erforderliche Felder fehlen
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
     * Parst Athletendaten aus einer Excel oder CSV Datei.
     * 
     * Diese Methode verarbeitet die Upload-Datei und extrahiert Informationen über Athleten.
     * Je nach Dateityp (CSV oder Excel) wird ein entsprechender Parser aufgerufen. Die Datei
     * wird zeilenweise gelesen, Leerzeilen werden übersprungen und die Header-Zeile ignoriert.
     * 
     * Spaltenformat (Excel/CSV):
     * - Spalte 0: firstName (erforderlich) - Vorname des Athleten
     * - Spalte 1: lastName (erforderlich) - Nachname des Athleten
     * - Spalte 2: countryCode (optional) - ISO-Code des Landes des Athleten
     * 
     * @param file die zu parsende Datei (Excel .xlsx/.xls oder CSV)
     * @return Liste von AthleteImportDto Objekten mit den geparsten Athletendaten
     * @throws IOException wenn die Datei nicht gelesen werden kann
     * @throws InvalidImportDataException wenn erforderliche Felder fehlen oder Datentypen falsch sind
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
     * Parst Wettkampfergebnisse aus einer Excel oder CSV Datei.
     * 
     * Diese Methode verarbeitet die Upload-Datei und extrahiert Ergebnisdaten von Athleten
     * in verschiedenen Sportarten. Je nach Dateityp (CSV oder Excel) wird ein entsprechender
     * Parser aufgerufen. Die Datei wird zeilenweise verarbeitet, Leerzeilen ignoriert und
     * die Header-Zeile übersprungen. Ergebnisse können Zeiten, Punkte oder Medaillen enthalten.
     * 
     * Spaltenformat (Excel/CSV):
     * - Spalte 0: athleteFirstName (erforderlich) - Vorname des Athleten
     * - Spalte 1: athleteLastName (erforderlich) - Nachname des Athleten
     * - Spalte 2: sport (erforderlich) - Name der Sportart
     * - Spalte 3: rank (erforderlich) - Platzierung/Rang als Ganzzahl
     * - Spalte 4: timeOrPoints (optional) - Zeitleistung oder Punktzahl
     * - Spalte 5: scoreType (optional) - Typ der Bewertung (z.B. "TIME" oder "POINTS")
     * - Spalte 6: medal (optional) - Gewonnene Medaille (z.B. "GOLD", "SILVER", "BRONZE")
     * 
     * @param file die zu parsende Datei (Excel .xlsx/.xls oder CSV)
     * @return Liste von ResultImportDto Objekten mit den geparsten Ergebnisdaten
     * @throws IOException wenn die Datei nicht gelesen werden kann
     * @throws InvalidImportDataException wenn erforderliche Felder fehlen oder Datentypen falsch sind
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

    /**
     * Parst Längerdaten aus einer CSV-Datei.
     * 
     * Diese interne Hilfsmethode verarbeitet CSV-Dateien mit Längerdaten. Die Datei wird
     * als UTF-8 gelesen und mit der Apache Commons CSV Bibliothek geparst. Die erste Zeile
     * wird als Header interpretiert (Spaltennamen). Leerzeilen werden übersprungen.
     * 
     * @param file die zu parsende CSV-Datei
     * @return Liste von CountryImportDto Objekten mit den geparsten Längerdaten
     * @throws IOException wenn die Datei nicht gelesen werden kann
     * @throws InvalidImportDataException wenn erforderliche Spalten fehlen oder Daten ungültig sind
     */
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

    /**
     * Parst Athletendaten aus einer CSV-Datei.
     * 
     * Diese interne Hilfsmethode verarbeitet CSV-Dateien mit Athletendaten. Die Datei wird
     * als UTF-8 gelesen und mit der Apache Commons CSV Bibliothek geparst. Die erste Zeile
     * wird als Header interpretiert (Spaltennamen). Leerzeilen werden übersprungen.
     * 
     * @param file die zu parsende CSV-Datei
     * @return Liste von AthleteImportDto Objekten mit den geparsten Athletendaten
     * @throws IOException wenn die Datei nicht gelesen werden kann
     * @throws InvalidImportDataException wenn erforderliche Spalten fehlen oder Daten ungültig sind
     */
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

    /**
     * Parst Wettkampfergebnisse aus einer CSV-Datei.
     * 
     * Diese interne Hilfsmethode verarbeitet CSV-Dateien mit Ergebnisdaten. Die Datei wird
     * als UTF-8 gelesen und mit der Apache Commons CSV Bibliothek geparst. Die erste Zeile
     * wird als Header interpretiert (Spaltennamen). Leerzeilen werden übersprungen. Ergebnisse
     * können Zeiten, Punkte und Medaillen enthalten.
     * 
     * @param file die zu parsende CSV-Datei
     * @return Liste von ResultImportDto Objekten mit den geparsten Ergebnisdaten
     * @throws IOException wenn die Datei nicht gelesen werden kann
     * @throws InvalidImportDataException wenn erforderliche Spalten fehlen oder Daten ungültig sind
     */
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
     * Erstellt ein Workbook-Objekt aus einer Excel-Datei.
     * 
     * Diese Methode prüft die Dateiendung und erstellt das entsprechende Workbook-Objekt:
     * - .xlsx Dateien werden mit XSSFWorkbook (Excel 2007+) geöffnet
     * - .xls Dateien werden mit HSSFWorkbook (Excel 97-2003) geöffnet
     * 
     * @param file die Excel-Datei, die geöffnet werden soll
     * @return Workbook-Objekt (XSSFWorkbook oder HSSFWorkbook je nach Format)
     * @throws IOException wenn die Datei nicht gelesen werden kann
     * @throws InvalidImportDataException wenn die Datei keinen Namen hat oder ein nicht unterstütztes Format hat
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

    /**
     * Prüft, ob eine hochgeladene Datei eine CSV-Datei ist.
     * 
     * Diese Methode untersucht die Dateiendung (case-insensitive) um festzustellen,
     * ob die Datei vom Typ CSV ist.
     * 
     * @param file die zu prüfende Datei
     * @return true wenn die Datei .csv Endung hat, false sonst
     */
    private boolean isCsvFile(MultipartFile file) {
        String filename = file.getOriginalFilename();
        return filename != null && filename.toLowerCase(Locale.ROOT).endsWith(".csv");
    }

    /**
     * Prüft, ob ein CSV-Record vollständig leer ist.
     * 
     * Ein CSV-Record wird als leer betrachtet, wenn alle Spalten null oder
     * nur Whitespace enthalten. Dies ist hilfreich um Leerzeilen zu filtern.
     * 
     * @param record der zu prüfende CSV-Record
     * @return true wenn alle Spalten des Records leer sind, false wenn mindestens ein Wert vorhanden ist
     */
    private boolean isCsvRecordEmpty(CSVRecord record) {
        for (int i = 0; i < record.size(); i++) {
            String value = record.get(i);
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Holt einen String-Wert aus einem CSV-Record anhand des Spaltennamens.
     * 
     * Diese Methode sucht nach einer benannten Spalte im CSV-Header und holt deren Wert.
     * Bei erforderlichen Feldern wird eine Exception geworfen, wenn die Spalte fehlt oder
     * der Wert leer ist. Bei optionalen Feldern wird null zurückgegeben, wenn die Spalte
     * nicht existiert oder der Wert leer ist.
     * 
     * @param record der CSV-Record mit Spaltendaten
     * @param columnName der Name der gesuchten Spalte (aus dem Header)
     * @param rowNum die Zeilennummer (für Fehlerberichterstattung)
     * @param required ob das Feld erforderlich ist
     * @return der getrimmte Wert der Spalte oder null für optionale leere Felder
     * @throws InvalidImportDataException wenn Spalte nicht existiert (erforderlich) oder Wert leer ist (erforderlich)
     */
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

    /**
     * Holt einen Integer-Wert aus einem CSV-Record anhand des Spaltennamens.
     * 
     * Diese Methode sucht nach einer benannten Spalte im CSV-Header und parst deren
     * Wert zu einem Integer. Bei erforderlichen Feldern wird eine Exception geworfen,
     * wenn die Spalte fehlt, der Wert leer ist oder kein Integer geparst werden kann.
     * Bei optionalen Feldern wird null zurückgegeben, wenn die Spalte nicht existiert
     * oder der Wert leer ist.
     * 
     * @param record der CSV-Record mit Spaltendaten
     * @param columnName der Name der gesuchten Spalte (aus dem Header)
     * @param rowNum die Zeilennummer (für Fehlerberichterstattung)
     * @param required ob das Feld erforderlich ist
     * @return der geparste Integer-Wert oder null für optionale leere Felder
     * @throws InvalidImportDataException wenn Spalte nicht existiert (erforderlich), Wert leer ist (erforderlich),
     *                                     oder Wert kein Integer ist
     */
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

    /**
     * Holt einen String-Wert aus einem CSV-Record anhand des Spaltenindex.
     * 
     * Diese Methode liest einen Wert aus dem CSV-Record basierend auf dem Spaltenindex
     * (anstelle des Spaltennamens). Bei erforderlichen Feldern wird eine Exception
     * geworfen, wenn der Wert leer oder nicht vorhanden ist. Bei optionalen Feldern
     * wird null zurückgegeben.
     * 
     * @param record der CSV-Record mit Spaltendaten
     * @param index der Spaltenindex (0-basiert)
     * @param rowNum die Zeilennummer (für Fehlerberichterstattung)
     * @param fieldName der Feldname (für Fehlerberichterstattung)
     * @param required ob das Feld erforderlich ist
     * @return der getrimmte Wert oder null für optionale leere Felder
     * @throws InvalidImportDataException wenn Wert leer ist und Feld erforderlich ist
     */
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

    /**
     * Holt einen Integer-Wert aus einem CSV-Record anhand des Spaltenindex.
     * 
     * Diese Methode liest einen Wert aus dem CSV-Record basierend auf dem Spaltenindex
     * und parst ihn zu einem Integer. Bei erforderlichen Feldern wird eine Exception
     * geworfen, wenn der Wert leer ist oder kein Integer geparst werden kann. Bei
     * optionalen Feldern wird null zurückgegeben, wenn der Wert leer ist.
     * 
     * @param record der CSV-Record mit Spaltendaten
     * @param index der Spaltenindex (0-basiert)
     * @param rowNum die Zeilennummer (für Fehlerberichterstattung)
     * @param fieldName der Feldname (für Fehlerberichterstattung)
     * @param required ob das Feld erforderlich ist
     * @return der geparste Integer-Wert oder null für optionale leere Felder
     * @throws InvalidImportDataException wenn Wert leer ist (erforderlich) oder kein Integer geparst werden kann
     */
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
     * Prüft, ob eine Excel-Zeile vollständig leer ist.
     * 
     * Eine Zeile wird als leer betrachtet, wenn:
     * - die Zeile selbst null ist, oder
     * - alle Zellen in der Zeile blank oder null sind
     * 
     * @param row die zu prüfende Excel-Zeile
     * @return true wenn die Zeile leer ist, false wenn sie Daten enthält
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
     * Holt den String-Wert aus einer erforderlichen Excel-Zelle.
     * 
     * Diese Methode liest einen Zellwert und konvertiert ihn zu einem String.
     * Sie unterstützt STRING- und NUMERIC-Zelltypen. NUMERIC-Werte werden als
     * ganzzahlige Werte konvertiert. Null oder leere Zellen werfen eine Exception.
     * 
     * @param cell die zu lesende Zelle
     * @param rowNum die Zeilennummer (für Fehlerberichterstattung)
     * @param fieldName der Feldname (für Fehlerberichterstattung)
     * @return String-Wert der Zelle
     * @throws InvalidImportDataException wenn Zelle null/leer ist oder ein ungültiger Zelltyp vorliegt
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
     * Holt den String-Wert aus einer optionalen Excel-Zelle.
     * 
     * Diese Methode liest einen Zellwert und konvertiert ihn zu einem String, wobei
     * null oder leere Zellen akzeptiert werden. Sie unterstützt STRING- und NUMERIC-Zelltypen.
     * NUMERIC-Werte werden als ganzzahlige Werte konvertiert. Leere Strings werden als null zurückgegeben.
     * 
     * @param cell die zu lesende Zelle (kann null sein)
     * @param rowNum die Zeilennummer (für zukünftige Fehlerberichterstattung)
     * @return String-Wert der Zelle oder null wenn leer/blank
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
     * Holt den Integer-Wert aus einer erforderlichen Excel-Zelle.
     * 
     * Diese Methode liest einen Zellwert und konvertiert ihn zu einem Integer.
     * Sie unterstützt NUMERIC-Zelltypen direkt sowie STRING-Zelltypen (mit Parsing).
     * Null oder leere Zellen werfen eine Exception. Invalid integer values werfen ebenfalls eine Exception.
     * 
     * @param cell die zu lesende Zelle
     * @param rowNum die Zeilennummer (für Fehlerberichterstattung)
     * @param fieldName der Feldname (für Fehlerberichterstattung)
     * @return Integer-Wert der Zelle
     * @throws InvalidImportDataException wenn Zelle null/leer ist, kein Integer konvertiert werden kann,
     *                                     oder ein ungültiger Zelltyp vorliegt
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

