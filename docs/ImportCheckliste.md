# ✅ Import-Checkliste & Quick Reference

## 🎯 Vor dem Import - Checkliste

### ✅ Allgemein
- [ ] Datei ist in einem unterstützten Format (`.xlsx`, `.xls`, oder `.csv`)
- [ ] Sie sind als Admin angemeldet
- [ ] Sie haben die richtige Import-Reihenfolge notiert (Countries → Athletes → Results)
- [ ] Sie haben Backups erstellt, falls nötig

---

## 📋 Länder (Countries) - Checkliste

### ✅ Dateistruktur
- [ ] Erste Zeile enthält: `code,name`
- [ ] Spalte A: Ländercode (2-3 Zeichen, z.B. "de", "us")
- [ ] Spalte B: Vollständiger Länder-Name
- [ ] Keine leeren Zeilen in der Mitte der Datei

### ✅ Datenqualität
- [ ] Keine Duplikate in der `code`-Spalte
- [ ] Alle `name`-Werte sind gefüllt
- [ ] Länder-Codes sind eindeutig

### ✅ Format-Beispiel
```csv
code,name
de,Germany
fr,France
us,United States
```

### ✅ Nach dem Import
- [ ] Status ist "COMPLETED"
- [ ] Alle Zeilen wurden erfolgreich importiert (failedRecords = 0)
- [ ] Im Admin-Dashboard werden die Länder angezeigt

---

## 🏃 Athleten (Athletes) - Checkliste

### ✅ Dateistruktur
- [ ] Erste Zeile enthält: `firstName,lastName,countryCode` (und optional `gender`)
- [ ] Spalte A: Vorname
- [ ] Spalte B: Nachname
- [ ] Spalte C: Ländercode (muss in Countries existieren!)
- [ ] Spalte D (Optional): Geschlecht (`M` oder `F`)
- [ ] Keine leeren Zeilen in der Mitte der Datei

### ✅ Datenqualität
- [ ] Alle `firstName`-Werte sind gefüllt
- [ ] Alle `lastName`-Werte sind gefüllt
- [ ] Alle `countryCode`-Werte existieren in Countries
- [ ] Länder-Codes sind in Kleinbuchstaben
- [ ] Geschlecht ist `M` oder `F` (oder leer)

### ⚠️ Abhängigkeiten
- [ ] **Countries wurden ZUERST importiert!**
- [ ] Alle verwendeten Länder-Codes existieren in der Countries-Tabelle
- [ ] Beispiel Fehler vermeiden: Nicht `us` verwenden, wenn USA nicht in Countries ist

### ✅ Format-Beispiel
```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,ch,M
Nathan,Chen,us,M
```

### ✅ Nach dem Import
- [ ] Status ist "COMPLETED"
- [ ] Alle Zeilen wurden erfolgreich importiert (failedRecords = 0)
- [ ] Im Admin-Dashboard werden die Athleten angezeigt
- [ ] Länder-Zuordnung ist korrekt

---

## 🏅 Ergebnisse (Results) - Checkliste

### ✅ Dateistruktur
- [ ] Erste Zeile enthält: `athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal`
- [ ] Spalte A: Vorname des Athleten
- [ ] Spalte B: Nachname des Athleten
- [ ] Spalte C: Sportart-Name
- [ ] Spalte D: Platzierung (Zahl ≥ 1)
- [ ] Spalte E: Zeit oder Punkte
- [ ] Spalte F (Optional): ScoreType (`PTS`, `WINS`, oder `TIME`)
- [ ] Spalte G (Optional): Medaille (`GOLD`, `SILVER`, `BRONZE`)
- [ ] Keine leeren Zeilen in der Mitte der Datei

### ✅ Datenqualität
- [ ] Alle `athleteFirstName`-Werte sind gefüllt
- [ ] Alle `athleteLastName`-Werte sind gefüllt
- [ ] Alle `sport`-Werte sind gefüllt
- [ ] Alle `rank`-Werte sind positive Zahlen (≥ 1)
- [ ] `timeOrPoints` hat das richtige Format (Zeit oder Zahl)
- [ ] `scoreType` ist `PTS`, `WINS`, `TIME` oder leer
- [ ] `medal` ist `GOLD`, `SILVER`, `BRONZE` oder leer

### ⚠️ Abhängigkeiten
- [ ] **Athletes wurden ZUERST importiert!**
- [ ] Alle Athleten (firstName + lastName) existieren in der Athletes-Tabelle
- [ ] **Namen müssen EXAKT übereinstimmen** (auch Groß-/Kleinschreibung)
- [ ] Sportarten sind konsistent geschrieben

### ✅ Zeit-Format-Beispiele
```
"1:32.03"       ✅ MM:SS.MS
"32.03"         ✅ SS.MS
"1:02:30.5"     ✅ HH:MM:SS.MS
"2:30"          ✅ MM:SS
```

### ✅ Punkte-Format-Beispiele
```
"314.56"        ✅ Dezimalzahl
"100"           ✅ Ganzzahl
"3.5"           ✅ Mit Dezimaltrennzeichen
```

