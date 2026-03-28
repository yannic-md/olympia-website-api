# Backend Tests - Dokumentation

## Überblick

Das Projekt enthält eine umfassende Testsuite mit **Unit Tests** und **Integration Tests**. Die Tests sind in folgende Kategorien unterteilt:

- **Controller Tests** — REST-API Endpunkte testen
- **Service Tests** — Business Logic testen
- **Repository Tests** — Datenbankoperationen testen

Die Tests nutzen das **JUnit 5** Framework mit **Mockito** für Mocking und **Spring Boot Test** für Integration Tests.

---

## Test-Struktur

```
src/test/java/de/olympia/main/
├── MainApplicationTests.java          # Kontextladetest
├── controller/
│   ├── AuthControllerTest.java
│   ├── AthleteControllerTest.java
│   ├── CountryControllerTest.java
│   └── ResultControllerTest.java
├── service/
│   ├── AuthServiceTest.java
│   ├── AthleteServiceTest.java
│   ├── CountryServiceTest.java
│   ├── ResultServiceTest.java
│   └── V2PublicServiceTest.java
└── repository/
    ├── AthleteRepositoryTest.java
    ├── CountryRepositoryTest.java
    ├── ResultRepositoryTest.java
    ├── SportsRepositoryTest.java
    └── UserRepositoryTest.java
```

---

## Tests starten

### Voraussetzungen

- **Java 17+** installiert
- **Gradle** installiert (oder Gradle Wrapper nutzen)
- **Projekt-Abhängigkeiten** heruntergeladen (erste `build` lädt diese automatisch)

### 1. Alle Tests ausführen

#### Mit Gradle (Windows)
```bash
gradlew test
```

#### Mit Gradle (Mac/Linux)
```bash
./gradlew test
```

#### Mit Maven (falls vorhanden)
```bash
mvn test
```

**Erwartete Ausgabe:**
```
BUILD SUCCESSFUL
Test summary: X tests run, Y failed, Z skipped
```

---

### 2. Spezifische Test-Kategorie ausführen

#### Nur Controller Tests
```bash
gradlew test --tests "de.olympia.main.controller.*"
```

#### Nur Service Tests
```bash
gradlew test --tests "de.olympia.main.service.*"
```

#### Nur Repository Tests
```bash
gradlew test --tests "de.olympia.main.repository.*"
```

---

### 3. Einzelne Test-Klasse ausführen

```bash
gradlew test --tests "de.olympia.main.controller.AuthControllerTest"
```

---

### 4. Einzelne Test-Methode ausführen

```bash
gradlew test --tests "de.olympia.main.controller.AuthControllerTest.testLoginSuccess"
```

---

### 5. Tests mit detailliertem Report

```bash
gradlew test --info
```

---

### 6. Tests in der IDE ausführen

#### IntelliJ IDEA
1. Test-Datei im Editor öffnen
2. Rechtsklick auf die Klasse oder Methode
3. Wähle `Run 'ClassName'` oder `Debug 'ClassName'`

#### Eclipse
1. Test-Datei im Editor öffnen
2. Rechtsklick auf die Klasse oder Methode
3. Wähle `Run As → JUnit Test`

#### VS Code mit Extension
1. **Test Explorer UI** Extension installieren
2. Tests im Test Explorer Bereich ausführen

---

## Test-Übersicht

### Controller Tests

Diese Tests prüfen die REST-API Endpunkte mit **Mockito**. Sie testen:
- Request-Parameter-Validierung
- Response-Status-Codes
- Korrekte Service-Aufrufe

**Dateien:**
- `AuthControllerTest.java` — Login, Register, Logout
- `AthleteControllerTest.java` — Athlete CRUD Operationen
- `CountryControllerTest.java` — Country CRUD Operationen
- `ResultControllerTest.java` — Result Management

**Testmuster:**
```java
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Mock
    private AuthService authService;
    
    @InjectMocks
    private AuthController authController;
    
    @Test
    @DisplayName("Should login successfully")
    void testLoginSuccess() {
        // Given: Eingabedaten vorbereiten
        // When: Methode aufrufen
        // Then: Ergebnis überprüfen
    }
}
```

---

### Service Tests

Diese Tests prüfen die Business Logic mit **Mockito-Mocks**. Sie testen:
- Geschäftslogik
- Validierung
- Fehlerbehandlung
- Service-zu-Service Aufrufe

