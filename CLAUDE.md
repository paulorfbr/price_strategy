# prix-strategie Development Guidelines

Auto-generated from all feature plans. Last updated: 2026-04-10

## Active Technologies

- Java 21 (backend) · HTML5/CSS3/ES6+ vanilla JS (frontend) + Spring Boot 3.3 · Spring Data JPA/Hibernate · Flyway · (main)

## Project Structure

```text
index.html                               ← standalone frontend (primary deliverable)
css/styles.css                           ← CSS variables and all rules
js/state.js                              ← global state object (single source of truth)
js/utils.js                              ← calcCosts(), strategyPrice(), applyPriceType()
js/main.js                               ← navigation, initApp(), event listeners
js/pages/                                ← costs, strategy, pricetype, positioning, ansoff, synthesis
partials/                                ← page-*.html (modular, requires HTTP server)
db/schema.sql                            ← PostgreSQL schema (source for Flyway V1 migration)
src/main/java/com/prf/prixstrategie/     ← Spring Boot backend
  entity/                                ← JPA entities + enums (StrategyType, PriceType, AnsoffQuadrant)
  repository/                            ← Spring Data JPA repositories
  dto/                                   ← Java Records for request/response
  service/                               ← @Transactional business logic
  controller/                            ← REST controllers under /api/v1/
src/main/resources/application.yml       ← Spring Boot config (ddl-auto: validate)
src/main/resources/db/migration/         ← Flyway migrations (V{YYYYMMDD}{seq}__*.sql)
src/test/java/com/prf/prixstrategie/     ← JUnit 5 + Spring Boot Test
specs/main/                              ← Feature planning artifacts
  plan.md · research.md · data-model.md · quickstart.md · contracts/rest-api.md
```

## Commands

```bash
# Start backend
mvn spring-boot:run

# Run backend tests
mvn test

# Init database (first time)
psql -U postgres -c "CREATE DATABASE prixstrategie;"
psql -U postgres -d prixstrategie -f db/schema.sql

# Open standalone frontend (no server needed)
# Double-click index.html OR use IntelliJ: right-click → Open In → Browser

# Add a Flyway migration
# Create: src/main/resources/db/migration/V{YYYYMMDD}{seq}__description.sql
```

## Code Style

- **Java**: Java 21 records for DTOs; `@Transactional` on service methods; Jakarta Bean Validation on all request bodies; standard Spring Boot conventions
- **JavaScript**: ES6+ vanilla; no framework; `const`/`let` only; 2-space indent; `toFixed(2)` for monetary display
- **SQL**: lowercase keywords; snake_case identifiers; explicit CHECK constraints for all business rules

## Recent Changes

- main: Initial v1.0 plan — standalone HTML frontend + optional Spring Boot + PostgreSQL backend

<!-- MANUAL ADDITIONS START -->
<!-- MANUAL ADDITIONS END -->
