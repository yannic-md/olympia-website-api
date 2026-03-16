# Excel Import API Dokumentation

## Übersicht
Diese API bietet Endpunkte zum Importieren von Daten aus Excel-Dateien (`.xlsx` und `.xls`). Es können **Länder**, **Athleten** und **Ergebnisse** importiert werden. Jeder Import wird protokolliert und Fehler werden auf Zeilenebene erfasst.

## Endpunkte

### 1. Länder importieren
**POST** `/api/imports/countries`

Importiert Länder aus einer Excel-Datei.

**Authentifizierung:** Erforderlich (alle authentifizierten Benutzer)

**Request Parameter:**
- `file` (MultipartFile, erforderlich) — Excel-Datei (`.xlsx` oder `.xls`)
- `userId` (Long, optional) — Benutzer-ID des Importierenden (Standard: `1`)

**Excel-Format (Spalten):**

| Spalte A | Spalte B |
|----------|----------|
| code     | name     |
| USA      | United States |
| GER      | Germany  |

**Response (200 OK):**
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

---

### 2. Athleten importieren
**POST** `/api/imports/athletes`

Importiert Athleten aus einer Excel-Datei. Länder müssen vorher existieren (Zuordnung über Ländercode).

**Authentifizierung:** Erforderlich (alle authentifizierten Benutzer)

**Request Parameter:**
- `file` (MultipartFile, erforderlich) — Excel-Datei (`.xlsx` oder `.xls`)
- `userId` (Long, optional) — Benutzer-ID des Importierenden (Standard: `1`)

**Excel-Format (Spalten):**

| Spalte A  | Spalte B  | Spalte C    | Spalte D |
|-----------|-----------|-------------|----------|
| firstName | lastName  | countryCode | gender   |
| Katie     | Ledecky   | USA         | F        |
| Max       | Mustermann| GER         | M        |

**Response (200 OK):**
```json
{
  "importLogId": 2,
  "status": "COMPLETED",
  "importType": "ATHLETES",
  "filename": "athletes.xlsx",
  "totalRecords": 2,
  "successfulRecords": 2,
  "failedRecords": 0,
  "message": "Import completed. Success: 2, Failed: 0"
}
```

---

### 3. Ergebnisse importieren
**POST** `/api/imports/results`

Importiert Turnier-Ergebnisse aus einer Excel- oder CSV-Datei. Athleten und Sportarten müssen vorher existieren. Die Zuordnung erfolgt über Vor-/Nachname des Athleten und den Sportnamen.

**Authentifizierung:** Erforderlich (alle authentifizierten Benutzer)

**Request Parameter:**
- `file` (MultipartFile, erforderlich) — Excel-Datei (`.xlsx` oder `.xls`)
- `userId` (Long, optional) — Benutzer-ID des Importierenden (Standard: `1`)

**Excel-Format (Spalten):**

| Spalte A         | Spalte B         | Spalte C | Spalte D | Spalte E     | Spalte F  | Spalte G |
|------------------|------------------|----------|----------|--------------|-----------|----------|
| athleteFirstName | athleteLastName  | sport    | rank     | timeOrPoints | scoreType | medal    |
| Mikaela          | Shiffrin         | Alpine Skiing | 1   | 1:31.88      | TIME      | GOLD     |
| Nathan           | Chen             | Figure Skating | 1  | 314.56       | PTS       | GOLD     |

**Medaillen-Werte:** `GOLD`, `SILVER`, `BRONZE` oder leer

**ScoreType-Werte:** `PTS`, `WINS`, `TIME` oder leer

**Sport-Werte:** müssen exakt einem vorhandenen Eintrag in der Tabelle `sports.name` entsprechen, z. B. `Alpine Skiing`, `Biathlon`, `Figure Skating`, `Curling`.

**Response (200 OK):**
```json
{
  "importLogId": 3,
  "status": "COMPLETED",
  "importType": "RESULTS",
  "filename": "results.xlsx",
  "totalRecords": 2,
  "successfulRecords": 2,
  "failedRecords": 0,
  "message": "Import completed. Success: 2, Failed: 0"
}
```

