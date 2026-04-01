-- ============================================================
--  PrixStratégie — PostgreSQL Schema
-- ============================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";  -- gen_random_uuid()

-- ── Enums ────────────────────────────────────────────────────

CREATE TYPE strategy_type AS ENUM ('luxury', 'penetration', 'alignment', 'discriminatory');
CREATE TYPE pricetype_type AS ENUM ('magic', 'psychological', 'rounded');
CREATE TYPE ansoff_quadrant AS ENUM ('penetration', 'market-dev', 'product-dev', 'diversification');

-- ── Helper: auto-update updated_at ───────────────────────────

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$;

-- ── 1. pricing_project ───────────────────────────────────────
--  Top-level entity. One row = one pricing analysis session.

CREATE TABLE pricing_project (
  id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
  name        TEXT        NOT NULL,
  description TEXT,
  created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TRIGGER trg_pricing_project_updated_at
  BEFORE UPDATE ON pricing_project
  FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ── 2. pricing_costs ─────────────────────────────────────────
--  Cost inputs and currency. 1:1 with pricing_project.

CREATE TABLE pricing_costs (
  project_id      UUID         PRIMARY KEY REFERENCES pricing_project(id) ON DELETE CASCADE,
  variable_cost   NUMERIC(12,2) NOT NULL DEFAULT 0    CHECK (variable_cost   >= 0),
  fixed_cost      NUMERIC(12,2) NOT NULL DEFAULT 0    CHECK (fixed_cost      >= 0),
  volume          INTEGER       NOT NULL DEFAULT 1     CHECK (volume          >  0),
  target_margin   NUMERIC(5,2)  NOT NULL DEFAULT 30   CHECK (target_margin   BETWEEN 0 AND 100),  -- %
  currency        CHAR(3)       NOT NULL DEFAULT 'EUR',
  alignment_price NUMERIC(12,2)                       CHECK (alignment_price >= 0)  -- nullable; used only for alignment strategy
);

-- ── 3. pricing_strategy ──────────────────────────────────────
--  Active strategy and price-type choices. 1:1 with pricing_project.

CREATE TABLE pricing_strategy (
  project_id UUID          PRIMARY KEY REFERENCES pricing_project(id) ON DELETE CASCADE,
  strategy   strategy_type NOT NULL DEFAULT 'luxury',
  pricetype  pricetype_type NOT NULL DEFAULT 'magic'
);

-- ── 4. price_segment ─────────────────────────────────────────
--  Segments for discriminatory pricing (e.g. Starter 0.7×, Pro 1.0×, Enterprise 1.6×).

CREATE TABLE price_segment (
  id         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID         NOT NULL REFERENCES pricing_project(id) ON DELETE CASCADE,
  name       TEXT         NOT NULL,
  multiplier NUMERIC(5,2) NOT NULL DEFAULT 1.0 CHECK (multiplier > 0),
  sort_order SMALLINT     NOT NULL DEFAULT 0
);

CREATE INDEX idx_price_segment_project ON price_segment(project_id);

-- ── 5. positioning_config ────────────────────────────────────
--  Perceptual map axis labels and own-company position. 1:1 with pricing_project.

CREATE TABLE positioning_config (
  project_id    UUID     PRIMARY KEY REFERENCES pricing_project(id) ON DELETE CASCADE,
  axis_x_left   TEXT     NOT NULL DEFAULT 'Bas Prix',
  axis_x_right  TEXT     NOT NULL DEFAULT 'Haut Prix',
  axis_y_top    TEXT     NOT NULL DEFAULT 'Haute Qualité',
  axis_y_bottom TEXT     NOT NULL DEFAULT 'Faible Qualité',
  my_x          SMALLINT NOT NULL DEFAULT 60 CHECK (my_x BETWEEN 0 AND 100),
  my_y          SMALLINT NOT NULL DEFAULT 75 CHECK (my_y BETWEEN 0 AND 100),
  my_name       TEXT     NOT NULL DEFAULT 'Mon Entreprise'
);

-- ── 6. competitor ────────────────────────────────────────────
--  Competitors plotted on the perceptual map.

CREATE TABLE competitor (
  id         UUID     PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id UUID     NOT NULL REFERENCES pricing_project(id) ON DELETE CASCADE,
  name       TEXT     NOT NULL,
  position_x SMALLINT NOT NULL DEFAULT 50 CHECK (position_x BETWEEN 0 AND 100),
  position_y SMALLINT NOT NULL DEFAULT 50 CHECK (position_y BETWEEN 0 AND 100),
  color      CHAR(7)  NOT NULL DEFAULT '#ef4444',  -- hex RGB
  sort_order SMALLINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_competitor_project ON competitor(project_id);

-- ── 7. ansoff_initiative ─────────────────────────────────────
--  Strategic initiatives classified by Ansoff quadrant.

CREATE TABLE ansoff_initiative (
  id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
  project_id  UUID            NOT NULL REFERENCES pricing_project(id) ON DELETE CASCADE,
  name        TEXT            NOT NULL,
  quadrant    ansoff_quadrant NOT NULL DEFAULT 'penetration',
  description TEXT,
  sort_order  SMALLINT        NOT NULL DEFAULT 0,
  created_at  TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE INDEX idx_ansoff_initiative_project  ON ansoff_initiative(project_id);
CREATE INDEX idx_ansoff_initiative_quadrant ON ansoff_initiative(project_id, quadrant);

-- ── Convenience view ─────────────────────────────────────────
--  Full project snapshot in one query.

CREATE VIEW v_project_summary AS
SELECT
  p.id,
  p.name                                                    AS project_name,
  p.created_at,
  c.variable_cost,
  c.fixed_cost,
  c.volume,
  c.target_margin,
  c.currency,
  c.alignment_price,
  s.strategy,
  s.pricetype,
  -- unit cost, optimal price, minimum price
  ROUND((c.fixed_cost / NULLIF(c.volume, 0)) + c.variable_cost, 2)                        AS unit_cost,
  ROUND(((c.fixed_cost / NULLIF(c.volume, 0)) + c.variable_cost)
        / NULLIF(1 - c.target_margin / 100, 0), 2)                                        AS optimal_price,
  ROUND((c.fixed_cost / NULLIF(c.volume, 0)) + c.variable_cost, 2)                        AS min_price,
  pc.my_name,
  pc.my_x,
  pc.my_y,
  pc.axis_x_left,
  pc.axis_x_right,
  pc.axis_y_top,
  pc.axis_y_bottom
FROM pricing_project    p
JOIN pricing_costs      c  ON c.project_id = p.id
JOIN pricing_strategy   s  ON s.project_id = p.id
JOIN positioning_config pc ON pc.project_id = p.id;
