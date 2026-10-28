-- V006: Resource tables

-- Resource suppliers table
CREATE TABLE resource_suppliers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    lead_time_days INT DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_resource_suppliers_name ON resource_suppliers(name);
CREATE INDEX idx_resource_suppliers_is_active ON resource_suppliers(is_active);

-- Resources table
CREATE TABLE resources (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    category VARCHAR(30) NOT NULL,
    unit_of_measure VARCHAR(20) NOT NULL,
    minimum_threshold INT NOT NULL DEFAULT 0,
    reorder_point INT NOT NULL DEFAULT 0,
    criticality_level VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    default_supplier_id UUID REFERENCES resource_suppliers(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID,
    updated_by UUID
);

CREATE INDEX idx_resources_category ON resources(category);
CREATE INDEX idx_resources_criticality_level ON resources(criticality_level);

-- Resource inventory table
CREATE TABLE resource_inventory (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    resource_id UUID NOT NULL REFERENCES resources(id),
    location VARCHAR(100) NOT NULL,
    current_stock INT NOT NULL DEFAULT 0,
    expiration_date DATE,
    batch_number VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(resource_id, location, batch_number)
);

CREATE INDEX idx_resource_inventory_resource_id ON resource_inventory(resource_id);
CREATE INDEX idx_resource_inventory_resource_location ON resource_inventory(resource_id, location);
CREATE INDEX idx_resource_inventory_current_stock ON resource_inventory(current_stock);
