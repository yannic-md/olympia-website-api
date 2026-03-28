# 📋 Import-Dateien Format Guide

## 📖 Übersicht

Dieses Dokument beschreibt die genaue Struktur und Anforderungen für Dateien, die in das Olympia-Website-System importiert werden sollen. Es werden drei Datentypen unterstützt:

1. **Länder (Countries)**
2. **Athleten (Athletes)**
3. **Ergebnisse (Results)**

---

## ⚠️ Wichtig: Importreihenfolge

Die Import-Reihenfolge ist **KRITISCH**. Bitte beachten Sie die folgende Reihenfolge:

```
1️⃣  LÄNDER (Countries) ← ZUERST
2️⃣  ATHLETEN (Athletes) ← ZWEITE
3️⃣  ERGEBNISSE (Results) ← ZULETZT
```

**Grund:** Athleten benötigen bestehende Länder, und Ergebnisse benötigen bestehende Athleten.

---

## 📁 Unterstützte Dateiformate

- ✅ **Excel**: `.xlsx`, `.xls`
- ✅ **CSV**: `.csv` (mit Komma-Trennzeichen)

---

## 1️⃣ Länder (Countries) importieren

### 📋 Spaltenstruktur

| Spalte | Name | Datentyp | Erforderlich | Beschreibung |
|--------|------|----------|--------------|--------------|
| A | `code` | String (2-3 Zeichen) | ✅ Ja | Ländercode (z.B. "us", "de", "fr") - **Case-insensitive** |
| B | `name` | String | ✅ Ja | Vollständiger Länder-Name (z.B. "United States") |

### 📝 Beispiel-Datei (countries.csv)

