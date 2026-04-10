<!--
SYNC IMPACT REPORT
==================
Version change: 0.0.0 (template) → 1.0.0
Bump rationale: MAJOR — initial constitution ratification replacing all placeholder tokens with
                concrete project governance for PrixStratégie.

Modified principles:
  [PRINCIPLE_1_NAME] → I. Frontend Autonomy
  [PRINCIPLE_2_NAME] → II. Real-Time State Synchronization
  [PRINCIPLE_3_NAME] → III. Business Rules Inviolability
  [PRINCIPLE_4_NAME] → IV. Database Integrity at Source
  [PRINCIPLE_5_NAME] → V. Simplicity Over Abstraction

Added sections:
  - Technology Constraints
  - Quality Gates

Removed sections: none

Templates requiring updates:
  ✅ .specify/templates/plan-template.md — "Constitution Check" gate is generic and compatible;
     no changes required.
  ✅ .specify/templates/spec-template.md — Requirements structure aligns with PRD §5 business
     rules model; no changes required.
  ✅ .specify/templates/tasks-template.md — Phase structure and parallel markers align with
     Quality Gates; no changes required.

Follow-up TODOs: none — all placeholders resolved.
-->

# PrixStratégie Constitution

## Core Principles

### I. Frontend Autonomy

The standalone `index.html` is the primary deliverable of this project.
It MUST function without any server, build tool, or external dependency.
No npm, no webpack, no CDN imports — ever. All CSS, JS, and HTML MUST be
embeddable in a single self-contained file that opens directly in a browser.

The modular source layout (`css/`, `js/`, `partials/`) exists for development
convenience only; it is NOT a distribution format. Any feature that works in
the modular layout MUST also work in the compiled standalone file.

**Rationale**: The core value proposition of PrixStratégie for PME IT users is
zero-friction access. A deployable artifact that requires infrastructure or a
runtime negates that value.

### II. Real-Time State Synchronization

Every user input MUST trigger a global recalculation of all active pages within
50 ms. The global `state` object is the single source of truth for all computed
values. Stale derived values and partial updates are forbidden.

Concretely: changing a cost input on the Costs page MUST immediately update
the Synthesis page KPIs, the Strategy page strategic price, and any other
dependent display — without requiring navigation or a manual refresh.

**Rationale**: Delayed feedback breaks the decision-making flow that makes the
tool valuable. Real-time reaction is listed as an explicit NFR in the PRD.

### III. Business Rules Inviolability

The seven business rules defined in PRD §5 (R1–R7) are non-negotiable
constraints, not implementation suggestions:

- **R1 — Price floor**: The final price MUST never be displayed below the full
  unit cost without a visible red alert.
- **R2 — Transformation precedence**: Strategy multiplier is applied first;
  price-type transformation is applied second. This order MUST NOT be reversed.
- **R3 — Independent segment validation**: Each discriminatory segment is
  validated individually against the minimum price.
- **R4 — Alignment requirement**: If strategy = alignment, the competitive
  market price field is mandatory before a strategic price can be computed.
- **R5 — Volume positivity**: Volume MUST be ≥ 1 (enforced in both frontend
  validation and backend CHECK constraint).
- **R6 — Margin bounds**: Target margin MUST be in [0, 100). A margin of 100%
  or above yields an infinite optimal price and MUST be rejected.
- **R7 — Global sync**: Any state change triggers a full recalculation across
  all pages (see Principle II).

No user action may bypass these rules silently. Violations MUST surface as
clearly visible, colour-coded alerts (🔴 / 🟡 / 🟢 as defined in the PRD).

**Rationale**: The PRD is the authoritative contract with users. Silent violations
corrupt the decision output and undermine user trust.

### IV. Database Integrity at Source

All data constraints MUST be enforced at the PostgreSQL schema level first:
CHECK constraints, NOT NULL, ON DELETE CASCADE, and triggers. Application-level
validation via Jakarta Bean Validation is a secondary defence layer, not the
primary one.

Schema changes MUST be delivered as Flyway migration files. Direct manual
schema edits in any environment beyond a developer's local machine are
forbidden. The Spring Boot datasource MUST use `ddl-auto: validate` in all
non-local environments.