### ✅ Format-Beispiel
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
Mikaela,Shiffrin,Alpine Skiing,2,1:32.47,TIME,SILVER
Nathan,Chen,Figure Skating,1,314.56,PTS,GOLD
```

### ✅ Nach dem Import
- [ ] Status ist "COMPLETED"
- [ ] Alle Zeilen wurden erfolgreich importiert (failedRecords = 0)
- [ ] Im Admin-Dashboard werden die Ergebnisse angezeigt
- [ ] Athleten-Zuordnung ist korrekt

---

## 🚨 Fehler-Troubleshooting

### Problem: `COUNTRY_NOT_FOUND` oder `ATHLETE_COUNTRY_NOT_FOUND`
```
❌ FEHLER: Ländercode existiert nicht
✅ LÖSUNG: 
   - Countries importieren ZUERST
   - Ländercode überprüfen (Case: "de" nicht "DE")
   - Im Countries-Import sicherstellen, dass das Land mit diesem Code existiert
```

### Problem: `ATHLETE_NOT_FOUND`
```
❌ FEHLER: Athlet existiert nicht
✅ LÖSUNG:
   - Athletes importieren VOR Results
   - Namen überprüfen (exakte Übereinstimmung!)
   - Beispiel: "Sofia" (nicht "sofia") und "Goggia" (nicht "goggia")
```

### Problem: `DUPLICATE_COUNTRY`
```
❌ FEHLER: Ländercode existiert bereits
✅ LÖSUNG:
   - Duplikate in der Datei entfernen
   - Ländercode überprüfen
   - Optional: Country löschen und neu importieren
```

### Problem: `INVALID_RANK` oder `INVALID_NUMBER_FORMAT`
```
❌ FEHLER: Rank oder timeOrPoints falsch formatiert
✅ LÖSUNG:
   - rank muss eine positive Zahl sein (1, 2, 3...)
   - timeOrPoints muss Zeit (1:32.03) oder Zahl (314.56) sein
   - Keine Buchstaben oder ungültigen Zeichen
```

### Problem: `INVALID_MEDAL` oder `INVALID_SCORE_TYPE`
```
❌ FEHLER: Medal oder scoreType hat ungültigen Wert
✅ LÖSUNG:
   - medal: Nur "GOLD", "SILVER", "BRONZE" oder leer (Großbuchstaben!)
   - scoreType: Nur "PTS", "WINS", "TIME" oder leer (Großbuchstaben!)
   - Beachten Sie die Groß-/Kleinschreibung genau
```

---

## 📊 Import-Status erklärt

### ✅ COMPLETED
```
Alle Zeilen wurden erfolgreich importiert
- totalRecords: Anzahl der Zeilen in der Datei
- successfulRecords: Alle wurden importiert
- failedRecords: 0
→ Keine Aktion nötig
```

### ⚠️ COMPLETED_WITH_ERRORS
```
Einige Zeilen wurden importiert, andere nicht
- totalRecords: Anzahl der Zeilen in der Datei
- successfulRecords: Teilweise importiert
- failedRecords: > 0
→ Fehler in den fehlgeschlagenen Zeilen beheben und neu importieren
```

### ❌ FAILED
```
Import ist völlig fehlgeschlagen
- totalRecords: Anzahl der Zeilen in der Datei
- successfulRecords: 0
- failedRecords: Alle
→ Dateiformat oder allgemeiner Fehler → Siehe Logs
```

---

## 📝 Import-Reihenfolge Übersicht

```
┌─────────────────────────────────────────┐
│ SCHRITT 1: Countries importieren        │
│ ✓ Länder in die Datenbank einfügen      │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│ SCHRITT 2: Athletes importieren         │
│ ✓ Athleten zuordnen zu Countries        │
│ ✓ Abhängigkeit: Countries müssen         │
│   existieren!                            │
└─────────────────────────────────────────┘
                   ↓
┌─────────────────────────────────────────┐
│ SCHRITT 3: Results importieren          │
│ ✓ Ergebnisse zuordnen zu Athletes       │
│ ✓ Abhängigkeit: Athletes müssen         │
│   existieren!                            │
└─────────────────────────────────────────┘
```

---

## 🎯 Schnell-Tipps

| Tipp | Beschreibung |
|------|--------------|
| 💡 **Immer die Logs prüfen** | Nach jedem Import → Admin-Dashboard → Import-Logs überprüfen |
| 💡 **Namen müssen exakt sein** | Groß-/Kleinschreibung beachten (z.B. "Sofia" nicht "sofia") |
| 💡 **Länder zuerst!** | Countries → Athletes → Results (in dieser Reihenfolge!) |
| 💡 **CSV vs Excel** | Beide Formate funktionieren, CSV ist am sichersten |
| 💡 **Duplikate vermeiden** | Bei Countries: Ländercode muss eindeutig sein |
| 💡 **Test-Dateien nutzen** | `/test-excel-files/` enthält Beispiele zum Testen |

---

## 📞 Hilfreiche Links

- [Vollständiges Import-Format-Dokument](./ImportDateiFormat.md)
- [Excel Import Feature-Dokumentation](./FeatureExcelImport.md)
- [API Dokumentation](./API-Overview.md)

---

**Letzte Aktualisierung:** 28.03.2026  
**Version:** 1.0

