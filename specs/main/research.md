# Research: PrixStratégie v1.0

**Date**: 2026-04-10 | **Branch**: `main`

## Decision Log

---

### 1. Frontend Testing Strategy

**Decision**: No automated test framework for the frontend. Manual browser smoke
tests using the standalone `index.html` are the primary validation mechanism.

**Rationale**: Constitution Principle V (Simplicity) prohibits adding a build
pipeline or package manager. Introducing Jest, Vitest, or any equivalent would
require a bundler. The six-page application has deterministic calculation
functions (`calcCosts`, `strategyPrice`, `applyPriceType`) that are fully
specifiable by the business rule formulas in PRD §4 — these can be validated by
inspection and by running the standalone file in a browser. PRD §5 R7 (global
sync) is testable by direct interaction.

**Alternatives considered**:
- **Jest (Node-based)**: Rejected — requires npm and a test runner; violates
  Principle I (Frontend Autonomy) and Principle V (Simplicity).
- **QUnit (CDN import in tests/)**: Rejected — adds an external CDN dependency
  that is unnecessary given the small surface area and low mutation rate of
  core utilities.
- **Playwright E2E**: Deferred to v1.1 when the backend integration exists and
  multi-page flows can be validated end-to-end more efficiently.

---

### 2. Backend CORS Configuration

**Decision**: Enable CORS for `http://localhost:63342` (IntelliJ IDEA built-in
server) and `http://localhost:*` for local development. In production the
frontend is served from the same origin (same Spring Boot static resources) so
CORS is a dev-only concern.

**Rationale**: The README instructs developers to open `index.html` via IntelliJ
IDEA on port 63342. The Spring Boot backend runs on port 8080. Without CORS
headers on `http://localhost:8080/api/v1/**`, browsers block XHR from 63342.

**Alternatives considered**:
- **Global `@CrossOrigin("*")`**: Rejected — too permissive for any
  non-localhost deployment context; wildcard disables credentials header.
- **Nginx reverse proxy for dev**: Rejected — introduces infrastructure dependency
  that violates Principle V for a local dev concern.

**Implementation note**: Use a `@Bean WebMvcConfigurer` with
`registry.addMapping("/api/v1/**").allowedOriginPatterns("http://localhost:*")`.

---

### 3. Flyway Migration Naming Convention

**Decision**: Use timestamp-prefixed versioned migrations:
`V{YYYYMMDD}{sequence}__description.sql` (e.g., `V20260410001__initial_schema.sql`).
Repeatable migrations (`R__`) are permitted only for views and functions, not
for table definitions.

**Rationale**: The existing `db/schema.sql` is the v1 source of truth. The
first Flyway migration is a straight import of that file. Timestamp prefixes
avoid merge conflicts in team settings. `ddl-auto: validate` ensures Hibernate
validates the schema without modifying it.

**Alternatives considered**:
- **Sequential integers (V1__, V2__)**: Simpler but creates renumbering conflicts
  on parallel feature branches; rejected in favour of timestamp approach.

---

### 4. PostgreSQL Enum Storage

**Decision**: Store enum values as `VARCHAR` columns, not native PostgreSQL
`ENUM` types. Hibernate maps Java enums via `@Enumerated(EnumType.STRING)`.

**Rationale**: Already established in the existing schema per PRD §7
("Enums PostgreSQL — migrés en VARCHAR après fix de compatibilité Hibernate").
This decision is locked by the constitution (Technology Constraints table).

**Alternatives considered**:
- **Native PG enums**: Rejected — Hibernate requires DDL changes to add enum
  values, which breaks `ddl-auto: validate`.

---

### 5. Price Calculation Precision

**Decision**: All monetary amounts computed to exactly 2 decimal places using
JavaScript's built-in `Math` and `toFixed(2)`. No external currency library.

**Rationale**: PRD §2 specifies "Précision des calculs: 2 décimales sur tous
les montants." The price transformation algorithms (§4.3) are defined with
exact integer/float arithmetic. JavaScript's floating-point is sufficient at
this precision level for amounts up to ~99,999.

**Alternatives considered**:
- **Dinero.js**: Rejected — adds a dependency and a build step; violates
  Principle I and Principle V.
- **BigDecimal-style string arithmetic**: Rejected — unnecessary overhead given
  the 2-decimal precision requirement and scale ceiling.

---

## Resolved Unknowns from Technical Context

| Item | Was | Now |
|------|-----|-----|
| Testing | NEEDS CLARIFICATION | Manual browser smoke tests (no framework; by constitution) |
| CORS | Implicit | `localhost:*` allowed patterns in dev; same-origin in production |
| Migration naming | Implicit | Timestamp-based versioned migrations |
| Enum storage | Partial | VARCHAR confirmed; locked by constitution |
| Calculation precision | Partial | 2 decimals; native JS Math; no library |
