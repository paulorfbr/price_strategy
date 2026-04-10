---
description: "Task list for PrixStratégie v1.0 implementation"
---

# Tasks: PrixStratégie v1.0

**Input**: Design documents from `specs/main/`
**Prerequisites**: plan.md ✅ · research.md ✅ · data-model.md ✅ · contracts/rest-api.md ✅ · quickstart.md ✅
**Spec source**: `PRD.md` (authoritative — no separate spec.md on main branch)

**Tests**: Not explicitly requested — test tasks are OMITTED per template rules.
Test coverage expectations: manual browser smoke tests (see quickstart.md) + Spring Boot Test for backend.

**Organization**: Tasks grouped by user story (derived from PRD objectives O1–O6).
Each story maps to one page of the application and is independently completable.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[USn]**: Maps to user story n (O1→US1 … O6→US6 per PRD §2)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify project skeleton, configure build, and wire the Flyway migration
so the backend database matches the schema source of truth.

- [ ] T001 Verify project directory layout matches `specs/main/plan.md` source structure (index.html · css/ · js/ · partials/ · db/ · src/)
- [ ] T002 Create Flyway migration `src/main/resources/db/migration/V20260410001__initial_schema.sql` by copying `db/schema.sql` content
- [ ] T003 [P] Configure `src/main/resources/application.yml` with datasource, `ddl-auto: validate`, and Flyway settings per `specs/main/quickstart.md`
- [ ] T004 [P] Add `WebMvcConfigurer` CORS bean in `src/main/java/com/prf/prixstrategie/config/CorsConfig.java` allowing `http://localhost:*`
- [ ] T005 [P] Verify `pom.xml` declares Java 21, Spring Boot 3.3, Spring Data JPA, Flyway, Jakarta Validation, and PostgreSQL driver dependencies

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core JS state and calculation engine that ALL six pages depend on,
plus JPA entity layer that ALL backend controllers depend on.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

### Frontend Foundations

- [ ] T006 Implement `js/state.js` — global `state` object with all fields from `specs/main/data-model.md` (Frontend State Model section): variableCost, fixedCost, volume, targetMargin, currency, alignmentPrice, strategy, pricetype, axis config, myX/myY/myName, segments[], competitors[], ansoffInitiatives[]
- [ ] T007 Implement `calcCosts(state)` in `js/utils.js` — returns `{ unitCost, minPrice, optimalPrice }` using PRD §4.1 formulas: `unitCost = variableCost + fixedCost/volume`, `minPrice = unitCost`, `optimalPrice = unitCost / (1 − targetMargin/100)`; enforce R5 (volume > 0) and R6 (margin < 100)
- [ ] T008 Implement `strategyPrice(state, optimalPrice)` in `js/utils.js` — returns strategic price per PRD §4.2: luxury ×2.2, penetration ×1.05, alignment = alignmentPrice (R4: require non-null), discriminatory = optimalPrice (base for segments)
- [ ] T009 Implement `applyPriceType(price, pricetype)` in `js/utils.js` — returns transformed price per PRD §4.3 exact formulas: magic (`floor(price) − 0.01`), psychological (threshold array lookup `−3`), rounded (magnitude-step rounding)
- [ ] T010 Implement `finalPrice(state)` in `js/utils.js` — returns `applyPriceType(strategyPrice(state, optimalPrice), state.pricetype)`; enforce R2 (strategy first, then type)
- [ ] T011 Implement `js/main.js` — `initApp()` bootstrapping, page navigation (show/hide `.page` sections), global `input` event listener on `document` that calls `renderAll()` on every state change (R7 — global sync)

### Backend Entity Layer

