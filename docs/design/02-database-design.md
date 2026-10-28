# 02 — Database Design

## 1. Logical Schema

### 1.1 Schema Organization

All tables reside in the `public` schema. Tables are organized by functional domain.

```
public
├── Authentication Domain
│   ├── users
│   ├── refresh_tokens
│   ├── password_history
│   └── login_history
├── Patient Domain
│   ├── patients
│   ├── clinical_assessments
│   └── admissions
├── Facility Domain
│   ├── wards
│   ├── beds
│   └── bed_cleaning
├── Resource Domain
│   ├── resources
│   ├── resource_inventory
│   ├── inventory_transactions
│   └── resource_suppliers
├── Equipment Domain
│   ├── equipment
│   └── equipment_maintenance
├── Staff Domain
│   ├── staff
│   ├── staff_shifts
│   └── shift_assignments
├── Allocation Domain
│   ├── staff_admissions
│   ├── resource_allocations
│   └── equipment_allocations
├── Recommendation Domain
│   ├── allocation_recommendations
│   ├── recommendation_items
│   └── recommendation_decisions
├── Notification Domain
│   └── notifications
├── Forecast Domain
│   └── forecast_snapshots
├── Configuration Domain
│   └── system_configurations
└── Audit Domain
    ├── audit_logs
    └── login_audit_logs
```

---

## 2. Physical Schema

### 2.1 users

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULLABLE,
    last_login_at TIMESTAMP NULLABLE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
```

### 2.2 refresh_tokens

```sql
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
```

### 2.3 password_history

```sql
CREATE TABLE password_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_password_history_user_id ON password_history(user_id);
```

### 2.4 login_history

```sql
CREATE TABLE login_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username_attempted VARCHAR(50) NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    success BOOLEAN NOT NULL,
    ip_address VARCHAR(45) NULLABLE,
    user_agent VARCHAR(500) NULLABLE,
    failure_reason VARCHAR(100) NULLABLE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_login_history_user_id ON login_history(user_id);
CREATE INDEX idx_login_history_username ON login_history(username_attempted);
CREATE INDEX idx_login_history_created_at ON login_history(created_at);
```

### 2.5 patients

```sql
CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_number VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    phone_number VARCHAR(20) NULLABLE,
    address TEXT NULLABLE,
    next_of_kin_name VARCHAR(100) NULLABLE,
    next_of_kin_phone VARCHAR(20) NULLABLE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_patients_full_name ON patients(full_name);
CREATE INDEX idx_patients_patient_number ON patients(patient_number);
CREATE INDEX idx_patients_is_active ON patients(is_active, created_at);
CREATE INDEX idx_patients_phone ON patients(phone_number);
```

### 2.6 clinical_assessments

```sql
CREATE TABLE clinical_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    admission_id UUID REFERENCES admissions(id) ON DELETE SET NULL,
    assessed_by UUID NOT NULL REFERENCES users(id),
    severity_level VARCHAR(20) NOT NULL,
    triage_classification VARCHAR(20) NOT NULL,
    infection_status VARCHAR(20) NOT NULL DEFAULT 'SUSPECTED',
    clinical_notes TEXT NULLABLE,
    is_reassessment BOOLEAN NOT NULL DEFAULT false,
    assessment_timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clinical_assessments_patient_id ON clinical_assessments(patient_id);
CREATE INDEX idx_clinical_assessments_admission_id ON clinical_assessments(admission_id);
CREATE INDEX idx_clinical_assessments_assessment_ts ON clinical_assessments(patient_id, assessment_timestamp);
CREATE INDEX idx_clinical_assessments_admission_reassess ON clinical_assessments(admission_id, is_reassessment);
```

### 2.7 admissions

```sql
CREATE TABLE admissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admission_number VARCHAR(20) NOT NULL UNIQUE,
    patient_id UUID NOT NULL REFERENCES patients(id),
    ward_id UUID NOT NULL REFERENCES wards(id),
    bed_id UUID REFERENCES beds(id) ON DELETE SET NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    admission_notes TEXT NULLABLE,
    discharge_outcome VARCHAR(30) NULLABLE,
    discharge_notes TEXT NULLABLE,
    admitted_at TIMESTAMP NOT NULL,
    discharged_at TIMESTAMP NULLABLE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_admissions_patient_id ON admissions(patient_id);
