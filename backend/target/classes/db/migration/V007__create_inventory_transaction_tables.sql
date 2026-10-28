-- V007: Inventory transaction tables

CREATE TABLE inventory_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    resource_inventory_id UUID NOT NULL REFERENCES resource_inventory(id),
    transaction_type VARCHAR(20) NOT NULL,
    quantity INT NOT NULL,
    admission_id UUID REFERENCES admissions(id),
    reference_document VARCHAR(100),
    notes TEXT,
    performed_by UUID NOT NULL REFERENCES users(id),
    transaction_timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inventory_transactions_resource_inventory ON inventory_transactions(resource_inventory_id, transaction_timestamp);
CREATE INDEX idx_inventory_transactions_admission_id ON inventory_transactions(admission_id);
CREATE INDEX idx_inventory_transactions_type ON inventory_transactions(transaction_type);
