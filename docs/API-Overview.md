# API Endpoints Overview

This file provides a quick overview of all available API endpoints.

## Public Endpoints (No Authentication Required)

### Leaderboard
- `GET /api/public/leaderboard?lang={en|de|fr}` - All tournament results with sport names
- `GET /api/public/leaderboard/medals?lang={en|de|fr}` - Medal winners only

**Translations:** Query parameter `lang` supports `en` (default), `de`, `fr`.
Translated fields: `sportName`, `countryName`, `medal`, `scoreType`.

**Documentation:** [FeaturePublicLeaderboard.md](FeaturePublicLeaderboard.md)

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

### 1. Login as Admin
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
# Englisch (Standard)
curl -X GET "http://localhost:8080/api/public/leaderboard" | jq

# Deutsch
curl -X GET "http://localhost:8080/api/public/leaderboard?lang=de" | jq

# Französisch
curl -X GET "http://localhost:8080/api/public/leaderboard?lang=fr" | jq
```

**Response (lang=en):**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Sofia Goggia",
    "countryCode": "it",
    "countryName": "Italy",
    "timeOrPoints": "1:32.03",
    "scoreType": "Time",
    "medal": "Gold",
    "sportName": "Alpine Skiing"
  }
]
```

**Response (lang=de):**
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Sofia Goggia",
    "countryCode": "it",
    "countryName": "Italien",
    "timeOrPoints": "1:32.03",
    "scoreType": "Zeit",
    "medal": "Gold",
    "sportName": "Ski Alpin"
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

#### GET /api/public/leaderboard?lang=de
```json
[
  {
    "resultId": 1,
    "rank": 1,
    "athleteName": "Sofia Goggia",
    "countryCode": "it",
    "countryName": "Italien",
    "timeOrPoints": "1:32.03",
    "scoreType": "Zeit",
    "medal": "Gold",
    "sportName": "Ski Alpin"
  }
]
```

### Error Responses

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
- **Spring Cache** on backend
- **HTTP Cache-Control** for frontend
- Leaderboard endpoints cached for 5 minutes

### CORS
- Public endpoints allow all origins
- For production: restrict origins

---

## Database Reset

The admin can reset the database:

```bash
# 1. Login as admin
TOKEN=$(curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}' \
  | jq -r '.token')

# 2. Database reset
curl -X POST "http://localhost:8080/api/admin/reset" \
  -H "Authorization: Bearer $TOKEN"
```

**Note:** Only the admin user remains; all other data will be deleted!

---

## Translation

The leaderboard endpoints support **English**, **German** and **French**.

### Usage
```bash
# Default (English)
curl http://localhost:8080/api/public/leaderboard

# German
curl http://localhost:8080/api/public/leaderboard?lang=de

# French
curl http://localhost:8080/api/public/leaderboard?lang=fr
```

### Translated Fields

| Field         | EN (Default)    | DE                  | FR                              |
|---------------|-----------------|---------------------|---------------------------------|
| `sportName`   | Alpine Skiing   | Ski Alpin           | Ski alpin                       |
| `countryName` | Germany         | Deutschland         | Allemagne                       |
| `medal`       | Gold / Silver / Bronze | Gold / Silber / Bronze | Or / Argent / Bronze     |
| `scoreType`   | Time / Points / Wins | Zeit / Punkte / Siege | Temps / Points / Victoires |

### Non-translated Fields
- `athleteName` – Name remains unchanged
- `countryCode` – ISO code remains unchanged
- `timeOrPoints` – Raw value remains unchanged
- `rank`, `resultId` – numeric values

### Technical Details
- **Service:** `TranslationService` with static translation maps
- **Fallback:** Unknown values are returned unchanged
- **Caching:** Cache keys are language-dependent (`all_en`, `all_de`, `medals_fr`, etc.)
- **Default:** `en` if no `lang` parameter specified or invalid value

---

## Further Documentation

Detailed information for each feature can be found in the corresponding documentation files:

- [FeatureLogin.md](FeatureLogin.md) - Authentication & JWT
- [FeatureAthleteManagement.md](FeatureAthleteManagement.md) - Athlete CRUD
- [FeatureCountryManagement.md](FeatureCountryManagement.md) - Country CRUD
- [FeaturePublicLeaderboard.md](FeaturePublicLeaderboard.md) - Public API
- [FeatureAdminDatabaseReset.md](FeatureAdminDatabaseReset.md) - Database Reset
- [SportEntity.md](SportEntity.md) - Sport Relationship Details

