-- V009: Staff tables

CREATE TABLE staff (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    staff_number VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    specialization VARCHAR(50),
    certification_status VARCHAR(20) NOT NULL DEFAULT 'CURRENT',
    certification_expiry DATE,
    ward_id UUID REFERENCES wards(id),
    max_workload_threshold DECIMAL(5, 2) NOT NULL DEFAULT 1.00,
    availability_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_staff_ward_id ON staff(ward_id);
CREATE INDEX idx_staff_role ON staff(role);
CREATE INDEX idx_staff_ward_availability ON staff(ward_id, availability_status);
CREATE INDEX idx_staff_specialization ON staff(specialization);
