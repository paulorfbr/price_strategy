# Implementation Plan: PrixStratégie v1.0

**Branch**: `main` | **Date**: 2026-04-10 | **Spec**: PRD.md  
**Input**: Feature specification from `PRD.md` (pricing strategy tool for IT SMBs)

## Summary

Build a standalone HTML5 pricing-decision tool (primary deliverable: `index.html`) with six
interconnected pages (Costs → Strategy → Price Type → Positioning → Ansoff → Synthesis),
backed by an optional Spring Boot 3.3 + PostgreSQL REST API for multi-project persistence.
All seven PRD business rules (R1–R7) must be enforced in real time with colour-coded alerts.

## Technical Context

**Language/Version**: Java 21 (backend) · ES6+ Vanilla JS (frontend)  
**Primary Dependencies**: Spring Boot 3.3 · Spring Data JPA/Hibernate · Flyway · Jakarta Bean Validation  
**Storage**: PostgreSQL 15+ (optional backend) · in-memory `state` object (frontend)  
**Testing**: JUnit 5 + Spring Boot Test (backend) · Manual browser smoke tests (frontend — no framework by constitution)  
**Target Platform**: Any modern browser (Chrome/Firefox/Edge/Safari last 2 major) for frontend; Linux/macOS/Windows JVM for backend  
**Project Type**: Web application — standalone frontend + optional REST backend  
**Performance Goals**: All page recalculations within 50 ms of any user input (R7)  
**Constraints**: Frontend must run with zero external dependencies from `file://` protocol · Backend uses `ddl-auto: validate`  
**Scale/Scope**: Single-user tool for IT SMBs; backend supports multi-project persistence; no concurrent user scaling target for v1.0

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Notes |
|-----------|--------|-------|
| I. Frontend Autonomy | PASS | `index.html` is self-contained; no npm/CDN/bundler; modular `css/`+`js/`+`partials/` for dev only |
| II. Real-Time State Sync | PASS | Global `state` object is single source of truth; all inputs trigger `calcCosts()` + full re-render within 50 ms |
| III. Business Rules Inviolability | PASS | R1–R7 enforced; violations surface as 🔴/🟡/🟢 colour-coded alerts (no silent bypass) |
| IV. Database Integrity at Source | PASS | All constraints in PostgreSQL schema (`CHECK`, `NOT NULL`, `ON DELETE CASCADE`, trigger); Flyway migrations; `ddl-auto: validate` |
| V. Simplicity Over Abstraction | PASS | No framework, no extra layers; standard Spring Boot idioms; no QueryDSL/jOOQ; 3-similar-lines rule applied |

**Technology Constraints compliance**:
- Frontend runtime: HTML5 · CSS3 · ES6+ vanilla JS ✓
- Frontend distribution: single `index.html` ✓
- Backend: Java 21 records for DTOs ✓
- Framework: Spring Boot 3.3 standard idioms ✓
- Build: Maven 3.9+ ✓
- Persistence: Spring Data JPA/Hibernate ✓
- Database: PostgreSQL 15+ ✓
- Migrations: Flyway (timestamp-prefixed `V{YYYYMMDD}{seq}__*.sql`) ✓
- REST base path: `/api/v1/` ✓
- HTTP status codes: 200/201/204/400/404 only ✓

**Quality Gates pre-design**:
1. Business rule compliance — formulas from PRD §4 locked in `utils.js` (`calcCosts`, `strategyPrice`, `applyPriceType`) ✓
2. Schema-first changes — `db/schema.sql` delivered as Flyway `V20260410001__initial_schema.sql` ✓
3. DTO validation — Java Records + Jakarta Bean Validation on all request bodies ✓
4. Standalone verification — `index.html` opens directly in browser ✓
5. Alert coverage — R1–R7 produce visible alerts ✓
6. No trailing state — removing competitor/segment/initiative removes derived displays immediately ✓

**Complexity Tracking**: No violations — no additional entries required.

## Project Structure

### Documentation (this feature)

```text
specs/main/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output — 5 decisions resolved
├── data-model.md        # Phase 1 output — 6 entities + frontend state model
├── quickstart.md        # Phase 1 output — setup + smoke tests
├── contracts/
│   └── rest-api.md      # Phase 1 output — full REST contract
└── tasks.md             # Phase 2 output (/speckit-tasks command)
```

### Source Code (repository root)

```text
index.html                               ← primary deliverable (self-contained)
css/
└── styles.css                           ← CSS variables + all rules
js/
├── state.js                             ← global state object (single source of truth)
├── utils.js                             ← calcCosts(), strategyPrice(), applyPriceType()
└── main.js                              ← navigation, initApp(), event listeners
js/pages/
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
└── schema.sql                           ← PostgreSQL schema (source of truth for Flyway V1)
src/main/java/com/prf/prixstrategie/
├── entity/                              ← JPA entities + enums (StrategyType, PriceType, AnsoffQuadrant)
├── repository/                          ← Spring Data JPA repositories
├── dto/                                 ← Java Records (request + response)
├── service/                             ← @Transactional business logic
└── controller/                          ← REST controllers under /api/v1/
src/main/resources/
├── application.yml                      ← Spring Boot config (ddl-auto: validate)
└── db/migration/
    └── V20260410001__initial_schema.sql ← Flyway V1 (copy of db/schema.sql)
src/test/java/com/prf/prixstrategie/     ← JUnit 5 + Spring Boot Test
```

**Structure Decision**: Web application layout — standalone `index.html` as primary delivery;
Spring Boot backend in `src/` as secondary. Both share the same repository with no submodule
or monorepo tooling (Principle V).

## Complexity Tracking

> No constitution violations — table empty by design.
