# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# copy the gradle wrapper and the config to the container
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# download dependencies (for better caching)
RUN ./gradlew dependencies --no-daemon

# copy the source-code and build the jar
COPY src src
RUN ./gradlew bootJar --no-daemon

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Only copies the builded JAR
COPY --from=build /app/build/libs/olympia-api.jar app.jar

# Spring Boot Standard-Port freigeben
EXPOSE 8080

# App starten
ENTRYPOINT ["java", "-jar", "app.jar"]