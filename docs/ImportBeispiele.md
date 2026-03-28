# 📚 Import-Beispiele & Best Practices

## 📖 Inhaltsverzeichnis

1. [Länder-Beispiele](#-länder-beispiele)
2. [Athleten-Beispiele](#-athleten-beispiele)
3. [Ergebnisse-Beispiele](#-ergebnisse-beispiele)
4. [Best Practices](#-best-practices)
5. [Häufige Fehler & deren Lösungen](#-häufige-fehler--deren-lösungen)

---

## 🌍 Länder-Beispiele

### Beispiel 1: Einfacher Import (Minimal)

**Dateiname:** `countries_simple.csv`

```csv
code,name
us,United States
de,Germany
fr,France
```

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 3,
  "successfulRecords": 3,
  "failedRecords": 0
}
```

---

### Beispiel 2: Großer Import (Olympia-Staaten)

**Dateiname:** `countries_olympic.csv`

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
kr,South Korea
mx,Mexico
br,Brazil
ru,Russia
se,Sweden
no,Norway
nl,Netherlands
ch,Switzerland
at,Austria
be,Belgium
```

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 20,
  "successfulRecords": 20,
  "failedRecords": 0
}
```

---

### Beispiel 3: Mit Sonderzeichen

**Dateiname:** `countries_special.csv`

```csv
code,name
cz,Czech Republic
gk,Côte d'Ivoire
nz,New Zealand
za,South Africa
ae,United Arab Emirates
```

**Notiz:** ✅ Sonderzeichen und Umlaute werden unterstützt

---

### ❌ Häufiger Fehler: Duplikate

**FALSCH - wird fehlschlagen:**
```csv
code,name
us,United States
us,USA
de,Germany
```

**Fehler:** `DUPLICATE_COUNTRY` - Code "us" existiert zweimal

**RICHTIG:**
```csv
code,name
us,United States
de,Germany
```

---

## 🏃 Athleten-Beispiele

### Beispiel 1: Einfacher Import (Winter-Sportarten)

**Dateiname:** `athletes_winter.csv`

```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,ch,M
Petra,Vlhova,sk,F
Johannes,Boe,no,M
```

**Voraussetzung:** Countries müssen existieren: `us`, `ch`, `sk`, `no`

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 4,
  "successfulRecords": 4,
  "failedRecords": 0
}
```

---

### Beispiel 2: Großer Import mit Geschlecht

**Dateiname:** `athletes_complete.csv`

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

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 10,
  "successfulRecords": 10,
  "failedRecords": 0
}
```

---

### Beispiel 3: Ohne Geschlechtseintrag (Optional-Feld)

**Dateiname:** `athletes_no_gender.csv`

```csv
firstName,lastName,countryCode
Mikaela,Shiffrin,us
Marco,Odermatt,ch
Petra,Vlhova,sk
```

**Notiz:** ✅ Geschlecht ist optional - leere Werte sind erlaubt

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 3,
  "successfulRecords": 3,
  "failedRecords": 0
}
```

---

### ❌ Häufiger Fehler: Ländercode existiert nicht

**FALSCH - wird fehlschlagen:**
```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,xx,M
```

**Fehler:** `ATHLETE_COUNTRY_NOT_FOUND` - Ländercode "xx" existiert nicht

**RICHTIG:**
```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,ch,M
```

---

### ❌ Häufiger Fehler: Leere Namen

**FALSCH - wird fehlschlagen:**
```csv
firstName,lastName,countryCode,gender
Mikaela,,us,F
,Odermatt,ch,M
```

**Fehler:** `INVALID_DATA` - firstName oder lastName ist leer

**RICHTIG:**
```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,ch,M
```

---

## 🏅 Ergebnisse-Beispiele

### Beispiel 1: Einfache Zeit-Ergebnisse

**Dateiname:** `results_time.csv`

```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Mikaela,Shiffrin,Alpine Skiing,2,1:32.47,TIME,SILVER
Petra,Vlhova,Alpine Skiing,3,1:32.88,TIME,BRONZE
```

**Voraussetzung:** Athletes müssen existieren mit exakten Namen

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 3,
  "successfulRecords": 3,
  "failedRecords": 0
}
```

---

### Beispiel 2: Punkte-basierte Ergebnisse

**Dateiname:** `results_points.csv`

```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Nathan,Chen,Figure Skating,1,314.56,PTS,GOLD
Yuzuru,Hanyu,Figure Skating,2,312.45,PTS,SILVER
Jin,Yang,Figure Skating,3,310.23,PTS,BRONZE
```

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 3,
  "successfulRecords": 3,
  "failedRecords": 0
}
```

---

### Beispiel 3: Gemischte Ergebnisse (Zeit, Punkte, Siege)

**Dateiname:** `results_mixed.csv`

```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Nathan,Chen,Figure Skating,1,314.56,PTS,GOLD
Johannes,Boe,Biathlon,1,23:45.2,TIME,GOLD
Mikaela,Shiffrin,Alpine Skiing,2,1:32.47,TIME,SILVER
Yuzuru,Hanyu,Figure Skating,2,312.45,PTS,SILVER
Marte,Roeiseland,Biathlon,2,20:23.1,TIME,SILVER
```

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 6,
  "successfulRecords": 6,
  "failedRecords": 0
}
```

---

### Beispiel 4: Ohne optionale Felder (medal und scoreType)

**Dateiname:** `results_minimal.csv`

```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints
Sofia,Goggia,Alpine Skiing,1,1:32.03
Nathan,Chen,Figure Skating,1,314.56
```

**Notiz:** ✅ medal und scoreType sind optional

**Erwartetes Ergebnis:**
```json
{
  "totalRecords": 2,
  "successfulRecords": 2,
  "failedRecords": 0
}
```

---

### ❌ Häufiger Fehler: Athlet existiert nicht

**FALSCH - wird fehlschlagen:**
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
sofia,goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
```

