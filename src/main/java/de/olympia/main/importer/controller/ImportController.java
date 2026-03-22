package de.olympia.main.importer.controller;

import de.olympia.main.importer.dto.ImportResponseDto;
import de.olympia.main.importer.entity.ImportLog;
import de.olympia.main.importer.exception.ImportException;
import de.olympia.main.importer.repository.ImportErrorRepository;
import de.olympia.main.importer.service.ExcelImporterService;
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
     * Import countries from Excel file
     * Admin-only endpoint
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

        if (!isValidFile(file.getOriginalFilename())) {
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
     * Import athletes from Excel file
     * Admin-only endpoint
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

        if (!isValidFile(file.getOriginalFilename())) {
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
     * Import results from Excel file
     * Admin-only endpoint
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

        if (!isValidFile(file.getOriginalFilename())) {
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
     * Build response DTO from ImportLog
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
     * Format response message
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
     * Validate if file is Excel or CSV format
     */
    private boolean isValidFile(String filename) {
        if (filename == null) {
            return false;
        }
        return filename.endsWith(".xlsx") || filename.endsWith(".xls") || filename.endsWith(".csv");
    }
}

