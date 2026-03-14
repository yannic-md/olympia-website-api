<h1 align="center">
     📡 Olympia Website (API) ~ Spring Boot REST-API
</h1>

<p align="center">
  <i align="center">This project provides the complete functionality required to process database queries from the frontend and deliver data to it via HTTP requests.</i>
  <br /><br />
  <img src="https://i.imgur.com/LKXvNDX.png" alt="Showcase img" />
<br />

<h4 align="center">
  <a href="https://gradle.org/">
    <img src="https://img.shields.io/badge/Gradle-9.2.1-27ae60?style=for-the-badge" alt="gradle version" style="height: 25px;">
  </a>
  <a href="https://mariadb.org/">
    <img src="https://img.shields.io/badge/MariaDB-12.2.2-27ae60?style=for-the-badge" alt="tailwind version" style="height: 25px;">
  </a>
  <a href="https://junit.org/">
    <img src="https://img.shields.io/badge/JUnit-6.1.0-27ae60?style=for-the-badge" alt="jest version" style="height: 25px;">
  </a>
  <br>
</h4>

## 📑 Table of Contents
- [🗯️ Introduction](#%EF%B8%8F-introduction)
- [🪛 Features](#-features)
- [🔨 How can I run the project?](#-how-can-i-run-the-project)
   - [Requirements](#requirements)
   - [Default Login-Data](#default-login-data)
   - [Start the project](#start-the-project)
- [⚙️ Explanation of the Code-Structure](#%EF%B8%8F-explanation-of-the-code-structure)
    - [1. General](#1-general)
    - [2. Flyway](#2-flyway)
    - [3. Entity](#3-entity)
    - [4. Repository](#4-repository)
    - [5. Service](#5-service)
    - [6. Controller](#6-controller)

<hr>

## 🗯️ Introduction
› This project handles all REST API requests sent by the frontend (olympia-website). Based on these requests, database queries are executed using Spring Boot and JPA Hibernate, and the corresponding responses are returned.

The project is well documented and was created as part of an assignment during training as an IT specialist for application development.

💝 › It was developed by Yannic Drews, Yanic Doepner, and Nils Sievers. The project follows best practices and, thanks to its clear documentation, can be easily extended.
## 🪛 Features
› The project implemented all features that were required within the scope of the requirements specification and functional specification. These include the following features (that the backend needs to do):
<ul>
  <li>🌐 <strong>REST API for Frontend Communication</strong>: The backend provides a RESTful API that processes all HTTP requests sent by the olympia-website frontend and returns structured JSON responses.</li>
  <br />
  <li>🗄️ <strong>Database Integration with JPA & Hibernate</strong>: The application uses JPA with Hibernate to interact with the MySQL database, enabling efficient object-relational mapping and structured data persistence.</li>
  <br />
  <li>📊 <strong>Competition Data Management</strong>: The API manages the core Olympic tournament entities such as athletes, countries, and competition results.</li>
  <br />
  <li>✏️ <strong>Full CRUD Operations</strong>: The backend supports Create, Read, Update, and Delete operations for all relevant entities, allowing judges to maintain competition data easily.</li>
  <br />
  <li>🔐 <strong>Secure Authentication System</strong>: Judges can register and log in through dedicated API endpoints, ensuring that only authenticated users are able to modify competition data.</li>
  <br />
  <li>🧩 <strong>Layered Architecture</strong>: The application follows a clean layered architecture (Controller → Service → Repository), which separates responsibilities and improves maintainability and scalability.</li>
  <br />
  <li>📦 <strong>DTO Pattern</strong>: Data Transfer Objects are used to separate internal database models from API responses, improving security, maintainability, and API clarity.</li>
  <br />
  <li>⚡ <strong>Spring Boot Caching</strong>: Frequently requested data is cached using Spring Boot’s caching mechanism, improving performance and reducing unnecessary database queries.</li>
  <br />
  <li>📥 <strong>Excel Data Import</strong>: The backend supports importing structured Excel files to quickly populate or update competition datasets.</li>
  <br />
  <li>🐳 <strong>Easy Setup with Docker</strong>: The entire backend environment can be started with a single Docker command, making the setup process fast and beginner-friendly.</li>
  <br />
  <li>📚 <strong>Well Documented & Extendable</strong>: The project follows best practices and is clearly documented, making it easy for developers to understand, maintain, and extend.</li>
</ul>

## 🔨 How can i run the project?
### Requirements
› You need to have <strong><a href="https://www.docker.com/products/docker-desktop/" target="_blank">Docker Desktop</a></strong> installed and started.
### Default Login-Data:
- Judge1: `judge1` / `judge1pwd`
- Judge2: `judge2` / `judge2pwd`

### Start the project
1. Clone the repository by using `git clone https://github.com/yannic-md/olympia-website-api.git`
2. Switch to the correct folder: `cd olympia-website-api`
3. Run `docker compose up --build` and wait a few minutes.
4. Run the Angular Frontend (More Details <strong><a href="https://github.com/yannic-md/olympia-website/blob/main/README.md#-how-can-i-run-the-project" target="_blank">here</a></strong>)
5. The API is now listening for HTTP requests on http://localhost:8080.

<strong>You only need to do that once. From now on, you can start/stop the project in the "Docker Desktop" application.</strong>
## ⚙️ Explanation of the Code-Structure

### 1. General
* The code is located in the package `de.olympia.main.example` (in the `get-started` branch).
* Every subpackage of `de.olympia.main` is automatically loaded when the project starts; a manual definition in `MainApplication` is not required.
* The structure follows a classic layered architecture (Controller, Service, Repository, Entity).

### 2. Flyway
* Flyway is used to manage database migrations.
* The scripts for creating and updating the database can be found in `src/main/resources/db/migration`.
* On every application start, the migrations are executed automatically if necessary.

### 3. Entity
* Contains the JPA entities that represent the database tables.
* In the example, the table `countries` is represented by the `Country` class.
* Each entity is annotated with `@Entity` so that Hibernate can detect it.
* `@Table(name = "countries")` ensures that the exact existing table is used.
* The primary key is defined using `@Id` and `@GeneratedValue`.

### 4. Repository
* Contains interfaces for database access.
* `CountryRepository` extends `JpaRepository`.
* Standard methods such as `findAll`, `findById`, and `save` are automatically available.

### 5. Service
* Contains the business logic of the application.
* Encapsulates access to one or more repositories.
* Provides methods that are used by controllers.

### 6. Controller
* Provides the REST endpoints of the application.
* Annotated with `@RestController`.
* Processes HTTP requests and returns JSON responses.
* `http://localhost:8080/api/countries` returns a list of all countries from the database.