**Fehler:** `ATHLETE_NOT_FOUND` - Athlet "sofia goggia" existiert nicht (Groß-/Kleinschreibung!)

**RICHTIG:**
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
```

---

### ❌ Häufiger Fehler: Ungültiger rank

**FALSCH - wird fehlschlagen:**
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,0,1:32.03,TIME,GOLD
Sofia,Goggia,Alpine Skiing,-1,1:32.03,TIME,GOLD
```

**Fehler:** `INVALID_RANK` - rank muss ≥ 1 sein

**RICHTIG:**
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Sofia,Goggia,Alpine Skiing,2,1:32.03,TIME,GOLD
```

---

### ❌ Häufiger Fehler: Ungültige Medal-Werte

**FALSCH - wird fehlschlagen:**
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,gold
Nathan,Chen,Figure Skating,2,312.45,PTS,silver
```

**Fehler:** `INVALID_MEDAL` - Groß-/Kleinschreibung beachten!

**RICHTIG:**
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Nathan,Chen,Figure Skating,2,312.45,PTS,SILVER
```

---

## 🎯 Best Practices

### ✅ Praxis 1: Immer Backups erstellen

```
Vor einem großen Import:
1. Aktuelle Datenbank exportieren
2. Backup speichern
3. Import durchführen
4. Ergebnis überprüfen
→ Im Fehlerfall Backup wiederherstellen
```

---

### ✅ Praxis 2: Dateiformat konsistent halten

```
❌ VERMEIDEN: Mix verschiedener Formate
- Länder: UPPERCASE
- Athleten: lowercase
- Ergebnisse: MiXeD cAsE