- [ ] T012 [P] Implement `PricingProject.java` entity with `id`, `name`, `description`, `createdAt`, `updatedAt`; `@PreUpdate` hook for `updatedAt`; `@OneToOne(cascade=ALL)` to costs, strategy, positioning; `@OneToMany(cascade=ALL, orphanRemoval=true)` to segments, competitors, initiatives
- [ ] T013 [P] Implement `PricingCosts.java` entity per `specs/main/data-model.md` — all columns with `@Column` constraints; `@OneToOne(mappedBy="costs")` back-reference
- [ ] T014 [P] Implement `PricingStrategy.java` entity — `strategy VARCHAR`, `priceType VARCHAR`; `@Enumerated(EnumType.STRING)` for `StrategyType` and `PriceType` enums in `entity/` package
- [ ] T015 [P] Implement `PositioningConfig.java` entity — axis labels, `myX`, `myY`, `myName`; SMALLINT CHECK 0–100 reflected as `@Column(columnDefinition=...)`
- [ ] T016 [P] Implement `PriceSegment.java`, `Competitor.java`, `AnsoffInitiative.java` entities per data-model.md; include `sortOrder` field on all three
- [ ] T017 [P] Implement `StrategyType.java`, `PriceType.java`, `AnsoffQuadrant.java` enums with values matching `specs/main/contracts/rest-api.md` validation constraints
- [ ] T018 Implement Spring Data JPA repositories: `PricingProjectRepository`, `PricingCostsRepository`, `PricingStrategyRepository`, `PositioningConfigRepository`, `PriceSegmentRepository`, `CompetitorRepository`, `AnsoffInitiativeRepository` in `repository/` package
- [ ] T019 Implement `ProjectService.java` in `service/` — `@Transactional` create method that creates project + default pricing_costs + pricing_strategy + positioning_config + 3 default segments (Starter ×0.70, Pro ×1.00, Enterprise ×1.60); delete method (cascade handles children)

**Checkpoint**: Foundation ready — all user stories can now begin in parallel.

---

## Phase 3: User Story 1 — Costs & Profitability (Priority: P1) 🎯 MVP

**PRD Objective**: O1 — Calculate unit cost, minimum price, and optimal price from 4 inputs.
**Goal**: User enters cost data and immediately sees the 3 price thresholds with colour alerts.

**Independent Test**: Open `index.html`, navigate to "Coûts & Rentabilité", enter
`variableCost=45, fixedCost=3000, volume=100, targetMargin=30` → verify
`unitCost=75.00`, `minPrice=75.00`, `optimalPrice=107.14`; set finalPrice below minimum → red alert appears.

### Frontend — US1

- [ ] T020 [US1] Implement `partials/page-costs.html` — 5 input fields (variableCost, fixedCost, volume, targetMargin, currency selector EUR/USD/GBP/CHF) wired to `state` via `id` attributes; layout per PRD §4.1
- [ ] T021 [US1] Implement `js/pages/costs.js` — `renderCosts()` function: calls `calcCosts(state)`, renders 5 KPI cards (Coût Variable, Coût Complet, Prix Minimum, Prix Optimal, Prix Final), draws price bar with coloured zones, displays 🔴/🟡/🟢 alert per R1 threshold logic
- [ ] T022 [US1] Wire `renderCosts()` into `js/main.js` `renderAll()` and register it for the "Coûts" navigation tab
- [ ] T023 [US1] Embed `page-costs.html` content and `costs.js` logic inline in `index.html` standalone section (preserve R1 alert, price bar, and all 5 KPIs)

### Backend — US1

- [ ] T024 [P] [US1] Implement `CostsRequest.java` Java Record in `dto/` with Jakarta Validation: `@NotNull`, `@DecimalMin("0")` on variableCost/fixedCost, `@Min(1)` on volume, `@DecimalMin("0") @DecimalMax(value="100", inclusive=false)` on targetMargin
- [ ] T025 [P] [US1] Implement `CostsResponse.java` Java Record in `dto/`
- [ ] T026 [US1] Implement `CostsService.java` in `service/` — `@Transactional` `upsert(projectId, CostsRequest)` method; validate R4 (alignmentPrice required when strategy=alignment via cross-field check)
- [ ] T027 [US1] Implement `CostsController.java` in `controller/` — `GET /api/v1/projects/{id}/costs` and `PUT /api/v1/projects/{id}/costs` per `specs/main/contracts/rest-api.md`

**Checkpoint**: User Story 1 fully functional and independently testable.

---

## Phase 4: User Story 2 — Pricing Strategy (Priority: P2)

**PRD Objective**: O2 — 4 configurable strategies each producing a distinct strategic price.
**Goal**: User selects a strategy; strategic price updates immediately; discriminatory shows segment table.

