# API Endpoints Übersicht

Diese Datei bietet eine schnelle Übersicht über alle verfügbaren API-Endpunkte.

## Public Endpoints (Keine Authentifizierung erforderlich)

### Leaderboard
- `GET /api/public/leaderboard` - Alle Turnier-Ergebnisse mit Sport-Namen
- `GET /api/public/leaderboard/medals` - Nur Medaillengewinner

**Dokumentation:** [FeaturePublicLeaderboard.md](FeaturePublicLeaderboard.md)

---

## Authentication Endpoints

### Login & Registration
- `POST /api/auth/login` - Login für Admin/Judge (erhält JWT-Token)
- `POST /api/auth/register` - Neuen User registrieren (Admin only)

**Dokumentation:** [FeatureLogin.md](FeatureLogin.md)

---

## Athlete Management (Admin/Judge only)

### CRUD Operationen
- `GET /api/athletes` - Alle Athleten abrufen
- `GET /api/athletes/{id}` - Einzelnen Athleten abrufen
- `POST /api/athletes` - Neuen Athleten erstellen
- `PUT /api/athletes/{id}` - Athleten aktualisieren
- `DELETE /api/athletes/{id}` - Athleten löschen

**Dokumentation:** [FeatureAthleteManagement.md](FeatureAthleteManagement.md)

---

## Country Management (Admin/Judge only)

### CRUD Operationen
- `GET /api/countries` - Alle Länder abrufen
- `GET /api/countries/{id}` - Einzelnes Land abrufen
- `POST /api/countries` - Neues Land erstellen
- `PUT /api/countries/{id}` - Land aktualisieren
- `DELETE /api/countries/{id}` - Land löschen

**Dokumentation:** [FeatureCountryManagement.md](FeatureCountryManagement.md)

---

## Admin Endpoints (Admin only)

### Database Management
- `POST /api/admin/reset` - Datenbank zurücksetzen (nur Admin-User bleibt erhalten)

**Dokumentation:** [FeatureAdminDatabaseReset.md](FeatureAdminDatabaseReset.md)

---

## Quick Start Examples

### 1. Login als Admin
```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

### 2. Public Leaderboard abrufen (keine Auth)
```bash
curl -X GET "http://localhost:8080/api/public/leaderboard" | jq
```

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
  }
]
```

### 3. Athleten erstellen (mit JWT-Token)
```bash
TOKEN="<your-jwt-token>"

curl -X POST "http://localhost:8080/api/athletes" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "countryId": 1
  }'
```

### 4. Land erstellen (mit JWT-Token)
```bash
TOKEN="<your-jwt-token>"

curl -X POST "http://localhost:8080/api/countries" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "code": "CAN",
    "name": "Canada"
  }'
```

### 5. Datenbank zurücksetzen (Admin only)
```bash
TOKEN="<admin-jwt-token>"

curl -X POST "http://localhost:8080/api/admin/reset" \
  -H "Authorization: Bearer $TOKEN"
```

---

## Authentication & Authorization

### Rollen
- **ADMIN** - Voller Zugriff auf alle Endpunkte
- **JUDGE** - Zugriff auf Athlete/Country Management, kein Admin-Bereich

### JWT-Token verwenden
Nach dem Login erhältst du einen JWT-Token. Verwende diesen in allen geschützten Endpunkten:

```bash
curl -X GET "http://localhost:8080/api/athletes" \
  -H "Authorization: Bearer <your-token-here>"
```

### Token-Format
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Response-Formate

### Erfolgreiche Antworten

#### GET /api/athletes
```json
[
  {
    "id": 1,
    "firstName": "Katie",
    "lastName": "Ledecky",
    "countryCode": "USA",
    "countryName": "United States"
  }
]
```

#### GET /api/countries
```json
[
  {
    "id": 1,
    "code": "USA",
    "name": "United States"
  }
]
```

#### GET /api/public/leaderboard
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
  }
]
```

### Fehler-Antworten

#### 401 Unauthorized
```json
{
  "error": "Unauthorized",
  "message": "Invalid credentials"
}
```

#### 403 Forbidden
```json
{
  "error": "Forbidden",
  "message": "Access denied"
}
```

#### 404 Not Found
```json
{
  "error": "Not Found",
  "message": "Athlete with id 999 not found"
}
```

---

## Performance Features

### Caching
- **Spring Cache** auf Backend
- **HTTP Cache-Control** für Frontend
- Leaderboard-Endpunkte gecacht für 5 Minuten

### CORS
- Public Endpunkte erlauben alle Origins
- Für Production: Origins einschränken

---

## Database Reset

Der Admin kann die Datenbank zurücksetzen:

```bash
# 1. Als Admin einloggen
TOKEN=$(curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  | jq -r '.token')

# 2. Database Reset
curl -X POST "http://localhost:8080/api/admin/reset" \
  -H "Authorization: Bearer $TOKEN"
```

**Hinweis:** Nur der Admin-User bleibt erhalten, alle anderen Daten werden gelöscht!

---

## Weitere Dokumentation

Detaillierte Informationen zu jedem Feature findest du in den entsprechenden Dokumentationsdateien:

- [FeatureLogin.md](FeatureLogin.md) - Authentication & JWT
- [FeatureAthleteManagement.md](FeatureAthleteManagement.md) - Athlete CRUD
- [FeatureCountryManagement.md](FeatureCountryManagement.md) - Country CRUD
- [FeaturePublicLeaderboard.md](FeaturePublicLeaderboard.md) - Public API
- [FeatureAdminDatabaseReset.md](FeatureAdminDatabaseReset.md) - Database Reset
- [SportEntity.md](SportEntity.md) - Sport Relationship Details

