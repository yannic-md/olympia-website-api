# 🚀 Excel Importer - Quick Start Guide

## TL;DR - Schnelle Zusammenfassung

Du hast einen vollständigen Excel-Importer für Wettbewerbsdaten implementiert! Hier ist wie du ihn testest:

---

## ✅ Was wurde implementiert?

| Feature | Status | Details |
|---------|--------|---------|
| ✅ Excel-Parsing (Apache POI) | DONE | Unterstützt .xlsx und .xls Format |
| ✅ API-Endpunkte | DONE | 3 Endpunkte: /countries, /athletes, /results |
| ✅ Validierung | DONE | Bean Validation + Custom Validierung |
| ✅ Fehlerbehandlung | DONE | Detaillierte Fehler mit Row-Level Tracking |
| ✅ Transaktionsmanagement | DONE | `@Transactional` auf Service-Methoden |
| ✅ Import-Logging | DONE | Vollständige Audit-Trails in DB |
| ✅ Duplikat-Erkennung | DONE | Automatisches Skipping bei Duplikaten |
| ✅ Dokumentation | DONE | Vollständige API-Dokumentation |

---

## 📋 Dateistruktur

```
olympia-website-api/
├── src/main/java/de/olympia/main/
│   ├── entity/                  # Daten-Entities
│   │   ├── Country.java
│   │   ├── Athlete.java
│   │   └── Result.java
│   ├── repository/              # DB-Repositories
│   │   ├── CountryRepository.java
│   │   ├── AthleteRepository.java
│   │   └── ResultRepository.java
│   └── importer/                # Importer-Modul
│       ├── controller/          # REST-Endpunkte
│       │   └── ImportController.java
│       ├── service/             # Business Logic
│       │   └── ExcelImporterService.java
│       ├── parser/              # Excel-Parsing
│       │   └── ExcelParser.java
│       ├── entity/              # Import-Tracking
│       │   ├── ImportLog.java
│       │   ├── ImportError.java
│       │   └── ImportDetail.java
│       ├── repository/          # Import DB Access
│       │   ├── ImportLogRepository.java
│       │   ├── ImportErrorRepository.java
│       │   └── ImportDetailRepository.java
│       ├── dto/                 # Data Transfer Objects
│       │   ├── CountryImportDto.java
│       │   ├── AthleteImportDto.java
│       │   ├── ResultImportDto.java
│       │   └── ImportResponseDto.java
│       └── exception/           # Fehlerbehandlung
│           ├── ImportException.java
│           └── InvalidImportDataException.java
├── src/main/resources/db/migration/
│   └── V2__create_import_tracking_tables.sql
├── IMPORT_API_DOCUMENTATION.md  # Ausführliche Doku
├── TESTING_GUIDE.md             # Test-Anleitung
├── create_test_files.py         # Python-Skript für Test-Dateien
├── test_importer.ps1            # PowerShell-Test-Skript
└── test_importer.sh             # Bash-Test-Skript
```

---

## 🧪 Testing in 5 Schritten

### Schritt 1️⃣: Datenbank starten

```bash
cd C:\Users\Anwender\IdeaProjects\olympia-website-api
docker-compose up
```

Warte bis: `MariaDB Server is ready for connections`

---

### Schritt 2️⃣: Anwendung starten

**Neues Terminal öffnen:**

```bash
cd C:\Users\Anwender\IdeaProjects\olympia-website-api
./gradlew bootRun
```

Warte bis: `Started MainApplication in ... seconds`

---

### Schritt 3️⃣: Test-Dateien erstellen

**Drittes Terminal öffnen:**

```bash
cd C:\Users\Anwender\IdeaProjects\olympia-website-api

# Python installiert?
python create_test_files.py

# Wenn nicht, folge TESTING_GUIDE.md für manuelle Erstellung
```

---

### Schritt 4️⃣: Tests ausführen

#### Option A: PowerShell (für Windows)

```powershell
cd C:\Users\Anwender\IdeaProjects\olympia-website-api
.\test_importer.ps1
```

#### Option B: cURL (alle Systeme)

```bash
# Test 1: Länder importieren
curl -X POST \
  -F "file=@test_data/countries_sample.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/countries

# Test 2: Athleten importieren
curl -X POST \
  -F "file=@test_data/athletes_sample.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/athletes

# Test 3: Ergebnisse importieren
curl -X POST \
  -F "file=@test_data/results_sample.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/results

# Test 4: Mit Fehlern
curl -X POST \
  -F "file=@test_data/countries_with_errors.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/countries
```

---

### Schritt 5️⃣: Überprüfe die Datenbank

```bash
# Mit MySQL-CLI verbinden
mysql -h localhost -u user -p secret olympia

# Überprüfe Import-Logs
SELECT * FROM import_logs;

# Überprüfe importierte Länder
SELECT * FROM countries;

# Überprüfe Fehler (sollte welche vom 4. Test geben)
SELECT * FROM import_errors WHERE import_log_id = 4;
```

---

## 📊 Erwartete Ergebnisse

