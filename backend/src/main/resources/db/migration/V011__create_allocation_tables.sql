-- V011: Allocation tables

-- Staff admissions (junction table)
CREATE TABLE staff_admissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    staff_id UUID NOT NULL REFERENCES staff(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    role_in_care VARCHAR(30) NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    released_at TIMESTAMP,
    is_primary BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_staff_admissions_staff_id ON staff_admissions(staff_id);
CREATE INDEX idx_staff_admissions_admission_id ON staff_admissions(admission_id);

-- Resource allocations table
CREATE TABLE resource_allocations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    resource_id UUID NOT NULL REFERENCES resources(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    quantity INT NOT NULL,
    allocated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    released_at TIMESTAMP,
    allocated_by UUID NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_resource_allocations_resource_id ON resource_allocations(resource_id);
CREATE INDEX idx_resource_allocations_admission_id ON resource_allocations(admission_id);

-- Equipment allocations table
CREATE TABLE equipment_allocations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    equipment_id UUID NOT NULL REFERENCES equipment(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    allocated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    released_at TIMESTAMP,
    allocated_by UUID NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_equipment_allocations_equipment_id ON equipment_allocations(equipment_id);
CREATE INDEX idx_equipment_allocations_admission_id ON equipment_allocations(admission_id);
