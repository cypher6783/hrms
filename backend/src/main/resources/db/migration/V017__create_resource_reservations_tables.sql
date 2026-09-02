-- V017: Create Resource Reservations Table

CREATE TABLE resource_reservations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    resource_id UUID NOT NULL REFERENCES resources(id),
    admission_id UUID REFERENCES admissions(id),
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RESERVED',
    reserved_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP,
    reserved_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_resource_reservations_resource_id ON resource_reservations(resource_id);
CREATE INDEX idx_resource_reservations_admission_id ON resource_reservations(admission_id);
CREATE INDEX idx_resource_reservations_status ON resource_reservations(status);
