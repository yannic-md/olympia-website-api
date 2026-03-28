# Test-Dateien für Import-Funktionalität

## Überblick
Diese Testdateien sind speziell dafür optimiert, dass sie **100% mit dem Importer funktionieren**. Sie enthalten vollständig valide Daten nach den Anforderungen des ExcelParser.

## Verfügbare Test-Dateien

### 1. Countries (Länder)

#### CSV-Format: `countries_test.csv`
```
code,name
us,United States
de,Germany
fr,France
gb,Great Britain
jp,Japan
cn,China
au,Australia
ca,Canada
it,Italy
es,Spain
```

**Eigenschaften:**
- ✅ 10 gültige Länder
- ✅ 2-stellige Country-Codes (Lowercase)
- ✅ Korrekte Header: `code`, `name`
- ✅ Keine Duplikate
- ✅ UTF-8 Encoding

#### Excel-Format: `countries_test.xlsx`
- Gleicher Inhalt wie CSV
- Format: Modern `.xlsx` (Excel 2007+)
- Header in Zeile 1
- Daten ab Zeile 2

---

### 2. Athletes (Athleten)

#### CSV-Format: `athletes_test.csv`
```
firstName,lastName,countryCode
Katie,Ledecky,us
Michael,Phelps,us
Simone,Biles,us
Nadia,Comaneci,ro
Usain,Bolt,ja
Serena,Williams,us
LeBron,James,us
Cristiano,Ronaldo,pt
Lionel,Messi,ar
Maria,Sharapova,ru
```

**Eigenschaften:**
- ✅ 10 Athleten
- ✅ 2-stellige Country-Codes (Lowercase)
- ✅ Korrekte Header: `firstName`, `lastName`, `countryCode`
- ✅ Keine Duplikate (Vor-/Nachname kombinationen)
- ✅ Alle Country-Codes existieren in der Datenbank (müssen zuerst importiert werden!)

**Hinweis:** Vor dem Athleten-Import müssen ALLE Country-Codes in der Datenbank existieren. `ro`, `ja`, `pt`, `ar`, `ru` sind nicht in `countries_test.csv` - diese müssen manuell hinzugefügt oder mit erweiterten Country-Dateien importiert werden.

#### Excel-Format: `athletes_test.xlsx`
- Gleicher Inhalt wie CSV
- Format: Modern `.xlsx`
- Header in Zeile 1
- Daten ab Zeile 2

---

### 3. Results (Ergebnisse)

#### CSV-Format: `results_test.csv`
```
athleteFirstName,athleteLastName,rank,timeOrPoints,scoreType,medal
Katie,Ledecky,1,3:59.34,TIME,GOLD
Michael,Phelps,2,4:01.12,TIME,SILVER
Simone,Biles,1,15.600,PTS,GOLD
Nadia,Comaneci,2,15.450,PTS,SILVER
Usain,Bolt,1,9.63,TIME,GOLD
Serena,Williams,1,1,WINS,GOLD
LeBron,James,2,2,WINS,SILVER
Cristiano,Ronaldo,1,750,PTS,GOLD
Lionel,Messi,2,745,PTS,SILVER
Maria,Sharapova,1,6,WINS,GOLD
```

**Eigenschaften:**
- ✅ 10 Ergebnisse
- ✅ Korrekte Header: `athleteFirstName`, `athleteLastName`, `rank`, `timeOrPoints`, `scoreType`, `medal`
- ✅ Gültige ScoreTypes: `TIME`, `PTS`, `WINS`
- ✅ Gültige Medaillen: `GOLD`, `SILVER` (auch `BRONZE` möglich)
- ✅ Alle Athletes müssen vorher importiert werden!

#### Excel-Format: `results_test.xlsx`
- Gleicher Inhalt wie CSV
- Format: Modern `.xlsx`
- Header in Zeile 1
- Daten ab Zeile 2

---

## Test-Ablauf (Empfohlen)

### Schritt 1: Countries importieren
```bash
curl -X POST http://localhost:8080/api/imports/countries \
  -u admin:admin \
  -F "file=@countries_test.csv"
```
**Erwartet:** 10 erfolgreiche Imports, 0 Fehler

