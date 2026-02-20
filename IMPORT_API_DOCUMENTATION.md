# Excel Import API Documentation

## Overview
The Excel Importer module provides functionality to import competition data (countries, athletes, and results) from Excel files (.xlsx or .xls format).

## Features
- ✅ Excel/XLS file parsing using Apache POI
- ✅ API endpoints for file uploads
- ✅ Data validation with detailed error reporting
- ✅ Error handling for invalid data with row-level error tracking
- ✅ Transaction management for bulk imports (automatic rollback on critical errors)
- ✅ Import log/history tracking in database
- ✅ Duplicate detection (skip existing records)
- ✅ Admin-only access restriction (ready for Spring Security integration)

## API Endpoints

### 1. Import Countries
**Endpoint:** `POST /api/imports/countries`

**Parameters:**
- `file` (required): Excel file with country data
- `userId` (optional): User ID performing the import (defaults to 1 for admin)

**Excel File Format:**
The Excel file should have the following columns in the first sheet:

| Column A | Column B |
|----------|----------|
| code     | name     |
| USA      | United States |
| GER      | Germany |
| FRA      | France |

**Example cURL:**
```bash
curl -X POST \
  -F "file=@countries.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/countries
```

**Response Example (Success):**
```json
{
  "importLogId": 1,
  "status": "COMPLETED",
  "importType": "COUNTRIES",
  "filename": "countries.xlsx",
  "totalRecords": 3,
  "successfulRecords": 3,
  "failedRecords": 0,
  "message": "Import completed. Success: 3, Failed: 0"
}
```

**Response Example (With Errors):**
```json
{
  "importLogId": 2,
  "status": "COMPLETED",
  "importType": "COUNTRIES",
  "filename": "countries_with_errors.xlsx",
  "totalRecords": 4,
  "successfulRecords": 2,
  "failedRecords": 2,
  "message": "Import completed. Success: 2, Failed: 2",
  "errors": [
    {
      "rowNumber": 3,
      "errorCode": "DUPLICATE_ENTRY",
      "errorMessage": "Country already exists: USA",
      "fieldName": "code",
      "fieldValue": "USA"
    },
    {
      "rowNumber": 5,
      "errorCode": "MISSING_REQUIRED_FIELD",
      "errorMessage": "Required field is empty",
      "fieldName": "name",
      "fieldValue": null
    }
  ]
}
```

---

### 2. Import Athletes
**Endpoint:** `POST /api/imports/athletes`

**Parameters:**
- `file` (required): Excel file with athlete data
- `userId` (optional): User ID performing the import (defaults to 1 for admin)

**Excel File Format:**
The Excel file should have the following columns in the first sheet:

| Column A   | Column B   | Column C | Column D |
|------------|------------|----------|----------|
| firstName  | lastName   | countryCode | gender |
| Katie      | Ledecky    | USA      | F      |
| Max        | Mustermann | GER      | M      |
| Claire     | Dupont     | FRA      | F      |

Note: `countryCode` should reference an existing country code. `gender` is optional (M, F, or D).

**Example cURL:**
```bash
curl -X POST \
  -F "file=@athletes.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/athletes
```

**Response Example:**
```json
{
  "importLogId": 3,
  "status": "COMPLETED",
  "importType": "ATHLETES",
  "filename": "athletes.xlsx",
  "totalRecords": 3,
  "successfulRecords": 3,
  "failedRecords": 0,
  "message": "Import completed. Success: 3, Failed: 0"
}
```

---

### 3. Import Results
**Endpoint:** `POST /api/imports/results`

**Parameters:**
- `file` (required): Excel file with result data
- `userId` (optional): User ID performing the import (defaults to 1 for admin)

**Excel File Format:**
The Excel file should have the following columns in the first sheet:

| Column A | Column B    | Column C | Column D       | Column E |
|----------|-------------|----------|----------------|----------|
| athleteFirstName | athleteLastName | rank | timeOrPoints | medal |
| Katie    | Ledecky     | 1        | 3:59.34        | GOLD   |
| Max      | Mustermann  | 2        | 4:01.12        | SILVER |
| Claire   | Dupont      | 3        | 12.34          |        |

Note: `rank` is required. `timeOrPoints` and `medal` are optional. Medal values: GOLD, SILVER, BRONZE.

**Example cURL:**
```bash
curl -X POST \
  -F "file=@results.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/results
```

**Response Example:**
```json
{
  "importLogId": 4,
  "status": "COMPLETED",
  "importType": "RESULTS",
  "filename": "results.xlsx",
  "totalRecords": 3,
  "successfulRecords": 3,
  "failedRecords": 0,
  "message": "Import completed. Success: 3, Failed: 0"
}
```

---

## Error Codes

