# Public Leaderboard API Dokumentation

## Übersicht
Diese öffentliche API bietet Endpunkte zum Abrufen von Turnier-Ergebnissen für die Leaderboard-Tabelle im Frontend. Diese Endpunkte sind **ohne Authentifizierung** zugänglich und können auf der Startseite verwendet werden.

## Übersetzungen

Die Leaderboard-Endpunkte unterstützen **drei Sprachen** über den Query-Parameter `lang`:

| Parameter | Sprache   | Beispiel                              |
|-----------|-----------|---------------------------------------|
| `en`      | Englisch  | `/api/public/leaderboard?lang=en`     |
| `de`      | Deutsch   | `/api/public/leaderboard?lang=de`     |
| `fr`      | Französisch | `/api/public/leaderboard?lang=fr`   |

**Standard:** `en` (wenn kein `lang` Parameter angegeben wird)

### Übersetzte Felder
Die folgenden Felder werden je nach Sprache übersetzt:

| Feld          | EN (Default)    | DE                  | FR                              |
|---------------|-----------------|---------------------|---------------------------------|
| `sportName`   | Alpine Skiing   | Ski Alpin           | Ski alpin                       |
| `countryName` | Germany         | Deutschland         | Allemagne                       |
| `medal`       | Gold            | Gold                | Or                              |
| `scoreType`   | Points          | Punkte              | Points                          |

**Nicht übersetzte Felder:** `athleteName`, `countryCode`, `timeOrPoints`, `rank`, `resultId`

## Caching-Strategie

### Backend-Caching
- **Spring Cache** (`@Cacheable`) aktiviert
- Cache-Name: `leaderboard`
- Automatisches Caching aller Abfragen
- Reduziert Datenbankzugriffe erheblich

### Frontend-Caching
- **HTTP Cache-Control** Header aktiviert
- `Cache-Control: max-age=300, public` (5 Minuten)
- Browser und Proxies können Responses cachen
- Reduziert Netzwerk-Traffic

### Vorteil
Durch das zweistufige Caching (Backend + Frontend) wird bei mehrfacher Verwendung des Endpunkts (z.B. Startseite + Leaderboard-Seite) nur **eine** Datenbankabfrage durchgeführt!

---

## Endpunkte

### 1. Alle Turnier-Ergebnisse abrufen
**GET** `/api/public/leaderboard?lang={en|de|fr}`

Gibt alle Ergebnisse aus der Datenbank zurück, sortiert nach Rang.

**Authentifizierung:** Nicht erforderlich (öffentlich)

**Query-Parameter:**
| Parameter | Typ    | Pflicht | Default | Beschreibung                    |
|-----------|--------|---------|---------|----------------------------------|
| `lang`    | String | Nein    | `en`    | Sprache: `en`, `de` oder `fr`   |

**Response (lang=en):**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Katie Ledecky",
    "countryCode": "us",
    "countryName": "United States",
    "timeOrPoints": "3:59.34",
    "scoreType": "Time",
    "medal": "Gold",
    "sportName": "Swimming"
  }
]
```

**Response (lang=de):**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Katie Ledecky",
    "countryCode": "us",
    "countryName": "Vereinigte Staaten",
    "timeOrPoints": "3:59.34",
    "scoreType": "Zeit",
    "medal": "Gold",
    "sportName": "Schwimmen"
  }
]
```

