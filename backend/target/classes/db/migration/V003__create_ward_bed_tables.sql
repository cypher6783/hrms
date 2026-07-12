-- V003: Ward and Bed tables

-- Wards table
CREATE TABLE wards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    ward_type VARCHAR(30) NOT NULL,
    max_bed_capacity INT NOT NULL,
    isolation_level VARCHAR(20) NOT NULL DEFAULT 'NONE',
    equipment_zone VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_wards_status ON wards(status);

-- Beds table
CREATE TABLE beds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    bed_number VARCHAR(20) NOT NULL,
    ward_id UUID NOT NULL REFERENCES wards(id),
    bed_type VARCHAR(40) NOT NULL,
    is_isolation_capable BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    current_admission_id UUID,
    last_maintenance_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID,
    UNIQUE(bed_number, ward_id)
);

CREATE INDEX idx_beds_ward_id_status ON beds(ward_id, status);
CREATE INDEX idx_beds_status ON beds(status);
CREATE INDEX idx_beds_ward_type_status ON beds(ward_id, bed_type, status);
