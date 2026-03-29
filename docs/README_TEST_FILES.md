# Test Files for Import Functionality

## Overview
These test files enable validation of import functionality with **multilingual name support**. For each import type there are exactly **3 files**: one working, one broken, and one with different data type.

**Total: 9 Files** (CSV + XLSX for each category)

## Available Test Files (9 total)

### 1. Countries - 3 Files

#### `countries_test.xlsx` - ✅ Working (Excel Format)
- Format: Modern `.xlsx` (Excel 2007+)
- 10 valid countries with multilingual names
- **Columns**: code, name, nameEn, nameDe, nameFr
- **Usage**: Main file for successful imports

**Example:**
```
us,United States,United States,Vereinigte Staaten,États-Unis
de,Germany,Germany,Deutschland,Allemagne
fr,France,France,Frankreich,France
```

#### `countries_broken.csv` - ❌ Broken (CSV Format)
- Format: CSV (text format)
- **Errors**: Contains invalid data (e.g. invalid codes, missing required fields)
- **Usage**: Test error handling — frontend shows generic error message

#### `countries_test.csv` - ✅ Working (CSV Format)
- Format: CSV (text format)
- 10 valid countries with multilingual names (identical to XLSX)
- **Columns**: code, name, nameEn, nameDe, nameFr
- **Usage**: Test CSV import and format variant

---

### 2. Athletes - 3 Files

#### `athletes_test.xlsx` - ✅ Working (Excel Format)
- Format: Modern `.xlsx` (Excel 2007+)
- 10 athletes with valid data
- Correct headers: `firstName`, `lastName`, `countryCode`
- **Usage**: Main file for successful imports

#### `athletes_broken.csv` - ❌ Broken (CSV Format)
- Format: CSV
- **Errors**: Contains invalid data (e.g. missing required fields, invalid country codes)
- **Usage**: Test error handling — frontend shows generic error message

#### `athletes_test.csv` - ✅ Working (CSV Format)
- Format: CSV
- 10 athletes with valid data (identical to XLSX)
- Correct headers: `firstName`, `lastName`, `countryCode`
- **Usage**: Test CSV import

---

### 3. Results - 3 Files

#### `results_test.xlsx` - ✅ Working (Excel Format)
- Format: Modern `.xlsx` (Excel 2007+)
- 10 results with valid data
- Correct headers: `athleteFirstName`, `athleteLastName`, `rank`, `timeOrPoints`, `scoreType`, `medal`
- **Usage**: Main file for successful imports

#### `results_broken.csv` - ❌ Broken (CSV Format)
- Format: CSV
- **Errors**: Contains invalid data (e.g. non-existent athletes, invalid values)
- **Usage**: Test error handling — frontend shows generic error message

#### `results_test.csv` - ✅ Working (CSV Format)
- Format: CSV
- 10 results with valid data (identical to XLSX)
- Correct headers: `athleteFirstName`, `athleteLastName`, `rank`, `timeOrPoints`, `scoreType`, `medal`
- **Usage**: Test CSV import

---

## 📊 File Overview

| Category | Working | Broken | Alternative Format |
|----------|---------|--------|-------------------|
| **Countries** | `countries_test.xlsx` | `countries_broken.csv` | `countries_test.csv` |
| **Athletes** | `athletes_test.xlsx` | `athletes_broken.csv` | `athletes_test.csv` |
| **Results** | `results_test.xlsx` | `results_broken.csv` | `results_test.csv` |

---

## 🌍 Multilingual Name Support (Countries)

The country import now supports **multilingual names**. This enables:
- ✅ Storage of country names in English, German and French
- ✅ Flexible UI (users see their local language name instead of just English)
- ✅ Optionale Felder (nameEn, nameDe, nameFr) können leer gelassen werden

**CSV-Format für Countries:**
```csv
code,name,nameEn,nameDe,nameFr
us,United States,United States,Vereinigte Staaten,États-Unis
de,Germany,Germany,Deutschland,Allemagne
fr,France,France,Frankreich,France
```

---

## 🟢 Quick Start - Test Imports

### Successful Imports (XLSX - recommended)
```bash
# Countries
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:admin -F "file=@countries_test.xlsx"

# Athletes
curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:admin -F "file=@athletes_test.xlsx"

# Results
curl -X POST http://localhost:8080/api/imports/results \
  -u admin:admin -F "file=@results_test.xlsx"
```

### Test Error Handling
```bash
# Countries with errors
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:admin -F "file=@countries_broken.csv"

# Athletes with errors
curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:admin -F "file=@athletes_broken.csv"

# Results with errors
curl -X POST http://localhost:8080/api/imports/results \
  -u admin:admin -F "file=@results_broken.csv"
```

### Test CSV Format
```bash
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:admin -F "file=@countries_test.csv"

curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:admin -F "file=@athletes_test.csv"

curl -X POST http://localhost:8080/api/imports/results \
  -u admin:admin -F "file=@results_test.csv"
```

---

## Test Workflow (Recommended)

1. **Import Countries** → `countries_test.xlsx` or `countries_test.csv`
2. **Import Athletes** → `athletes_test.xlsx` or `athletes_test.csv`
3. **Import Results** → `results_test.xlsx` or `results_test.csv`

**Note**: All country codes must exist before importing athletes!

---

## Format Specification

### CSV Files
- **Separator:** Comma (`,`)
- **Encoding:** UTF-8
- **Line Break:** LF (`\n`)
- **Header:** Line 1 (not imported)

### Excel Files (`.xlsx`)
- **Format:** Modern Excel 2007+
- **Encoding:** UTF-8
- **Sheets:** 1 (only first sheet used)
- **Header:** Line 1 (not imported)

### Countries - Columns

| Column | Type | Optional | Description |
|--------|------|----------|-------------|
| `code` | String(2) | ❌ No | Country code (ISO 3166-1 alpha-2) |
| `name` | String | ❌ No | Standard name (usually English) |
| `nameEn` | String | ✅ Yes | English name |
| `nameDe` | String | ✅ Yes | German name |
| `nameFr` | String | ✅ Yes | French name |

---

**Last Updated:** 2026-03-29  
**Status:** ✅ Multilingual support enabled  
**Build:** ✅ Successfully compiled

