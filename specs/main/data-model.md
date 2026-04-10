# Data Model: PrixStratégie v1.0

**Date**: 2026-04-10 | **Branch**: `main` | **Source**: PRD §7

## Entity Relationship Overview

```
pricing_project (root aggregate)
    │
    ├── pricing_costs        1:1  (upsert — always one record per project)
    ├── pricing_strategy     1:1  (upsert — always one record per project)
    ├── positioning_config   1:1  (upsert — always one record per project)
    │
    ├── price_segment        1:N  (ordered list; default 3 segments)
    ├── competitor           1:N  (ordered list; dynamic)
    └── ansoff_initiative    1:N  (ordered list; dynamic)
```

All child records are deleted via `ON DELETE CASCADE` when the parent
`pricing_project` is deleted.

---

## Entities

### `pricing_project`

Root aggregate. One row per pricing scenario.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGSERIAL` | PK | Auto-generated |
| `name` | `VARCHAR(255)` | NOT NULL | Project name |
| `description` | `TEXT` | nullable | Optional description |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL, DEFAULT NOW() | |
| `updated_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL, DEFAULT NOW() | Updated by trigger |

**Trigger**: `set_updated_at()` fires BEFORE UPDATE to refresh `updated_at`.

---

### `pricing_costs`

One-to-one with `pricing_project`. Holds cost inputs and strategy-related
financial fields.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGSERIAL` | PK | |
| `project_id` | `BIGINT` | FK → pricing_project, NOT NULL, UNIQUE, ON DELETE CASCADE | |
| `variable_cost` | `NUMERIC(12,2)` | NOT NULL, CHECK ≥ 0, DEFAULT 0 | Coût variable unitaire |
| `fixed_cost` | `NUMERIC(12,2)` | NOT NULL, CHECK ≥ 0, DEFAULT 0 | Coûts fixes mensuels |
| `volume` | `INTEGER` | NOT NULL, CHECK > 0, DEFAULT 1 | Volume prévu unités/mois |
| `target_margin` | `NUMERIC(5,2)` | NOT NULL, CHECK 0 ≤ x < 100, DEFAULT 30 | Marge souhaitée (%) |
| `currency` | `VARCHAR(3)` | NOT NULL, DEFAULT 'EUR' | EUR/USD/GBP/CHF |
| `alignment_price` | `NUMERIC(12,2)` | nullable, CHECK ≥ 0 | Required when strategy = alignment |

**Derived (computed in application, not persisted)**:
- `unit_cost = variable_cost + (fixed_cost / volume)`
- `min_price = unit_cost`
- `optimal_price = unit_cost / (1 − target_margin / 100)`

---

### `pricing_strategy`

One-to-one with `pricing_project`. Holds strategy and price type selections.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGSERIAL` | PK | |
| `project_id` | `BIGINT` | FK → pricing_project, NOT NULL, UNIQUE, ON DELETE CASCADE | |
| `strategy` | `VARCHAR(20)` | NOT NULL, DEFAULT 'penetration' | See StrategyType enum |
| `price_type` | `VARCHAR(20)` | NOT NULL, DEFAULT 'rounded' | See PriceType enum |

**Enum — `StrategyType`** (stored as VARCHAR):
- `luxury` — × 2.2 multiplier
- `penetration` — × 1.05 multiplier
- `alignment` — uses `alignment_price` from pricing_costs
- `discriminatory` — uses segments from price_segment

**Enum — `PriceType`** (stored as VARCHAR):
- `magic` — floor(price) − 0.01
- `psychological` — nearest threshold − 3
- `rounded` — round to nearest magnitude step

---

### `positioning_config`

One-to-one with `pricing_project`. Holds perceptual map axis configuration
and the company's own position.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGSERIAL` | PK | |
| `project_id` | `BIGINT` | FK → pricing_project, NOT NULL, UNIQUE, ON DELETE CASCADE | |
| `axis_x_left` | `VARCHAR(100)` | NOT NULL, DEFAULT 'Bas Prix' | X axis negative label |
| `axis_x_right` | `VARCHAR(100)` | NOT NULL, DEFAULT 'Haut Prix' | X axis positive label |
| `axis_y_top` | `VARCHAR(100)` | NOT NULL, DEFAULT 'Haute Qualité' | Y axis positive label |
| `axis_y_bottom` | `VARCHAR(100)` | NOT NULL, DEFAULT 'Faible Qualité' | Y axis negative label |
| `my_x` | `SMALLINT` | NOT NULL, CHECK 0–100, DEFAULT 60 | Company X position |
| `my_y` | `SMALLINT` | NOT NULL, CHECK 0–100, DEFAULT 75 | Company Y position |
| `my_name` | `VARCHAR(100)` | NOT NULL, DEFAULT 'Mon Entreprise' | Company display name |