**Response (lang=fr):**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Katie Ledecky",
    "countryCode": "us",
    "countryName": "États-Unis",
    "timeOrPoints": "3:59.34",
    "scoreType": "Temps",
    "medal": "Or",
    "sportName": "Natation"
  }
]
```

**Response Headers:**
```
Cache-Control: max-age=300, public
Content-Type: application/json
```

**Felder:**
- `resultId` - ID des Ergebnisses
- `rank` - Platzierung (1, 2, 3, ... oder null)
- `athleteName` - Vollständiger Name des Athleten (nicht übersetzt)
- `countryCode` - Ländercode ISO 3166-1 alpha-2 (z.B. "us", "de")
- `countryName` - Vollständiger Ländername (übersetzt je nach `lang`)
- `timeOrPoints` - Zeit oder Punkte
- `scoreType` - Typ des Werts in `timeOrPoints` (übersetzt: "Time"/"Zeit"/"Temps", "Points"/"Punkte"/"Points", "Wins"/"Siege"/"Victoires")
- `medal` - Medaille (übersetzt: "Gold"/"Gold"/"Or", "Silver"/"Silber"/"Argent", "Bronze"/"Bronze"/"Bronze" oder null)
- `sportName` - Name der Sportart (übersetzt je nach `lang`, z.B. "Alpine Skiing"/"Ski Alpin"/"Ski alpin")

---

### 2. Nur Medaillen-Gewinner abrufen
**GET** `/api/public/leaderboard/medals?lang={en|de|fr}`

Gibt nur Ergebnisse mit Medaillen zurück, sortiert nach Medaillen-Typ (Gold → Silber → Bronze).

**Authentifizierung:** Nicht erforderlich (öffentlich)

**Query-Parameter:**
| Parameter | Typ    | Pflicht | Default | Beschreibung                    |
|-----------|--------|---------|---------|----------------------------------|
| `lang`    | String | Nein    | `en`    | Sprache: `en`, `de` oder `fr`   |

**Response (lang=de):**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Katie Ledecky",
    "countryCode": "us",
    "countryName": "Vereinigte Staaten",
    "timeOrPoints": "3:59.34",
    "scoreType": "Zeit",
    "medal": "Gold",
    "sportName": "Schwimmen"
  },
  {
    "resultId": 3,
    "rank": 1,
    "athleteName": "Max Mustermann",
    "countryCode": "de",
    "countryName": "Deutschland",
    "timeOrPoints": "9.85",
    "scoreType": "Zeit",
    "medal": "Gold",
    "sportName": "Leichtathletik"
  },
  {
    "resultId": 2,
    "rank": 2,
    "athleteName": "Caeleb Dressel",
    "countryCode": "us",
    "countryName": "Vereinigte Staaten",
    "timeOrPoints": "4:01.12",
    "scoreType": "Zeit",
    "medal": "Silber",
    "sportName": "Schwimmen"
  }
]
```

**Response Headers:**
```
Cache-Control: max-age=300, public
Content-Type: application/json
```

---

## Beispiel-Requests

### Mit cURL
```bash
# Alle Ergebnisse abrufen (Englisch - Standard)
curl http://localhost:8080/api/public/leaderboard

# Alle Ergebnisse auf Deutsch
curl http://localhost:8080/api/public/leaderboard?lang=de

# Alle Ergebnisse auf Französisch
curl http://localhost:8080/api/public/leaderboard?lang=fr

# Nur Medaillen-Gewinner (Deutsch)
curl http://localhost:8080/api/public/leaderboard/medals?lang=de

# Mit Cache-Control Headers anzeigen
curl -I http://localhost:8080/api/public/leaderboard?lang=en
```
---- 

## Performance & Caching

### Backend-Caching
Der Service verwendet Spring Cache (`@Cacheable`):
```java
@Cacheable(value = "leaderboard", key = "'all'")
public List<LeaderboardEntryResponse> getAllResults() {
    // Wird nur beim ersten Request ausgeführt
    // Nachfolgende Requests kommen aus dem Cache
}
```

**Vorteile:**
- ✅ Reduziert Datenbankabfragen
- ✅ Schnellere Response-Zeiten
- ✅ Geringere Server-Last

### Frontend-Caching
Die API sendet Cache-Control Headers:
```
Cache-Control: max-age=300, public
```

**Bedeutung:**
- `max-age=300` - Browser darf Response 5 Minuten cachen
- `public` - Auch Proxies/CDNs dürfen cachen

**Vorteile:**
- ✅ Reduziert HTTP-Requests
- ✅ Sofortige Anzeige bei wiederholten Besuchen
- ✅ Funktioniert auch auf Startseite UND Leaderboard-Seite

### Gesamteffekt

**Szenario:** User besucht Startseite, dann Leaderboard-Seite

1. **Erster Request (Startseite):**
   - Frontend → Backend → Datenbank
   - Dauer: ~100ms
   - Backend cached Ergebnis

2. **Zweiter Request (Leaderboard-Seite, innerhalb 5 Min):**
   - Frontend → Cache (0ms, kein HTTP-Request!)
   - Dauer: ~0ms

3. **Dritter Request (nach 5 Min):**
   - Frontend → Backend → Cache (kein DB-Zugriff!)
   - Dauer: ~20ms

**Resultat:** Nur **EINE** Datenbankabfrage für mehrere Page-Loads! 🚀