```csv
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

### 📌 Regeln & Anforderungen

| Regel | Beschreibung |
|-------|--------------|
| **Eindeutigkeit** | Der `code` muss eindeutig sein - Duplikate werden abgelehnt |
| **Format** | Der `code` wird automatisch in **Kleinbuchstaben** konvertiert |
| **Länge** | `code` sollte 2-3 Zeichen lang sein |
| **Name** | Darf nicht leer sein |
| **Header** | Die erste Zeile MUSS die Spaltennamen enthalten (`code`, `name`) |

### ❌ Häufige Fehler

| Fehler | Grund | Lösung |
|--------|-------|--------|
| `DUPLICATE_COUNTRY` | Ländercode existiert bereits | Code überprüfen oder Duplifikate entfernen |
| `INVALID_DATA` | Name ist leer | Sicherstellen, dass alle Länder einen Namen haben |
| `MISSING_HEADER` | Header-Zeile fehlt | Erste Zeile muss `code,name` enthalten |

### ✅ Erfolgreiche Antwort

```json
{
  "importLogId": 1,
  "status": "COMPLETED",
  "importType": "COUNTRIES",
  "filename": "countries.csv",
  "totalRecords": 10,
  "successfulRecords": 10,
  "failedRecords": 0,
  "message": "Import completed. Success: 10, Failed: 0"
}
```

---

## 2️⃣ Athleten (Athletes) importieren

### 📋 Spaltenstruktur

| Spalte | Name | Datentyp | Erforderlich | Beschreibung |
|--------|------|----------|--------------|--------------|
| A | `firstName` | String | ✅ Ja | Vorname des Athleten |
| B | `lastName` | String | ✅ Ja | Nachname des Athleten |
| C | `countryCode` | String (2-3 Zeichen) | ✅ Ja | Ländercode (muss in Countries existieren) - **Case-insensitive** |
| D | `gender` | String (M/F oder m/f) | ❌ Optional | Geschlecht: `M` (Männlich) oder `F` (Weiblich) |

### 📝 Beispiel-Datei (athletes.csv)

```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,ch,M
Petra,Vlhova,sk,F
Alexis,Pinturault,fr,M
Sofia,Goggia,it,F
Johannes,Boe,no,M
Marte,Roeiseland,no,F
Nathan,Chen,us,M
Yuzuru,Hanyu,jp,M
Ireen,Wust,nl,F
```

### 📌 Regeln & Anforderungen

| Regel | Beschreibung |
|-------|--------------|
| **Ländercode** | Der `countryCode` muss in der Countries-Tabelle existieren, sonst wird der Import fehlgeschlagen |
| **Eindeutigkeit** | Der Kombinationsname (firstName + lastName) sollte eindeutig sein, wird aber technisch nicht erzwungen |
| **Geschlecht** | Nur `M` oder `F` akzeptiert. Andere Werte oder leere Werte sind zulässig |
| **Header** | Die erste Zeile muss Spaltennamen enthalten: `firstName`, `lastName`, `countryCode` (und optional `gender`) |
| **Namen** | Dürfen nicht leer sein |
| **Case-Insensitivity** | Ländercode wird automatisch in Kleinbuchstaben konvertiert |

### ❌ Häufige Fehler

| Fehler | Grund | Lösung |
|--------|-------|--------|
| `ATHLETE_COUNTRY_NOT_FOUND` | Ländercode existiert nicht | Sicherstellen, dass das Land zuvor importiert wurde |
| `INVALID_DATA` | Name ist leer | Beide Namen (firstName, lastName) müssen angegeben sein |
| `MISSING_HEADER` | Header-Zeile fehlt | Erste Zeile muss die Spaltennamen enthalten |
| `INVALID_GENDER` | Ungültiges Geschlechtsformat | Nur `M` oder `F` verwenden (oder Feld leer lassen) |

### ⚠️ Wichtiger Hinweis

**Länder müssen VOR dem Import von Athleten existieren!** Andernfalls wird der Import mit `ATHLETE_COUNTRY_NOT_FOUND` fehlschlagen.

### ✅ Erfolgreiche Antwort

```json
{
  "importLogId": 2,
  "status": "COMPLETED",
  "importType": "ATHLETES",
  "filename": "athletes.csv",
  "totalRecords": 10,
  "successfulRecords": 10,
  "failedRecords": 0,
  "message": "Import completed. Success: 10, Failed: 0"
}
```

---

## 3️⃣ Ergebnisse (Results) importieren

### 📋 Spaltenstruktur

| Spalte | Name | Datentyp | Erforderlich | Beschreibung |
|--------|------|----------|--------------|--------------|
| A | `athleteFirstName` | String | ✅ Ja | Vorname des Athleten (muss in Athletes existieren) |
| B | `athleteLastName` | String | ✅ Ja | Nachname des Athleten (muss in Athletes existieren) |
| C | `sport` | String | ✅ Ja | Name der Sportart (z.B. "Alpine Skiing", "Biathlon") |
| D | `rank` | Zahl (Integer) | ✅ Ja | Platzierung (z.B. 1, 2, 3) - muss ≥ 1 sein |
| E | `timeOrPoints` | String/Zahl | ✅ Ja | Zeit oder Punkte (z.B. "1:32.03" oder "314.56") |
| F | `scoreType` | String (PTS/WINS/TIME) | ❌ Optional | Bewertungstyp: `PTS` (Punkte), `WINS` (Siege), `TIME` (Zeit) |
| G | `medal` | String (GOLD/SILVER/BRONZE) | ❌ Optional | Medaillentyp: `GOLD`, `SILVER`, `BRONZE` oder leer |

### 📝 Beispiel-Datei (results.csv)

```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Mikaela,Shiffrin,Alpine Skiing,2,1:32.47,TIME,SILVER
Petra,Vlhova,Alpine Skiing,3,1:32.88,TIME,BRONZE
Johannes,Boe,Biathlon,1,23:45.2,TIME,GOLD
Marte,Roeiseland,Biathlon,2,20:23.1,TIME,SILVER
Nathan,Chen,Figure Skating,1,314.56,PTS,GOLD
Yuzuru,Hanyu,Figure Skating,2,312.45,PTS,SILVER
Ireen,Wust,Speed Skating,1,1:43.50,TIME,GOLD
```

### 📌 Regeln & Anforderungen

| Regel | Beschreibung |
|-------|--------------|
| **Athlet-Existenz** | Der Athlet (mit firstName + lastName) muss in der Athletes-Tabelle existieren |
| **Platzierung** | Der `rank` muss eine positive ganze Zahl (≥ 1) sein |
| **Zeit/Punkte Format** | Kann verschiedene Formate haben: `1:32.03` (Zeit), `314.56` (Punkte), `23:45.2` (Zeit mit ms) |
| **ScoreType** | Nur `PTS`, `WINS` oder `TIME` akzeptiert. Optional, Standard ist keine Angabe |
| **Medal** | Nur `GOLD`, `SILVER`, `BRONZE` oder leer. **Case-sensitive!** |
| **Header** | Die erste Zeile muss Spaltennamen enthalten |
| **Sportart** | Die Sportart wird wie angegeben gespeichert - Schreibweise ist wichtig |

### ✅ Gültige Zeit-Formate

```
"1:32.03"       → MM:SS.MS (Minuten:Sekunden.Millisekunden)
"32.03"         → SS.MS (Sekunden.Millisekunden)
"1:02:30.5"     → HH:MM:SS.MS (Stunden:Minuten:Sekunden.Millisekunden)
"2:30"          → MM:SS
```

### ✅ Gültige Punkte-Formate

```
"314.56"        → Dezimalzahl
"100"           → Ganzzahl
"3.5"           → Dezimalzahl mit Komma
```

### ❌ Häufige Fehler

| Fehler | Grund | Lösung |
|--------|-------|--------|
| `ATHLETE_NOT_FOUND` | Athlet existiert nicht | Sicherstellen, dass Athletes importiert wurden und Namen korrekt sind |
| `INVALID_NUMBER_FORMAT` | rank oder timeOrPoints falsch formatiert | Rank muss integer, timeOrPoints kann Zeit oder Zahl sein |
| `INVALID_RANK` | Rank ist ≤ 0 | rank muss ≥ 1 sein |
| `INVALID_MEDAL` | Ungültiges Medaillen-Format | Nur `GOLD`, `SILVER`, `BRONZE` (groß geschrieben) |
| `INVALID_SCORE_TYPE` | Ungültiger scoreType | Nur `PTS`, `WINS`, `TIME` (groß geschrieben) |

### ⚠️ Wichtige Hinweise

1. **Athleten müssen VOR dem Import von Ergebnissen existieren!** Andernfalls wird der Import mit `ATHLETE_NOT_FOUND` fehlschlagen.

2. **Namen müssen exakt übereinstimmen!** (Groß-/Kleinschreibung wird beachtet)
   - Richtig: ✅ `Sofia` + `Goggia`
   - Falsch: ❌ `sofia` + `goggia` oder `Sofia` + `goggia`

3. **Sportarten** - Werden wie angegeben übernommen. Verwenden Sie konsistent die gleiche Schreibweise:
   - Richtig: ✅ Alle verwenden "Alpine Skiing"
   - Falsch: ❌ Mix aus "Alpine Skiing", "alpine skiing", "AlpineSKIING"

### ✅ Erfolgreiche Antwort

```json
{
  "importLogId": 3,
  "status": "COMPLETED",
  "importType": "RESULTS",
  "filename": "results.csv",
  "totalRecords": 8,
  "successfulRecords": 8,
  "failedRecords": 0,
  "message": "Import completed. Success: 8, Failed: 0"
}
```

---

## 🚀 Schritt-für-Schritt Anleitung zum Import

### Schritt 1: Länder vorbereiten & importieren

```
1. Datei "countries.csv" erstellen oder bereitstellen
2. Sicherstellen, dass die Spalten korrekt sind: code, name
3. Im Admin-Dashboard: Datei hochladen
4. Import starten und auf Bestätigung warten
✅ Länder sollten jetzt in der Datenbank existieren
```

### Schritt 2: Athleten vorbereiten & importieren

```
1. Datei "athletes.csv" erstellen oder bereitstellen
2. Sicherstellen, dass die Spalten korrekt sind: firstName, lastName, countryCode
3. Überprüfen, dass alle countryCode-Werte in Countries existieren
4. Im Admin-Dashboard: Datei hochladen
5. Import starten und auf Bestätigung warten
✅ Athleten sollten jetzt in der Datenbank existieren
```

### Schritt 3: Ergebnisse vorbereiten & importieren

```
1. Datei "results.csv" erstellen oder bereitstellen
2. Sicherstellen, dass die Spalten korrekt sind: athleteFirstName, athleteLastName, sport, rank, timeOrPoints
3. Überprüfen, dass alle athleteFirstName + athleteLastName-Kombinationen in Athletes existieren
4. Im Admin-Dashboard: Datei hochladen
5. Import starten und auf Bestätigung warten
✅ Ergebnisse sollten jetzt in der Datenbank existieren
```

---

## 🛠️ Troubleshooting

### Problem: "Import fehlgeschlagen mit 0 erfolgreichen Records"

**Schritt 1:** Überprüfen Sie die Import-Logs im Admin-Dashboard
```
Suchen Sie nach: Import-Fehler → Klicken Sie auf das fehlgeschlagene Import-Log
```

**Schritt 2:** Lesen Sie die fehlgeschlagenen Zeilen
```
Jede fehlerhafte Zeile wird mit dem genauen Fehler aufgelistet
```

**Schritt 3:** Beheben Sie den Fehler
```
- COUNTRY_NOT_FOUND? → Länder zuerst importieren
- ATHLETE_NOT_FOUND? → Athletes zuerst importieren
- Formatfehler? → Siehe Spalten-Format oben
```

### Problem: "Einige Zeilen sind fehlgeschlagen, andere erfolgreich"

```
✅ Das ist normal - die erfolgreich importierten Zeilen werden gespeichert
❌ Die fehlgeschlagenen Zeilen werden nicht importiert
→ Beheben Sie die Fehler in den fehlgeschlagenen Zeilen
→ Importieren Sie diese erneut
```

### Problem: "Cache zeigt alte Daten nach Import"

```
1. Warten Sie 1-2 Minuten (Cache wird automatisch aktualisiert)
   oder
