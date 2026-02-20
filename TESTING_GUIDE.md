# Test-Anleitung für Excel Importer

## Überblick
Diese Anleitung zeigt dir Schritt für Schritt, wie du den Excel-Importer testest.

---

## Schritt 1: Starte die Anwendung

```bash
# Terminal im Projektverzeichnis öffnen
cd C:\Users\Anwender\IdeaProjects\olympia-website-api

# Anwendung starten
./gradlew bootRun
```

Warte bis die Konsole folgende Nachricht zeigt:
```
... Started MainApplication in ... seconds
```

Die App läuft dann auf: **http://localhost:8080**

---

## Schritt 2: Starte die Datenbank (Docker)

Öffne ein zweites Terminal und führe aus:

```bash
cd C:\Users\Anwender\IdeaProjects\olympia-website-api

# Docker Compose starten
docker-compose up
```

Die MariaDB-Datenbank sollte jetzt auf Port 3306 laufen.

---

## Schritt 3: Test-Dateien erstellen

### Option A: Manuelle Erstellung mit Excel/LibreOffice

#### Datei 1: `countries_sample.xlsx`

Erstelle eine neue Excel-Datei mit folgenden Spalten:

| code | name          |
|------|---------------|
| USA  | United States |
| GER  | Germany       |
| FRA  | France        |
| JPN  | Japan         |
| CHN  | China         |

Speichere als: `test_data/countries_sample.xlsx`

---

#### Datei 2: `athletes_sample.xlsx`

| firstName | lastName   | countryCode | gender |
|-----------|------------|-------------|--------|
| Katie     | Ledecky    | USA         | F      |
| Caeleb    | Dressel    | USA         | M      |
| Max       | Mustermann | GER         | M      |
| Claire    | Dupont     | FRA         | F      |
| Yuki      | Tanaka     | JPN         | F      |

Speichere als: `test_data/athletes_sample.xlsx`

---

#### Datei 3: `results_sample.xlsx`

| athleteFirstName | athleteLastName | rank | timeOrPoints | medal  |
|------------------|-----------------|------|--------------|--------|
| Katie            | Ledecky         | 1    | 3:59.34      | GOLD   |
| Caeleb           | Dressel         | 2    | 4:01.12      | SILVER |
| Max              | Mustermann      | 1    | 9.85         | GOLD   |
| Claire           | Dupont          | 3    | 12.34        |        |
| Yuki             | Tanaka          | 2    | 15.45        | SILVER |

Speichere als: `test_data/results_sample.xlsx`

---

#### Datei 4: `countries_with_errors.xlsx` (für Error-Testing)

| code  | name          |
|-------|---------------|
| CAN   | Canada        |
| USA   | United States | ← Duplicate (sollte fehler sein)
|       | Mexico        | ← Missing code (Fehler)
| AUS   | Australia     |

Speichere als: `test_data/countries_with_errors.xlsx`

---

### Option B: Automatische Erstellung mit Python-Skript

Wenn du Python installiert hast, erstelle eine Datei `create_test_files.py`:

```python
import openpyxl
from openpyxl.worksheet.worksheet import Worksheet
from pathlib import Path

# Erstelle test_data Ordner
Path("test_data").mkdir(exist_ok=True)

# 1. Countries
wb = openpyxl.Workbook()
ws = wb.active
ws.append(["code", "name"])
ws.append(["USA", "United States"])
ws.append(["GER", "Germany"])
ws.append(["FRA", "France"])
ws.append(["JPN", "Japan"])
ws.append(["CHN", "China"])
wb.save("test_data/countries_sample.xlsx")
print("✓ countries_sample.xlsx erstellt")

# 2. Athletes
wb = openpyxl.Workbook()
ws = wb.active
ws.append(["firstName", "lastName", "countryCode", "gender"])
ws.append(["Katie", "Ledecky", "USA", "F"])
ws.append(["Caeleb", "Dressel", "USA", "M"])
ws.append(["Max", "Mustermann", "GER", "M"])
ws.append(["Claire", "Dupont", "FRA", "F"])
ws.append(["Yuki", "Tanaka", "JPN", "F"])
wb.save("test_data/athletes_sample.xlsx")
print("✓ athletes_sample.xlsx erstellt")

# 3. Results
wb = openpyxl.Workbook()
ws = wb.active
ws.append(["athleteFirstName", "athleteLastName", "rank", "timeOrPoints", "medal"])
ws.append(["Katie", "Ledecky", 1, "3:59.34", "GOLD"])
ws.append(["Caeleb", "Dressel", 2, "4:01.12", "SILVER"])
ws.append(["Max", "Mustermann", 1, "9.85", "GOLD"])
ws.append(["Claire", "Dupont", 3, "12.34", ""])
ws.append(["Yuki", "Tanaka", 2, "15.45", "SILVER"])
wb.save("test_data/results_sample.xlsx")
print("✓ results_sample.xlsx erstellt")

# 4. Countries mit Fehlern
wb = openpyxl.Workbook()
ws = wb.active
ws.append(["code", "name"])
ws.append(["CAN", "Canada"])
ws.append(["USA", "United States"])  # Duplicate
ws.append(["", "Mexico"])  # Missing code
ws.append(["AUS", "Australia"])
wb.save("test_data/countries_with_errors.xlsx")
print("✓ countries_with_errors.xlsx erstellt")

print("\nAlle Test-Dateien wurden erstellt!")
```