---

### `price_segment`

One-to-many with `pricing_project`. Used when strategy = `discriminatory`.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGSERIAL` | PK | |
| `project_id` | `BIGINT` | FK → pricing_project, NOT NULL, ON DELETE CASCADE | |
| `name` | `VARCHAR(100)` | NOT NULL | e.g., Starter, Pro, Enterprise |
| `multiplier` | `NUMERIC(5,2)` | NOT NULL, CHECK > 0 | Applied to optimal price |
| `sort_order` | `INTEGER` | NOT NULL, DEFAULT 0 | Display ordering |

**Default rows** (inserted when project created): Starter (×0.70), Pro (×1.00),
Enterprise (×1.60).

**Derived (computed in application)**:
- `base_price = optimal_price × multiplier`
- `final_price = applyPriceType(base_price)`
- `status` = 🟢 / 🟡 / 🔴 based on comparison to min/optimal

---

### `competitor`

One-to-many with `pricing_project`. Displayed on the perceptual map.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGSERIAL` | PK | |
| `project_id` | `BIGINT` | FK → pricing_project, NOT NULL, ON DELETE CASCADE | |
| `name` | `VARCHAR(100)` | NOT NULL | Competitor display name |
| `position_x` | `SMALLINT` | NOT NULL, CHECK 0–100 | Canvas X position |
| `position_y` | `SMALLINT` | NOT NULL, CHECK 0–100 | Canvas Y position |
| `color` | `VARCHAR(7)` | NOT NULL, DEFAULT '#e74c3c' | Hex color (e.g., #ff0000) |
| `sort_order` | `INTEGER` | NOT NULL, DEFAULT 0 | Display ordering |

---

### `ansoff_initiative`

One-to-many with `pricing_project`. Initiatives classified on the Ansoff matrix.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | `BIGSERIAL` | PK | |
| `project_id` | `BIGINT` | FK → pricing_project, NOT NULL, ON DELETE CASCADE | |
| `name` | `VARCHAR(255)` | NOT NULL | Initiative name |
| `quadrant` | `VARCHAR(20)` | NOT NULL | See AnsoffQuadrant enum |
| `description` | `TEXT` | nullable | Free text |
| `sort_order` | `INTEGER` | NOT NULL, DEFAULT 0 | Display ordering |
| `created_at` | `TIMESTAMP WITH TIME ZONE` | NOT NULL, DEFAULT NOW() | |

**Enum — `AnsoffQuadrant`** (stored as VARCHAR):
- `penetration` — Existing market / Existing product
- `market-dev` — New market / Existing product
- `product-dev` — Existing market / New product
- `diversification` — New market / New product

---

## Frontend State Model

The standalone frontend maintains equivalent state in memory:

```javascript
state = {
  // pricing_costs fields
  variableCost: 0,
  fixedCost: 0,
  volume: 1,
  targetMargin: 30,
  currency: 'EUR',
  alignmentPrice: null,

  // pricing_strategy fields
  strategy: 'penetration',     // StrategyType enum value
  pricetype: 'rounded',        // PriceType enum value

  // positioning_config fields
  axisXLeft: 'Bas Prix', axisXRight: 'Haut Prix',
  axisYTop: 'Haute Qualité', axisYBottom: 'Faible Qualité',
  myX: 60, myY: 75, myName: 'Mon Entreprise',

  // collections
  segments: [
    { name: 'Starter', multiplier: 0.7 },
    { name: 'Pro', multiplier: 1.0 },
    { name: 'Enterprise', multiplier: 1.6 }
  ],
  competitors: [],               // { name, x, y, color }
  ansoffInitiatives: []          // { name, quadrant, description }
}
```

---

## Validation Rules Summary

| Rule | Entity | Constraint |
|------|--------|------------|
| R1 — Price floor | pricing_costs | `variable_cost ≥ 0`, `fixed_cost ≥ 0` |
| R4 — Alignment required | pricing_costs | `alignment_price` NOT NULL when `strategy = alignment` (app-level) |
| R5 — Volume > 0 | pricing_costs | `CHECK (volume > 0)` |
| R6 — Margin bounds | pricing_costs | `CHECK (target_margin >= 0 AND target_margin < 100)` |
| Segment multiplier | price_segment | `CHECK (multiplier > 0)` |
| Canvas positions | competitor, positioning_config | `CHECK (position_x BETWEEN 0 AND 100)` etc. |
| Color format | competitor | App-level hex validation (7 chars starting with #) |