2. Führen Sie einen Seitenaktualisierung durch (F5 oder Ctrl+R)
   oder
3. Kontaktieren Sie einen Admin zum manuellen Cache-Clear
```

---

## 📊 Beispiel: Vollständiger Import-Workflow

Hier ist ein Beispiel eines vollständigen Import-Workflows mit realen Daten:

### 📁 Datei 1: countries.csv
```csv
code,name
de,Germany
ch,Switzerland
at,Austria
```

### 📁 Datei 2: athletes.csv
```csv
firstName,lastName,countryCode,gender
Anna,Fenninger,at,F
Marcel,Hirscher,at,M
Lindsey,Vonn,us,F
```

### 📁 Datei 3: results.csv
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Anna,Fenninger,Alpine Skiing,1,1:45.23,TIME,GOLD
Marcel,Hirscher,Alpine Skiing,2,1:45.67,TIME,SILVER
Lindsey,Vonn,Alpine Skiing,3,1:46.12,TIME,BRONZE
```

### 🎯 Import-Ablauf:
```
1. countries.csv importieren → 3 Länder erfolgreich
2. athletes.csv importieren → 3 Athleten erfolgreich (bei "us" Fehler, da USA nicht in countries.csv)
3. results.csv importieren → 3 Ergebnisse erfolgreich
```

