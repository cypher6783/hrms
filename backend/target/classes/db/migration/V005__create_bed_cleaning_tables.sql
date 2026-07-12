-- V005: Bed cleaning tables

CREATE TABLE bed_cleaning (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bed_id UUID NOT NULL REFERENCES beds(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assigned_to UUID REFERENCES users(id),
    assigned_at TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    verified_by UUID REFERENCES users(id),
    verified_at TIMESTAMP,
    cleaning_notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bed_cleaning_bed_id ON bed_cleaning(bed_id);
CREATE INDEX idx_bed_cleaning_admission_id ON bed_cleaning(admission_id);
CREATE INDEX idx_bed_cleaning_status_bed_id ON bed_cleaning(status, bed_id);
CREATE INDEX idx_bed_cleaning_assigned_to ON bed_cleaning(assigned_to);