**Dateien:**
- `AuthServiceTest.java` — Authentifizierung und Benutzer
- `AthleteServiceTest.java` — Athlet Management (CRUD, Validierung)
- `CountryServiceTest.java` — Land Management
- `ResultServiceTest.java` — Ergebnis Management
- `V2PublicServiceTest.java` — Public API Service

**Testmuster:**
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("AthleteService Tests")
public class AthleteServiceTest {
    @Mock
    private AthleteRepository athleteRepository;
    
    @InjectMocks
    private AthleteService athleteService;
    
    @BeforeEach
    void setUp() {
        // Test-Daten initialisieren
    }
    
    @Test
    @DisplayName("Should create athlete successfully")
    void testCreateAthlete() {
        // AAA Pattern: Arrange, Act, Assert
    }
}
```

---

### Repository Tests

Diese Tests prüfen Datenbankoperationen mit **echte H2-Datenbank**. Sie testen:
- Datenbankzugriff
- Queries
- Transaktionen
- Datenbeziehungen

**Dateien:**
- `AthleteRepositoryTest.java` — Athlete Datenbankoperationen
- `CountryRepositoryTest.java` — Country Datenbankoperationen
- `ResultRepositoryTest.java` — Result Datenbankoperationen
- `SportsRepositoryTest.java` — Sports Datenbankoperationen
- `UserRepositoryTest.java` — User Datenbankoperationen

**Testmuster:**
```java
@SpringBootTest
@Transactional
@DisplayName("Athlete Repository Tests")
public class AthleteRepositoryTest {
    @Autowired
    private AthleteRepository athleteRepository;
    
    @BeforeEach
    void setUp() {
        // Test-Daten in die Datenbank einfügen
    }
    
    @Test
    @DisplayName("Should find athlete by name")
    void testFindByName() {
        // Datenbankoperation testen
    }
}
```

---

## Test-Dependencies

Die folgenden Abhängigkeiten sind für die Tests konfiguriert:

```kotlin
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("org.springframework.boot:spring-boot-starter-security-test")
testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
testImplementation("org.mockito:mockito-core")
testImplementation("org.mockito:mockito-junit-jupiter")
testRuntimeOnly("org.junit.platform:junit-platform-launcher")
testRuntimeOnly("com.h2database:h2")
```

**Kurzbeschreibung:**

| Dependency | Zweck |
|-----------|-------|
| `spring-boot-starter-test` | JUnit 5, AssertJ, Mockito, JSONassert |
| `spring-boot-starter-security-test` | Security Context Testing |
| `spring-boot-starter-webmvc-test` | MockMvc für HTTP Tests |
| `mockito-core` | Object Mocking |
| `mockito-junit-jupiter` | Mockito JUnit 5 Integration |
| `junit-platform-launcher` | JUnit Platform Test Runner |
| `h2` | Eingebettete Datenbank für Tests |

---

## Test-Konfiguration

### application.properties (Test)

Die Tests nutzen automatisch die Test-Konfiguration:

```properties
# Standard Test-Konfiguration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

Diese Konfiguration wird automatisch geladen, wenn Tests laufen.

---

## Best Practices beim Testen

### 1. Naming Convention
```java
// Gut: Klar beschreibende Testnamen
@Test
void testCreateAthleteWithValidDataShouldSucceed()

@Test
@DisplayName("Should create athlete successfully with valid data")
void testCreateValidAthlete()

// Ungünstig: Vage oder zu kurz
@Test
void test1()

@Test
void testCreate()
```

### 2. AAA Pattern (Arrange-Act-Assert)
```java
@Test
void testAthleteCreation() {
    // ARRANGE: Setup
    AthleteImportDto athlete = new AthleteImportDto("John", "Doe", "USA", "M");
    
    // ACT: Aktion
    AthleteResponse result = athleteService.createAthlete(athlete);
    
    // ASSERT: Überprüfung
    assertEquals("John", result.getFirstName());
    assertNotNull(result.getId());
}
```

### 3. Verwendung von @DisplayName
```java
@Test
@DisplayName("Should return 401 when credentials are invalid")
void testLoginWithInvalidCredentials() {
    // Test
}
```

