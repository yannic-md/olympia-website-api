# 📑 Import-Dokumentation - Übersichtsseite

Willkommen in der Import-Dokumentation für das Olympia-Website-API System! Diese Seite bietet einen schnellen Überblick über alle verfügbaren Dokumentationen.

---

## 📚 Verfügbare Dokumentationen

### 1. 📋 **[ImportDateiFormat.md](./ImportDateiFormat.md)** - Das Hauptdokument

**Für wen?** Benutzer, die die genauen Anforderungen für Import-Dateien verstehen möchten.

**Inhalte:**
- ✅ Detaillierte Spaltenstruktur für Länder, Athleten und Ergebnisse
- ✅ Formatierungsregeln und Anforderungen
- ✅ Häufige Fehler und deren Lösungen
- ✅ Schritt-für-Schritt Anleitung
- ✅ Troubleshooting-Guide
- ✅ FAQs

**Nutzen Sie dieses Dokument für:**
- 🎯 Aufbau von neuen Import-Dateien
- 🎯 Verständnis der Datenformate
- 🎯 Fehler-Behebung

---

### 2. ✅ **[ImportCheckliste.md](./ImportCheckliste.md)** - Quick Reference

**Für wen?** Benutzer, die schnell überprüfen möchten, ob ihre Datei korrekt ist.

**Inhalte:**
- ✅ Checklisten für jede Datentyp (Länder, Athleten, Ergebnisse)
- ✅ Schnelle Format-Überblicke
- ✅ Fehler-Troubleshooting Kurz-Guide
- ✅ Import-Reihenfolge
- ✅ Schnell-Tipps

**Nutzen Sie dieses Dokument für:**
- 🎯 Schnelle Überprüfung vor dem Import
- 🎯 Fehlerhafte Imports schnell debuggen
- 🎯 Die richtige Reihenfolge nicht zu vergessen

---

### 3. 📚 **[ImportBeispiele.md](./ImportBeispiele.md)** - Praktische Beispiele

**Für wen?** Benutzer, die praktische Beispiele und Best Practices sehen möchten.

**Inhalte:**
- ✅ Vollständige Beispiel-Dateien für alle Datentypen
- ✅ Häufige Fehler mit Lösungen
- ✅ Best Practices
- ✅ Komplette Workflow-Beispiele
- ✅ Do's and Don'ts

**Nutzen Sie dieses Dokument für:**
- 🎯 Konkrete Beispiele sehen
- 🎯 Best Practices verstehen
- 🎯 Von Fehlern anderer lernen

---

### 4. 📖 **[FeatureExcelImport.md](./FeatureExcelImport.md)** - Technische Details

**Für wen?** Entwickler und technische Nutzer, die die API-Details verstehen möchten.

**Inhalte:**
- ✅ API-Endpunkte
- ✅ Authentifizierung
- ✅ Request/Response Formate
- ✅ Statusmeldungen

**Nutzen Sie dieses Dokument für:**
- 🎯 API-Integration
- 🎯 Programmgesteuerte Imports
- 🎯 Technische Details

---

## 🚀 Schnelleinstieg (3 Schritte)

### Schritt 1️⃣ : Import-Reihenfolge verstehen

```
1. Länder (Countries) importieren
2. Athleten (Athletes) importieren
3. Ergebnisse (Results) importieren
```

**Warum?** Athleten brauchen Länder, Ergebnisse brauchen Athleten.

---

### Schritt 2️⃣ : Datei vorbereiten

Verwenden Sie folgende Formate:

**Länder:**
```csv
code,name
us,United States
de,Germany
```

**Athleten:**
```csv
firstName,lastName,countryCode,gender
Mikaela,Shiffrin,us,F
Marco,Odermatt,ch,M
```

**Ergebnisse:**
```csv
athleteFirstName,athleteLastName,sport,rank,timeOrPoints,scoreType,medal
Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD
```

---

### Schritt 3️⃣ : Importieren und überprüfen

1. Admin-Dashboard öffnen
2. Datei hochladen
3. Import starten
4. Status überprüfen
5. ✅ Fertig!

---

## 🎯 Nach Anwendungsfall

### 👤 "Ich bin Anfänger und möchte schnell starten"

1. Lesen Sie: [ImportCheckliste.md](./ImportCheckliste.md) (5 Minuten)
2. Kopieren Sie: Beispiele aus [ImportBeispiele.md](./ImportBeispiele.md)
3. Testen Sie: Mit 3-5 Zeilen
4. Importieren Sie: Die komplette Datei

---

### 📋 "Ich muss eine große Datei importieren"

