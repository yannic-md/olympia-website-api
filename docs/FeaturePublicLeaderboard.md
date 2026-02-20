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
    "sportName": "Swimming"
  },
  {
    "resultId": 2,
    "rank": 2,
    "athleteName": "Caeleb Dressel",
    "countryCode": "USA",
    "countryName": "United States",
    "timeOrPoints": "4:01.12",
    "medal": "SILVER",
    "sportName": "Swimming"
  },
  {
    "resultId": 4,
    "rank": null,
    "athleteName": "Claire Dupont",
    "countryCode": "FRA",
    "countryName": "France",
    "timeOrPoints": "12.34",
    "medal": null,
    "sportName": "Gymnastics"
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
- `sportName` - Name der Sportart (z.B. "Swimming", "Athletics", "Gymnastics" oder null)

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
    "sportName": "Swimming"
  },
  {
    "resultId": 3,
    "rank": 1,
    "athleteName": "Max Mustermann",
    "countryCode": "GER",
    "countryName": "Germany",
    "timeOrPoints": "9.85",
    "medal": "GOLD",
    "sportName": "Athletics"
  },
  {
    "resultId": 2,
    "rank": 2,
    "athleteName": "Caeleb Dressel",
    "countryCode": "USA",
    "countryName": "United States",
    "timeOrPoints": "4:01.12",
    "medal": "SILVER",
    "sportName": "Swimming"
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

Die Daten kommen aus der `results` Tabelle mit Joins zu `athletes`, `countries` und `sports`:

```sql
SELECT 
    r.id,
    r.rank,
    r.time_or_points,
    r.medal,
    s.name as sport_name,
    a.first_name,
    a.last_name,
    c.code,
    c.name
FROM results r
LEFT JOIN athletes a ON r.athlete_id = a.id
LEFT JOIN countries c ON a.country_id = c.id
LEFT JOIN sports s ON r.event_id = s.id
ORDER BY r.rank ASC NULLS LAST
```

---

## Sport-Relationship (Neu!)

### Verbesserung: Keine "Magic Numbers" mehr!

**Vorher:**
```json
{
  "eventId": 1  // Was ist Sport ID 1? 🤔
}
```

**Jetzt:**
```json
{
  "sportName": "Swimming"  // Sofort verständlich! ✅
}
```

### Technische Details

Die `results` Tabelle hat jetzt eine Foreign Key Beziehung zur `sports` Tabelle:

```sql
ALTER TABLE results
ADD CONSTRAINT fk_results_sport
FOREIGN KEY (event_id) REFERENCES sports(id) ON DELETE CASCADE;
```

**Vorteile:**
- ✅ **Keine Magic Numbers** - Sport-Namen sind direkt lesbar
- ✅ **Type Safety** - Die Beziehung wird auf Entity-Ebene erzwungen
- ✅ **Referential Integrity** - Foreign Key garantiert gültige Sport-Referenzen
- ✅ **Bessere API** - Frontend erhält Sport-Namen statt IDs
- ✅ **Eager Loading** - Sport-Daten werden automatisch mit Results geladen

### Verfügbare Sports
Die Sample-Daten enthalten folgende Sports:
- `Swimming` (ID: 1)
- `Athletics` (ID: 2)
- `Gymnastics` (ID: 3)

### Entity-Mapping
```java
@Entity
@Table(name = "results")
public class Result {
    // ...
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Sport sport;  // Statt: private Long eventId;
    
    // ...
}
```

Der Spaltenname in der Datenbank bleibt `event_id` für Rückwärtskompatibilität, aber die Anwendung verwendet jetzt die `Sport` Entity.

