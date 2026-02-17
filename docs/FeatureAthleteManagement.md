# Athlete Management API Dokumentation

## Übersicht
Diese API bietet Endpunkte zur Verwaltung von Athleten. Die Endpunkte zum Erstellen, Bearbeiten und Löschen sind nur für Benutzer mit den Rollen **ADMIN** und **JUDGE** zugänglich.

## Endpunkte

### 1. Alle Athleten abrufen
**GET** `/api/athletes`

Gibt eine Liste aller Athleten zurück.

**Authentifizierung:** Erforderlich (alle authentifizierten Benutzer)

**Response:**
```json
[
  {
    "id": 1,
    "firstName": "Katie",
    "lastName": "Ledecky",
    "country": {
      "id": 1,
      "code": "USA",
      "name": "United States"
    },
    "gender": "F",
    "createdAt": "2026-02-17T10:00:00"
  }
]
```

---

### 2. Athlet nach ID abrufen
**GET** `/api/athletes/{id}`

Gibt einen spezifischen Athleten anhand der ID zurück.

**Authentifizierung:** Erforderlich (alle authentifizierten Benutzer)

**URL Parameter:**
- `id` (Long) - Die ID des Athleten

**Response:**
```json
{
  "id": 1,
  "firstName": "Katie",
  "lastName": "Ledecky",
  "country": {
    "id": 1,
    "code": "USA",
    "name": "United States"
  },
  "gender": "F",
  "createdAt": "2026-02-17T10:00:00"
}
```

**Fehler:**
- `404 Not Found` - Athlet nicht gefunden

---

### 3. Neuen Athleten erstellen
**POST** `/api/athletes`

Erstellt einen neuen Athleten.

**Authentifizierung:** Erforderlich (**ADMIN** oder **JUDGE**)

**Request Body:**
```json
{
  "firstName": "Max",
  "lastName": "Mustermann",
  "countryId": 2,
  "gender": "M"
}
```

**Felder:**
- `firstName` (String, erforderlich) - Vorname des Athleten
- `lastName` (String, erforderlich) - Nachname des Athleten
- `countryId` (Long, optional) - ID des Landes
- `gender` (String, optional) - Geschlecht ("M", "F", oder "D")

**Response:**
```json
{
  "id": 5,
  "firstName": "Max",
  "lastName": "Mustermann",
  "country": {
    "id": 2,
    "code": "GER",
    "name": "Germany"
  },
  "gender": "M",
  "createdAt": "2026-02-17T10:30:00"
}
```

**Status Codes:**
- `201 Created` - Athlet erfolgreich erstellt
- `400 Bad Request` - Ungültige Eingabedaten
- `403 Forbidden` - Keine Berechtigung (nur ADMIN/JUDGE)
- `500 Internal Server Error` - Serverfehler

---

### 4. Athlet bearbeiten
**PUT** `/api/athletes/{id}`

Aktualisiert die Daten eines vorhandenen Athleten.

**Authentifizierung:** Erforderlich (**ADMIN** oder **JUDGE**)

**URL Parameter:**
- `id` (Long) - Die ID des zu aktualisierenden Athleten

**Request Body:**
```json
{
  "firstName": "Maximilian",
  "lastName": "Mustermann",
  "countryId": 2,
  "gender": "M"
}
```

**Felder:** (Alle Felder sind optional, nur angegebene Felder werden aktualisiert)
- `firstName` (String) - Neuer Vorname
- `lastName` (String) - Neuer Nachname
- `countryId` (Long) - Neue Land-ID
- `gender` (String) - Neues Geschlecht ("M", "F", oder "D")

**Response:**
```json
{
  "id": 5,
  "firstName": "Maximilian",
  "lastName": "Mustermann",
  "country": {
    "id": 2,
    "code": "GER",
    "name": "Germany"
  },
  "gender": "M",
  "createdAt": "2026-02-17T10:30:00"
}
```

**Status Codes:**
- `200 OK` - Athlet erfolgreich aktualisiert
- `400 Bad Request` - Ungültige Eingabedaten
- `403 Forbidden` - Keine Berechtigung (nur ADMIN/JUDGE)
- `404 Not Found` - Athlet nicht gefunden

---

### 5. Athlet löschen
**DELETE** `/api/athletes/{id}`

Löscht einen Athleten.

**Authentifizierung:** Erforderlich (**ADMIN** oder **JUDGE**)

**URL Parameter:**
- `id` (Long) - Die ID des zu löschenden Athleten

**Response:**
- Kein Body (204 No Content)

**Status Codes:**
- `204 No Content` - Athlet erfolgreich gelöscht
- `403 Forbidden` - Keine Berechtigung (nur ADMIN/JUDGE)
- `404 Not Found` - Athlet nicht gefunden

---

## Beispiel-Requests mit cURL

### Athlet erstellen (als Admin/Judge)
```bash
curl -X POST http://localhost:8080/api/athletes \
  -u admin:adminpwd \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anna",
    "lastName": "Schmidt",
    "countryId": 2,
    "gender": "F"
  }'
```

### Athlet bearbeiten (als Admin/Judge)
```bash
curl -X PUT http://localhost:8080/api/athletes/5 \
  -u judge1:judge1pwd \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Anna-Maria",
    "lastName": "Schmidt-Meyer"
  }'
```

### Athlet löschen (als Admin/Judge)
```bash
curl -X DELETE http://localhost:8080/api/athletes/5 \
  -u admin:adminpwd
```

### Alle Athleten abrufen (als beliebiger authentifizierter Benutzer)
```bash
curl -X GET http://localhost:8080/api/athletes \
  -u judge1:judge1pwd
```

---

## Sicherheit

- **Lesezugriff (GET)**: Alle authentifizierten Benutzer können Athleten abrufen
- **Schreibzugriff (POST, PUT, DELETE)**: Nur Benutzer mit den Rollen **ADMIN** oder **JUDGE** können Athleten erstellen, bearbeiten oder löschen
- Die Authentifizierung erfolgt über HTTP Basic Auth
- CSRF ist für die API deaktiviert

## Datenbankstruktur

Die Athleten werden in der `athletes` Tabelle gespeichert mit folgenden Feldern:
- `id` (BIGINT) - Primärschlüssel
- `first_name` (VARCHAR) - Vorname
- `last_name` (VARCHAR) - Nachname
- `country_id` (BIGINT) - Fremdschlüssel zu `countries`
- `gender` (ENUM) - Geschlecht (M, F, D)
- `created_at` (TIMESTAMP) - Erstellungszeitpunkt