CREATE INDEX idx_admissions_ward_id ON admissions(ward_id);
CREATE INDEX idx_admissions_bed_id ON admissions(bed_id);
CREATE INDEX idx_admissions_status ON admissions(status, admitted_at);
CREATE INDEX idx_admissions_patient_active ON admissions(patient_id, is_active);
CREATE INDEX idx_admissions_ward_status ON admissions(ward_id, status, admitted_at);
```

### 2.8 wards

```sql
CREATE TABLE wards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    ward_type VARCHAR(30) NOT NULL,
    max_bed_capacity INTEGER NOT NULL,
    isolation_level VARCHAR(20) NOT NULL DEFAULT 'NONE',
    equipment_zone VARCHAR(50) NULLABLE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_wards_name ON wards(name);
CREATE INDEX idx_wards_status ON wards(status);
```

### 2.9 beds

```sql
CREATE TABLE beds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bed_number VARCHAR(20) NOT NULL,
    ward_id UUID NOT NULL REFERENCES wards(id),
    bed_type VARCHAR(40) NOT NULL,
    is_isolation_capable BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    current_admission_id UUID REFERENCES admissions(id) ON DELETE SET NULL,
    last_maintenance_at TIMESTAMP NULLABLE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id),
    UNIQUE(bed_number, ward_id)
);

