# mySringBootBase

Spring Boot base project with:

- Spring Web MVC
- Spring Security
- Spring Data JPA
- Liquibase
- PostgreSQL
- H2 in-memory database for tests
- OpenAPI UI (springdoc)

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL running locally or reachable from this app

## Project Structure

- Application entry point: src/main/java/com/ycyw/chatapi/ChatApiApplication.java
- Main config: src/main/resources/application.yaml
- Environment values: root `.env` for Docker Compose, or environment variables for direct execution
- Example environment values: backend/.env.sample.properties

## Environment Configuration

The app reads environment values from environment variables. Docker Compose loads these values from the repository root `.env` file.

1. Copy the sample file to the repo root if needed for local Compose usage:

```bash
cp backend/.env.sample.properties .env
```

2. Update values in `.env`.

Required keys:

- POSTGRES_DB
- POSTGRES_HOST
- POSTGRES_PORT
- POSTGRES_USER
- POSTGRES_PASSWORD
- JWT_SECRET_TOKEN
- MAIN_APP_PORT
- FRONTEND_ORIGIN

Notes:

- Use a strong random value for JWT_SECRET_TOKEN.
- MAIN_APP_PORT controls the HTTP port used by Spring Boot.
- FRONTEND_ORIGIN controls the allowed origin for WebSocket/STOMP connections and should match the frontend URL, for example `http://localhost:4250`.

## Run the Application

Preferred mode (from repository root):

```bash
docker compose up --build
```

Direct backend run (from `backend/`) :

```bash
mvn spring-boot:run
```

Build a jar:

```bash
mvn clean package
```

Run tests:

```bash
mvn test
```

## Testing

- Unit and integration tests are configured to use an H2 in-memory database.
- The Maven `pom.xml` now includes `com.h2database:h2` as a test dependency.
- Test datasource settings are defined in `src/test/resources/application.yaml`.
- Liquibase runs during tests using `classpath:db/changelog/db.changelog-master.yaml`.

## API Docs

Once the app is running, OpenAPI UI is available at:

- http://localhost:${MAIN_APP_PORT}/swagger-ui/index.html

## Ports et endpoints

- Backend API : http://localhost:8050
- Swagger/OpenAPI UI : http://localhost:8050/swagger-ui/index.html

## Database Migrations

Liquibase dependency is included. Add changelogs under:

- src/main/resources/db/changelog

## Important Note About Maven Wrapper

This repository currently has mvnw/mvnw.cmd scripts but is missing wrapper metadata in .mvn/wrapper.
Because of that, ./mvnw does not work at the moment.

Use system Maven commands (mvn ...) until wrapper files are restored.
