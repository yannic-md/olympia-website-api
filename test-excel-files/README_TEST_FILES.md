# Test-Dateien für Import-Funktionalität

## Überblick
Diese Test-Dateien ermöglichen die Validierung der Import-Funktionalität mit **multilingual name support**. Für jeden Import-Typ existieren genau **3 Dateien**: eine funktionierende, eine fehlerhafte und eine mit anderem Datentyp.

**Gesamtanzahl: 9 Dateien** (CSV + XLSX für jede Kategorie)

## Verfügbare Test-Dateien (9 insgesamt)

### 1. Countries (Länder) - 3 Dateien

#### `countries_test.xlsx` - ✅ Working (Excel-Format)
- Format: Modern `.xlsx` (Excel 2007+)
- 10 gültige Länder mit multilingual names
- **Spalten**: code, name, nameEn, nameDe, nameFr
- **Verwendung**: Hauptdatei für erfolgreiche Imports

**Beispiel:**
```
us,United States,United States,Vereinigte Staaten,États-Unis
de,Germany,Germany,Deutschland,Allemagne
fr,France,France,Frankreich,France
```

#### `countries_broken.csv` - ❌ Broken (CSV-Format)
- Format: CSV (Textformat)
- **Fehler**:
  - Ungültige Country-Codes (3-stellig: `XXX`)
  - Fehlende Codes (leere Spalten)
  - Fehlende German-Name (leere nameDe Spalte)
- **Verwendung**: Fehlerbehandlung und Validierung testen

#### `countries_test.csv` - ✅ Working (CSV-Format)
- Format: CSV (Textformat)
- 10 gültige Länder mit multilingual names (identisch mit XLSX)
- **Spalten**: code, name, nameEn, nameDe, nameFr
- **Verwendung**: CSV-Import und Format-Variante testen

---

### 2. Athletes (Athleten) - 3 Dateien

#### `athletes_test.xlsx` - ✅ Working (Excel-Format)
- Format: Modern `.xlsx` (Excel 2007+)
- 10 Athleten mit gültigen Daten
- Korrekte Header: `firstName`, `lastName`, `countryCode`
- **Verwendung**: Hauptdatei für erfolgreiche Imports

#### `athletes_broken.csv` - ❌ Broken (CSV-Format)
- Format: CSV
- **Fehler**:
  - Leere Felder (fehlende LastName)
  - Ungültige Country-Codes (`ZZ`)
  - Fehlende Country-Codes
- **Verwendung**: Fehlerbehandlung und Datenvalidierung testen

#### `athletes_test.csv` - ✅ Working (CSV-Format)
- Format: CSV
- 10 Athleten mit gültigen Daten (identisch mit XLSX)
- Korrekte Header: `firstName`, `lastName`, `countryCode`
- **Verwendung**: CSV-Import testen

---

### 3. Results (Ergebnisse) - 3 Dateien

#### `results_test.xlsx` - ✅ Working (Excel-Format)
- Format: Modern `.xlsx` (Excel 2007+)
- 10 Ergebnisse mit gültigen Daten
- Korrekte Header: `athleteFirstName`, `athleteLastName`, `rank`, `timeOrPoints`, `scoreType`, `medal`
- **Verwendung**: Hauptdatei für erfolgreiche Imports

#### `results_broken.csv` - ❌ Broken (CSV-Format)
- Format: CSV
- **Fehler**:
  - Nicht existierende Athletes
  - Ungültige Rank-Werte (`x` statt Zahl)
  - Ungültige ScoreTypes (`INVALID_TYPE`)
  - Ungültige Zeitformate (`invalid_time`)
- **Verwendung**: Fehlerbehandlung und Validierung testen

#### `results_test.csv` - ✅ Working (CSV-Format)
- Format: CSV
- 10 Ergebnisse mit gültigen Daten (identisch mit XLSX)
- Korrekte Header: `athleteFirstName`, `athleteLastName`, `rank`, `timeOrPoints`, `scoreType`, `medal`
- **Verwendung**: CSV-Import testen

---

## 📊 Datei-Übersicht

| Kategorie | Working | Broken | Alternative Format |
|-----------|---------|--------|-------------------|
| **Countries** | `countries_test.xlsx` | `countries_broken.csv` | `countries_test.csv` |
| **Athletes** | `athletes_test.xlsx` | `athletes_broken.csv` | `athletes_test.csv` |
| **Results** | `results_test.xlsx` | `results_broken.csv` | `results_test.csv` |

---

## 🌍 Multilingual Name Support (Countries)

Der Country-Import unterstützt jetzt **multilingual names**. Dies ermöglicht:
- ✅ Speicherung von Länder-Namen in Englisch, Deutsch und Französisch
- ✅ Flexibles UI (Benutzer sehen ihren lokalen Sprachname statt nur Englisch)
- ✅ Optionale Felder (nameEn, nameDe, nameFr) können leer gelassen werden

**CSV-Format für Countries:**
```csv
code,name,nameEn,nameDe,nameFr
us,United States,United States,Vereinigte Staaten,États-Unis
de,Germany,Germany,Deutschland,Allemagne
fr,France,France,Frankreich,France
```

---

## 🟢 Quick Start - Import testen

### Erfolgreiche Imports (XLSX - empfohlen)
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

### Fehlerbehandlung testen
```bash
# Countries mit Fehlern
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:admin -F "file=@countries_broken.csv"

# Athletes mit Fehlern
curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:admin -F "file=@athletes_broken.csv"

# Results mit Fehlern
curl -X POST http://localhost:8080/api/imports/results \
  -u admin:admin -F "file=@results_broken.csv"
```

### CSV-Format testen
```bash
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:admin -F "file=@countries_test.csv"

curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:admin -F "file=@athletes_test.csv"

curl -X POST http://localhost:8080/api/imports/results \
  -u admin:admin -F "file=@results_test.csv"
```

---

## Test-Ablauf (Empfohlen)

1. **Countries importieren** → `countries_test.xlsx` oder `countries_test.csv`
2. **Athletes importieren** → `athletes_test.xlsx` oder `athletes_test.csv`
3. **Results importieren** → `results_test.xlsx` oder `results_test.csv`

**Hinweis**: Alle Country-Codes müssen existieren, bevor Athletes importiert werden!

---

## Format-Spezifikation

### CSV-Dateien
- **Separator:** Komma (`,`)
- **Encoding:** UTF-8
- **Zeilenumbruch:** LF (`\n`)
- **Header:** Zeile 1 (wird nicht importiert)

### Excel-Dateien (`.xlsx`)
- **Format:** Modern Excel 2007+
- **Encoding:** UTF-8
- **Sheets:** 1 (nur das erste Sheet wird verwendet)
- **Header:** Zeile 1 (wird nicht importiert)

### Countries - Spalten

| Spalte | Typ | Optional | Beschreibung |
|--------|-----|----------|-------------|
| `code` | String(2) | ❌ Nein | Land-Code (ISO 3166-1 alpha-2) |
| `name` | String | ❌ Nein | Standardname (meist Englisch) |
| `nameEn` | String | ✅ Ja | Englischer Name |
| `nameDe` | String | ✅ Ja | Deutscher Name |
| `nameFr` | String | ✅ Ja | Französischer Name |

---

**Letzte Aktualisierung:** 2026-03-29  
**Status:** ✅ Multilingual support aktiviert  
**Build:** ✅ Erfolgreich kompiliert

