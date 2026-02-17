# Country Management API Dokumentation

## Übersicht
Diese API bietet Endpunkte zur Verwaltung von Ländern. Die Endpunkte zum Erstellen, Bearbeiten und Löschen sind nur für Benutzer mit den Rollen **ADMIN** und **JUDGE** zugänglich.

## Endpunkte

### 1. Alle Länder abrufen
**GET** `/api/countries`

Gibt eine Liste aller Länder zurück.

**Authentifizierung:** Erforderlich (alle authentifizierten Benutzer)

**Response:**
```json
[
  {
    "id": 1,
    "code": "USA",
    "name": "United States"
  },
  {
    "id": 2,
    "code": "GER",
    "name": "Germany"
  },
  {
    "id": 3,
    "code": "FRA",
    "name": "France"
  }
]
```

---

### 2. Land nach ID abrufen
**GET** `/api/countries/{id}`

Gibt ein spezifisches Land anhand der ID zurück.

**Authentifizierung:** Erforderlich (alle authentifizierten Benutzer)

**URL Parameter:**
- `id` (Long) - Die ID des Landes

**Response:**
```json
{
  "id": 1,
  "code": "USA",
  "name": "United States"
}
```

**Fehler:**
- `404 Not Found` - Land nicht gefunden

---

### 3. Neues Land erstellen
**POST** `/api/countries`

Erstellt ein neues Land.

**Authentifizierung:** Erforderlich (**ADMIN** oder **JUDGE**)

**Request Body:**
```json
{
  "code": "JPN",
  "name": "Japan"
}
```

**Felder:**
- `code` (String, erforderlich) - Ländercode (max. 8 Zeichen, muss eindeutig sein)
- `name` (String, erforderlich) - Name des Landes (max. 150 Zeichen)

**Response:**
```json
{
  "id": 4,
  "code": "JPN",
  "name": "Japan"
}
```

**Status Codes:**
- `201 Created` - Land erfolgreich erstellt
- `400 Bad Request` - Ungültige Eingabedaten oder Code bereits vorhanden
- `403 Forbidden` - Keine Berechtigung (nur ADMIN/JUDGE)
- `500 Internal Server Error` - Serverfehler

**Validierungen:**
- Code darf nicht leer sein
- Code darf max. 8 Zeichen lang sein
- Code muss eindeutig sein
- Name darf nicht leer sein
- Name darf max. 150 Zeichen lang sein

---

### 4. Land bearbeiten
**PUT** `/api/countries/{id}`

Aktualisiert die Daten eines vorhandenen Landes.

**Authentifizierung:** Erforderlich (**ADMIN** oder **JUDGE**)

**URL Parameter:**
- `id` (Long) - Die ID des zu aktualisierenden Landes

**Request Body:**
```json
{
  "code": "DEU",
  "name": "Deutschland"
}
```

**Felder:** (Alle Felder sind optional, nur angegebene Felder werden aktualisiert)
- `code` (String) - Neuer Ländercode
- `name` (String) - Neuer Name

**Response:**
```json
{
  "id": 2,
  "code": "DEU",
  "name": "Deutschland"
}
```

**Status Codes:**
- `200 OK` - Land erfolgreich aktualisiert
- `400 Bad Request` - Ungültige Eingabedaten oder Code bereits vergeben
- `403 Forbidden` - Keine Berechtigung (nur ADMIN/JUDGE)
- `404 Not Found` - Land nicht gefunden

---

### 5. Land löschen
**DELETE** `/api/countries/{id}`

Löscht ein Land.

**Authentifizierung:** Erforderlich (**ADMIN** oder **JUDGE**)

**URL Parameter:**
- `id` (Long) - Die ID des zu löschenden Landes

**⚠️ Hinweis:** Wenn Athleten mit diesem Land verknüpft sind, werden sie aufgrund von `ON DELETE CASCADE` ebenfalls gelöscht!

**Response:**
- Kein Body (204 No Content)

**Status Codes:**
- `204 No Content` - Land erfolgreich gelöscht
- `403 Forbidden` - Keine Berechtigung (nur ADMIN/JUDGE)
- `404 Not Found` - Land nicht gefunden

---

## Beispiel-Requests mit cURL

### Land erstellen (als Admin/Judge)
```bash
curl -X POST http://localhost:8080/api/countries \
  -u admin:adminpwd \
  -H "Content-Type: application/json" \
  -d '{
    "code": "JPN",
    "name": "Japan"
  }'
```

### Land bearbeiten (als Admin/Judge)
```bash
curl -X PUT http://localhost:8080/api/countries/2 \
  -u judge1:judge1pwd \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Deutschland"
  }'
```

### Land löschen (als Admin/Judge)
```bash
curl -X DELETE http://localhost:8080/api/countries/4 \
  -u admin:adminpwd
```

### Alle Länder abrufen (als beliebiger authentifizierter Benutzer)
```bash
curl -X GET http://localhost:8080/api/countries \
  -u judge1:judge1pwd
```

### Einzelnes Land abrufen
```bash
curl -X GET http://localhost:8080/api/countries/1 \
  -u admin:adminpwd
```

---

## Fehlermeldungen

### Duplikater Code
```bash
curl -X POST http://localhost:8080/api/countries \
  -u admin:adminpwd \
  -H "Content-Type: application/json" \
  -d '{
    "code": "USA",
    "name": "Another USA"
  }'
```

**Response (400 Bad Request):**
```
Country with code 'USA' already exists
```

### Code zu lang
```bash
curl -X POST http://localhost:8080/api/countries \
  -u admin:adminpwd \
  -H "Content-Type: application/json" \
  -d '{
    "code": "VERYLONGCODE",
    "name": "Test"
  }'
```

**Response (400 Bad Request):**
```
Country code must not exceed 8 characters
```

### Fehlende Pflichtfelder
```bash
curl -X POST http://localhost:8080/api/countries \
  -u admin:adminpwd \
  -H "Content-Type: application/json" \
  -d '{
    "code": ""
  }'
```

**Response (400 Bad Request):**
```
Country code is required
```

---

## Sicherheit

- **Lesezugriff (GET)**: Alle authentifizierten Benutzer können Länder abrufen
- **Schreibzugriff (POST, PUT, DELETE)**: Nur Benutzer mit den Rollen **ADMIN** oder **JUDGE** können Länder erstellen, bearbeiten oder löschen
- Die Authentifizierung erfolgt über HTTP Basic Auth
- CSRF ist für die API deaktiviert

## Datenbankstruktur

Die Länder werden in der `countries` Tabelle gespeichert mit folgenden Feldern:
- `id` (BIGINT) - Primärschlüssel
- `code` (VARCHAR(8)) - Ländercode (eindeutig)
- `name` (VARCHAR(150)) - Ländername

## Beziehungen

- **Athleten**: Über `athletes.country_id` → `countries.id`
- **Cascade Delete**: Wenn ein Land gelöscht wird, werden alle zugehörigen Athleten ebenfalls gelöscht

---

## Automatisches Test-Script

Führe alle Tests automatisch aus:
```bash
./test-country-endpoints.sh
```

Das Script testet:
1. ✅ Alle Länder abrufen
2. ✅ Einzelnes Land abrufen
3. ✅ Land erstellen (als Admin)
4. ✅ Land erstellen (als Judge)
5. ✅ Land bearbeiten
6. ✅ Land löschen
7. ✅ Duplikat-Fehlerbehandlung

