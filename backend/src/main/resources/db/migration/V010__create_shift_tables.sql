-- V010: Shift tables

-- Staff shifts table
CREATE TABLE staff_shifts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    shift_name VARCHAR(50) NOT NULL,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    ward_id UUID NOT NULL REFERENCES wards(id),
    min_required_staff INT NOT NULL DEFAULT 1,
    max_staff INT NOT NULL DEFAULT 10,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_staff_shifts_ward_id ON staff_shifts(ward_id);
CREATE INDEX idx_staff_shifts_shift_date ON staff_shifts(shift_date);
CREATE INDEX idx_staff_shifts_ward_date_time ON staff_shifts(ward_id, shift_date, start_time);

-- Shift assignments table
CREATE TABLE shift_assignments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    staff_id UUID NOT NULL REFERENCES staff(id),
    shift_id UUID NOT NULL REFERENCES staff_shifts(id),
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    assigned_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(staff_id, shift_id)
);

CREATE INDEX idx_shift_assignments_staff_id ON shift_assignments(staff_id);
CREATE INDEX idx_shift_assignments_shift_id ON shift_assignments(shift_id);
