-- V002: Patient tables

-- Patients table
CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_number VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone_number VARCHAR(20),
    address TEXT,
    next_of_kin_name VARCHAR(100),
    next_of_kin_phone VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_patients_is_active_created_at ON patients(is_active, created_at);
CREATE INDEX idx_patients_phone_number ON patients(phone_number);

-- Clinical assessments table
CREATE TABLE clinical_assessments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    admission_id UUID,
    assessed_by UUID NOT NULL REFERENCES users(id),
    severity_level VARCHAR(20) NOT NULL,
    triage_classification VARCHAR(20) NOT NULL,
    infection_status VARCHAR(20) NOT NULL,
    clinical_notes TEXT,
    is_reassessment BOOLEAN NOT NULL DEFAULT FALSE,
    assessment_timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clinical_assessments_patient_id ON clinical_assessments(patient_id);
CREATE INDEX idx_clinical_assessments_patient_timestamp ON clinical_assessments(patient_id, assessment_timestamp);
