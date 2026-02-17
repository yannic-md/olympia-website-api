# Public Leaderboard API Dokumentation

## Übersicht
Diese öffentliche API bietet Endpunkte zum Abrufen von Turnier-Ergebnissen für die Leaderboard-Tabelle im Frontend. Diese Endpunkte sind **ohne Authentifizierung** zugänglich und können auf der Startseite verwendet werden.

## ⚡ Caching-Strategie

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
**GET** `/api/public/leaderboard`

Gibt alle Ergebnisse aus der Datenbank zurück, sortiert nach Rang.

**Authentifizierung:** ❌ Nicht erforderlich (öffentlich)

**Response:**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Katie Ledecky",
    "countryCode": "USA",
    "countryName": "United States",
    "timeOrPoints": "3:59.34",
    "medal": "GOLD",
    "eventId": null
  },
  {
    "resultId": 2,
    "rank": 2,
    "athleteName": "Caeleb Dressel",
    "countryCode": "USA",
    "countryName": "United States",
    "timeOrPoints": "4:01.12",
    "medal": "SILVER",
    "eventId": null
  },
  {
    "resultId": 4,
    "rank": null,
    "athleteName": "Claire Dupont",
    "countryCode": "FRA",
    "countryName": "France",
    "timeOrPoints": "12.34",
    "medal": null,
    "eventId": null
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
- `athleteName` - Vollständiger Name des Athleten
- `countryCode` - Ländercode (z.B. "USA", "GER")
- `countryName` - Vollständiger Ländername
- `timeOrPoints` - Zeit oder Punkte
- `medal` - Medaille ("GOLD", "SILVER", "BRONZE" oder null)
- `eventId` - Event/Disziplin ID (kann null sein)

---

### 2. Nur Medaillen-Gewinner abrufen
**GET** `/api/public/leaderboard/medals`

Gibt nur Ergebnisse mit Medaillen zurück, sortiert nach Medaillen-Typ (Gold → Silber → Bronze).

**Authentifizierung:** ❌ Nicht erforderlich (öffentlich)

**Response:**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Katie Ledecky",
    "countryCode": "USA",
    "countryName": "United States",
    "timeOrPoints": "3:59.34",
    "medal": "GOLD",
    "eventId": null
  },
  {
    "resultId": 3,
    "rank": 1,
    "athleteName": "Maximilian Mustermann",
    "countryCode": "GER",
    "countryName": "Deutschland",
    "timeOrPoints": "9.85",
    "medal": "GOLD",
    "eventId": null
  },
  {
    "resultId": 2,
    "rank": 2,
    "athleteName": "Caeleb Dressel",
    "countryCode": "USA",
    "countryName": "United States",
    "timeOrPoints": "4:01.12",
    "medal": "SILVER",
    "eventId": null
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
# Alle Ergebnisse abrufen
curl http://localhost:8080/api/public/leaderboard

# Nur Medaillen-Gewinner
curl http://localhost:8080/api/public/leaderboard/medals

# Mit Cache-Control Headers anzeigen
curl -I http://localhost:8080/api/public/leaderboard
```

### Mit JavaScript (Frontend)
```javascript
// Fetch mit automatischem Browser-Caching
async function getLeaderboard() {
  const response = await fetch('http://localhost:8080/api/public/leaderboard');
  const data = await response.json();
  return data;
}

// Nur Medaillen-Gewinner
async function getMedalWinners() {
  const response = await fetch('http://localhost:8080/api/public/leaderboard/medals');
  const data = await response.json();
  return data;
}

// Verwendung
const results = await getLeaderboard();
console.log(results);
```

### Mit Axios (Frontend)
```javascript
import axios from 'axios';

// Alle Ergebnisse
const getAllResults = async () => {
  const { data } = await axios.get('http://localhost:8080/api/public/leaderboard');
  return data;
};

// Nur Medaillen
const getMedals = async () => {
  const { data } = await axios.get('http://localhost:8080/api/public/leaderboard/medals');
  return data;
};
```

---

## Frontend-Integration

### React Beispiel
```jsx
import { useEffect, useState } from 'react';

function Leaderboard() {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Dieser Request wird vom Browser gecacht
    fetch('http://localhost:8080/api/public/leaderboard')
      .then(res => res.json())
      .then(data => {
        setResults(data);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading...</div>;

  return (
    <table>
      <thead>
        <tr>
          <th>Rang</th>
          <th>Athlet</th>
          <th>Land</th>
          <th>Zeit/Punkte</th>
          <th>Medaille</th>
        </tr>
      </thead>
      <tbody>
        {results.map(result => (
          <tr key={result.resultId}>
            <td>{result.rank || '-'}</td>
            <td>{result.athleteName}</td>
            <td>{result.countryCode}</td>
            <td>{result.timeOrPoints}</td>
            <td>{result.medal || '-'}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
```

---

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

---

## CORS-Konfiguration

Die API erlaubt Zugriffe von allen Origins:
```java
@CrossOrigin(origins = "*")
```

Dies ermöglicht:
- ✅ Zugriff vom Frontend (verschiedene Ports während Development)
- ✅ Zugriff von mobilen Apps
- ✅ Zugriff von externen Clients

**Für Production:** Passe `origins` an spezifische Domains an:
```java
@CrossOrigin(origins = "https://your-frontend-domain.com")
```

---

## Datenbankstruktur

Die Daten kommen aus der `results` Tabelle mit Joins zu `athletes` und `countries`:

```sql
SELECT 
    r.id,
    r.rank,
    r.time_or_points,
    r.medal,
    r.event_id,
    a.first_name,
    a.last_name,
    c.code,
    c.name
FROM results r
LEFT JOIN athletes a ON r.athlete_id = a.id
LEFT JOIN countries c ON a.country_id = c.id
ORDER BY r.rank ASC NULLS LAST
```

---

## Automatisches Test-Script

Führe alle Tests aus:
```bash
./test-leaderboard-endpoints.sh
```

Das Script testet:
1. ✅ GET /api/public/leaderboard (ohne Auth)
2. ✅ GET /api/public/leaderboard/medals (ohne Auth)
3. ✅ Cache-Performance
4. ✅ Cache-Control Headers

---

## HTTP Status Codes

- **200 OK** - Erfolgreiche Abfrage
- **500 Internal Server Error** - Serverfehler

---

## Verwendung im Frontend

### Für Startseite
```javascript
// Zeige Top 3 Medaillen-Gewinner
const topMedals = await fetch('http://localhost:8080/api/public/leaderboard/medals')
  .then(res => res.json())
  .then(data => data.slice(0, 3));
```

### Für Leaderboard-Seite
```javascript
// Zeige vollständige Tabelle
const allResults = await fetch('http://localhost:8080/api/public/leaderboard')
  .then(res => res.json());
```

**Wichtig:** Beide Requests verwenden denselben Cache! Der zweite Request kommt aus dem Browser-Cache und verursacht **keinen** zusätzlichen HTTP-Request! 🎉

---

## Zusammenfassung

✅ **Öffentlich zugänglich** - Keine Authentifizierung erforderlich  
✅ **Backend-Caching** - Reduziert Datenbankzugriffe  
✅ **Frontend-Caching** - Reduziert HTTP-Requests  
✅ **CORS aktiviert** - Zugriff von Frontend möglich  
✅ **Zwei Endpunkte** - Alle Ergebnisse oder nur Medaillen  
✅ **Optimiert für Performance** - Eine DB-Abfrage für mehrere Page-Loads  

Die API ist produktionsbereit und für öffentliche Nutzung optimiert! 🚀

