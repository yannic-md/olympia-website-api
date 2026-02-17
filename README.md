# olympia-website-api
## Login Daten
- Admin: `admin` / `adminpwd`
- Judge1: `judge1` / `judge1pwd`
- Judge2: `judge2` / `judge2pwd`

---

## Erforderliche Software
- [Openjdk 17](https://openjdk.org/projects/jdk/17/)
- [Docker](https://www.docker.com/get-started/)

## Einrichtung der Entwicklungsumgebung
1. erstellung einer Run config
- in der rechten seitenleiste auf das Gradle Symbol klicken (der Elefant)
- den Punkt main auswählen (notfalls auf das reload icon klicken)
- dann Tasks und application auswählen
- auf bootRun klicken
- IntelliJ sollte das Projekt starten und eine run config erstellt haben
2. Mariadb Docker Container starten
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

## Erklärung des beispiel Codes

1. Allgemein
- der Code befindet sich im package `de.olympia.main.example`
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
