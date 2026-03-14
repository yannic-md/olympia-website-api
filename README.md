# olympia-website-api

## 🔨 How can i use the project?

## Login Daten
- Admin: `admin` / `adminpwd`
- Judge1: `judge1` / `judge1pwd`
- Judge2: `judge2` / `judge2pwd`

## Ausführen der Software
### Voraussetzungen
- [Openjdk 17](https://winget.run/pkg/Microsoft/OpenJDK.17)
- [Docker](https://www.docker.com/get-started/)
- [Node.js](https://nodejs.org/en/download/)

### Windows

<!---
fick dieses Drecks system absoluter fiebertraum auch nur eine sache darauf zum laufen zu kriegen,
warum nutzt man das freiwillig????????????????????????????
- Yanic (schwer traumatisiert)
--->

#### Backend
- Docker Desktop installieren
- in der Powershell wsl updaten
```ps
  wsl --update
```
<br>

- Java 17 installieren
###### **ein weg mit winget**
- Powershell starten und folgenden befehl eingeben
```ps
  winget install Microsoft.OpenJDK.17
```
<br>

- den Github release herunterladen
[olympia-api-release.zip](https://github.com/yannic-md/olympia-website-api/releases/download/release/olympia-api-release.zip)
- die zip datei entpacken
- in das entpackte verzeichnis wechseln
- die datenbank mit folgendem befehl starten
```ps
  docker compose up -d
```
- die jar mit folgendem befehl starten
```ps
  java -jar olympia-api.jar
```

#### Frontend
- das Github Projekt klonen
```ps
  git clone https://github.com/yannic-md/olympia-website
```
- in das Projektverzeichnis wechseln
- die Powershell dort starten und folgenden befehl eingeben
```ps
  docker compose up
```

Die Webseite ist unter http://localhost:4200 erreichbar und kommuniziert mit dem Backend unter http://localhost:8080

---

## Dev Documentation


### Erforderliche Software
- [Openjdk 17](https://winget.run/pkg/Microsoft/OpenJDK.17)
- [Docker](https://www.docker.com/get-started/)
- [IntelliJ](https://www.jetbrains.com/idea/download/)

### Einrichtung der Entwicklungsumgebung
1. erstellung einer Run config
- in der rechten seitenleiste auf das Gradle Symbol klicken (der Elefant)
- den Punkt main auswählen (notfalls auf das reload icon klicken)
- dann Tasks und application auswählen
- auf bootRun klicken
- IntelliJ sollte das Projekt starten und eine run config erstellt haben
2. Mariadb Docker Container starten
 
   2.1. Docker Auf Windows
   - Docker Desktop installieren
   - in der Powershell wsl updaten
   ```ps
     wsl --update
   ```
   2.2. Docker Auf Linux
   ```bash
     sudo pacman -S docker docker-compose
   ```
   ```bash
     sudo systemctl enable --now docker
   ```
   ```bash
     sudo usermod -aG docker $USER
   ```

<br>

- der Docker Container starten mit den Programm, siehe schritt 1
- ist es notwendig nur die Datenbank zu starten
```bash
docker compose up
```
- sollte der Container kaputt sein kann dieser mit folgendem Befehl gelöscht werden
```bash
docker compose down
```
3. Mariadb zu IntelliJ hinzufügen
- in der rechten seitenleiste auf das Datenbank Symbol klicken
- auf das Plus Symbol klicken
- auf Data Source und dann auf MariaDB klicken
- bei Host `0.0.0.0` eintragen
- bei Port `3306` eintragen
- bei User `user` eintragen
- bei Password `secret` eintragen

### Erklärung des beispiel Codes

1. Allgemein
- der Code befindet sich im package `de.olympia.main.example` (in der branch `get-started`)
- jedes unterpackage von `de.olympia.main` wird automatisch mit den Projekt gestartet, eine Definition in MainApplication ist nicht notwendig
- die Struktur folgt einer klassischen Layer-Architektur (Controller, Service, Repository, Entity)

2. Flyway
- Flyway wird verwendet, um die Datenbankmigrationen zu verwalten
- die skripte zur Erstellung der Db sind unter `src/main/resources/db/migration` zu finden
- bei jeden start werden die Migrationen automatisch ausgeführt, sofern notwendig

3. entity
- enthält die JPA-Entities, welche die Datenbanktabellen abbilden
- im Beispiel wird die Tabelle `countries` durch die Klasse `Country` repräsentiert
- jede Entity ist mit `@Entity` annotiert, damit Hibernate diese erkennt
- `@Table(name = "countries")` stellt sicher, dass exakt die bestehende Tabelle verwendet wird
- Primärschlüssel wird über `@Id` und `@GeneratedValue` definiert

4. repository
- enthält Interfaces für den Datenbankzugriff
- `CountryRepository` erweitert `JpaRepository`
- Standardmethoden wie `findAll`, `findById` oder `save` stehen automatisch zur Verfügung

5. service
- enthält die Geschäftslogik der Anwendung
- kapselt den Zugriff auf ein oder mehrere Repositories
- stellt Methoden bereit, die von Controllern verwendet werden

6. controller
- stellt die REST-Endpunkte der Anwendung bereit
- ist mit `@RestController` annotiert
- verarbeitet HTTP-Anfragen und gibt JSON-Antworten zurück
- http://localhost:8080/api/countries liefert eine Liste aller Länder aus der Datenbank