| Error Code | Description | Example |
|-----------|-------------|---------|
| `EMPTY_SHEET` | No sheets found in Excel file | File is empty or corrupted |
| `UNSUPPORTED_FORMAT` | File is not .xlsx or .xls format | Uploaded .csv or .pdf |
| `INVALID_FILE` | File has no name | Malformed file upload |
| `MISSING_REQUIRED_FIELD` | Required column is empty | Missing country code |
| `INVALID_CELL_TYPE` | Cell value is not expected type | String in numeric field |
| `INVALID_NUMBER_FORMAT` | Cannot parse number | "abc" in rank field |
| `VALIDATION_ERROR` | DTO validation failed | Blank name field |
| `DUPLICATE_ENTRY` | Record already exists | Country code already in DB |
| `COUNTRY_NOT_FOUND` | Referenced country doesn't exist | countryCode="XXX" not found |
| `ATHLETE_NOT_FOUND` | Referenced athlete doesn't exist | Athlete name not in DB |
| `INVALID_MEDAL` | Invalid medal value | medal="PLATINUM" |
| `PROCESSING_ERROR` | Unexpected error during import | Database error |

---

## Database Schema

### import_logs
Tracks the overall import session.

```sql
CREATE TABLE import_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255) NOT NULL,
    import_type VARCHAR(50) NOT NULL,  -- COUNTRIES, ATHLETES, RESULTS
    total_records INT DEFAULT 0,
    successful_records INT DEFAULT 0,
    failed_records INT DEFAULT 0,
    status ENUM('PENDING','IN_PROGRESS','COMPLETED','FAILED'),
    imported_by BIGINT,
    imported_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    error_message TEXT NULL,
    FOREIGN KEY (imported_by) REFERENCES users(id) ON DELETE SET NULL
);
```

### import_errors
Tracks row-level errors during import.

```sql
CREATE TABLE import_errors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    import_log_id BIGINT NOT NULL,
    row_number INT NOT NULL,
    error_code VARCHAR(50) NOT NULL,
    error_message TEXT NOT NULL,
    field_name VARCHAR(100) NULL,
    field_value VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (import_log_id) REFERENCES import_logs(id) ON DELETE CASCADE
);
```

### import_details
Tracks what was imported (INSERT, UPDATE, SKIP).

```sql
CREATE TABLE import_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    import_log_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,  -- COUNTRY, ATHLETE, RESULT
    entity_id BIGINT NULL,
    action ENUM('INSERT','UPDATE','SKIP'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (import_log_id) REFERENCES import_logs(id) ON DELETE CASCADE
);
```

---

## Implementation Notes

1. **Transaction Management:**
   - Each import method is wrapped in a `@Transactional` annotation
   - Automatic rollback on critical errors
   - Individual record failures don't trigger full rollback

2. **Security:**
   - Currently using a default userId parameter
   - Ready for Spring Security integration: Replace `userId` parameter with `@AuthenticationPrincipal`
   - Add `@PreAuthorize("hasRole('ADMIN')")` to endpoints when Spring Security is enabled

3. **Validation:**
   - Bean Validation (Jakarta Validation API) for DTO validation
   - Apache POI for Excel parsing
   - Custom validation for duplicate detection and foreign key constraints

4. **File Format Support:**
   - `.xlsx` (Office Open XML) - Modern Excel format
   - `.xls` (Compound Document Format) - Legacy Excel format

---

## Configuration

In `application.properties`:
```properties
spring.application.name=main
spring.datasource.url=jdbc:mariadb://localhost:3306/olympia?createDatabaseIfNotExist=true
spring.datasource.username=user
spring.datasource.password=secret
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
```

---

## Dependencies

```gradle
implementation("org.apache.poi:poi:5.2.5")
implementation("org.apache.poi:poi-ooxml:5.2.5")
implementation("org.apache.commons:commons-csv:1.10.0")
implementation("org.springframework.boot:spring-boot-starter-validation")
```

---

## Future Enhancements

1. ✅ CSV support (already added commons-csv dependency)
2. Support for batch uploads of multiple files
3. Preview mode to validate data without importing
4. Scheduled imports from external sources
5. Export current data to Excel
6. Conflict resolution strategies (merge, update, skip)
7. Role-based permission control for different import types
8. Webhook notifications on import completion

---

## Testing

### Manual Testing with cURL

**Create a test Excel file first:**
```bash
# Using LibreOffice/Excel, create countries.xlsx with:
# | code | name         |
# | USA  | United States|
# | GER  | Germany      |
# | FRA  | France       |

curl -X POST \
  -H "Content-Type: multipart/form-data" \
  -F "file=@countries.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/countries
```

---

## Troubleshooting

**Issue:** `Unsupported file format`
- **Cause:** File is not .xlsx or .xls
- **Solution:** Convert to Excel format

**Issue:** `Country not found` error for athletes
- **Cause:** Referenced country code doesn't exist
- **Solution:** Import countries first, ensure correct spelling/case

**Issue:** `Athlete not found` error for results
- **Cause:** Athlete name doesn't match exactly
- **Solution:** Verify athlete names are imported first and match exactly (case-sensitive)

**Issue:** Import seems slow with large files
- **Cause:** File is too large or database is slow
- **Solution:** Split into smaller files or optimize database indexes