### 4. Isolation mit Mocking
```java
@ExtendWith(MockitoExtension.class)
public class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
    
    @Test
    void testShouldUseRepository() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockEntity));
        // Service wird getestet, nicht Repository
    }
}
```

### 5. Transactional für Repository Tests
```java
@SpringBootTest
@Transactional  // Wird nach jedem Test zurückgerollt
public class RepositoryTest {
    // Keine Datenverschmutzung zwischen Tests
}
```

---

## Häufige Test-Fehler und Lösungen

### Problem 1: "No qualifying bean of type AuthService"
**Ursache:** Service wird nicht korrekt gemockt
**Lösung:**
```java
@ExtendWith(MockitoExtension.class)  // Nicht @SpringBootTest!
public class ControllerTest {
    @Mock
    private AuthService authService;
    
    @InjectMocks
    private AuthController controller;
}
```

### Problem 2: "Lazy initialization exception"
**Ursache:** Lazy-loaded Collections außerhalb einer Transaktion
**Lösung:** `@Transactional` auf der Test-Klasse verwenden
```java
@SpringBootTest
@Transactional
public class RepositoryTest {
    // ...
}
```

### Problem 3: "Unable to find a @SpringBootConfiguration"
**Ursache:** Test befindet sich in falschem Package
**Lösung:** Tests müssen unter `src/test/java` im gleichen Package liegen

### Problem 4: Zu viele Datenbank-Queries in Tests
**Ursache:** N+1 Query Problem
**Lösung:** `@Query` mit `join fetch` optimieren
```java
@Query("SELECT a FROM Athlete a JOIN FETCH a.country WHERE a.id = :id")
Optional<Athlete> findByIdWithCountry(@Param("id") Long id);
```

---

## Continuous Integration

### GitHub Actions Beispiel

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - run: ./gradlew test
```

---

## Test-Reports

Nach der Test-Ausführung finden Sie Reports unter:

```
build/reports/tests/test/index.html
```

Öffnen Sie diese Datei im Browser für einen detaillierten Report mit:
- Test-Erfolgsquote
- Fehlgeschlagene Tests mit Stack Traces
- Test-Dauer
- Test-Pakete und Klassen

---

## Debugging von Tests

### 1. Debug-Modus in der IDE
1. Rechtsklick auf Test
2. `Debug 'TestName'` wählen
3. Breakpoints setzen und schrittweise durchgehen

### 2. Print Debugging
```java
@Test
void testSomething() {
    System.out.println("Debug output: " + variable);
    assertEquals(expected, actual);
}
```

### 3. Verbose Logging
```bash
gradlew test --info
```

### 4. Spezifische Fehlerausgabe
```java
@Test
void testWithDetailedMessage() {
    assertEquals(expected, actual, 
        "Detailed message when assertion fails: expected=" + expected);
}
```

---

## Tipps & Tricks

### Test-Filterung nach Tags
```bash
# Nur schnelle Tests
gradlew test --tests "*Fast"

# Alle außer langsamen Tests
gradlew test --tests "*" -x "*Slow"
```

### Parallelisierung
```bash
gradlew test --max-workers=4
```

### Tests mit Gradle Wrapper in CI/CD
```bash
./gradlew test --no-daemon --no-build-cache
```

### Test-Coverage (mit Jacoco Plugin)
Falls Jacoco konfiguriert ist:
```bash
gradlew test jacoco
```

---

## Weitere Ressourcen

- **JUnit 5 Dokumentation:** https://junit.org/junit5/
- **Mockito Dokumentation:** https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **Spring Boot Test Guide:** https://spring.io/guides/gs/testing-web/
- **Spring Security Test:** https://docs.spring.io/spring-security/reference/servlet/test/index.html

---

## Test-Checkliste

Vor dem Commit überprüfen Sie:

- [ ] Alle Tests laufen: `gradlew test`
- [ ] Keine neuen Test-Fehler
- [ ] Code-Coverage ist akzeptabel (>70% ist gut)
- [ ] Tests haben aussagekräftige Namen mit `@DisplayName`
- [ ] Tests folgen dem AAA-Pattern
- [ ] Mocks sind korrekt konfiguriert
- [ ] Keine Hard-coded Testdaten (Factories/Builders verwenden)
- [ ] Tests sind unabhängig (können in beliebiger Reihenfolge laufen)