**Independent Test**: Select "Luxury" strategy → strategic price = optimalPrice × 2.2.
Select "Discriminatory" → segment table appears with Starter/Pro/Enterprise rows, each showing
their computed final price and status badge.

### Frontend — US2

- [ ] T028 [US2] Implement `partials/page-strategy.html` — 4 strategy cards (luxury/penetration/alignment/discriminatory); conditional alignment price input (shown only when alignment selected, R4); conditional segments table (shown only when discriminatory selected)
- [ ] T029 [US2] Implement `js/pages/strategy.js` — `renderStrategy()`: highlights active strategy card, shows/hides conditional sections, renders segment table rows with `base_price = optimalPrice × multiplier`, `final_price = applyPriceType(base_price)`, status badge per R3
- [ ] T030 [US2] Add inline segment row editing (name, multiplier) to `page-strategy.html` and `strategy.js`; changes update `state.segments` and trigger `renderAll()`
- [ ] T031 [US2] Embed strategy page and segment logic inline in `index.html`

### Backend — US2

- [ ] T032 [P] [US2] Implement `StrategyRequest.java` and `StrategyResponse.java` Records in `dto/` with enum validation
- [ ] T033 [P] [US2] Implement `SegmentRequest.java` and `SegmentResponse.java` Records in `dto/`
- [ ] T034 [US2] Implement `StrategyService.java` — upsert strategy; validate enum values
- [ ] T035 [US2] Implement `StrategyController.java` — `GET/PUT /api/v1/projects/{id}/strategy`
- [ ] T036 [US2] Implement `SegmentController.java` — `GET /api/v1/projects/{id}/segments`, `POST`, `PUT /{sid}`, `DELETE /{sid}` per contract

**Checkpoint**: User Stories 1 and 2 independently functional.

---

## Phase 5: User Story 3 — Price Type Transformation (Priority: P3)

**PRD Objective**: O3 — 3 psychological transformations with comparison display.
**Goal**: User selects a price type; final price is recalculated and before/after is shown clearly.

**Independent Test**: Set optimalPrice=299.50, select "Magic" → displays 299.99.
Select "Psychological" with price=1050 → displays 997.
Select "Rounded" with price=312 → displays 310.

### Frontend — US3

- [ ] T037 [US3] Implement `partials/page-pricetype.html` — 3 type cards; large before/after price display; comparison table with 4 example price levels per PRD §4.3
- [ ] T038 [US3] Implement `js/pages/pricetype.js` — `renderPriceType()`: highlights active type, renders before price (strategic price), after price (finalPrice), example comparison table with colour-coded variation, alert on active type's psychological effect
- [ ] T039 [US3] Embed price type page inline in `index.html`

*(Backend: priceType is a field on pricing_strategy — already covered by T032–T035 in US2.)*

**Checkpoint**: User Stories 1, 2, and 3 independently functional.

---

## Phase 6: User Story 4 — Competitive Perceptual Map (Priority: P4)

**PRD Objective**: O4 — Visualize competitive positioning on two customisable axes.
**Goal**: User places their company and competitors on an interactive canvas; distance table sorts by proximity.

**Independent Test**: Configure axes, add 2 competitors with positions; canvas renders
company square + competitor circles; distance table shows euclidean distances in ascending order.

### Frontend — US4

- [ ] T040 [US4] Implement `partials/page-positioning.html` — axis label inputs (4), company position (X/Y/name), competitor list (add/remove rows with name/x/y/color picker)
- [ ] T041 [US4] Implement `js/pages/positioning.js` — `renderPositioning()`: draws HTML5 Canvas (background grid at 25/50/75, axis arrows with labels, company as 45°-rotated blue square, competitors as coloured circles with labels), renders sorted distance analysis table per PRD §4.4 formula `√((Δx)²+(Δy)²)`
- [ ] T042 [US4] Add competitor CRUD in `page-positioning.html` and `positioning.js`; each add/remove/edit triggers `renderAll()`
- [ ] T043 [US4] Embed positioning page and canvas logic inline in `index.html`

### Backend — US4

