FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

RUN apt-get update \
	&& apt-get install -y --no-install-recommends curl \
	&& rm -rf /var/lib/apt/lists/*

COPY --from=builder /workspace/target/*.jar ./app.jar

EXPOSE 8080
HEALTHCHECK --interval=5s --timeout=3s --retries=12 --start-period=20s CMD curl --fail --silent http://127.0.0.1:8050/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
