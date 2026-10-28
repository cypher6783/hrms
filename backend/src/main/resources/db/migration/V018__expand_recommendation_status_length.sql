-- V018: Expand recommendation status and decision type column lengths to support canonical status tokens (e.g., NO_FEASIBLE_ALLOCATION)

ALTER TABLE allocation_recommendations ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE recommendation_items ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE recommendation_decisions ALTER COLUMN decision_type TYPE VARCHAR(50);
