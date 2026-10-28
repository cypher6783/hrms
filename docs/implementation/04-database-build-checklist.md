# 04 — Database Build Checklist

## 1. Flyway Migration Sequence

### 1.1 Schema Migrations

| Version | Description | Tables | Status |
|---------|-------------|--------|--------|
| V001 | Authentication tables | users, refresh_tokens, password_history, login_history | [ ] |
| V002 | Patient tables | patients, clinical_assessments | [ ] |
| V003 | Facility tables | wards, beds | [ ] |
| V004 | Admission tables | admissions | [ ] |
| V005 | Bed cleaning tables | bed_cleaning | [ ] |
| V006 | Resource tables | resources, resource_inventory, resource_suppliers | [ ] |
| V007 | Inventory tables | inventory_transactions | [ ] |
| V008 | Equipment tables | equipment, equipment_maintenance | [ ] |
| V009 | Staff tables | staff | [ ] |
| V010 | Shift tables | staff_shifts, shift_assignments | [ ] |
| V011 | Allocation tables | staff_admissions, resource_allocations, equipment_allocations | [ ] |
| V012 | Recommendation tables | allocation_recommendations, recommendation_items, recommendation_decisions | [ ] |
| V013 | Audit tables | audit_logs, login_audit_logs | [ ] |
| V014 | Notification tables | notifications | [ ] |
| V015 | Forecast tables | forecast_snapshots | [ ] |
| V016 | Configuration tables | system_configurations | [ ] |
| V017 | Foreign key constraints | All FK constraints | [ ] |
| V018 | Unique constraints | All unique constraints | [ ] |
| V019 | Primary key defaults | UUID generation defaults | [ ] |
| V020 | Audit fields | created_at, updated_at, created_by, updated_by | [ ] |
| V021 | Soft delete flags | is_active on patients, admissions, resource_suppliers | [ ] |
| V022 | Foreign key indexes | All FK indexes | [ ] |
| V023 | Query-driven indexes | Performance indexes | [ ] |
| V024 | Composite indexes | Composite indexes | [ ] |
| V025 | Partial indexes | Unique partial indexes | [ ] |
| V026 | Audit log triggers | Prevent UPDATE/DELETE on audit_logs | [ ] |
| V027 | Login audit triggers | Prevent UPDATE/DELETE on login_audit_logs | [ ] |
| V028 | Inventory triggers | Prevent UPDATE/DELETE on inventory_transactions | [ ] |
| V029 | Clinical assessment triggers | Prevent UPDATE on clinical_assessments | [ ] |
| V030 | Database functions | UUID generation, audit field auto-update | [ ] |

### 1.2 Seed Data Migrations

| Version | Description | Status |
|---------|-------------|--------|
| R__001 | Default user roles and permissions | [ ] |
| R__002 | Default administrator account | [ ] |
| R__003 | Default ward configurations | [ ] |
| R__004 | Default resource categories and types | [ ] |
| R__005 | Default system configuration parameters | [ ] |
| R__006 | Demo data for development environment | [ ] |

---

## 2. Table Creation Verification

### 2.1 Authentication Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| users | 14 | id | created_by, updated_by | username, email | username, email, status | [ ] |
| refresh_tokens | 6 | id | user_id | token_hash | user_id, token_hash, expires_at | [ ] |
| password_history | 3 | id | user_id | - | user_id | [ ] |
| login_history | 7 | id | user_id | - | user_id, username_attempted, created_at | [ ] |

### 2.2 Patient Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| patients | 14 | id | created_by, updated_by | patient_number | patient_number, full_name, is_active, phone | [ ] |
| clinical_assessments | 11 | id | patient_id, admission_id, assessed_by | - | patient_id, admission_id, timestamps | [ ] |

### 2.3 Facility Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| wards | 12 | id | created_by, updated_by | name | name, status | [ ] |
| beds | 13 | id | ward_id, current_admission_id, created_by, updated_by | (bed_number, ward_id) | ward_id, status, composite | [ ] |

### 2.4 Admission Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| admissions | 17 | id | patient_id, ward_id, bed_id, created_by, updated_by | admission_number | patient_id, ward_id, bed_id, status, composite | [ ] |

### 2.5 Bed Cleaning Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| bed_cleaning | 13 | id | bed_id, admission_id, assigned_to, verified_by | - | bed_id, admission_id, status, assigned_to | [ ] |

