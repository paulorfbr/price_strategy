# Quickstart: PrixStratégie v1.0

**Date**: 2026-04-10 | **Branch**: `main`

## Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| Java | 21+ | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| PostgreSQL | 15+ | `psql --version` |
| Any modern browser | Chrome/Firefox/Edge/Safari last 2 major | — |

## Mode 1 — Standalone Frontend (No Server Required)

This is the primary delivery mode. No installation, no database, no backend.

```bash
# Option A: Open directly in browser (no fetch/partials)
# Double-click index.html or drag it into your browser window

# Option B: Via IntelliJ IDEA (enables partials via fetch)
# 1. Right-click index.html in the Project pane
# 2. Open In → Browser → (your preferred browser)
# IntelliJ starts its built-in server at http://localhost:63342/...
```

**Validation**: All 6 pages load. Enter cost values → Synthesis page updates
within 50 ms. Change strategy → strategic price updates immediately.

---

## Mode 2 — Backend + Database (Multi-Project Persistence)

### 1. Create the PostgreSQL database

```bash
psql -U postgres -c "CREATE DATABASE prixstrategie;"
psql -U postgres -d prixstrategie -f db/schema.sql
```

### 2. Configure the application

Edit `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/prixstrategie
    username: postgres
    password: <your_password>
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
```

### 3. Start the backend

```bash
mvn spring-boot:run
# API available at http://localhost:8080/api/v1
```

### 4. Verify the API

```bash
# Create a project
curl -s -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Project"}' | jq .

# List projects
curl -s http://localhost:8080/api/v1/projects | jq .
```

**Expected**: 201 Created with project JSON including `id`, then 200 OK with
an array containing the new project.

---

## Development Workflow

### Running backend tests

```bash
mvn test
```

### Rebuilding the standalone index.html

The `index.html` is manually maintained as a self-contained file. When making
changes to the modular sources (`css/`, `js/`, `partials/`), replicate the
changes into the corresponding inline sections of `index.html`.

> There is no automated build step. This is intentional (Principle I + V).

### Adding a Flyway migration

1. Create `src/main/resources/db/migration/V{YYYYMMDD}{seq}__description.sql`
   e.g. `V20260410001__initial_schema.sql`
2. Add only `CREATE TABLE`, `ALTER TABLE`, `CREATE INDEX`, or DML for seed data.
3. Never modify an existing migration file after it has run anywhere.
4. Run `mvn spring-boot:run` — Flyway applies pending migrations automatically.

---

## Business Rule Smoke Tests

Verify these manually in `index.html` after any change to `utils.js`:

| Test | Input | Expected output |
|------|-------|-----------------|
| Price floor alert | variableCost=100, fixedCost=0, volume=1, targetMargin=30, finalPrice=80 | 🔴 alert "vente à perte" |
| Optimal met | variableCost=50, fixedCost=0, volume=1, targetMargin=20, finalPrice=63 | 🟢 alert |
| Margin gap | variableCost=50, fixedCost=0, volume=1, targetMargin=20, finalPrice=55 | 🟡 alert |
| Magic price | price=299.50 | 299.99 |
| Psychological price | price=1050 | 997 |
| Rounded price | price=312 | 310 |
| Volume=0 | volume=0 | Input rejected (R5) |
| Margin=100 | targetMargin=100 | Input rejected (R6) |
| Alignment — no price | strategy=alignment, alignmentPrice=null | strategic price blocked |

---

## Troubleshooting

| Symptom | Cause | Fix |
|---------|-------|-----|
| Partials not loading in browser via `file://` | `fetch()` blocked on `file://` protocol | Use IntelliJ built-in server or `python -m http.server 8000` |
| `FlywayException: Validate failed` | Schema differs from migration history | Check if `db/schema.sql` matches all applied migrations |
| `ddl-auto: create` drops tables | Wrong config in non-local env | Ensure `ddl-auto: validate` in non-local `application.yml` |
| CORS error calling API from IntelliJ server | Missing CORS config | Confirm `WebMvcConfigurer` allows `http://localhost:*` |
| `OptimalPrice = Infinity` | `targetMargin = 100` passed to backend | Jakarta validation should catch this; check constraint on `pricing_costs` |