1. Lesen Sie: [ImportDateiFormat.md](./ImportDateiFormat.md) (15 Minuten)
2. Vorbereitung: [ImportCheckliste.md](./ImportCheckliste.md) durchgehen
3. Validierung: Datei überprüfen
4. Test-Import: Mit kleinem Subset
5. Produktion: Kompletter Import

---

### 🔍 "Mein Import schlägt fehl, ich brauche Hilfe"

1. Siehe: [ImportDateiFormat.md - Häufige Fehler](./ImportDateiFormat.md#häufige-fehler)
2. Überprüfen Sie: [ImportCheckliste.md - Error Troubleshooting](./ImportCheckliste.md#-fehler-troubleshooting)
3. Vergleichen Sie: Mit [ImportBeispiele.md](./ImportBeispiele.md#-häufige-fehler--deren-lösungen)

---

### 👨‍💻 "Ich entwickle und benötige die API-Details"

1. Lesen Sie: [FeatureExcelImport.md](./FeatureExcelImport.md)
2. Siehe auch: [ImportDateiFormat.md - Regeln](./ImportDateiFormat.md)

---

## 📊 Datentyp-Übersicht

### 🌍 Länder (Countries)

| Eigenschaft | Wert |
|------------|------|
| **Spalten** | `code`, `name` |
| **code Format** | 2-3 Zeichen, Kleinbuchstaben (z.B. "us", "de") |
| **Abhängigkeiten** | Keine (zuerst importieren!) |
| **Beispiel** | `us,United States` |

**Dokumentation:** [ImportDateiFormat.md → Länder](./ImportDateiFormat.md#1️⃣-länder-countries-importieren)

---

### 🏃 Athleten (Athletes)

| Eigenschaft | Wert |
|------------|------|
| **Spalten** | `firstName`, `lastName`, `countryCode`, `gender` (optional) |
| **countryCode** | Muss in Countries existieren |
| **gender** | `M` oder `F` (optional) |
| **Abhängigkeiten** | **Countries müssen existieren!** |
| **Beispiel** | `Mikaela,Shiffrin,us,F` |

**Dokumentation:** [ImportDateiFormat.md → Athleten](./ImportDateiFormat.md#2️⃣-athleten-athletes-importieren)

---

### 🏅 Ergebnisse (Results)

| Eigenschaft | Wert |
|------------|------|
| **Spalten** | `athleteFirstName`, `athleteLastName`, `sport`, `rank`, `timeOrPoints`, `scoreType` (optional), `medal` (optional) |
| **rank** | Positive Ganzzahl (≥ 1) |
| **scoreType** | `PTS`, `WINS`, `TIME` oder leer |
| **medal** | `GOLD`, `SILVER`, `BRONZE` oder leer |
| **Abhängigkeiten** | **Athletes müssen existieren!** |
| **Beispiel** | `Sofia,Goggia,Alpine Skiing,1,1:32.03,TIME,GOLD` |

**Dokumentation:** [ImportDateiFormat.md → Ergebnisse](./ImportDateiFormat.md#3️⃣-ergebnisse-results-importieren)

---

## 🔗 Wichtigste Links

| Link | Beschreibung | Für wen |
|------|--------------|---------|
| [ImportDateiFormat.md](./ImportDateiFormat.md) | Vollständige Format-Dokumentation | Alle |
| [ImportCheckliste.md](./ImportCheckliste.md) | Schnelle Checkliste & Quick Reference | Schnell-Nutzer |
| [ImportBeispiele.md](./ImportBeispiele.md) | Praktische Beispiele & Best Practices | Lernende |
| [FeatureExcelImport.md](./FeatureExcelImport.md) | API-Details & Technisches | Entwickler |
| [/test-excel-files/](../test-excel-files/) | Test-Dateien zum Ausprobieren | Alle |

---

## ⚠️ Wichtige Regeln (Nicht vergessen!)

### 🎯 Regel 1: Import-Reihenfolge

```
Countries → Athletes → Results
(NICHT: Athletes → Countries!)
```

### 🎯 Regel 2: Namen müssen exakt passen

```
Athlet in Athletes: "Sofia Goggia"
Athlet in Results:  "Sofia Goggia"  ✅ KORREKT
                     "sofia goggia"  ❌ FALSCH
```

### 🎯 Regel 3: Ländercode in Kleinbuchstaben

```
✅ "us", "de", "fr" (Kleinbuchstaben)
❌ "US", "DE", "FR" (Großbuchstaben)
```

### 🎯 Regel 4: Medaille und ScoreType in Großbuchstaben

```
✅ "GOLD", "SILVER", "BRONZE" (Großbuchstaben)
❌ "gold", "silver", "bronze" (Kleinbuchstaben)

✅ "PTS", "TIME", "WINS" (Großbuchstaben)
❌ "pts", "time", "wins" (Kleinbuchstaben)
```

---

## 🆘 Häufig gestellte Fragen

### F: Kann ich die Importreihenfolge ändern?
**A:** Nein. Countries müssen vor Athletes importiert werden, Athletes müssen vor Results importiert werden.

### F: Was passiert bei Duplikaten?
**A:** Bei Countries wird ein Fehler geworfen. Bei Athletes und Results werden neue Einträge erstellt.

### F: Kann ich eine falsche Spaltenreihenfolge verwenden?
**A:** Nein. Die Spalten müssen in der festgelegten Reihenfolge sein, aber die Spaltennamen müssen korrekt sein.

### F: Werden Umlaute unterstützt?
**A:** Ja, Umlaute und Sonderzeichen werden vollständig unterstützt.

### F: Was ist die maximale Dateigröße?
**A:** Das System kann große Dateien importieren. Im Zweifelsfall: testen Sie mit 100+ Zeilen.

---

## 📞 Support

Falls Sie Fragen haben:

1. **Fragen Sie die Dokumentation:**
   - [ImportDateiFormat.md - FAQs](./ImportDateiFormat.md#-häufig-gestellte-fragen)
   - [ImportBeispiele.md - Häufige Fehler](./ImportBeispiele.md#-häufige-fehler--deren-lösungen)

2. **Überprüfen Sie die Logs:**
   - Admin-Dashboard → Import-Logs
   - Sehen Sie sich die fehlgeschlagenen Zeilen an

3. **Vergleichen Sie mit Beispielen:**
   - [ImportBeispiele.md](./ImportBeispiele.md) - Praktische Beispiele
   - [/test-excel-files/](../test-excel-files/) - Test-Dateien

---

## 🎓 Lernpfade

### 🟢 Anfänger-Lernpfad (30 Minuten)

1. Lesen Sie: Diese Seite (Übersicht)
2. Lesen Sie: [ImportCheckliste.md](./ImportCheckliste.md)
3. Sehen Sie: Beispiele in [ImportBeispiele.md](./ImportBeispiele.md)
4. Praktizieren: Testen Sie mit Test-Dateien

### 🟡 Mittelstufen-Lernpfad (1-2 Stunden)

1. Lesen Sie: [ImportDateiFormat.md](./ImportDateiFormat.md) - ganz
2. Lesen Sie: [ImportBeispiele.md](./ImportBeispiele.md) - ganz
3. Studieren Sie: Alle Fehlerbeispiele
4. Praktizieren: Erstellen Sie Ihre eigene Datei

### 🔴 Fortgeschrittener Lernpfad (2+ Stunden)

1. Lesen Sie: [FeatureExcelImport.md](./FeatureExcelImport.md)
2. Lesen Sie: [ImportDateiFormat.md](./ImportDateiFormat.md) - alle Details
3. Studieren Sie: Quellcode und API-Integration
4. Implementieren: Programmgesteuerte Importe

---

## 🔍 Nach Fehlertyp suchen

- **`DUPLICATE_COUNTRY`** → [ImportDateiFormat.md - Länder Fehler](./ImportDateiFormat.md#häufige-fehler) oder [ImportBeispiele.md - Fehler 1](./ImportBeispiele.md#fehler-1-duplicate_country)

- **`ATHLETE_COUNTRY_NOT_FOUND`** → [ImportDateiFormat.md - Athleten Fehler](./ImportDateiFormat.md#häufige-fehler-1) oder [ImportBeispiele.md - Fehler 2](./ImportBeispiele.md#fehler-2-athlete_country_not_found)

- **`ATHLETE_NOT_FOUND`** → [ImportDateiFormat.md - Ergebnisse Fehler](./ImportDateiFormat.md#häufige-fehler-2) oder [ImportBeispiele.md - Fehler 3](./ImportBeispiele.md#fehler-3-athlete_not_found)

- **`INVALID_RANK`** → [ImportBeispiele.md - Fehler 4](./ImportBeispiele.md#fehler-4-invalid_rank)

- **`INVALID_MEDAL`** oder **`INVALID_SCORE_TYPE`** → [ImportBeispiele.md - Fehler 5](./ImportBeispiele.md#fehler-5-invalid_medal-oder-invalid_score_type)

- **`INVALID_NUMBER_FORMAT`** → [ImportBeispiele.md - Fehler 6](./ImportBeispiele.md#fehler-6-invalid_number_format)

---

**Letzte Aktualisierung:** 28.03.2026  
**Version:** 1.0  
**Dokumentation vollständig:** ✅

