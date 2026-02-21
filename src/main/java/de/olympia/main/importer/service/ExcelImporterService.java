package de.olympia.main.importer.service;

import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.entity.Result;
import de.olympia.main.entity.User;
import de.olympia.main.importer.dto.AthleteImportDto;
import de.olympia.main.importer.dto.CountryImportDto;
import de.olympia.main.importer.dto.ResultImportDto;
import de.olympia.main.importer.entity.ImportDetail;
import de.olympia.main.importer.entity.ImportError;
import de.olympia.main.importer.entity.ImportLog;
import de.olympia.main.importer.exception.InvalidImportDataException;
import de.olympia.main.importer.parser.ExcelParser;
import de.olympia.main.importer.repository.ImportDetailRepository;
import de.olympia.main.importer.repository.ImportErrorRepository;
import de.olympia.main.importer.repository.ImportLogRepository;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
import de.olympia.main.repository.ResultRepository;
import de.olympia.main.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
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

    public ExcelImporterService(
        ExcelParser excelParser,
        CountryRepository countryRepository,
        AthleteRepository athleteRepository,
        ResultRepository resultRepository,
        ImportLogRepository importLogRepository,
        ImportErrorRepository importErrorRepository,
        ImportDetailRepository importDetailRepository,
        Validator validator,
        UserRepository userRepository
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
    }

    /**
     * Import countries from Excel file
     */
    @Transactional
    public ImportLog importCountries(MultipartFile file, Long userId) {
        ImportLog importLog = createImportLog(file.getOriginalFilename(), "COUNTRIES", userId);

        try {
            List<CountryImportDto> countries = excelParser.parseCountries(file);
            importLog.setTotalRecords(countries.size());
            importLogRepository.save(importLog);

            importLog = processCountriesImport(importLog, countries);

            importLog.setStatus(ImportLog.ImportStatus.COMPLETED);
            importLog.setCompletedAt(LocalDateTime.now());

        } catch (IOException e) {
            log.error("Error reading Excel file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Error reading file: " + e.getMessage());
        } catch (InvalidImportDataException e) {
            log.error("Invalid data in import file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage(e.getMessage());
            recordImportError(importLog, e.getRowNumber(), e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Unexpected error: " + e.getMessage());
        }

        return importLogRepository.save(importLog);
    }

    /**
     * Import athletes from Excel file
     */
    @Transactional
    public ImportLog importAthletes(MultipartFile file, Long userId) {
        ImportLog importLog = createImportLog(file.getOriginalFilename(), "ATHLETES", userId);

        try {
            List<AthleteImportDto> athletes = excelParser.parseAthletes(file);
            importLog.setTotalRecords(athletes.size());
            importLogRepository.save(importLog);

            importLog = processAthletesImport(importLog, athletes);

            importLog.setStatus(ImportLog.ImportStatus.COMPLETED);
            importLog.setCompletedAt(LocalDateTime.now());

        } catch (IOException e) {
            log.error("Error reading Excel file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Error reading file: " + e.getMessage());
        } catch (InvalidImportDataException e) {
            log.error("Invalid data in import file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage(e.getMessage());
            recordImportError(importLog, e.getRowNumber(), e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Unexpected error: " + e.getMessage());
        }

        return importLogRepository.save(importLog);
    }

    /**
     * Import results from Excel file
     */
    @Transactional
    public ImportLog importResults(MultipartFile file, Long userId) {
        ImportLog importLog = createImportLog(file.getOriginalFilename(), "RESULTS", userId);

        try {
            List<ResultImportDto> results = excelParser.parseResults(file);
            importLog.setTotalRecords(results.size());
            importLogRepository.save(importLog);

            importLog = processResultsImport(importLog, results, userId);

            importLog.setStatus(ImportLog.ImportStatus.COMPLETED);
            importLog.setCompletedAt(LocalDateTime.now());

        } catch (IOException e) {
            log.error("Error reading Excel file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Error reading file: " + e.getMessage());
        } catch (InvalidImportDataException e) {
            log.error("Invalid data in import file: {}", e.getMessage());
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage(e.getMessage());
            recordImportError(importLog, e.getRowNumber(), e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
        } catch (Exception e) {
            log.error("Unexpected error during import", e);
            importLog.setStatus(ImportLog.ImportStatus.FAILED);
            importLog.setErrorMessage("Unexpected error: " + e.getMessage());
        }

        return importLogRepository.save(importLog);
    }

    /**
     * Process countries import with validation
     */
    private ImportLog processCountriesImport(ImportLog importLog, List<CountryImportDto> countries) {
        int rowNum = 2; // Start from row 2 (after header)

        for (CountryImportDto dto : countries) {
            try {
                // Validate DTO
                Set<ConstraintViolation<CountryImportDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Validation failed");
                    throw new InvalidImportDataException(errorMsg, "VALIDATION_ERROR", rowNum, "countryData");
                }

                // Check if country already exists
                Optional<Country> existing = countryRepository.findByCodeIgnoreCase(dto.getCode());
                if (existing.isPresent()) {
                    log.info("Country {} already exists, skipping", dto.getCode());
                    importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                    recordImportDetail(importLog, "COUNTRY", existing.get().getId(), ImportDetail.ImportAction.SKIP);
                    recordImportError(importLog, rowNum, "DUPLICATE_ENTRY", "Country already exists: " + dto.getCode(), "code", dto.getCode());
                } else {
                    // Create and save new country
                    Country country = new Country();
                    country.setCode(dto.getCode());
                    country.setName(dto.getName());
                    Country saved = countryRepository.save(country);

                    importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                    recordImportDetail(importLog, "COUNTRY", saved.getId(), ImportDetail.ImportAction.INSERT);
                    log.info("Imported country: {}", dto.getCode());
                }

            } catch (InvalidImportDataException e) {
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, e.getRowNumber() != null ? e.getRowNumber() : rowNum,
                    e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception e) {
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, rowNum, "PROCESSING_ERROR", e.getMessage(), null, null);
            }

            rowNum++;
        }

        return importLog;
    }

    /**
     * Process athletes import with validation
     */
    private ImportLog processAthletesImport(ImportLog importLog, List<AthleteImportDto> athletes) {
        int rowNum = 2; // Start from row 2 (after header)

        for (AthleteImportDto dto : athletes) {
            try {
                // Validate DTO
                Set<ConstraintViolation<AthleteImportDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Validation failed");
                    throw new InvalidImportDataException(errorMsg, "VALIDATION_ERROR", rowNum, "athleteData");
                }

                // Resolve country if provided
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

                // Check if athlete already exists
                Optional<Athlete> existing = athleteRepository.findByFirstNameAndLastName(dto.getFirstName(), dto.getLastName());
                if (existing.isPresent()) {
                    log.info("Athlete {} {} already exists, skipping", dto.getFirstName(), dto.getLastName());
                    importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                    recordImportDetail(importLog, "ATHLETE", existing.get().getId(), ImportDetail.ImportAction.SKIP);
                    recordImportError(importLog, rowNum, "DUPLICATE_ENTRY",
                        "Athlete already exists: " + dto.getFirstName() + " " + dto.getLastName(),
                        "firstName,lastName", dto.getFirstName() + " " + dto.getLastName());
                } else {
                    // Create and save new athlete
                    Athlete athlete = new Athlete();
                    athlete.setFirstName(dto.getFirstName());
                    athlete.setLastName(dto.getLastName());
                    athlete.setCountry(countryId);
                    Athlete saved = athleteRepository.save(athlete);

                    importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                    recordImportDetail(importLog, "ATHLETE", saved.getId(), ImportDetail.ImportAction.INSERT);
                    log.info("Imported athlete: {} {}", dto.getFirstName(), dto.getLastName());
                }

            } catch (InvalidImportDataException e) {
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, e.getRowNumber() != null ? e.getRowNumber() : rowNum,
                    e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception e) {
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, rowNum, "PROCESSING_ERROR", e.getMessage(), null, null);
            }

            rowNum++;
        }

        return importLog;
    }

    /**
     * Process results import with validation
     */
    private ImportLog processResultsImport(ImportLog importLog, List<ResultImportDto> results, Long userId) {
        int rowNum = 2; // Start from row 2 (after header)
        final int headerRow = rowNum;

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
                // Validate DTO
                Set<ConstraintViolation<ResultImportDto>> violations = validator.validate(dto);
                if (!violations.isEmpty()) {
                    String errorMsg = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("Validation failed");
                    throw new InvalidImportDataException(errorMsg, "VALIDATION_ERROR", rowNum, "resultData");
                }

                // Find athlete by first and last name
                Optional<Athlete> athlete = athleteRepository.findByFirstNameAndLastName(dto.getAthleteFirstName(), dto.getAthleteLastName());
                if (athlete.isEmpty()) {
                    throw new InvalidImportDataException(
                        "Athlete not found: " + dto.getAthleteFirstName() + " " + dto.getAthleteLastName(),
                        "ATHLETE_NOT_FOUND",
                        rowNum,
                        "athleteName"
                    );
                }

                // Create and save new result
                Result result = new Result();
                result.setAthlete(athlete.get());
                result.setRank(dto.getRank());
                result.setTimeOrPoints(dto.getTimeOrPoints());
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
                result.setCreatedBy(createdBy);
                Result saved = resultRepository.save(result);

                importLog.setSuccessfulRecords(importLog.getSuccessfulRecords() + 1);
                recordImportDetail(importLog, "RESULT", saved.getId(), ImportDetail.ImportAction.INSERT);
                log.info("Imported result for athlete: {} {}", dto.getAthleteFirstName(), dto.getAthleteLastName());

            } catch (InvalidImportDataException e) {
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, e.getRowNumber() != null ? e.getRowNumber() : rowNum,
                    e.getErrorCode(), e.getMessage(), e.getFieldName(), null);
            } catch (Exception e) {
                importLog.setFailedRecords(importLog.getFailedRecords() + 1);
                recordImportError(importLog, rowNum, "PROCESSING_ERROR", e.getMessage(), null, null);
            }

            rowNum++;
        }

        return importLog;
    }

    /**
     * Create initial import log
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
     * Record import error
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
     * Record import detail
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

