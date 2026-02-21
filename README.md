# Olympia Website API

# Schnellstart

Die Anwendung kann direkt als JAR-Datei aus den GitHub Releases heruntergeladen werden.

## Voraussetzungen
- Java 17 oder höher
- Docker Desktop (Windows) oder Docker (Linux)

### Installation

### Windows (mit Docker Desktop)

<!---
Windows ist sonnn scheisssssssss wie kann auch nur irgendwer das freiwillig nutzen 
laueft scheisse und dann nichtmal gut wie absoluter fiebertraum auch nur irgendwas damit zu machen
- Ich (Yanic) nachdem ich Nils geholfen habe das backend zu starten
--->

#### 1. Java 17 installieren
1. Download: [OpenJDK 17](https://adoptium.net/de/temurin/releases/?version=17)
2. Installer ausführen und Installation abschließen
3. Überprüfen:
```cmd
java -version
```

#### 2. WSL 2 aktualisieren
Öffne PowerShell als Administrator:
```powershell
wsl --update
```

#### 3. Docker Desktop installieren
1. Download: [Docker Desktop für Windows](https://www.docker.com/products/docker-desktop/)
2. Installer ausführen
3. Docker Desktop starten
4. Warten bis Docker vollständig gestartet ist (Icon in der Taskleiste wird grün)

#### 4. Projekt herunterladen
1. Gehe zu den [GitHub Releases](https://github.com/YOUR-USERNAME/olympia-website-api/releases)
2. Lade die neueste `olympia-website-api.jar` herunter
3. Lade die `compose.yaml` herunter (aus dem Repository oder Release)

#### 5. Anwendung starten
```cmd
# Im Ordner mit der JAR und compose.yaml
docker compose up -d
java -jar olympia-website-api.jar
```

---

### Linux (Arch Linux)

#### 1. Java 17 installieren
```bash
sudo pacman -S jdk17-openjdk
```

festlegen, dass Java 17 die Standardversion ist:
```bash
archlinux-java set java-17-openjdk
```

#### 2. Docker installieren
```bash
# Docker und Docker Compose installieren
sudo pacman -S docker docker-compose

# Docker-Dienst aktivieren und starten
sudo systemctl enable docker
sudo systemctl start docker

# Benutzer zur Docker-Gruppe hinzufügen (ohne sudo nutzen zu müssen)
sudo usermod -aG docker $USER

# Abmelden und neu anmelden, damit Gruppenänderung wirksam wird
```

Nach erneutem Anmelden testen:
```bash
docker --version
docker compose version
```

#### 3. Projekt herunterladen
```bash
# Erstelle Projektordner
mkdir olympia-api
cd olympia-api

# Lade JAR herunter (ersetze VERSION mit aktueller Version)
wget https://github.com/YOUR-USERNAME/olympia-website-api/releases/download/v1.0.0/olympia-website-api.jar

# Lade compose.yaml herunter
wget https://raw.githubusercontent.com/YOUR-USERNAME/olympia-website-api/main/compose.yaml
```

#### 4. Anwendung starten
```bash
# Docker Container starten
docker compose up -d

# Warten bis MariaDB bereit ist (ca. 10-15 Sekunden)
sleep 15

# JAR ausführen
java -jar olympia-website-api.jar
```
---

## Anwendung nutzen

### API ist erreichbar unter:
```
http://localhost:8080
```

### Login-Daten
- **Admin**: `admin` / `admin123`
- **Judge1**: `judge1` / `judge1pwd`
- **Judge2**: `judge2` / `judge2pwd`

### Öffentliche API (keine Anmeldung erforderlich)
```bash
# Alle Ergebnisse abrufen
curl http://localhost:8080/api/public/leaderboard

# Nur Medaillengewinner
curl http://localhost:8080/api/public/leaderboard/medals
```

### Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "adminpwd"}'
```

---

## Befehle

### Anwendung stoppen
```bash
# STRG+C im Terminal wo die JAR läuft

# Docker Container stoppen
docker compose down
```

### Datenbank zurücksetzen
```bash
# Container und Daten löschen
docker compose down -v

# Container neu starten
docker compose up -d

# JAR neu starten
java -jar olympia-website-api.jar
```

### Logs anzeigen
```bash
# Docker Logs
docker compose logs -f mariadb

# Anwendungs-Logs erscheinen direkt im Terminal
```

---

## Dokumentation
- [API Endpoints Übersicht](docs/API-Overview.md)
- [Login & Authentication](docs/FeatureLogin.md)
- [Athlete Management](docs/FeatureAthleteManagement.md)
- [Country Management](docs/FeatureCountryManagement.md)
- [Public Leaderboard API](docs/FeaturePublicLeaderboard.md)
- [Sport Entity](docs/SportEntity.md)

---

## Entwicklung

### Projekt aus Quellcode kompilieren
```bash
# Repository klonen
git clone https://github.com/YOUR-USERNAME/olympia-website-api.git
cd olympia-website-api

# Ausführen
./gradlew bootRun
```

### IntelliJ IDEA Setup
1. Öffne das Projekt in IntelliJ
2. Gradle Symbol (Elefant) in der rechten Seitenleiste öffnen
3. `main` > `Tasks` > `application` > `bootRun` auswählen
4. Rechtsklick > Run

### Datenbank zu IntelliJ hinzufügen
1. Database Tool Window öffnen (rechte Seitenleiste)
2. Plus-Symbol > Data Source > MariaDB
3. Verbindungsdaten:
   - **Host**: `localhost`
   - **Port**: `3306`
   - **Database**: `olympia`
   - **User**: `user`
   - **Password**: `secret`