Enums MUST be stored as VARCHAR (not native PostgreSQL enum types) to preserve
Hibernate portability, as established in the current schema.

**Rationale**: The database is the persistence boundary. Constraints enforced
only in application code can be bypassed by direct DB access or ORM edge cases.
Schema-as-code via Flyway ensures reproducible environments.

### V. Simplicity Over Abstraction

The frontend uses no framework, no build pipeline, and no package manager.
The backend uses standard Spring Boot idioms without additional layers.

New complexity requires explicit justification in the implementation plan's
Complexity Tracking table. YAGNI applies: do not build for hypothetical future
requirements. Three similar lines of code are preferable to a premature
abstraction.

**Rationale**: PrixStratégie is a focused decision-support tool. Accidental
complexity lengthens onboarding, increases maintenance burden, and conflicts
with the standalone frontend principle.

## Technology Constraints

The following technology choices are fixed for v1.x and MUST NOT be changed
without a constitution amendment:

| Layer | Technology | Constraint |
|-------|------------|------------|
| Frontend runtime | HTML5 · CSS3 · ES6+ vanilla JS | No framework, no transpiler, no bundler |
| Frontend distribution | Single `index.html` | Zero external files required at runtime |
| Backend language | Java 21 | Records MUST be used for DTOs |
| Backend framework | Spring Boot 3.3 | Standard idioms only; no extra DI frameworks |
| Build tool | Maven 3.9+ | No Gradle migration without amendment |
| Persistence | Spring Data JPA / Hibernate | No QueryDSL, jOOQ, or raw JDBC layers |
| Database | PostgreSQL 15+ | No other RDBMS; no NoSQL stores |
| Migrations | Flyway | No Liquibase migration without amendment |
| REST base path | `/api/v1/` | All endpoints MUST be under this path |
| HTTP status codes | 200 · 201 · 204 · 400 · 404 | No non-standard codes; no 200-wrapped errors |
| Browser compatibility | Chrome · Firefox · Edge · Safari (last 2 major) | MUST be verified before release |

## Quality Gates

The following gates MUST be satisfied before any feature is considered complete:

1. **Business rule compliance**: Any change touching cost formulas, price
   transformation logic, or strategy multipliers MUST be validated against the
   exact formulas in PRD §4 before merge.

2. **Schema-first changes**: Any new entity, column, or constraint MUST be
   delivered as a Flyway migration. The migration MUST be idempotent and
   reversible where possible.

3. **DTO validation**: All REST request bodies MUST use Java Records annotated
   with Jakarta Bean Validation. Unannotated fields that accept user input are
   a blocking defect.

4. **Standalone verification**: The compiled `index.html` MUST be opened
   directly in a browser (no server) and validated to work before any frontend
   release.

5. **Alert coverage**: Every business rule violation (R1–R7) MUST produce a
   visible, colour-coded alert. Silent failures are blocking defects.

6. **No trailing state**: Removing a competitor, segment, or initiative MUST
   remove all its derived displays immediately (real-time sync, Principle II).

## Governance

This constitution supersedes all other development practices and conventions for
the PrixStratégie project. In case of conflict between this document and any
other guideline, this constitution takes precedence.

**Amendment procedure**:
1. Describe the change and its rationale in the PR description.
2. Perform an impact analysis: which principles, templates, and existing
   features are affected?
3. Bump the version according to semantic versioning:
   - **MAJOR**: Breaking change — principle removed, redefined, or governance
     model restructured.
   - **MINOR**: New principle, section, or materially expanded guidance added.
   - **PATCH**: Clarification, wording refinement, or typo fix.
4. Update `LAST_AMENDED_DATE` to the amendment date.
5. Run `/speckit-constitution` to propagate changes to dependent templates.

**Compliance review**: All pull requests MUST verify compliance with Core
Principles before merge. Implementation plans MUST include a Constitution Check
gate. Complexity violations require a completed Complexity Tracking entry.

Refer to `PRD.md` for the authoritative source of business rules and feature
specifications.

**Version**: 1.0.0 | **Ratified**: 2026-04-10 | **Last Amended**: 2026-04-10
