-- V020: Allow recommendation_item_id to be nullable for direct admission overrides
ALTER TABLE recommendation_decisions ALTER COLUMN recommendation_item_id DROP NOT NULL;
