package de.olympia.main.controller;

import de.olympia.main.dto.ImportResponseDto;
import de.olympia.main.entity.ImportLog;
import de.olympia.main.importer.exception.ImportException;
import de.olympia.main.repository.ImportErrorRepository;
import de.olympia.main.service.ExcelImporterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/imports")
public class ImportController {

    private final ExcelImporterService excelImporterService;
    private final ImportErrorRepository importErrorRepository;

    public ImportController(ExcelImporterService excelImporterService, ImportErrorRepository importErrorRepository) {
        this.excelImporterService = excelImporterService;
        this.importErrorRepository = importErrorRepository;
    }

    /**
     * Importiert Länder aus einer Excel oder CSV Datei.
     * 
     * Dieses Endpunkt ist nur für Administratoren verfügbar und ermöglicht den
     * Massenimport von Ländern. Die Datei wird validiert, geparst und verarbeitet.
     * Bei erfolgreicher Verarbeitung wird ein ImportLog mit Details erstellt.
     * 
     * Unterstützte Dateiformate: .xlsx, .xls, .csv
     * 
     * @param file die hochzuladende Datei mit Längerdaten. Darf nicht leer sein.
     * @param userId optionale Admin-ID, die den Import durchführt. Wenn nicht angegeben,
     *               wird eine Standard-Admin-ID verwendet.
     * @return ResponseEntity mit ImportResponseDto enthält Status, Anzahl erfolgreicher/fehlgeschlagener
     *         Datensätze und eventuelle Fehler. HTTP 200 bei Erfolg, 400 bei Fehler.
     */
    @PostMapping("/countries")
    public ResponseEntity<ImportResponseDto> importCountries(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "userId", required = false) Long userId
    ) {
        log.info("Starting countries import from file: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message("File is empty")
                    .build());
        }

        if (!isSupportedImportFile(file.getOriginalFilename())) {
            return ResponseEntity.badRequest()
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message("Only .xlsx, .xls and .csv files are supported")
                    .build());
        }

        try {
            // Use a default admin ID if not provided (in production, get from security context)
            Long importUserId = userId != null ? userId : 1L;

            ImportLog result = excelImporterService.importCountries(file, importUserId);
            return buildResponse(result);
        } catch (ImportException e) {
            log.error("Import failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message(e.getMessage())
                    .build());
        }
    }

    /**
     * Importiert Athleten aus einer Excel oder CSV Datei.
     * 
     * Dieses Endpunkt ist nur für Administratoren verfügbar und ermöglicht den
     * Massenimport von Athleten mit ihren Länderinformationen. Die Datei wird
     * validiert, geparst und verarbeitet. Vorhandene Athleten können aktualisiert
     * oder neue hinzugefügt werden.
     * 
     * Unterstützte Dateiformate: .xlsx, .xls, .csv
     * 
     * @param file die hochzuladende Datei mit Athletendaten. Muss Spalten für
     *             Vorname, Nachname und Ländercode enthalten.
     * @param userId optionale Admin-ID, die den Import durchführt. Wenn nicht angegeben,
     *               wird eine Standard-Admin-ID verwendet.
     * @return ResponseEntity mit ImportResponseDto enthält Status, Anzahl erfolgreicher/fehlgeschlagener
     *         Datensätze und Details zu Validierungsfehlern. HTTP 200 bei Erfolg, 400 bei Fehler.
     */
    @PostMapping("/athletes")
    public ResponseEntity<ImportResponseDto> importAthletes(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "userId", required = false) Long userId
    ) {
        log.info("Starting athletes import from file: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message("File is empty")
                    .build());
        }

        if (!isSupportedImportFile(file.getOriginalFilename())) {
            return ResponseEntity.badRequest()
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message("Only .xlsx, .xls and .csv files are supported")
                    .build());
        }

        try {
            // Use a default admin ID if not provided (in production, get from security context)
            Long importUserId = userId != null ? userId : 1L;

            ImportLog result = excelImporterService.importAthletes(file, importUserId);
            return buildResponse(result);
        } catch (ImportException e) {
            log.error("Import failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message(e.getMessage())
                    .build());
        }
    }

    /**
     * Importiert Wettkampfergebnisse aus einer Excel oder CSV Datei.
     * 
     * Dieses Endpunkt ist nur für Administratoren verfügbar und ermöglicht den
     * Massenimport von Ergebnissen. Die Datei wird validiert, geparst und verarbeitet.
     * Jedes Ergebnis wird mit dem entsprechenden Athleten und Sportart verknüpft.
     * 
     * Unterstützte Dateiformate: .xlsx, .xls, .csv
     * 
     * @param file die hochzuladende Datei mit Ergebnisdaten. Muss Spalten für
     *             Athletenname, Sportart, Rang und Zeitleistung/Punkte enthalten.
     * @param userId optionale Admin-ID, die den Import durchführt. Wenn nicht angegeben,
     *               wird eine Standard-Admin-ID verwendet.
     * @return ResponseEntity mit ImportResponseDto enthält Status, Anzahl erfolgreicher/fehlgeschlagener
     *         Datensätze und Fehlerdetails pro Zeile. HTTP 200 bei Erfolg, 400 bei Fehler.
     */
    @PostMapping("/results")
    public ResponseEntity<ImportResponseDto> importResults(
        @RequestParam("file") MultipartFile file,
        @RequestParam(value = "userId", required = false) Long userId
    ) {
        log.info("Starting results import from file: {}", file.getOriginalFilename());

        // Validate file
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message("File is empty")
                    .build());
        }

        if (!isSupportedImportFile(file.getOriginalFilename())) {
            return ResponseEntity.badRequest()
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message("Only .xlsx, .xls and .csv files are supported")
                    .build());
        }

        try {
            // Use a default admin ID if not provided (in production, get from security context)
            Long importUserId = userId != null ? userId : 1L;

            ImportLog result = excelImporterService.importResults(file, importUserId);
            return buildResponse(result);
        } catch (ImportException e) {
            log.error("Import failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ImportResponseDto.builder()
                    .status("FAILED")
                    .message(e.getMessage())
                    .build());
        }
    }

    /**
     * Erstellt eine ResponseEntity aus einem ImportLog.
     * 
     * Diese Hilfsmethode konvertiert ein ImportLog-Objekt in eine ResponseEntity mit einem
     * ImportResponseDto. Sie holt alle zugehörigen Fehler aus der Datenbank und mappet sie
     * in die Response. Der HTTP-Statuscode wird basierend auf dem Import-Status gesetzt
     * (OK für COMPLETED, BAD_REQUEST für andere Status).
     * 
     * @param importLog das zu konvertierende ImportLog mit Import-Metadaten und Status
     * @return ResponseEntity mit ImportResponseDto und entsprechendem HTTP-Statuscode
     */
    private ResponseEntity<ImportResponseDto> buildResponse(ImportLog importLog) {
        var errors = importErrorRepository.findByImportLogId(importLog.getId())
            .stream()
            .map(error -> new ImportResponseDto.ImportErrorDto(
                error.getRowNumber(),
                error.getErrorCode(),
                error.getErrorMessage(),
                error.getFieldName(),
                error.getFieldValue()
            ))
            .collect(Collectors.toList());

        ImportResponseDto response = ImportResponseDto.builder()
            .importLogId(importLog.getId())
            .status(importLog.getStatus().toString())
            .importType(importLog.getImportType())
            .filename(importLog.getFilename())
            .totalRecords(importLog.getTotalRecords())
            .successfulRecords(importLog.getSuccessfulRecords())
            .failedRecords(importLog.getFailedRecords())
            .message(formatMessage(importLog))
            .errors(errors.isEmpty() ? null : errors)
            .build();

        HttpStatus statusCode = importLog.getStatus() == ImportLog.ImportStatus.COMPLETED
            ? HttpStatus.OK
            : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(statusCode).body(response);
    }

    /**
     * Formatiert eine benutzerfreundliche Nachricht aus einem ImportLog.
     * 
     * Diese Hilfsmethode erzeugt je nach Import-Status unterschiedliche Nachrichten:
     * - Bei FAILED: zeigt die Fehlermeldung an
     * - Bei COMPLETED: zeigt Statistik mit Erfolg- und Fehlerzahl
     * 
     * @param importLog das ImportLog mit Status und Fehlernachricht
     * @return formatierte Nachricht zur Anzeige in der Response
     */
    private String formatMessage(ImportLog importLog) {
        if (importLog.getStatus() == ImportLog.ImportStatus.FAILED) {
            return "Import failed: " + importLog.getErrorMessage();
        }
        return String.format("Import completed. Success: %d, Failed: %d",
            importLog.getSuccessfulRecords(),
            importLog.getFailedRecords());
    }

    /**
     * Validiert, ob eine Datei ein unterstütztes Import-Format hat.
     * 
     * Diese Methode prüft die Dateiendung gegen die Liste der unterstützten Formate.
     * Gültige Formate sind: .xlsx (Excel 2007+), .xls (Excel 97-2003), .csv (Comma-separated values).
     * Die Prüfung ist case-insensitive.
     * 
     * @param filename der Dateiname (mit oder ohne Pfad) zum Prüfen. Kann null sein.
     * @return true wenn die Dateiendung unterstützt ist, false sonst (auch bei null-Eingabe)
     */
    private boolean isSupportedImportFile(String filename) {
        if (filename == null) {
            return false;
        }
        String normalized = filename.toLowerCase();
        return normalized.endsWith(".xlsx") || normalized.endsWith(".xls") || normalized.endsWith(".csv");
    }
}