### 2.6 Resource Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| resources | 12 | id | default_supplier_id, created_by, updated_by | - | category, criticality, supplier | [ ] |
| resource_inventory | 8 | id | resource_id | (resource_id, location, batch_number) | resource_id, location, stock | [ ] |
| resource_suppliers | 11 | id | created_by, updated_by | - | name, is_active | [ ] |

### 2.7 Inventory Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| inventory_transactions | 10 | id | resource_inventory_id, admission_id, performed_by | - | resource_inventory_id, admission_id, type | [ ] |

### 2.8 Equipment Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| equipment | 12 | id | assigned_admission_id, assigned_ward_id, created_by, updated_by | serial_number | type, status, admission, ward, serial | [ ] |
| equipment_maintenance | 14 | id | equipment_id, created_by, updated_by | - | equipment_id, status, scheduled_date | [ ] |

### 2.9 Staff Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| staff | 14 | id | ward_id, created_by, updated_by | staff_number | ward_id, role, availability, number, specialization | [ ] |
| staff_shifts | 13 | id | ward_id, created_by, updated_by | - | ward_id, shift_date, composite | [ ] |
| shift_assignments | 5 | id | staff_id, shift_id, assigned_by | (staff_id, shift_id) | staff_id, shift_id | [ ] |

### 2.10 Allocation Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| staff_admissions | 6 | id | staff_id, admission_id, created_by | (staff_id, admission_id) WHERE released_at IS NULL | staff_id, admission_id | [ ] |
| resource_allocations | 7 | id | resource_id, admission_id, allocated_by | - | resource_id, admission_id | [ ] |
| equipment_allocations | 6 | id | equipment_id, admission_id, assigned_by | - | equipment_id, admission_id | [ ] |

### 2.11 Recommendation Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| allocation_recommendations | 7 | id | admission_id | - | admission_id, status | [ ] |
| recommendation_items | 12 | id | recommendation_id | - | recommendation_id, composite, status | [ ] |
| recommendation_decisions | 9 | id | recommendation_item_id, decided_by | - | recommendation_item_id, decision_type | [ ] |

### 2.12 Audit Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| audit_logs | 12 | id | user_id | - | timestamp, user_id, entity, action | [ ] |
| login_audit_logs | 8 | id | user_id | - | username, user_id, event_type | [ ] |

### 2.13 Notification Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| notifications | 12 | id | recipient_user_id | - | recipient_user_id, unread, type | [ ] |

### 2.14 Forecast Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| forecast_snapshots | 10 | id | - | - | type, period | [ ] |

### 2.15 Configuration Tables

| Table | Columns | PK | FKs | Unique | Indexes | Status |
|-------|---------|----|-----|--------|---------|--------|
| system_configurations | 11 | id | created_by, updated_by | config_key | key, category | [ ] |

---

## 3. Index Verification

### 3.1 Foreign Key Indexes

| Table | Column | Status |
|-------|--------|--------|
| refresh_tokens | user_id | [ ] |
| password_history | user_id | [ ] |
| login_history | user_id | [ ] |
| login_history | username_attempted | [ ] |
| clinical_assessments | patient_id | [ ] |
| clinical_assessments | admission_id | [ ] |
| admissions | patient_id | [ ] |
| admissions | ward_id | [ ] |
| admissions | bed_id | [ ] |
| beds | ward_id | [ ] |
| bed_cleaning | bed_id | [ ] |
| bed_cleaning | assigned_to | [ ] |
| staff | ward_id | [ ] |
| staff_admissions | staff_id | [ ] |
| staff_admissions | admission_id | [ ] |
| shift_assignments | staff_id | [ ] |
| shift_assignments | shift_id | [ ] |
| resource_inventory | resource_id | [ ] |
| inventory_transactions | resource_inventory_id | [ ] |
| resource_allocations | resource_id | [ ] |
| resource_allocations | admission_id | [ ] |
| equipment_allocations | equipment_id | [ ] |
| equipment_allocations | admission_id | [ ] |
| equipment_maintenance | equipment_id | [ ] |
| allocation_recommendations | admission_id | [ ] |
| recommendation_items | recommendation_id | [ ] |
| recommendation_decisions | recommendation_item_id | [ ] |
| audit_logs | user_id | [ ] |
| login_audit_logs | user_id | [ ] |
| login_audit_logs | username_attempted | [ ] |
| notifications | recipient_user_id | [ ] |
| staff_shifts | ward_id | [ ] |
| forecast_snapshots | forecast_type | [ ] |

### 3.2 Query-Driven Indexes