- [ ] T044 [P] [US4] Implement `PositioningRequest.java` and `PositioningResponse.java` Records in `dto/`
- [ ] T045 [P] [US4] Implement `CompetitorRequest.java` and `CompetitorResponse.java` Records — include hex color regex validation `@Pattern(regexp="#[0-9A-Fa-f]{6}")`
- [ ] T046 [US4] Implement `PositioningService.java` — upsert positioning config
- [ ] T047 [US4] Implement `PositioningController.java` — `GET/PUT /api/v1/projects/{id}/positioning`
- [ ] T048 [US4] Implement `CompetitorController.java` — full CRUD per contract (`GET`, `POST`, `PUT /{cid}`, `DELETE /{cid}`)

**Checkpoint**: User Stories 1–4 independently functional.

---

## Phase 7: User Story 5 — Ansoff Matrix (Priority: P5)

**PRD Objective**: O5 — Classify initiatives by quadrant with risk-aware pricing recommendations.
**Goal**: User adds initiatives to quadrants; clicking a quadrant shows the detail panel with recommendations.

**Independent Test**: Add an initiative to "Pénétration de Marché"; it appears as a
dot in the correct quadrant; clicking the quadrant shows the detail panel with risk badge
"Faible" and pricing recommendation "Pénétration ou Alignement".

### Frontend — US5

- [ ] T049 [US5] Implement `partials/page-ansoff.html` — 2×2 grid with quadrant labels, risk badges, initiative dots; initiative form (name/quadrant/description); detail side panel per PRD §4.5
- [ ] T050 [US5] Implement `js/pages/ansoff.js` — `renderAnsoff()`: renders initiative dots in correct quadrant, handles quadrant click to show detail panel (5 recommended actions, pricing recommendation, risk analysis, cannibalisation warning for product-dev quadrant)
- [ ] T051 [US5] Add initiative CRUD in `page-ansoff.html` and `ansoff.js`; each change triggers `renderAll()`
- [ ] T052 [US5] Embed Ansoff page inline in `index.html`

### Backend — US5

- [ ] T053 [P] [US5] Implement `AnsoffRequest.java` and `AnsoffResponse.java` Records — `@NotBlank` name, `@NotNull` quadrant with enum validation
- [ ] T054 [US5] Implement `AnsoffController.java` — full CRUD per contract (`GET`, `POST`, `PUT /{iid}`, `DELETE /{iid}`)

**Checkpoint**: User Stories 1–5 independently functional.

---

## Phase 8: User Story 6 — Synthesis & Validation (Priority: P6)

**PRD Objective**: O6 — Consolidated view with all KPIs, final price validation, and threshold status.
**Goal**: All decisions visible in one page; traffic-light validation of final price against thresholds.

**Independent Test**: With cost data and strategy set, navigate to Synthèse;
verify 5 KPI cards show correct computed values; verify summary table matches
all inputs; verify threshold alert matches 🔴/🟡/🟢 logic per PRD §4.6.

### Frontend — US6

- [ ] T055 [US6] Implement `partials/page-synthesis.html` — 5 KPI cards (Prix Minimum/Optimal/Final, Marge Effective, CA Mensuel Estimé), summary table with all strategy parameters, threshold validation alert, discriminatory segment table (shown only when strategy=discriminatory) per PRD §4.6
- [ ] T056 [US6] Implement `js/pages/synthesis.js` — `renderSynthesis()`: pulls from `calcCosts()`, `strategyPrice()`, `finalPrice()`; computes `effectiveMargin = (finalPrice − unitCost) / finalPrice × 100`; renders `caEstimé = finalPrice × volume`; threshold alert uses same R1 logic as costs page
- [ ] T057 [US6] Embed synthesis page inline in `index.html`

*(No separate backend controller needed — Synthesis reads from all other already-implemented endpoints.)*

**Checkpoint**: All 6 user stories independently functional.

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, standalone file integrity, and cross-browser compatibility.

