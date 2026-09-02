# Backend — API du PoC tchat

API Spring Boot du PoC Your Car Your Way, avec :

- Spring Web MVC
- Spring Security
- Spring Data JPA
- Liquibase
- PostgreSQL
- H2 in-memory database for tests
- WebSocket/STOMP avec SockJS
- OpenAPI UI (springdoc)

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL running locally or reachable from this app, unless tests are run
	with the Dockerized Maven command

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
./mvnw spring-boot:run
```

Build a jar:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test

The repository includes Maven Wrapper metadata. The Docker-based project
commands remain the preferred way to avoid local JDK, Maven, and browser
version differences; see the root `Makefile`.
```

## Testing

- Unit and integration tests are configured to use an H2 in-memory database.
- The Maven `pom.xml` includes `com.h2database:h2` as a test dependency.
- Test datasource settings are defined in `src/test/resources/application.yaml`.
- Liquibase runs during tests using `classpath:db/changelog/db.changelog-master.yaml`.

## API Docs

Once the app is running, OpenAPI UI is available at:

- http://localhost:${MAIN_APP_PORT}/swagger-ui/index.html

## API et WebSocket

- API : `http://localhost:8050`
- Swagger/OpenAPI UI : `http://localhost:8050/swagger-ui/index.html`
- Authentication: `/api/auth/register`, `/api/auth/login`, `/api/auth/me`
- User resource: `/api/user/{id}`
- Conversation history: `/api/conversations/{conversationId}/messages`
- SockJS/STOMP endpoint: `/ws`
- Client message destination: `/app/chat.send`
- Conversation topic: `/topic/conversations/{conversationId}`

Registration expects `name`, `email`, and `password`. A message sent through
STOMP expects `conversationId` and non-blank `content`. REST and STOMP access
are authenticated with JWT.

## Database Migrations

Liquibase dependency is included. Add changelogs under:

- src/main/resources/db/changelog

Pour les commandes Docker Compose et les scénarios de test complets, voir
le [README racine](../README.md).