✅ EMPFOHLEN: Konsistente Konvention
- Ländercode: IMMER lowercase (us, de, fr)
- Namen: IMMER wie in der Datenbank (Sofia, nicht sofia)
- Sportarten: IMMER gleich geschrieben (Alpine Skiing, nicht alpine skiing)
```

---

### ✅ Praxis 3: Daten vor Import validieren

```
Excel/CSV öffnen und folgende Checks durchführen:
1. ✓ Keine leeren Zeilen in der Mitte
2. ✓ Keine Duplikate
3. ✓ Alle erforderlichen Spalten vorhanden
4. ✓ Keine ungültigen Zeichen
5. ✓ Spaltennamen korrekt geschrieben
```

---

### ✅ Praxis 4: Kleine Tests vor Großimport

```
Workflow:
1. Mit 3-5 Beispiel-Zeilen testen
2. Import durchführen
3. Ergebnis überprüfen
4. Wenn erfolgreich: Mit kompletter Datei wiederholen
```

---

### ✅ Praxis 5: Import-Reihenfolge dokumentieren

```
In internen Prozessen festhalten:
1. IMMER: Countries zuerst
2. IMMER: Athletes zweite
3. IMMER: Results zuletzt

→ Diese Reihenfolge nie ignorieren!
```

---

### ✅ Praxis 6: Fehler-Logs regelmäßig überprüfen

```
Nach jedem Import:
1. Admin-Dashboard öffnen
2. Import-Logs Sektion aufrufen
3. Status überprüfen (COMPLETED vs FAILED)
4. Bei Fehlern: failedRecords überprüfen
5. Fehlgeschlagene Zeilen notieren
```

---

### ✅ Praxis 7: Konsistente Nomenklatur

**Sportarten - Empfohlene Namen:**
```
✅ Alpine Skiing
✅ Biathlon
✅ Cross-Country Skiing
✅ Figure Skating
✅ Freestyle Skiing
✅ Nordic Combined
✅ Short Track Speed Skating
✅ Ski Jumping
✅ Snowboarding
✅ Speed Skating
```

---

## 🔴 Häufige Fehler & deren Lösungen

### Fehler 1: `DUPLICATE_COUNTRY`

**Symptom:**
```
Import Status: COMPLETED_WITH_ERRORS
failedRecords: 1 (oder mehr)
Error Message: DUPLICATE_COUNTRY
```

**Ursache:** Ein Ländercode existiert bereits oder kommt doppelt vor

**Lösung:**
```
Option 1: Datei überprüfen auf Duplikate
- Ländercode überprüfen
- Ggfs. Duplikat-Zeile löschen

Option 2: Länder zuerst löschen
- Admin-Dashboard: Länder Management
- Problematisches Land löschen
- Neu importieren
```

---

### Fehler 2: `ATHLETE_COUNTRY_NOT_FOUND`

**Symptom:**
```
Import Status: COMPLETED_WITH_ERRORS
failedRecords: mehrere
Error Message: ATHLETE_COUNTRY_NOT_FOUND
```

**Ursache:** Ein Ländercode in Athletes existiert nicht in Countries

**Lösung:**
```
1. Countries import überprüfen
   → Wurde das Land vorher importiert?
   
2. Ländercode überprüfen
   → Schreibweise korrekt? (z.B. "ch" nicht "CH")
   
3. Länder nachträglich hinzufügen
   → Das fehlende Land zu Countries hinzufügen
   → Athletes erneut importieren
```

---

### Fehler 3: `ATHLETE_NOT_FOUND`

**Symptom:**
```
Import Status: COMPLETED_WITH_ERRORS
failedRecords: mehrere
Error Message: ATHLETE_NOT_FOUND
```

**Ursache:** Ein Athlete in Results existiert nicht in Athletes

**Lösung:**
```
1. Athletes-Import überprüfen
   → Wurde der Athlet importiert?
   
2. Namen überprüfen
   → Exakte Übereinstimmung? (Groß-/Kleinschreibung!)
   → Beispiel: "Sofia" nicht "sofia"
   
3. Athleten nachträglich hinzufügen
   → Den fehlenden Athleten zu Athletes hinzufügen
   → Results erneut importieren
```

---

### Fehler 4: `INVALID_RANK`

**Symptom:**
```
Import Status: COMPLETED_WITH_ERRORS
failedRecords: mehrere
Error Message: INVALID_RANK
```

**Ursache:** Rank ist keine positive Zahl oder 0/negativ

**Lösung:**
```
1. Datei öffnen
   → Spalte D (rank) überprüfen
   