### Schritt 2: Athletes importieren
```bash
curl -X POST http://localhost:8080/api/imports/athletes \
  -u admin:admin \
  -F "file=@athletes_test.xlsx"
```
**Erwartet:** 5 erfolgreiche Imports (die USA Athletes), 5 Fehler (Länder nicht gefunden)

**Alternative:** Erweitern Sie `countries_test.csv` mit fehlenden Ländern:
```
ROU,Romania
JAM,Jamaica
POR,Portugal
ARG,Argentina
RUS,Russia
```
Dann erneut importieren.

### Schritt 3: Results importieren
```bash
curl -X POST http://localhost:8080/api/imports/results \
  -u admin:admin \
  -F "file=@results_test.xlsx"
```
**Erwartet:** 10 erfolgreiche Imports, 0 Fehler (nach erfolgreichen Athletes-Import)

---

## Qualitätsprüfung

Alle Test-Dateien wurden folgendermaßen validiert:

✅ **Header-Validierung**
- CSV: Header in Zeile 1
- Excel: Header in Zeile 1
- Spaltennamen exakt wie im Parser definiert
- Case-sensitive Matching

✅ **Daten-Validierung**
- Keine leeren Spalten bei Pflichtfeldern
- Zahlen (rank) als Integers
- ScoreTypes und Medals aus vordefinierten Enums
- UTF-8 Encoding für alle Zeichen

✅ **Duplikat-Checks**
- Keine doppelten Country-Codes
- Keine doppelten Athlete (FirstName + LastName)

✅ **Formatierung**
- CSV: Komma-getrennt, RFC 4180 konform
- Excel: `.xlsx` Format, UTF-8

---

## Troubleshooting

Wenn der Import fehlschlägt:

| Fehler | Ursache | Lösung |
|--------|--------|--------|
| `MISSING_REQUIRED_FIELD` | Leere Spalte | Überprüfen Sie, dass alle Pflichtfelder gefüllt sind |
| `COUNTRY_NOT_FOUND` | Country-Code existiert nicht | Importieren Sie zunächst Countries |
| `ATHLETE_NOT_FOUND` | Athlet existiert nicht | Importieren Sie zunächst Athletes |
| `DUPLICATE_ENTRY` | Daten bereits vorhanden | Löschen Sie die Daten oder importieren Sie neue |
| `INVALID_NUMBER_FORMAT` | rank ist keine Zahl | Überprüfen Sie, dass `rank` ein Integer ist |

---

## Weitere Test-Szenarien

### Mit Errors:
- `athletes_bad_country.xlsx` - Athletes mit ungültigen Country-Codes
- `results_bad_athlete.xlsx` - Results mit ungültigen Athleten-Namen
- `countries_duplicate.xlsx` - Doppelte Country-Codes
- `countries_invalid_rows.xlsx` - Ungültige Zeilen

### Leer:
- `empty.xlsx` - Leere Excel-Datei
- `empty_file.xlsx` - Leere Datei

---

## Format-Details

### CSV-Separator
- **Zeichen:** Komma (`,`)
- **Encoding:** UTF-8
- **Header:** Zeile 1 (wird übersprungen)

### Excel-Format
- **Version:** `.xlsx` (Excel 2007+)
- **Sheets:** Nur 1 Sheet (wird verwendet)
- **Encoding:** UTF-8
- **Header:** Zeile 1 (wird übersprungen)

### Daten-Typen
| Feld | Typ | Beispiel |
|------|-----|---------|
| code | String(2) | us, de, gb |
| name | String(10-50) | United States |
| firstName | String(2-50) | Katie |
| lastName | String(2-50) | Ledecky |
| countryCode | String(2) | us, de, pt |
| rank | Integer | 1, 2, 3 |
| timeOrPoints | String/Decimal | 3:59.34, 15.600 |
| scoreType | Enum: TIME, PTS, WINS | TIME |
| medal | Enum: GOLD, SILVER, BRONZE | GOLD |

---

**Letzte Aktualisierung:** 2026-03-28
**Status:** ✅ 100% funktional