Führe aus:
```bash
python create_test_files.py
```

---

## Schritt 4: Testen mit cURL

### Test 1: Länder importieren

```bash
curl -X POST \
  -F "file=@test_data/countries_sample.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/countries
```

**Erwartete Response:**
```json
{
  "importLogId": 1,
  "status": "COMPLETED",
  "importType": "COUNTRIES",
  "filename": "countries_sample.xlsx",
  "totalRecords": 5,
  "successfulRecords": 5,
  "failedRecords": 0,
  "message": "Import completed. Success: 5, Failed: 0"
}
```

---

### Test 2: Athleten importieren

```bash
curl -X POST \
  -F "file=@test_data/athletes_sample.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/athletes
```

**Erwartete Response:**
```json
{
  "importLogId": 2,
  "status": "COMPLETED",
  "importType": "ATHLETES",
  "filename": "athletes_sample.xlsx",
  "totalRecords": 5,
  "successfulRecords": 5,
  "failedRecords": 0,
  "message": "Import completed. Success: 5, Failed: 0"
}
```

---

### Test 3: Ergebnisse importieren

```bash
curl -X POST \
  -F "file=@test_data/results_sample.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/results
```

**Erwartete Response:**
```json
{
  "importLogId": 3,
  "status": "COMPLETED",
  "importType": "RESULTS",
  "filename": "results_sample.xlsx",
  "totalRecords": 5,
  "successfulRecords": 5,
  "failedRecords": 0,
  "message": "Import completed. Success: 5, Failed: 0"
}
```

---

### Test 4: Mit Fehlern

```bash
curl -X POST \
  -F "file=@test_data/countries_with_errors.xlsx" \
  -F "userId=1" \
  http://localhost:8080/api/imports/countries
```

**Erwartete Response (mit Fehlern):**
```json
{
  "importLogId": 4,
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

## Schritt 5: Testen mit PowerShell

Öffne PowerShell und führe aus:

```powershell
cd C:\Users\Anwender\IdeaProjects\olympia-website-api
.\test_importer.ps1
```

Das Skript lädt alle Test-Dateien automatisch hoch und zeigt dir die Ergebnisse.

---

## Schritt 6: Überprüfe die Datenbank

### Mit MySQL CLI

```bash
mysql -h localhost -u user -p secret olympia

# Überprüfe importierte Länder
SELECT * FROM countries;

# Überprüfe importierte Athleten
SELECT * FROM athletes;

# Überprüfe importierte Ergebnisse
SELECT * FROM results;

# Überprüfe Import-Logs
SELECT * FROM import_logs;

# Überprüfe Import-Fehler
SELECT * FROM import_errors;

# Überprüfe Import-Details
SELECT * FROM import_details;
```

### Mit DBeaver/Intellij

1. Öffne Database Connection zu MariaDB
2. Durchsuche folgende Tabellen:
   - `countries` - sollte 5 neue Länder haben
   - `athletes` - sollte 5 neue Athleten haben
   - `results` - sollte 5 neue Ergebnisse haben
   - `import_logs` - sollte 4 Einträge haben
   - `import_errors` - sollte 2 Fehler haben (vom 4. Test)

---

## Troubleshooting

### ❌ "Connection refused" auf Port 8080

**Ursache:** App läuft nicht

**Lösung:** 
```bash
./gradlew bootRun
```

---

### ❌ "Database connection refused"

**Ursache:** MariaDB läuft nicht

**Lösung:**
```bash
docker-compose up
```

---

### ❌ "File not found" Fehler

**Ursache:** Test-Dateien existieren nicht

**Lösung:** 
```bash
# Ordner erstellen
mkdir test_data

# Dann Test-Dateien erstellen (siehe Schritt 3)
```

---

### ❌ "Unsupported file format"

**Ursache:** Datei ist nicht .xlsx oder .xls

**Lösung:** Konvertiere die Datei zu Excel-Format

---

### ❌ "Country not found" beim Athlete-Import

**Ursache:** Länder müssen zuerst importiert werden

**Lösung:** Führe Test 1 aus, bevor du Test 2 machst

---

### ❌ "Athlete not found" beim Result-Import

**Ursache:** Athleten müssen zuerst importiert werden

**Lösung:** Führe Test 2 aus, bevor du Test 3 machst

---

## Nächste Schritte

1. ✅ Alle Tests erfolgreich? Glückwunsch! 🎉
2. Überprüfe die Import-Logs in der Datenbank
3. Vergewissere dich, dass die Daten korrekt importiert wurden
4. Testen mit ungültigen Dateien (falsches Format, fehlende Spalten, etc.)
5. Integriere Spring Security für Admin-Only-Zugriff

---

## Notes für Produktiveinsatz

### Sicherheit aktivieren

Wenn du Spring Security aktivieren möchtest, bearbeite `build.gradle.kts`:

```kotlin
// Uncomment diese Zeile
implementation("org.springframework.boot:spring-boot-starter-security")
```

Und füge `@PreAuthorize("hasRole('ADMIN')")` zu den Controllern hinzu.

### File-Upload-Limits setzen

In `application.properties`:
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Logging konfigurieren

In `application.properties`:
```properties
logging.level.de.olympia.main.importer=DEBUG
```