---

## Fehlerbehandlung

### Datei-Validierung
Ungültige Dateien werden sofort abgelehnt:

- **Leere Datei:**
```json
{
  "status": "FAILED",
  "message": "File is empty"
}
```

- **Falsches Format (nicht `.xlsx`/`.xls`):**
```json
{
  "status": "FAILED",
  "message": "Only .xlsx and .xls files are supported"
}
```

### Zeilen-Level Fehler
Fehler auf Zeilenebene werden pro Datensatz erfasst und im Response zurückgegeben. Der Import wird dabei **nicht** abgebrochen — gültige Zeilen werden trotzdem importiert.

**Response mit Fehlern (200 OK):**
```json
{
  "importLogId": 4,
  "status": "COMPLETED",
  "importType": "ATHLETES",
  "filename": "athletes_with_errors.xlsx",
  "totalRecords": 3,
  "successfulRecords": 1,
  "failedRecords": 2,
  "message": "Import completed. Success: 1, Failed: 2",
  "errors": [
    {
      "rowNumber": 3,
      "errorCode": "COUNTRY_NOT_FOUND",
      "errorMessage": "Country not found: XYZ",
      "fieldName": "countryCode",
      "fieldValue": null
    },
    {
      "rowNumber": 4,
      "errorCode": "DUPLICATE_ENTRY",
      "errorMessage": "Athlete already exists: Katie Ledecky",
      "fieldName": "firstName,lastName",
      "fieldValue": "Katie Ledecky"
    }
  ]
}
```

### Mögliche Fehlercodes

| Fehlercode              | Beschreibung                              |
|-------------------------|-------------------------------------------|
| `VALIDATION_ERROR`      | Pflichtfeld fehlt oder ungültig           |
| `DUPLICATE_ENTRY`       | Datensatz existiert bereits               |
| `COUNTRY_NOT_FOUND`     | Ländercode nicht in Datenbank gefunden     |
| `ATHLETE_NOT_FOUND`     | Athlet nicht in Datenbank gefunden         |
| `SPORT_NOT_FOUND`       | Sportname nicht in Datenbank gefunden      |
| `USER_NOT_FOUND`        | Benutzer-ID nicht gefunden                |
| `INVALID_MEDAL`         | Ungültiger Medaillen-Wert                 |
| `MISSING_REQUIRED_FIELD`| Pflichtfeld ist leer                      |
| `INVALID_CELL_TYPE`     | Falscher Zelltyp in Excel                 |
| `INVALID_NUMBER_FORMAT` | Ungültiges Zahlenformat                   |
| `EMPTY_SHEET`           | Excel-Datei enthält kein Sheet            |
| `UNSUPPORTED_FORMAT`    | Datei ist weder `.xlsx` noch `.xls`       |
| `PROCESSING_ERROR`      | Allgemeiner Verarbeitungsfehler           |

---

## Duplikat-Verhalten

- **Länder:** Wenn ein Ländercode bereits existiert, wird die Zeile übersprungen (SKIP)
- **Athleten:** Wenn Vor- und Nachname bereits existieren, wird die Zeile übersprungen (SKIP)
- **Ergebnisse:** Werden pro Kombination aus **Sport + Athlet** importiert. Existiert für diese Kombination bereits ein Eintrag, wird er aktualisiert (UPDATE), sonst neu angelegt (INSERT).

---

## Code-Struktur

Die Implementierung folgt der klassischen Layer-Architektur:

1. **controller** — `ImportController.java`
   - Stellt die REST-Endpunkte unter `/api/imports` bereit
   - Validiert die hochgeladene Datei (leer? richtiges Format?)
   - Baut die Response aus dem `ImportLog`

2. **service** — `ExcelImporterService.java`
   - Enthält die Import-Logik für Länder, Athleten und Ergebnisse
   - Validiert jeden Datensatz einzeln mit Bean Validation
   - Prüft auf Duplikate und fehlende Referenzen
   - Löst bei Ergebnissen zusätzlich den Sportnamen gegen die Tabelle `sports` auf
   - Protokolliert Erfolge und Fehler