| Table | Columns | Type | Status |
|-------|---------|------|--------|
| patients | full_name | GIN (trgm) | [ ] |
| patients | patient_number | B-tree | [ ] |
| patients | (is_active, created_at) | B-tree | [ ] |
| clinical_assessments | (patient_id, assessment_timestamp) | B-tree | [ ] |
| clinical_assessments | (admission_id, is_reassessment) | B-tree | [ ] |
| beds | (ward_id, status) | B-tree | [ ] |
| beds | status | B-tree | [ ] |
| admissions | (status, admitted_at) | B-tree | [ ] |
| admissions | (patient_id, is_active) | B-tree | [ ] |
| bed_cleaning | (status, bed_id) | B-tree | [ ] |
| audit_logs | timestamp | B-tree | [ ] |
| audit_logs | (entity_type, entity_id) | B-tree | [ ] |
| audit_logs | (action_type, timestamp) | B-tree | [ ] |
| login_audit_logs | (username_attempted, timestamp) | B-tree | [ ] |
| notifications | (recipient_user_id, is_read) | B-tree | [ ] |
| inventory_transactions | (resource_inventory_id, transaction_timestamp) | B-tree | [ ] |
| equipment_maintenance | (equipment_id, status) | B-tree | [ ] |
| recommendation_items | (status, expires_at) | B-tree | [ ] |

### 3.3 Composite Indexes

| Table | Columns | Purpose | Status |
|-------|---------|---------|--------|
| beds | (ward_id, bed_type, status) | Bed recommendation | [ ] |
| staff | (ward_id, availability_status) | Staff recommendation | [ ] |
| admissions | (ward_id, status, admitted_at) | Ward occupancy | [ ] |
| staff_shifts | (ward_id, shift_date, start_time) | Shift queries | [ ] |
| resource_inventory | (resource_id, location) | Location stock | [ ] |
| recommendation_items | (recommendation_id, item_type, rank) | Item ranking | [ ] |

---

## 4. Constraint Verification

### 4.1 Primary Keys

| Table | Column | Constraint | Status |
|-------|--------|-----------|--------|
| All tables | id | UUID PRIMARY KEY DEFAULT gen_random_uuid() | [ ] |

### 4.2 Unique Constraints

| Table | Columns | Constraint | Status |
|-------|---------|-----------|--------|
| users | username | UNIQUE | [ ] |
| users | email | UNIQUE | [ ] |
| patients | patient_number | UNIQUE | [ ] |
| admissions | admission_number | UNIQUE | [ ] |
| beds | (bed_number, ward_id) | UNIQUE | [ ] |
| wards | name | UNIQUE | [ ] |
| equipment | serial_number | UNIQUE | [ ] |
| resource_inventory | (resource_id, location, batch_number) | UNIQUE | [ ] |
| shift_assignments | (staff_id, shift_id) | UNIQUE | [ ] |
| system_configurations | config_key | UNIQUE | [ ] |
| refresh_tokens | token_hash | UNIQUE | [ ] |

### 4.3 Check Constraints

| Table | Column | Check | Status |
|-------|--------|-------|--------|
| resource_inventory | current_stock | >= 0 | [ ] |

### 4.4 Foreign Key Constraints

All foreign keys verified per `docs/design/02-database-design.md` Section 3.

---

## 5. Seed Data Verification

| Seed Data | Tables Populated | Records | Status |
|-----------|-----------------|---------|--------|
| R__001: Roles | users (role enum) | 7 roles | [ ] |
| R__002: Admin user | users | 1 admin | [ ] |
| R__003: Wards | wards | 4-6 wards | [ ] |
| R__004: Resources | resources, resource_inventory | 10-20 resources | [ ] |
| R__005: System config | system_configurations | 15-20 configs | [ ] |
| R__006: Demo data | Multiple | Development only | [ ] |

---

## 6. Migration Verification Checklist

| Check | Status |
|-------|--------|
| All migrations run without errors | [ ] |
| All tables created with correct columns | [ ] |
| All constraints enforced | [ ] |
| All indexes created | [ ] |
| Seed data populated correctly | [ ] |
| Triggers created for append-only tables | [ ] |
| UUID generation working | [ ] |
| Audit fields auto-populated | [ ] |
| Soft delete working (Patient, Admission, ResourceSupplier) | [ ] |

---

## 7. Document References

| Document | Reference |
|----------|-----------|
| Database Design | `docs/design/02-database-design.md` |
| Database Migration Plan | `docs/design/05-database-migration-plan.md` |
| Database Plan | `docs/planning/07-database-plan.md` |
