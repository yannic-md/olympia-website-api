package de.olympia.main.service;

import de.olympia.main.dto.AthleteImportDto;
import de.olympia.main.dto.CountryImportDto;
import de.olympia.main.dto.ResultImportDto;
import de.olympia.main.entity.*;
import de.olympia.main.importer.exception.InvalidImportDataException;
import de.olympia.main.importer.parser.ExcelParser;
import de.olympia.main.repository.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class ExcelImporterService {

    private final ExcelParser excelParser;
    private final CountryRepository countryRepository;
    private final AthleteRepository athleteRepository;
    private final ResultRepository resultRepository;
    private final ImportLogRepository importLogRepository;
    private final ImportErrorRepository importErrorRepository;
    private final ImportDetailRepository importDetailRepository;
    private final Validator validator;
    private final UserRepository userRepository;
    private final SportsRepository sportsRepository;

    public ExcelImporterService(
        ExcelParser excelParser,
        CountryRepository countryRepository,
        AthleteRepository athleteRepository,
        ResultRepository resultRepository,
        ImportLogRepository importLogRepository,
        ImportErrorRepository importErrorRepository,
        ImportDetailRepository importDetailRepository,
        Validator validator,
        UserRepository userRepository,
        SportsRepository sportsRepository
    ) {
        this.excelParser = excelParser;
        this.countryRepository = countryRepository;
        this.athleteRepository = athleteRepository;
        this.resultRepository = resultRepository;
        this.importLogRepository = importLogRepository;
        this.importErrorRepository = importErrorRepository;
        this.importDetailRepository = importDetailRepository;
        this.validator = validator;
        this.userRepository = userRepository;
        this.sportsRepository = sportsRepository;
    }

    /**
     * Imports countries from an Excel or CSV file.
     * 
     * This is a complete import operation that:
     * 1. Accepts a multipart file (Excel .xlsx or CSV format)
     * 2. Parses the file into CountryImportDto objects via ExcelParser
     * 3. Processes each country with validation, UPSERT logic, and error handling
     * 4. Tracks success/failure counts and detailed error information
     * 5. Updates cache after completion to refresh leaderboard data
     * 6. Returns detailed import log for status monitoring
     *
     * File format requirements:
     * - Excel (.xlsx): First row is header, data starts at row 2
     * - CSV: Same format as Excel
     * - Required columns: code (country code), name (country name)
     * - Optional columns: nameEn, nameDe, nameFr (translations)
     *
     * Processing behavior:
     * - UPSERT logic: Updates country if code already exists, creates new otherwise
     * - Partial failures: Continues importing remaining records even if one fails
     * - Detailed error tracking: Each error is recorded with row number, field, and message
     * - Cache eviction: All leaderboard-related caches are cleared after import
     *
     * Return value:
     * - ImportLog entity with status (COMPLETED or FAILED), timestamps, and counts
     * - If FAILED: errorMessage field contains the root cause
     * - All records have associated ImportDetail and ImportError entries
     *
     * @param file The multipart file to import (Excel or CSV format)
     * @param userId The user ID performing the import (for audit trail)
     * @return ImportLog entity with complete import statistics and audit information
     * @throws IOException (caught and logged) if file cannot be read
     * @throws InvalidImportDataException (caught and logged) if data format is invalid
     */
    @CacheEvict(value = {"v2Leaderboard", "v2Countries", "v2Athletes", "v2Sports"}, allEntries = true)
    @Transactional
    public ImportLog importCountries(MultipartFile file, Long userId) {
        ImportLog importLog = createImportLog(file.getOriginalFilename(), "COUNTRIES", userId);

        try {
            List<CountryImportDto> countries = excelParser.parseCountries(file);
            importLog.setTotalRecords(countries.size());
            importLog = importLogRepository.save(importLog);

            importLog = processCountriesImport(importLog, countries);

            importLog.setStatus(ImportLog.ImportStatus.COMPLETED);
            importLog.setCompletedAt(LocalDateTime.now());
            importLog = importLogRepository.save(importLog);

        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage(), e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Error reading file: " + e.getMessage());
            importLog = importLogRepository.save(importLog);
        } catch (InvalidImportDataException e) {
            log.error("Invalid data in import file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage(e.getMessage());
            try {
                recordImportError(importLog, e.getRowNumber(), e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception ex) {
                log.warn("Failed to record import error", ex);
            }
            importLog = importLogRepository.save(importLog);
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Unexpected error: " + e.getMessage());
            importLog = importLogRepository.save(importLog);
        }

        return importLog;
    }

    /**
     * Imports athletes from an Excel or CSV file.
     * 
     * This is a complete import operation that:
     * 1. Accepts a multipart file (Excel .xlsx or CSV format)
     * 2. Parses the file into AthleteImportDto objects via ExcelParser
     * 3. Resolves country references by country code (case-insensitive)
     * 4. Processes each athlete with validation, UPSERT logic, and error handling
     * 5. Tracks success/failure counts and detailed error information
     * 6. Updates cache after completion to refresh leaderboard data
     * 7. Returns detailed import log for status monitoring
     *
     * File format requirements:
     * - Excel (.xlsx): First row is header, data starts at row 2
     * - CSV: Same format as Excel
     * - Required columns: firstName, lastName
     * - Optional columns: countryCode (reference to existing country)
     *
     * Processing behavior:
     * - UPSERT logic: Updates athlete if name already exists, creates new otherwise
     * - Country resolution: Looks up country by code (case-insensitive); fails if not found
     * - Partial failures: Continues importing remaining records even if one fails
     * - Athletes can be created without a country assignment
     * - Detailed error tracking: Each error is recorded with row number, field, and message
     * - Cache eviction: All leaderboard-related caches are cleared after import
     *
     * Return value:
     * - ImportLog entity with status (COMPLETED or FAILED), timestamps, and counts
     * - If FAILED: errorMessage field contains the root cause
     * - All records have associated ImportDetail and ImportError entries
     *
     * @param file The multipart file to import (Excel or CSV format)
     * @param userId The user ID performing the import (for audit trail)
     * @return ImportLog entity with complete import statistics and audit information
     * @throws IOException (caught and logged) if file cannot be read
     * @throws InvalidImportDataException (caught and logged) if data format is invalid
     */
    @CacheEvict(value = {"v2Leaderboard", "v2Countries", "v2Athletes", "v2Sports"}, allEntries = true)
    @Transactional
    public ImportLog importAthletes(MultipartFile file, Long userId) {
        ImportLog importLog = createImportLog(file.getOriginalFilename(), "ATHLETES", userId);

        try {
            List<AthleteImportDto> athletes = excelParser.parseAthletes(file);
            importLog.setTotalRecords(athletes.size());
            importLog = importLogRepository.save(importLog);

            importLog = processAthletesImport(importLog, athletes);

            importLog.setStatus(ImportLog.ImportStatus.COMPLETED);
            importLog.setCompletedAt(LocalDateTime.now());
            importLog = importLogRepository.save(importLog);

        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage(), e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Error reading file: " + e.getMessage());
            importLog = importLogRepository.save(importLog);
        } catch (InvalidImportDataException e) {
            log.error("Invalid data in import file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage(e.getMessage());
            try {
                recordImportError(importLog, e.getRowNumber(), e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception ex) {
                log.warn("Failed to record import error", ex);
            }
            importLog = importLogRepository.save(importLog);
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Unexpected error: " + e.getMessage());
            importLog = importLogRepository.save(importLog);
        }

        return importLog;
    }

    /**
     * Imports results from an Excel or CSV file.
     * 
     * This is the most complex and powerful import operation, enabling:
     * 1. Complete re-initialization of leaderboard data from external sources
     * 2. Batch updates to all results with atomic transaction support
     * 3. Detailed tracking and error reporting for data quality audits
     *
     * The method:
     * 1. Accepts a multipart file (Excel .xlsx or CSV format)
     * 2. Parses the file into ResultImportDto objects via ExcelParser
     * 3. Resolves athlete and sport references by name/ID
     * 4. Processes each result with validation, UPSERT logic, and error handling
     * 5. Converts and validates enum values (medal, scoreType)
     * 6. Tracks success/failure counts and detailed error information
     * 7. Records the importing user for audit trail
     * 8. Updates cache after completion to refresh leaderboard data
     * 9. Returns detailed import log for status monitoring
     *
     * File format requirements:
     * - Excel (.xlsx): First row is header, data starts at row 2
     * - CSV: Same format as Excel
     * - Required columns: athleteFirstName, athleteLastName, sport
     * - Optional columns: rank, timeOrPoints, medal (GOLD/SILVER/BRONZE), scoreType (PTS/WINS/TIME)
     *
     * Data resolution:
     * - Athletes: Resolved by exact match of firstName + lastName (case-sensitive)
     * - Sports: Resolved by name (case-insensitive)
     * - Missing references fail the record but don't stop the entire import
     * - Enum conversions: medal and scoreType are validated and converted to uppercase
     *
     * Processing behavior:
     * - UPSERT logic: Updates result if (sport, athlete) pair exists, creates new otherwise
     * - Partial failures: Continues importing remaining records even if one fails
     * - Detailed error tracking: Each error is recorded with row number, field, and message
     * - User association: The importing user is recorded in created_by for audit trail
     * - Cache eviction: All leaderboard-related caches are cleared after import
     *
     * Return value:
     * - ImportLog entity with status (COMPLETED or FAILED), timestamps, and counts
     * - If FAILED: errorMessage field contains the root cause
     * - All records have associated ImportDetail and ImportError entries
     *
     * @param file The multipart file to import (Excel or CSV format)
     * @param userId The user ID performing the import (for audit trail and created_by field)
     * @return ImportLog entity with complete import statistics and audit information
     * @throws IOException (caught and logged) if file cannot be read
     * @throws InvalidImportDataException (caught and logged) if data format is invalid
     */
    @CacheEvict(value = {"v2Leaderboard", "v2Countries", "v2Athletes", "v2Sports"}, allEntries = true)
    @Transactional
    public ImportLog importResults(MultipartFile file, Long userId) {
        ImportLog importLog = createImportLog(file.getOriginalFilename(), "RESULTS", userId);

        try {
            List<ResultImportDto> results = excelParser.parseResults(file);
            importLog.setTotalRecords(results.size());
            importLog = importLogRepository.save(importLog);

            importLog = processResultsImport(importLog, results, userId);

            importLog.setStatus(ImportLog.ImportStatus.COMPLETED);
            importLog.setCompletedAt(LocalDateTime.now());
            importLog = importLogRepository.save(importLog);

        } catch (IOException e) {
            log.error("Error reading file: {}", e.getMessage(), e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Error reading file: " + e.getMessage());
            importLog = importLogRepository.save(importLog);
        } catch (InvalidImportDataException e) {
            log.error("Invalid data in import file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage(e.getMessage());
            try {
                recordImportError(importLog, e.getRowNumber(), e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception ex) {
                log.warn("Failed to record import error", ex);
            }
            importLog = importLogRepository.save(importLog);
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Unexpected error: " + e.getMessage());
            importLog = importLogRepository.save(importLog);
        }

        return importLog;
    }

    /**
     * Processes the import of countries from a parsed DTO list.
     * 
     * This method:
     * 1. Validates each country DTO against Bean Validation constraints
     * 2. Performs UPSERT logic: updates existing countries by code, creates new ones
     * 3. Tracks success/failure counters for the import log
     * 4. Records import details and errors for audit trail
     * 5. Handles exceptions gracefully, continuing with next records
     *
     * @param importLog The import log entity tracking this batch import
     * @param countries The list of CountryImportDto objects parsed from the file
     * @return The updated ImportLog with success/failure counts and all recorded details
     */
    private ImportLog processCountriesImport(ImportLog importLog, List<CountryImportDto> countries) {
        int rowNum = 2; // Start from row 2 (after header)

        for (CountryImportDto dto : countries) {
            try {
                // Validate DTO against Bean Validation constraints (e.g. @NotBlank, @Length)
                Set<ConstraintViolation<CountryImportDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Validation failed");
                    throw new InvalidImportDataException(errorMsg, "VALIDATION_ERROR", rowNum, "countryData");
                }

                // Check if country with this code already exists (case-insensitive)
                Optional<Country> existing = countryRepository.findByCodeIgnoreCase(dto.getCode());
                Country country;
                ImportDetail.ImportAction action;
                
                if (existing.isPresent()) {
                    // UPSERT path: update existing country
                    country = existing.get();
                    country.setName(dto.getName());
                    country.setNameEn(dto.getNameEn());
                    country.setNameDe(dto.getNameDe());
                    country.setNameFr(dto.getNameFr());
                    country = countryRepository.save(country);
                    action = ImportDetail.ImportAction.UPDATE;
                    importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                    log.info("Updated country: {}", dto.getCode());
                } else {
                    // INSERT path: create new country
                    country = new Country();
                    country.setCode(dto.getCode());
                    country.setName(dto.getName());
                    country.setNameEn(dto.getNameEn());
                    country.setNameDe(dto.getNameDe());
                    country.setNameFr(dto.getNameFr());
                    country = countryRepository.save(country);
                    action = ImportDetail.ImportAction.INSERT;
                    importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                    log.info("Imported country: {}", dto.getCode());
                }
                
                // Record audit trail for this successful import
                recordImportDetail(importLog, "COUNTRY", country.getId(), action);

            } catch (InvalidImportDataException e) {
                // Handle validation/data errors: log failure and record error detail
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, e.getRowNumber() != null ? e.getRowNumber() : rowNum,
                    e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception e) {
                // Handle unexpected errors: log failure and record generic error
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, rowNum, "PROCESSING_ERROR", e.getMessage(), null, null);
            }

            rowNum++;
        }

        return importLog;
    }

    /**
     * Processes the import of athletes from a parsed DTO list.
     * 
     * This method:
     * 1. Validates each athlete DTO against Bean Validation constraints
     * 2. Resolves country references by code (case-insensitive lookup)
     * 3. Performs UPSERT logic: updates athletes by name, creates new ones
     * 4. Links athletes to their countries if found
     * 5. Tracks success/failure counters for the import log
     * 6. Records import details and errors for audit trail
     * 7. Continues gracefully on per-record errors, does not abort entire import
     *
     * @param importLog The import log entity tracking this batch import
     * @param athletes The list of AthleteImportDto objects parsed from the file
     * @return The updated ImportLog with success/failure counts and all recorded details
     */
    private ImportLog processAthletesImport(ImportLog importLog, List<AthleteImportDto> athletes) {
        int rowNum = 2; // Start from row 2 (after header)

        for (AthleteImportDto dto : athletes) {
            try {
                // Validate DTO against Bean Validation constraints (e.g. @NotBlank)
                Set<ConstraintViolation<AthleteImportDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Validation failed");
                    throw new InvalidImportDataException(errorMsg, "VALIDATION_ERROR", rowNum, "athleteData");
                }

                // Resolve country reference by country code (case-insensitive)
                // May be null if no country is specified in the import file
                Country countryId = null;
                if (dto.getCountryCode() != null && !dto.getCountryCode().isEmpty()) {
                    Optional<Country> country = countryRepository.findByCodeIgnoreCase(dto.getCountryCode());
                    if (country.isEmpty()) {
                        throw new InvalidImportDataException(
                            "Country not found: " + dto.getCountryCode(),
                            "COUNTRY_NOT_FOUND",
                            rowNum,
                            "countryCode"
                        );
                    }
                    countryId = country.get();
                }

                // Check if athlete already exists by first and last name (exact match)
                Optional<Athlete> existing = athleteRepository.findByFirstNameAndLastName(dto.getFirstName(), dto.getLastName());
                Athlete athlete;
                ImportDetail.ImportAction action;
                
                if (existing.isPresent()) {
                    // UPSERT path: update existing athlete (refreshes country if provided)
                    athlete = existing.get();
                    athlete.setCountry(countryId);
                    athlete = athleteRepository.save(athlete);
                    action = ImportDetail.ImportAction.UPDATE;
                    importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                    log.info("Updated athlete: {} {}", dto.getFirstName(), dto.getLastName());
                } else {
                    // INSERT path: create new athlete
                    athlete = new Athlete();
                    athlete.setFirstName(dto.getFirstName());
                    athlete.setLastName(dto.getLastName());
                    athlete.setCountry(countryId);
                    athlete = athleteRepository.save(athlete);
                    action = ImportDetail.ImportAction.INSERT;
                    importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                    log.info("Imported athlete: {} {}", dto.getFirstName(), dto.getLastName());
                }
                
                // Record audit trail for this successful import
                recordImportDetail(importLog, "ATHLETE", athlete.getId(), action);

            } catch (InvalidImportDataException e) {
                // Handle validation/data errors: log failure and record error detail
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, e.getRowNumber() != null ? e.getRowNumber() : rowNum,
                    e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception e) {
                // Handle unexpected errors: log failure and record generic error
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, rowNum, "PROCESSING_ERROR", e.getMessage(), null, null);
            }

            rowNum++;
        }

        return importLog;
    }

    /**
     * Processes the import of results from a parsed DTO list.
     * 
     * This is the most complex import operation, performing the following steps:
     * 1. Validates each result DTO against Bean Validation constraints
     * 2. Resolves athlete and sport references by name/ID (throws error if not found)
     * 3. Validates enum values (medal, scoreType) and converts them properly
     * 4. Performs UPSERT logic: updates existing results for (sport, athlete) pairs or creates new ones
     * 5. Sets the created_by user from the import session userId
     * 6. Tracks success/failure counters for the import log
     * 7. Records import details and errors for audit trail
     * 8. Continues gracefully on per-record errors, does not abort entire import
     *
     * @param importLog The import log entity tracking this batch import
     * @param results The list of ResultImportDto objects parsed from the file
     * @param userId The user ID performing the import (used for created_by field)
     * @return The updated ImportLog with success/failure counts and all recorded details
     */
    private ImportLog processResultsImport(ImportLog importLog, List<ResultImportDto> results, Long userId) {
        int rowNum = 2; // Start from row 2 (after header)
        final int headerRow = rowNum;

        // Resolve the user performing the import
        User createdBy = null;
        if (userId != null) {
            createdBy = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidImportDataException(
                    "User not found: " + userId,
                    "USER_NOT_FOUND",
                    headerRow,
                    "createdBy"
                ));
        }

        for (ResultImportDto dto : results) {
            try {
                // Validate DTO against Bean Validation constraints
                Set<ConstraintViolation<ResultImportDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Validation failed");
                    throw new InvalidImportDataException(errorMsg, "VALIDATION_ERROR", rowNum, "resultData");
                }

                // Resolve athlete reference by first and last name (exact match)
                // This allows athletes to be imported separately or in the same file
                Optional<Athlete> athlete = athleteRepository.findByFirstNameAndLastName(dto.getAthleteFirstName(), dto.getAthleteLastName());
                if (athlete.isEmpty()) {
                    throw new InvalidImportDataException(
                        "Athlete not found: " + dto.getAthleteFirstName() + " " + dto.getAthleteLastName(),
                        "ATHLETE_NOT_FOUND",
                        rowNum,
                        "athleteName"
                    );
                }

                // Resolve sport reference by name (case-insensitive)
                Optional<Sports> sport = null;
                if (dto.getSport() != null && !dto.getSport().isEmpty()) {
                    sport = sportsRepository.findByNameIgnoreCase(dto.getSport());
                    if (sport.isEmpty()) {
                        throw new InvalidImportDataException(
                            "Sport not found: " + dto.getSport(),
                            "SPORT_NOT_FOUND",
                            rowNum,
                            "sport"
                        );
                    }
                } else {
                    throw new InvalidImportDataException(
                        "Sport is required",
                        "MISSING_REQUIRED_FIELD",
                        rowNum,
                        "sport"
                    );
                }

                // Try to find existing result for this (sport, athlete) pair
                // This enables UPSERT: if exists, we update; otherwise we insert
                Optional<Result> existingResult = resultRepository.findBySportsIdAndAthleteId(
                    sport.get().getId(), 
                    athlete.get().getId()
                );
                
                Result result;
                ImportDetail.ImportAction action;
                
                if (existingResult.isPresent()) {
                    // UPSERT path: update existing result
                    result = existingResult.get();
                    action = ImportDetail.ImportAction.UPDATE;
                } else {
                    // INSERT path: create new result
                    result = new Result();
                    action = ImportDetail.ImportAction.INSERT;
                }
                
                // Set result properties
                result.setAthlete(athlete.get());
                result.setSports(sport.get());
                result.setRank(dto.getRank());
                result.setTimeOrPoints(dto.getTimeOrPoints());
                
                // Parse and validate scoreType enum (PTS, WINS, TIME)
                if (dto.getScoreType() != null) {
                    try {
                        result.setScoreType(Result.ScoreType.valueOf(dto.getScoreType().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new InvalidImportDataException(
                            "Invalid score type value: " + dto.getScoreType(),
                            "INVALID_SCORE_TYPE",
                            rowNum,
                            "scoreType"
                        );
                    }
                }
                
                // Parse and validate medal enum (GOLD, SILVER, BRONZE)
                if (dto.getMedal() != null) {
                    try {
                        result.setMedal(Result.Medal.valueOf(dto.getMedal().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new InvalidImportDataException(
                            "Invalid medal value: " + dto.getMedal(),
                            "INVALID_MEDAL",
                            rowNum,
                            "medal"
                        );
                    }
                }
                
                // Set created_by user (only on INSERT, not on UPDATE per best practices)
                result.setCreatedBy(createdBy);
                
                // Persist the result
                Result saved = resultRepository.save(result);

                // Update import log counters and audit trail
                importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                recordImportDetail(importLog, "RESULT", saved.getId(), action);
                
                if (action == ImportDetail.ImportAction.UPDATE) {
                    log.info("Updated result for athlete: {} {}", dto.getAthleteFirstName(), dto.getAthleteLastName());
                } else {
                    log.info("Imported result for athlete: {} {}", dto.getAthleteFirstName(), dto.getAthleteLastName());
                }

            } catch (InvalidImportDataException e) {
                // Handle validation/data errors: log failure and record error detail
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, e.getRowNumber() != null ? e.getRowNumber() : rowNum,
                    e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception e) {
                // Handle unexpected errors: log failure and record generic error
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, rowNum, "PROCESSING_ERROR", e.getMessage(), null, null);
            }

            rowNum++;
        }

        return importLog;
    }

    /**
     * Creates and initializes a new ImportLog entity for tracking a batch import operation.
     * 
     * The created log is not yet persisted to the database; the caller must save it.
     * All timestamps and status counters will be set during the import process.
     *
     * @param filename The original filename of the imported file (for audit trail)
     * @param importType The type of import: "COUNTRIES", "ATHLETES", or "RESULTS"
     * @param userId The ID of the user performing the import (for audit trail)
     * @return A new ImportLog entity with initial status IN_PROGRESS
     */
    private ImportLog createImportLog(String filename, String importType, Long userId) {
        ImportLog importLog = new ImportLog();
        importLog.setFilename(filename);
        importLog.setImportType(importType);
        importLog.setImportedBy(userId);
        importLog.setStatus(ImportLog.ImportStatus.IN_PROGRESS);
        return importLog;
    }

    /**
     * Records a detailed error for a failed import record.
     * 
     * This method persists an ImportError entity to the database for audit trail
     * and error analysis. Each error is linked to the parent import session.
     * Errors can be categorized by error code for better error reporting and debugging.
     *
     * @param importLog The parent import log this error belongs to
     * @param rowNumber The row number in the source file where the error occurred (1-based)
     * @param errorCode A machine-readable error category (e.g. "VALIDATION_ERROR", "NOT_FOUND", "PROCESSING_ERROR")
     * @param errorMessage A human-readable description of the error
     * @param fieldName The optional field name that caused the error (e.g. "countryCode", "athleteName")
     * @param fieldValue The optional problematic value that was in the field
     */
    private void recordImportError(ImportLog importLog, Integer rowNumber, String errorCode,
                                   String errorMessage, String fieldName, String fieldValue) {
        ImportError error = new ImportError();
        error.setImportLogId(importLog.getId());
        error.setRowNumber(rowNumber != null ? rowNumber : -1);
        error.setErrorCode(errorCode);
        error.setErrorMessage(errorMessage);
        error.setFieldName(fieldName);
        error.setFieldValue(fieldValue);
        importErrorRepository.save(error);
    }

    /**
     * Records a successful import detail for audit trail and analytics.
     * 
     * This method persists an ImportDetail entity to track which entities were
     * imported/updated and which action was performed on each one.
     * Allows detailed reporting on what was changed in each import session.
     *
     * @param importLog The parent import log this detail belongs to
     * @param entityType The type of entity imported: "COUNTRY", "ATHLETE", or "RESULT"
     * @param entityId The database ID of the newly created or updated entity
     * @param action The action performed: INSERT or UPDATE
     */
    private void recordImportDetail(ImportLog importLog, String entityType, Long entityId, ImportDetail.ImportAction action) {
        ImportDetail detail = new ImportDetail();
        detail.setImportLogId(importLog.getId());
        detail.setEntityType(entityType);
        detail.setEntityId(entityId);
        detail.setAction(action);
        importDetailRepository.save(detail);
    }
}