CREATE INDEX idx_beds_ward_id ON beds(ward_id);
CREATE INDEX idx_beds_status ON beds(status);
CREATE INDEX idx_beds_ward_status ON beds(ward_id, status);
CREATE INDEX idx_beds_ward_type_status ON beds(ward_id, bed_type, status);
CREATE INDEX idx_beds_current_admission ON beds(current_admission_id);
```

### 2.10 bed_cleaning

```sql
CREATE TABLE bed_cleaning (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bed_id UUID NOT NULL REFERENCES beds(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assigned_to UUID REFERENCES staff(id) ON DELETE SET NULL,
    assigned_at TIMESTAMP NULLABLE,
    started_at TIMESTAMP NULLABLE,
    completed_at TIMESTAMP NULLABLE,
    verified_by UUID REFERENCES users(id) ON DELETE SET NULL,
    verified_at TIMESTAMP NULLABLE,
    cleaning_notes TEXT NULLABLE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bed_cleaning_bed_id ON bed_cleaning(bed_id);
CREATE INDEX idx_bed_cleaning_admission_id ON bed_cleaning(admission_id);
CREATE INDEX idx_bed_cleaning_status ON bed_cleaning(status, bed_id);
CREATE INDEX idx_bed_cleaning_assigned_to ON bed_cleaning(assigned_to);
```

### 2.11 resources

```sql
CREATE TABLE resources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    category VARCHAR(30) NOT NULL,
    unit_of_measure VARCHAR(20) NOT NULL,
    minimum_threshold INTEGER NOT NULL DEFAULT 0,
    reorder_point INTEGER NOT NULL DEFAULT 0,
    criticality_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    default_supplier_id UUID REFERENCES resource_suppliers(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_resources_category ON resources(category);
CREATE INDEX idx_resources_criticality ON resources(criticality_level);
CREATE INDEX idx_resources_supplier ON resources(default_supplier_id);
```

### 2.12 resource_inventory

```sql
CREATE TABLE resource_inventory (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_id UUID NOT NULL REFERENCES resources(id) ON DELETE CASCADE,
    location VARCHAR(100) NOT NULL,
    current_stock INTEGER NOT NULL DEFAULT 0,
    expiration_date DATE NULLABLE,
    batch_number VARCHAR(50) NULLABLE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(resource_id, location, batch_number)
);

CREATE INDEX idx_resource_inventory_resource ON resource_inventory(resource_id);
CREATE INDEX idx_resource_inventory_location ON resource_inventory(resource_id, location);
CREATE INDEX idx_resource_inventory_stock ON resource_inventory(current_stock);
```

### 2.13 inventory_transactions

```sql
CREATE TABLE inventory_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_inventory_id UUID NOT NULL REFERENCES resource_inventory(id),
    transaction_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    admission_id UUID REFERENCES admissions(id) ON DELETE SET NULL,
    reference_document VARCHAR(100) NULLABLE,
    notes TEXT NULLABLE,
    performed_by UUID NOT NULL REFERENCES users(id),
    transaction_timestamp TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_inventory_txn_resource ON inventory_transactions(resource_inventory_id);
CREATE INDEX idx_inventory_txn_timestamp ON inventory_transactions(resource_inventory_id, transaction_timestamp);
CREATE INDEX idx_inventory_txn_admission ON inventory_transactions(admission_id);
CREATE INDEX idx_inventory_txn_type ON inventory_transactions(transaction_type);
```

### 2.14 resource_suppliers

```sql
CREATE TABLE resource_suppliers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100) NULLABLE,
    phone_number VARCHAR(20) NULLABLE,
    email VARCHAR(100) NULLABLE,
    address TEXT NULLABLE,
    lead_time_days INTEGER NULLABLE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_resource_suppliers_name ON resource_suppliers(name);
CREATE INDEX idx_resource_suppliers_active ON resource_suppliers(is_active);
```

### 2.15 equipment

```sql
CREATE TABLE equipment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    equipment_type VARCHAR(50) NOT NULL,
    serial_number VARCHAR(50) NOT NULL UNIQUE,
    location VARCHAR(100) NULLABLE,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    assigned_admission_id UUID REFERENCES admissions(id) ON DELETE SET NULL,
    assigned_ward_id UUID REFERENCES wards(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_equipment_type ON equipment(equipment_type);
CREATE INDEX idx_equipment_status ON equipment(status);
CREATE INDEX idx_equipment_admission ON equipment(assigned_admission_id);
CREATE INDEX idx_equipment_ward ON equipment(assigned_ward_id);
CREATE INDEX idx_equipment_serial ON equipment(serial_number);
```

### 2.16 equipment_maintenance

```sql
CREATE TABLE equipment_maintenance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id UUID NOT NULL REFERENCES equipment(id) ON DELETE CASCADE,
    maintenance_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    scheduled_date DATE NOT NULL,
    completed_date DATE NULLABLE,
    performed_by VARCHAR(100) NULLABLE,
    maintenance_notes TEXT NULLABLE,
    cost DECIMAL(10,2) NULLABLE,
    next_maintenance_date DATE NULLABLE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_equipment_maintenance_equipment ON equipment_maintenance(equipment_id);
CREATE INDEX idx_equipment_maintenance_status ON equipment_maintenance(equipment_id, status);
CREATE INDEX idx_equipment_maintenance_scheduled ON equipment_maintenance(scheduled_date);
```

### 2.17 staff

```sql
CREATE TABLE staff (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_number VARCHAR(20) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL,
    specialization VARCHAR(50) NULLABLE,
    certification_status VARCHAR(20) NOT NULL DEFAULT 'VALID',
    certification_expiry DATE NULLABLE,
    ward_id UUID REFERENCES wards(id) ON DELETE SET NULL,
    max_workload_threshold DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    availability_status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_staff_ward ON staff(ward_id);
CREATE INDEX idx_staff_role ON staff(role);
CREATE INDEX idx_staff_availability ON staff(ward_id, availability_status);
CREATE INDEX idx_staff_number ON staff(staff_number);
CREATE INDEX idx_staff_specialization ON staff(specialization);
```

### 2.18 staff_shifts

```sql
CREATE TABLE staff_shifts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shift_name VARCHAR(50) NOT NULL,
    shift_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    ward_id UUID NOT NULL REFERENCES wards(id),
    min_required_staff INTEGER NOT NULL,
    max_staff INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_staff_shifts_ward ON staff_shifts(ward_id);
CREATE INDEX idx_staff_shifts_date ON staff_shifts(shift_date);
CREATE INDEX idx_staff_shifts_ward_date ON staff_shifts(ward_id, shift_date, start_time);
```

### 2.19 shift_assignments

```sql
CREATE TABLE shift_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    shift_id UUID NOT NULL REFERENCES staff_shifts(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    assigned_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(staff_id, shift_id)
);

CREATE INDEX idx_shift_assignments_staff ON shift_assignments(staff_id);
CREATE INDEX idx_shift_assignments_shift ON shift_assignments(shift_id);
```

### 2.20 staff_admissions

```sql
CREATE TABLE staff_admissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    staff_id UUID NOT NULL REFERENCES staff(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    assigned_at TIMESTAMP NOT NULL,
    released_at TIMESTAMP NULLABLE,
    created_by UUID REFERENCES users(id)
);

CREATE INDEX idx_staff_admissions_staff ON staff_admissions(staff_id);
CREATE INDEX idx_staff_admissions_admission ON staff_admissions(admission_id);
CREATE UNIQUE INDEX idx_staff_admissions_active ON staff_admissions(staff_id, admission_id) WHERE released_at IS NULL;
```

### 2.21 resource_allocations

```sql
CREATE TABLE resource_allocations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resource_id UUID NOT NULL REFERENCES resources(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    quantity INTEGER NOT NULL,
    allocated_at TIMESTAMP NOT NULL,
    allocated_by UUID NOT NULL REFERENCES users(id),
    notes TEXT NULLABLE
);

CREATE INDEX idx_resource_allocations_resource ON resource_allocations(resource_id);
CREATE INDEX idx_resource_allocations_admission ON resource_allocations(admission_id);
```

### 2.22 equipment_allocations

```sql
CREATE TABLE equipment_allocations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id UUID NOT NULL REFERENCES equipment(id),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    assigned_at TIMESTAMP NOT NULL,
    released_at TIMESTAMP NULLABLE,
    assigned_by UUID NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_equipment_allocations_equipment ON equipment_allocations(equipment_id);
CREATE INDEX idx_equipment_allocations_admission ON equipment_allocations(admission_id);
```

### 2.23 allocation_recommendations

```sql
CREATE TABLE allocation_recommendations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    admission_id UUID NOT NULL REFERENCES admissions(id),
    batch_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    generated_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_allocation_recommendations_admission ON allocation_recommendations(admission_id);
CREATE INDEX idx_allocation_recommendations_status ON allocation_recommendations(status, expires_at);
```

### 2.24 recommendation_items

```sql
CREATE TABLE recommendation_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recommendation_id UUID NOT NULL REFERENCES allocation_recommendations(id) ON DELETE CASCADE,
    item_type VARCHAR(20) NOT NULL,
    recommended_entity_type VARCHAR(50) NOT NULL,
    recommended_entity_id UUID NOT NULL,
    rank INTEGER NOT NULL DEFAULT 1,
    confidence_score DECIMAL(3,2) NOT NULL,
    scoring_breakdown JSONB NULLABLE,
    rationale TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recommendation_items_recommendation ON recommendation_items(recommendation_id);
CREATE INDEX idx_recommendation_items_type_rank ON recommendation_items(recommendation_id, item_type, rank);
CREATE INDEX idx_recommendation_items_status ON recommendation_items(status);
```

### 2.25 recommendation_decisions

```sql
CREATE TABLE recommendation_decisions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recommendation_item_id UUID NOT NULL REFERENCES recommendation_items(id) ON DELETE CASCADE,
    decision_type VARCHAR(20) NOT NULL,
    overridden_entity_type VARCHAR(50) NULLABLE,
    overridden_entity_id UUID NULLABLE,
    override_justification TEXT NULLABLE,
    decided_by UUID NOT NULL REFERENCES users(id),
    decided_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_recommendation_decisions_item ON recommendation_decisions(recommendation_item_id);
CREATE INDEX idx_recommendation_decisions_type ON recommendation_decisions(decision_type);
```

### 2.26 audit_logs

```sql
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timestamp TIMESTAMP NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action_type VARCHAR(30) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    before_value JSONB NULLABLE,
    after_value JSONB NULLABLE,
    ip_address VARCHAR(45) NULLABLE,
    user_agent VARCHAR(500) NULLABLE,
    integrity_hash VARCHAR(64) NOT NULL
);

CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action_type, timestamp);
```

### 2.27 login_audit_logs

```sql
CREATE TABLE login_audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    timestamp TIMESTAMP NOT NULL,
    username_attempted VARCHAR(50) NOT NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    event_type VARCHAR(30) NOT NULL,
    ip_address VARCHAR(45) NULLABLE,
    user_agent VARCHAR(500) NULLABLE,
    details JSONB NULLABLE
);

CREATE INDEX idx_login_audit_logs_username ON login_audit_logs(username_attempted, timestamp);
CREATE INDEX idx_login_audit_logs_user ON login_audit_logs(user_id);
CREATE INDEX idx_login_audit_logs_event ON login_audit_logs(event_type);
```

### 2.28 notifications

```sql
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(20) NOT NULL,
    source_module VARCHAR(30) NOT NULL,
    source_entity_type VARCHAR(50) NULLABLE,
    source_entity_id UUID NULLABLE,
    recipient_user_id UUID NOT NULL REFERENCES users(id),
    is_read BOOLEAN NOT NULL DEFAULT false,
    read_at TIMESTAMP NULLABLE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_recipient ON notifications(recipient_user_id);
CREATE INDEX idx_notifications_unread ON notifications(recipient_user_id, is_read);
CREATE INDEX idx_notifications_type ON notifications(notification_type);
```

### 2.29 forecast_snapshots

```sql
CREATE TABLE forecast_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    forecast_type VARCHAR(30) NOT NULL,
    forecast_horizon VARCHAR(10) NOT NULL,
    target_period_start DATE NOT NULL,
    target_period_end DATE NOT NULL,
    predicted_values JSONB NOT NULL,
    model_used VARCHAR(30) NOT NULL,
    accuracy_score DECIMAL(5,2) NULLABLE,
    generated_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_forecast_snapshots_type ON forecast_snapshots(forecast_type);
CREATE INDEX idx_forecast_snapshots_period ON forecast_snapshots(target_period_start, target_period_end);
```

### 2.30 system_configurations

```sql
CREATE TABLE system_configurations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    value_type VARCHAR(20) NOT NULL,
    description TEXT NULLABLE,
    category VARCHAR(30) NOT NULL,
    default_value TEXT NULLABLE,
    requires_restart BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

CREATE INDEX idx_system_configurations_key ON system_configurations(config_key);
CREATE INDEX idx_system_configurations_category ON system_configurations(category);
```

---

## 3. Cascade Rules Summary

| Parent Table | Child Table | FK Column | On Delete | On Update |
|-------------|-------------|-----------|-----------|-----------|
| users | refresh_tokens | user_id | CASCADE | CASCADE |
| users | password_history | user_id | CASCADE | CASCADE |
| users | login_history | user_id | SET NULL | CASCADE |
| users | audit_logs | user_id | SET NULL | CASCADE |
| users | login_audit_logs | user_id | SET NULL | CASCADE |
| users | notifications | recipient_user_id | RESTRICT | CASCADE |
| patients | clinical_assessments | patient_id | RESTRICT | CASCADE |
| patients | admissions | patient_id | RESTRICT | CASCADE |
| admissions | clinical_assessments | admission_id | SET NULL | CASCADE |
| admissions | beds | current_admission_id | SET NULL | CASCADE |
| admissions | staff_admissions | admission_id | RESTRICT | CASCADE |
| admissions | resource_allocations | admission_id | RESTRICT | CASCADE |
| admissions | equipment_allocations | admission_id | RESTRICT | CASCADE |
| admissions | allocation_recommendations | admission_id | RESTRICT | CASCADE |
| admissions | bed_cleaning | admission_id | RESTRICT | CASCADE |
| wards | beds | ward_id | RESTRICT | CASCADE |
| wards | staff | ward_id | SET NULL | CASCADE |
| wards | staff_shifts | ward_id | RESTRICT | CASCADE |
| beds | bed_cleaning | bed_id | RESTRICT | CASCADE |
| resources | resource_inventory | resource_id | CASCADE | CASCADE |
| resources | resource_allocations | resource_id | RESTRICT | CASCADE |
| resource_inventory | inventory_transactions | resource_inventory_id | RESTRICT | CASCADE |
| resource_suppliers | resources | default_supplier_id | SET NULL | CASCADE |
| equipment | equipment_allocations | equipment_id | RESTRICT | CASCADE |
| equipment | equipment_maintenance | equipment_id | CASCADE | CASCADE |
| staff | staff_admissions | staff_id | RESTRICT | CASCADE |
| staff | shift_assignments | staff_id | CASCADE | CASCADE |
| staff | bed_cleaning | assigned_to | SET NULL | CASCADE |
| staff_shifts | shift_assignments | shift_id | CASCADE | CASCADE |
| allocation_recommendations | recommendation_items | recommendation_id | CASCADE | CASCADE |
| recommendation_items | recommendation_decisions | recommendation_item_id | CASCADE | CASCADE |

---

## 4. Soft Delete Strategy

### 4.1 Entities Using Soft Deletes

| Table | Column | Default | Query Pattern |
|-------|--------|---------|---------------|
| patients | is_active | true | WHERE is_active = true |
| admissions | is_active | true | WHERE is_active = true |
| resource_suppliers | is_active | true | WHERE is_active = true |

### 4.2 Entities Using Status-Based Deactivation

| Table | Column | Deactivated Value | Query Pattern |
|-------|--------|-------------------|---------------|
| users | status | 'DEACTIVATED' | WHERE status != 'DEACTIVATED' |
| wards | status | 'INACTIVE' | WHERE status = 'ACTIVE' |
| beds | status | 'OUT_OF_SERVICE' | WHERE status NOT IN ('OUT_OF_SERVICE') |

### 4.3 Append-Only Entities

| Table | Rationale |
|-------|-----------|
| clinical_assessments | Clinical history integrity |
| inventory_transactions | Financial audit trail |
| audit_logs | Compliance requirement |
| login_audit_logs | Security monitoring |
| bed_cleaning | Workflow audit trail |

---

## 5. Auditing Fields

Every business table includes:

| Column | Type | Default | Description |
|--------|------|---------|-------------|
| created_at | TIMESTAMP | NOW() | Record creation timestamp |
| updated_at | TIMESTAMP | NOW() | Last update timestamp |
| created_by | UUID | NULL | FK to users.id |
| updated_by | UUID | NULL | FK to users.id |

Tables that do NOT have updated_at (append-only):
- clinical_assessments
- inventory_transactions
- audit_logs
- login_audit_logs
- bed_cleaning (has updated_at for workflow status changes)
- refresh_tokens
- password_history
- login_history
- staff_admissions
- resource_allocations
- equipment_allocations
- shift_assignments
- recommendation_items
- recommendation_decisions
- notifications
- forecast_snapshots

---

## 6. Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Table names | Snake_case, plural | patients, clinical_assessments |
| Column names | Snake_case | patient_number, severity_level |
| Primary keys | id | id |
| Foreign keys | {referenced_table_singular}_id | patient_id, ward_id |
| Indexes | idx_{table}_{columns} | idx_patients_full_name |
| Unique constraints | uq_{table}_{columns} | uq_beds_ward_id |
| Check constraints | chk_{table}_{rule} | chk_inventory_quantity |

---

## 7. Document References

| Document | Reference |
|----------|-----------|
| Database Plan | `docs/planning/07-database-plan.md` |
| Domain Model | `docs/planning/06-domain-model.md` |
| Domain Review | `docs/design/01-domain-review.md` |
