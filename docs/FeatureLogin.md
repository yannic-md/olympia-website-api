# Feature Login und Registrierung

## Überblick

Die Login- und Registrierungsfunktion ermöglicht es neuen Benutzern, einen Account mit der Rolle **JUDGE** zu erstellen und sich anschließend anzumelden. Alle Passwörter werden mit **BCrypt** verschlüsselt gespeichert.

## Funktionsweise

### 1. Registrierung (Registration)

#### Was passiert
- Ein neuer Benutzer registriert sich mit Username, Passwort und Email
- Das Passwort wird mit BCrypt gehasht und nie im Klartext gespeichert
- Der neue Benutzer erhält automatisch die Rolle **JUDGE**
- Der Benutzer wird in der Datenbank gespeichert

#### Ablauf
1. Frontend sendet `POST /api/auth/register` mit Username, Passwort und Email
2. Backend validiert die Eingaben (Username muss eindeutig sein)
3. Passwort wird mit BCrypt gehasht
4. Neuer User mit Rolle JUDGE wird erstellt
5. Response mit Benutzer-ID und Daten wird zurückgesendet

#### HTTP Status
- `201 Created` - Registrierung erfolgreich
- `400 Bad Request` - Username existiert bereits oder ungültige Eingabe

### 2. Login (Authentifizierung)

#### Was passiert
- Ein bestehender Benutzer meldet sich mit Username und Passwort an
- Das eingegebene Passwort wird mit dem gespeicherten BCrypt-Hash verglichen
- Bei erfolgreicher Authentifizierung werden die Benutzerdaten zurückgesendet

#### Ablauf
1. Frontend sendet `POST /api/auth/login` mit Username und Passwort
2. Backend sucht den Benutzer nach Username
3. Passwort wird mit dem Hash verglichen (BCrypt)
4. Bei Erfolg: Benutzer-Daten werden zurückgesendet
5. Bei Fehler: Error-Message wird zurückgesendet

#### HTTP Status
- `200 OK` - Login erfolgreich
- `401 Unauthorized` - Username nicht gefunden oder falsches Passwort

## Code-Struktur

Die Implementierung folgt der klassischen Layer-Architektur:

1. **controller** - `AuthController.java`
   - Stellt die REST-Endpunkte `/api/auth/register` und `/api/auth/login` bereit
   - Nimmt Requests entgegen und gibt Responses zurück

2. **service** - `AuthService.java`
   - Enthält die Geschäftslogik für Registrierung und Login
   - Führt Validationen durch
   - Hasht Passwörter mit BCrypt
   - Sucht Benutzer in der Datenbank

3. **entity** - `User.java`
   - Stellt die `users` Tabelle dar
   - Felder: `id`, `username`, `passwordHash`, `email`, `role`, `createdAt`
   - Rolle ist ein Enum: `ADMIN`, `JUDGE`

4. **repository** - `UserRepository.java`
   - Interface für Datenbankzugriff
   - Methode: `findByUsername(String username)` - findet Benutzer nach Username

5. **dto** - DTOs für Request und Response
   - `LoginRequest.java` - Username und Passwort
   - `RegisterRequest.java` - Username, Passwort und Email
   - `LoginResponse.java` - Response mit Benutzer-Daten

6. **config** - `SecurityConfig.java`
   - Spring Security Konfiguration
   - Endpunkte `/api/auth/login` und `/api/auth/register` sind öffentlich
   - CSRF ist deaktiviert für API-Nutzung

## Datenbank

Die Benutzer werden in der `users` Tabelle gespeichert:

```sql
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(255),
  role ENUM('ADMIN', 'JUDGE') NOT NULL DEFAULT 'JUDGE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Tests mit cURL

### Test 1: Neuen Judge-User registrieren

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "judge_alice",
    "password": "AliceSecure123!",
    "email": "alice@olympia.de"
  }'
```

**Erwartete Response (201 Created):**
```json
{
  "id": 1,
  "username": "judge_alice",
  "role": "JUDGE",
  "message": "Registrierung erfolgreich"
}
```

### Test 2: Mit registriertem User anmelden

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "judge_alice",
    "password": "AliceSecure123!"
  }'
```

**Erwartete Response (200 OK):**
```json
{
  "id": 1,
  "username": "judge_alice",
  "role": "JUDGE",
  "message": "Login erfolgreich"
}
```

### Test 3: Login mit falschen Credentials

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "judge_alice",
    "password": "WrongPassword123!"
  }'
```

**Erwartete Response (401 Unauthorized):**
```json
{
  "id": null,
  "username": null,
  "role": null,
  "message": "Ungültige Anmeldedaten"
}
```

### Test 4: Doppelt registrieren (Username existiert bereits)

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "judge_alice",
    "password": "AnotherPassword123!",
    "email": "another@olympia.de"
  }'
```

**Erwartete Response (400 Bad Request):**
```json
{
  "id": null,
  "username": null,
  "role": null,
  "message": "Benutzername existiert bereits"
}
```