2. Ungültige Werte korrigieren
   → Nur Zahlen ≥ 1 erlaubt
   → Beispiele: 1, 2, 3... nicht 0, -1
   
3. Erneut importieren
```

---

### Fehler 5: `INVALID_MEDAL` oder `INVALID_SCORE_TYPE`

**Symptom:**
```
Import Status: COMPLETED_WITH_ERRORS
Error Message: INVALID_MEDAL oder INVALID_SCORE_TYPE
```

**Ursache:** Ungültige Werte oder falsche Groß-/Kleinschreibung

**Lösung - für MEDAL:**
```
Erlaubte Werte (GROSS geschrieben):
✅ GOLD
✅ SILVER
✅ BRONZE
✅ (leer)

Nicht erlaubt:
❌ gold, silver, bronze (Kleinbuchstaben)
❌ Gold, Silver, Bronze (Teilweise Großbuchstaben)
```

**Lösung - für SCORE_TYPE:**
```
Erlaubte Werte (GROSS geschrieben):
✅ PTS (Punkte)
✅ TIME (Zeit)
✅ WINS (Siege)
✅ (leer)

Nicht erlaubt:
❌ pts, time, wins (Kleinbuchstaben)
❌ Pts, Time, Wins (Teilweise Großbuchstaben)
```

---

### Fehler 6: `INVALID_NUMBER_FORMAT`

**Symptom:**
```
Import Status: COMPLETED_WITH_ERRORS
Error Message: INVALID_NUMBER_FORMAT
```

**Ursache:** Spalte "timeOrPoints" hat ungültiges Format

**Lösung:**
```
✅ Gültige Zeit-Formate:
   - "1:32.03" (MM:SS.MS)
   - "32.03" (SS.MS)
   - "1:02:30" (HH:MM:SS)

✅ Gültige Punkte-Formate:
   - "314.56" (Dezimalzahl)
   - "100" (Ganzzahl)

❌ Ungültig:
   - "1,32.03" (Komma + Punkt)
   - "1h32m03s" (mit Buchstaben)
   - "invalid" (Text)
```

---

## 📋 Komplettes Beispiel-Workflow

### Szenario: Olympische Spiele einrichten

**Schritt 1: Länder vorbereiten**

Datei: `countries.csv`
```csv
code,name
us,United States
de,Germany
fr,France
gb,Great Britain
jp,Japan
```

Import durchführen → ✅ 5 Länder importiert

---

**Schritt 2: Athleten vorbereiten**

Datei: `athletes.csv`
```csv
firstName,lastName,countryCode,gender
Sofia,Goggia,it,F
Mikaela,Shiffrin,us,F
Petra,Vlhova,sk,F
Nathan,Chen,us,M
Yuzuru,Hanyu,jp,M
```

**Problem:** Länder "it", "sk", "nl" fehlen!

**Lösung:** Countries ergänzen
```csv
code,name
us,United States
de,Germany
fr,France
gb,Great Britain
jp,Japan
it,Italy
sk,Slovakia
nl,Netherlands
```

Import durchführen → ✅ 8 Länder, ✅ 5 Athleten

---

**Schritt 3: Ergebnisse vorbereiten**

Datei: `results.csv`
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Mikaela,Shiffrin,Alpine Skiing,2,1:32.47,TIME,SILVER
Petra,Vlhova,Alpine Skiing,3,1:32.88,TIME,BRONZE
Nathan,Chen,Figure Skating,1,314.56,PTS,GOLD
Yuzuru,Hanyu,Figure Skating,2,312.45,PTS,SILVER
```

Import durchführen → ✅ 5 Ergebnisse

---

## 📞 Zusätzliche Ressourcen

- [Vollständiges Import-Format Guide](./ImportDateiFormat.md)
- [Import-Checkliste](./ImportCheckliste.md)
- [API Dokumentation](./FeatureExcelImport.md)
- [Test-Dateien](../test-excel-files/)

---

**Letzte Aktualisierung:** 28.03.2026  
**Version:** 1.0

