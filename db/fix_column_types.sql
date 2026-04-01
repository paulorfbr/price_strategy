-- ============================================================
--  Fix column types to match Hibernate expectations
--  Run once against the prixstrategie database
-- ============================================================

-- 0. Drop the view that depends on the enum columns
DROP VIEW IF EXISTS v_project_summary;

-- 1. CHAR(7) → VARCHAR(7) for competitor.color
ALTER TABLE competitor
  ALTER COLUMN color TYPE VARCHAR(7);

-- 2. CHAR(3) → VARCHAR(3) for pricing_costs.currency
ALTER TABLE pricing_costs
  ALTER COLUMN currency TYPE VARCHAR(3);

-- 3. PostgreSQL enum → VARCHAR for pricing_strategy columns
--    Drop column defaults before altering type (enum default would block the cast)
ALTER TABLE pricing_strategy
  ALTER COLUMN strategy  DROP DEFAULT,
  ALTER COLUMN pricetype DROP DEFAULT;

ALTER TABLE pricing_strategy
  ALTER COLUMN strategy  TYPE VARCHAR(32) USING strategy::TEXT,
  ALTER COLUMN pricetype TYPE VARCHAR(32) USING pricetype::TEXT;

-- 4. PostgreSQL enum → VARCHAR for ansoff_initiative.quadrant
ALTER TABLE ansoff_initiative
  ALTER COLUMN quadrant DROP DEFAULT;

ALTER TABLE ansoff_initiative
  ALTER COLUMN quadrant TYPE VARCHAR(32) USING quadrant::TEXT;

-- 5. Drop the now-unused pg enum types
DROP TYPE IF EXISTS strategy_type;
DROP TYPE IF EXISTS pricetype_type;
DROP TYPE IF EXISTS ansoff_quadrant;