- [ ] T058 [P] Run all quickstart.md business-rule smoke tests against `index.html` standalone (R1–R7, magic/psychological/rounded price transformations)
- [ ] T059 [P] Verify WCAG AA contrast on 🔴/🟡/🟢 alert colours in Chrome DevTools accessibility audit
- [ ] T060 [P] Test `index.html` in Chrome, Firefox, Edge, and Safari (last 2 major) — canvas rendering, all 6 pages, real-time recalculation
- [ ] T061 Verify `GET /api/v1/projects/{id}` snapshot endpoint returns complete nested object (project + all child records) via `ProjectService.getSnapshot()` and `ProjectController`
- [ ] T062 [P] Add `@ControllerAdvice` global exception handler in `src/main/java/com/prf/prixstrategie/config/GlobalExceptionHandler.java` returning error format per `specs/main/contracts/rest-api.md` (timestamp, status, errors[])
- [ ] T063 Run `mvn test` — confirm all backend unit/integration tests pass
- [ ] T064 [P] Validate that `index.html` file size is reasonable (< 500 KB); inline all modular sources if needed

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately
- **Foundational (Phase 2)**: Depends on Setup — **BLOCKS all user stories**
- **User Stories (Phase 3–8)**: All depend on Foundational completion; can proceed in priority order or in parallel if staffed
- **Polish (Phase N)**: Depends on all desired user stories complete

### User Story Dependencies

- **US1 (P1)**: Starts after Foundational — no story dependencies
- **US2 (P2)**: Starts after Foundational — uses `calcCosts()` from US1 foundations (T007)
- **US3 (P3)**: Starts after Foundational — uses `applyPriceType()` from foundations (T009); price type stored as part of strategy (US2 backend)
- **US4 (P4)**: Starts after Foundational — independent of US1–US3
- **US5 (P5)**: Starts after Foundational — independent of US1–US4
- **US6 (P6)**: Depends on US1 + US2 + US3 being functionally complete (reads their computed values)

### Within Each User Story

- Models/entities before services, services before controllers (backend)
- State bindings before render functions, render functions before navigation wiring (frontend)
- Both frontend and backend tasks within a story can proceed in parallel

### Parallel Opportunities

- T003, T004, T005 — all Setup tasks can run in parallel
- T012–T017 — all entity implementations can run in parallel (different files)
- T024/T025, T032/T033 — DTO pairs within a story can run in parallel
- US2, US3, US4, US5 can all begin in parallel after Foundational phase

---

## Parallel Example: Foundational Phase

```bash
# All of these can run simultaneously (different files):
Task T012: PricingProject.java entity
Task T013: PricingCosts.java entity
Task T014: PricingStrategy.java entity
Task T015: PositioningConfig.java entity
Task T016: PriceSegment, Competitor, AnsoffInitiative entities

# And simultaneously on the frontend:
Task T006: js/state.js
Task T007: calcCosts() in js/utils.js
Task T008: strategyPrice() in js/utils.js
Task T009: applyPriceType() in js/utils.js
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001–T005)
2. Complete Phase 2: Foundational (T006–T019) — **CRITICAL**
3. Complete Phase 3: US1 Costs & Profitability (T020–T027)
4. **STOP and VALIDATE**: open `index.html` → enter costs → verify 5 KPIs + alerts
5. The core value proposition (price floor calculation) is shippable here

### Incremental Delivery

1. Phase 1 + 2 → Foundation ready
2. Phase 3 (US1) → Core calculator MVP — demo to Dirigeant IT persona
3. Phase 4 (US2) + Phase 5 (US3) → Full pricing pipeline — demo pricing discriminatoire
4. Phase 6 (US4) → Competitive positioning — demo to Business Developer persona
5. Phase 7 (US5) → Ansoff matrix — complete strategic framing
6. Phase 8 (US6) → Synthesis — full consolidated view
7. Phase N → Production-ready

### Parallel Team Strategy

With frontend + backend developer:
- **Foundational**: Both work together (T006–T011 frontend, T012–T019 backend)
- **US1–US5**: Frontend dev → pages/partials; Backend dev → DTOs/services/controllers
- **US6 + Polish**: Both validate and merge

---

## Notes

- [P] tasks operate on different files with no blocking dependencies
- [USn] label maps every task to a user story for traceability
- Each user story phase produces an independently completable increment
- Business rules R1–R7 are enforced in BOTH frontend (utils.js) and backend (entities + DTOs)
- The standalone `index.html` MUST be kept in sync with modular sources manually (no build step — Principle I)
- Stop at any checkpoint to demo or validate independently