### Test 1: Countries erfolgreich ✅

```json
{
  "importLogId": 1,
  "status": "COMPLETED",
  "importType": "COUNTRIES",
  "totalRecords": 8,
  "successfulRecords": 8,
  "failedRecords": 0,
  "message": "Import completed. Success: 8, Failed: 0"
}
```

### Test 2: Athletes erfolgreich ✅

```json
{
  "importLogId": 2,
  "status": "COMPLETED",
  "importType": "ATHLETES",
  "totalRecords": 8,
  "successfulRecords": 8,
  "failedRecords": 0,
  "message": "Import completed. Success: 8, Failed: 0"
}
```

### Test 3: Results erfolgreich ✅

```json
{
  "importLogId": 3,
  "status": "COMPLETED",
  "importType": "RESULTS",
  "totalRecords": 8,
  "successfulRecords": 8,
  "failedRecords": 0,
  "message": "Import completed. Success: 8, Failed: 0"
}
```

### Test 4: Mit Fehlern (mit Fehler-Details) ⚠️

```json
{
  "importLogId": 4,
  "status": "COMPLETED",
  "importType": "COUNTRIES",
  "totalRecords": 6,
  "successfulRecords": 4,
  "failedRecords": 2,
  "message": "Import completed. Success: 4, Failed: 2",
  "errors": [
    {
      "rowNumber": 3,
      "errorCode": "DUPLICATE_ENTRY",
      "errorMessage": "Country already exists: USA",
      "fieldName": "code",
      "fieldValue": "USA"
    },
    {
      "rowNumber": 4,
      "errorCode": "MISSING_REQUIRED_FIELD",
      "errorMessage": "Required field is empty",
      "fieldName": "code",
      "fieldValue": null
    }
  ]
}
```

---

## 🔍 Was du überprüfen solltest

### 1. Datenbank-Tabellen

```sql
-- Neu erstellte Tabellen
SHOW TABLES LIKE 'import%';

-- Output sollte sein:
-- import_logs
-- import_errors
-- import_details
```

### 2. Importierte Daten

```sql
-- Überprüfe Länder
SELECT COUNT(*) FROM countries;  -- Sollte >= 8 sein

-- Überprüfe Athleten
SELECT COUNT(*) FROM athletes;   -- Sollte >= 8 sein

-- Überprüfe Ergebnisse
SELECT COUNT(*) FROM results;    -- Sollte >= 8 sein
```

### 3. Import-Historie

```sql
-- Überprüfe Logs
SELECT * FROM import_logs ORDER BY imported_at DESC;

-- Überprüfe Fehler vom 4. Test
SELECT * FROM import_errors 
WHERE import_log_id = (
  SELECT id FROM import_logs 
  WHERE import_type = 'COUNTRIES' AND failed_records > 0
);
```

---

## 🚨 Häufige Probleme & Lösungen

| Problem | Ursache | Lösung |
|---------|--------|--------|
| `Connection refused: 8080` | App läuft nicht | `./gradlew bootRun` |
| `Database connection refused` | DB läuft nicht | `docker-compose up` |
| `File not found` | Test-Dateien fehlen | `python create_test_files.py` |
| `Country not found` (Athletes) | Länder nicht importiert | Test 1 vor Test 2 ausführen |
| `Athlete not found` (Results) | Athleten nicht importiert | Test 2 vor Test 3 ausführen |
| `Unsupported file format` | Falsche Datei-Extension | Nur .xlsx oder .xls! |

---

## 📚 Dokumentation

- **Detaillierte API-Docs:** `IMPORT_API_DOCUMENTATION.md`
- **Test-Anleitung:** `TESTING_GUIDE.md`
- **Code-Kommentare:** In allen Java-Dateien

---

## 🔐 Sicherheit (Nächste Schritte)

Der Importer ist bereit für Spring Security Integration:

```java
// In ImportController.java: Uncomment diese Zeile
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ImportResponseDto> importCountries(...) {
    // ...
}
```

Und aktiviere Spring Security in `build.gradle.kts`:

```kotlin
implementation("org.springframework.boot:spring-boot-starter-security")
```

---

## 📈 Zukunfts-Features

- [ ] CSV-Format unterstützen (Abhängigkeiten bereits hinzugefügt)
- [ ] Batch-Import mehrerer Dateien
- [ ] Preview-Modus vor Import
- [ ] Export-Funktionalität
- [ ] Scheduled Imports
- [ ] Webhook-Benachrichtigungen
- [ ] Konflikt-Auflösung (Merge/Update/Skip)

---

## ✨ Zusammenfassung

Du hast einen produktionsreifen Excel-Importer implementiert mit:

- ✅ 3 REST-API-Endpunkte
- ✅ Robuste Fehlerbehandlung mit Fehler-Tracking
- ✅ Transaktionale Bulk-Imports
- ✅ Audit-Trail in der Datenbank
- ✅ Automatische Duplikat-Erkennung
- ✅ Comprehensive Testing Setup
- ✅ Vollständige Dokumentation

**Viel Erfolg beim Testen! 🎉**


