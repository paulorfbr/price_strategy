# ── Stage 1: Build ────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Download dependencies (cached layer) then package — skip tests (run in CI)
RUN --mount=type=cache,target=/root/.m2 \
    apk add --no-cache maven && \
    mvn package -DskipTests -q

# ── Stage 2: Runtime ──────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Extract Spring Boot layers for better Docker cache utilisation
COPY --from=build /app/target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
WORKDIR /app

COPY --from=build /app/dependencies/           ./
COPY --from=build /app/spring-boot-loader/     ./
COPY --from=build /app/snapshot-dependencies/  ./
COPY --from=build /app/application/            ./

EXPOSE 8080

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseContainerSupport"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.launch.JarLauncher"]
