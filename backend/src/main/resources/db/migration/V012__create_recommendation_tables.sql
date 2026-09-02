-- V012: Recommendation tables

-- Allocation recommendations table
CREATE TABLE allocation_recommendations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    batch_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    generated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_allocation_recommendations_admission_id ON allocation_recommendations(admission_id);
CREATE INDEX idx_allocation_recommendations_status_expires_at ON allocation_recommendations(status, expires_at);

-- Recommendation items table
CREATE TABLE recommendation_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recommendation_id UUID NOT NULL REFERENCES allocation_recommendations(id),
    item_type VARCHAR(20) NOT NULL,
    recommended_entity_type VARCHAR(50) NOT NULL,
    recommended_entity_id UUID NOT NULL,
    rank INT NOT NULL DEFAULT 1,
    confidence_score DECIMAL(3, 2) NOT NULL,
    scoring_breakdown JSONB,
    rationale TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recommendation_items_recommendation_id ON recommendation_items(recommendation_id);
CREATE INDEX idx_recommendation_items_rec_type_rank ON recommendation_items(recommendation_id, item_type, rank);
CREATE INDEX idx_recommendation_items_status ON recommendation_items(status);

-- Recommendation decisions table
CREATE TABLE recommendation_decisions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recommendation_item_id UUID NOT NULL REFERENCES recommendation_items(id),
    decision_type VARCHAR(20) NOT NULL,
    overridden_entity_type VARCHAR(50),
    overridden_entity_id UUID,
    override_justification TEXT,
    decided_by UUID NOT NULL REFERENCES users(id),
    decided_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recommendation_decisions_item_id ON recommendation_decisions(recommendation_item_id);
CREATE INDEX idx_recommendation_decisions_type ON recommendation_decisions(decision_type);