---

## 📞 Häufig gestellte Fragen

### F: Kann ich dieselbe Datei zweimal importieren?
**A:** Nein, bei Ländern wird es zu Duplikat-Fehlern führen. Bei Athleten und Ergebnissen werden neue Einträge erstellt.

### F: Kann ich Excel statt CSV verwenden?
**A:** Ja, `.xlsx` und `.xls` Dateien werden genauso akzeptiert wie CSV.

### F: Kann ich die Spaltenreihenfolge ändern?
**A:** Nein, die Spaltenreihenfolge ist festgelegt. Die Spalten müssen in der angegebenen Reihenfolge sein, können aber die gleichen Namen haben.

### F: Unterscheiden sich die Länder nach Groß-/Kleinschreibung?
**A:** Der Ländercode (`code`) wird automatisch in Kleinbuchstaben konvertiert, der Name wird so übernommen wie eingegeben.

### F: Was passiert mit Zeichen wie Umlauten (ä, ö, ü)?
**A:** Umlaute und Sonderzeichen werden korrekt unterstützt und übernommen.

### F: Kann ich leere Zeilen in der Datei haben?
**A:** Leere Zeilen am Ende werden ignoriert, aber leere Zeilen in der Mitte können zu Fehlern führen.

---

## 📞 Support

Falls Sie Probleme beim Import haben:

1. **Überprüfen Sie die Logs** im Admin-Dashboard
2. **Vergleichen Sie Ihre Datei** mit den Beispielen in diesem Dokument
3. **Befolgen Sie die Import-Reihenfolge** (Countries → Athletes → Results)
4. **Kontaktieren Sie einen Admin** bei technischen Problemen

---

**Letzte Aktualisierung:** 28.03.2026
**Version:** 1.0

