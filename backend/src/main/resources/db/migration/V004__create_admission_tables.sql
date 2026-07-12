-- V004: Admission tables

CREATE TABLE admissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admission_number VARCHAR(20) NOT NULL UNIQUE,
    patient_id UUID NOT NULL REFERENCES patients(id),
    ward_id UUID NOT NULL REFERENCES wards(id),
    bed_id UUID REFERENCES beds(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admission_notes TEXT,
    discharge_outcome VARCHAR(30),
    discharge_notes TEXT,
    admitted_at TIMESTAMP NOT NULL DEFAULT NOW(),
    discharged_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_admissions_patient_id ON admissions(patient_id);
CREATE INDEX idx_admissions_ward_id ON admissions(ward_id);
CREATE INDEX idx_admissions_bed_id ON admissions(bed_id);
CREATE INDEX idx_admissions_status_admitted_at ON admissions(status, admitted_at);
CREATE INDEX idx_admissions_patient_id_is_active ON admissions(patient_id, is_active);
CREATE INDEX idx_admissions_ward_status_admitted ON admissions(ward_id, status, admitted_at);