3. **parser** — `ExcelParser.java`
   - Liest Excel-Dateien mit Apache POI
   - Unterstützt `.xlsx` (XSSF) und `.xls` (HSSF)
   - Konvertiert Zeilen in DTOs
   - Erste Zeile wird als Header übersprungen

4. **dto** — Data Transfer Objects
   - `CountryImportDto` — Code und Name (beide Pflichtfelder)
   - `AthleteImportDto` — Vorname, Nachname (Pflicht), Ländercode, Geschlecht (optional)
   - `ResultImportDto` — Athletenname (Pflicht), Rang, Zeit/Punkte, Medaille (optional)
   - `ImportResponseDto` — Response mit Status, Statistiken und Fehlerliste

5. **entity** — Datenbank-Entitäten
   - `ImportLog` — Protokolliert jeden Import (Datei, Typ, Status, Statistiken)
   - `ImportError` — Erfasst Fehler pro Zeile (Fehlercode, Feld, Wert)
   - `ImportDetail` — Erfasst Details pro importiertem Datensatz (Typ, Aktion)

6. **exception** — Exceptions
   - `InvalidImportDataException` — Ungültige Daten mit Zeilennummer und Feld
   - `ImportException` — Allgemeine Import-Fehler

7. **repository** — Datenbankzugriff
   - `ImportLogRepository` — Import-Protokolle
   - `ImportErrorRepository` — Import-Fehler
   - `ImportDetailRepository` — Import-Details

---

## Datenbank

Die Import-Tracking-Daten werden in drei Tabellen gespeichert (Migration `V2`):

### import_logs
```sql
CREATE TABLE import_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    total_records INT NOT NULL DEFAULT 0,
    successful_records INT NOT NULL DEFAULT 0,
    failed_records INT NOT NULL DEFAULT 0,
    status ENUM('PENDING','IN_PROGRESS','COMPLETED','FAILED') NOT NULL DEFAULT 'PENDING',
    imported_by BIGINT NULL,
    imported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    error_message TEXT NULL
);
```

### import_errors
```sql
CREATE TABLE import_errors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_log_id BIGINT NOT NULL,
    `row_number` INT NOT NULL,
    error_code VARCHAR(50) NOT NULL,
    error_message TEXT NOT NULL,
    field_name VARCHAR(100) NULL,
    field_value VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

### import_details
```sql
CREATE TABLE import_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    import_log_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    action ENUM('INSERT','UPDATE','SKIP') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

---

## Tests mit cURL

### Test 1: Länder importieren
```bash
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:adminpwd \
  -F "file=@countries.xlsx"
```

### Test 2: Athleten importieren
```bash
curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:adminpwd \
  -F "file=@athletes.xlsx"
```

### Test 3: Ergebnisse importieren
```bash
curl -X POST http://localhost:8080/api/imports/results \
  -u admin:adminpwd \
  -F "file=@results.xlsx"
```

### Test 4: Ungültige Datei (falsches Format)
```bash
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:adminpwd \
  -F "file=@data.csv"
```

**Erwartete Response (400 Bad Request):**
```json
{
  "status": "FAILED",
  "message": "Only .xlsx and .xls files are supported"
}
```

### Test 5: Import mit bestimmter User-ID
```bash
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:adminpwd \
  -F "file=@countries.xlsx" \
  -F "userId=1"
```

---

## Import-Reihenfolge

Beim Import mehrerer Datentypen muss folgende Reihenfolge eingehalten werden:

1. **Länder** zuerst — werden von Athleten referenziert
2. **Athleten** als zweites — werden von Ergebnissen referenziert
3. **Ergebnisse** zuletzt — referenzieren Athleten über Vor- und Nachname

---

## Sicherheit

- Alle Import-Endpunkte erfordern Authentifizierung über HTTP Basic Auth
- Jeder Import wird mit der Benutzer-ID protokolliert
- Fehler werden detailliert gespeichert für Nachvollziehbarkeit
- Die Datei-Validierung verhindert das Hochladen ungültiger Formate

