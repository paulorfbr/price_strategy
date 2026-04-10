# Implementation Plan: PrixStratégie v1.0

**Branch**: `main` | **Date**: 2026-04-10 | **Spec**: `PRD.md`
**Input**: Product Requirements Document at `/PRD.md` (no separate spec.md — PRD is authoritative)

## Summary

Build PrixStratégie, a six-page pricing decision tool for IT SMEs, in two
delivery modes: a standalone `index.html` (zero dependencies, zero server) and
an optional Spring Boot 3.3 + PostgreSQL 15+ backend for multi-project
persistence. All price calculations MUST be real-time (< 50 ms). Seven business
rules (R1–R7 in PRD §5) are non-negotiable and enforced in both frontend
validation and backend constraints.

## Technical Context

**Language/Version**: Java 21 (backend) · HTML5/CSS3/ES6+ vanilla JS (frontend)
**Primary Dependencies**: Spring Boot 3.3 · Spring Data JPA/Hibernate · Flyway ·
  Jakarta Bean Validation · Maven 3.9+
**Storage**: PostgreSQL 15+ (backend mode); in-memory JS state (standalone mode)
**Testing**: JUnit 5 + Spring Boot Test (backend) · Browser manual smoke tests (frontend)
**Target Platform**: Modern browser (Chrome/Firefox/Edge/Safari, last 2 major versions)
**Project Type**: Web application — standalone HTML + optional REST API backend
**Performance Goals**: Recalculation < 50 ms after any user input; page load < 1 s
**Constraints**: `index.html` fully offline-capable; WCAG AA contrast on all alerts;
  `ddl-auto: validate` in non-local environments
**Scale/Scope**: Single-tenant in standalone mode; multi-project via backend;
  6 frontend pages · ~14 REST endpoints · 7 DB entities

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-checked after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Frontend Autonomy | ✅ Pass | `index.html` confirmed standalone in README; no npm or bundler present |
| II. Real-Time State Sync | ✅ Pass | PRD R7 mandates global sync on every input; global `state` object defined |
| III. Business Rules Inviolability | ✅ Pass | PRD §5 defines R1–R7 exhaustively; alert system (🔴/🟡/🟢) specified |
| IV. Database Integrity at Source | ✅ Pass | `db/schema.sql` defines CHECK constraints, CASCADE, trigger; Flyway configured |
| V. Simplicity Over Abstraction | ✅ Pass | Vanilla JS frontend; standard Spring Boot backend; no extra layers |
| Technology Constraints | ✅ Pass | Java 21, Spring Boot 3.3, Maven 3.9, PostgreSQL 15+, Flyway all match constitution |
| Quality Gates | ✅ Pass | PRD formulas in §4 are authoritative; DTOs use Java Records per README |

**No gate violations. Phase 0 research authorized.**

## Project Structure

### Documentation (this feature)

```text
specs/main/
├── plan.md          ← this file
├── research.md      ← Phase 0 output
├── data-model.md    ← Phase 1 output
├── quickstart.md    ← Phase 1 output
├── contracts/
│   └── rest-api.md  ← Phase 1 output
└── tasks.md         ← Phase 2 output (/speckit-tasks command)
```

### Source Code (repository root)

```text
index.html                            ← standalone frontend (primary deliverable)
css/
└── styles.css                        ← CSS variables and all rules
js/
├── state.js                          ← global state object
├── utils.js                          ← calcCosts(), strategyPrice(), applyPriceType(), finalPrice()
├── main.js                           ← navigation, initApp(), event listeners
└── pages/
    ├── costs.js
    ├── strategy.js
    ├── pricetype.js
    ├── positioning.js
    ├── ansoff.js
    └── synthesis.js
partials/
├── page-costs.html
├── page-strategy.html
├── page-pricetype.html
├── page-positioning.html
├── page-ansoff.html
└── page-synthesis.html
db/
└── schema.sql                        ← PostgreSQL schema (source of truth for migrations)
src/main/java/com/prf/prixstrategie/
├── PrixStrategieApplication.java
├── entity/                           ← JPA entities + enums
├── repository/                       ← Spring Data JPA
├── dto/                              ← Java Records
├── service/                          ← @Transactional business logic
└── controller/                       ← REST controllers (/api/v1/...)
src/main/resources/
├── application.yml
└── db/migration/                     ← Flyway migrations (V1__initial_schema.sql etc.)
src/test/java/com/prf/prixstrategie/  ← JUnit 5 tests
```

**Structure Decision**: Web application with independent frontend and optional backend.
Frontend is the primary deliverable; backend is additive and does not break the
standalone frontend.

## Complexity Tracking

> No constitution violations — table intentionally empty.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| — | — | — |
