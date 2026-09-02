# 07 — Database Plan

## 1. Database Strategy

The system uses **PostgreSQL 16.x** as its primary relational database. The database design follows normalization principles (Third Normal Form) for data integrity while incorporating selective denormalization for query performance in reporting and dashboard scenarios.

### 1.1 Design Principles

- **Referential Integrity**: Foreign key constraints enforce relationship validity.
- **Normalization**: Core entities normalized to 3NF to prevent data anomalies.
- **Soft Deletes**: Records are deactivated, not physically deleted, to preserve audit trail and historical data.
- **Audit Fields**: All tables include `created_at`, `updated_at`, `created_by`, and `updated_by` columns.
- **UUID Primary Keys**: All entities use UUIDs as primary keys for distributed uniqueness and security.
- **Schema Migration**: All schema changes managed through Flyway migrations.
- **Naming Convention**: Snake_case for table and column names; singular table names.
- **Transactional Ledger**: Inventory and financial operations use append-only transaction logs rather than mutable current-state columns.

### 1.2 Schema Organization

All tables reside in a single PostgreSQL schema (`public`) for the initial release. Future multi-tenant expansion may require schema-per-tenant isolation.

---

## 2. Entity List

### 2.1 users

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK, DEFAULT gen_random_uuid() | Unique identifier |
| username | VARCHAR(50) | UNIQUE, NOT NULL | Login username |
| email | VARCHAR(100) | UNIQUE, NOT NULL | Email address |
| password_hash | VARCHAR(255) | NOT NULL | bcrypt password hash |
| full_name | VARCHAR(100) | NOT NULL | Display name |
| role | VARCHAR(30) | NOT NULL | User role enum |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | Account status |
| failed_login_attempts | INTEGER | NOT NULL, DEFAULT 0 | Failed login counter |
| locked_until | TIMESTAMP | NULLABLE | Account lock expiry |
| last_login_at | TIMESTAMP | NULLABLE | Last successful login |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.2 refresh_tokens

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| token_hash | VARCHAR(64) | UNIQUE, NOT NULL | SHA-256 hash of refresh token |
| user_id | UUID | FK → users.id, NOT NULL | Token owner |
| expires_at | TIMESTAMP | NOT NULL | Token expiry |
| revoked | BOOLEAN | NOT NULL, DEFAULT false | Revocation status |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |

### 2.3 password_history

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| user_id | UUID | FK → users.id, NOT NULL | User |
| password_hash | VARCHAR(255) | NOT NULL | Previous password hash |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |

### 2.4 login_history

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| username_attempted | VARCHAR(50) | NOT NULL | Username used in attempt |
| user_id | UUID | FK → users.id, NULLABLE | Matched user (nullable for unknown usernames) |
| success | BOOLEAN | NOT NULL | Attempt success flag |
| ip_address | VARCHAR(45) | NULLABLE | Client IP address |
| user_agent | VARCHAR(500) | NULLABLE | Client user agent |
| failure_reason | VARCHAR(100) | NULLABLE | Failure reason description |
| created_at | TIMESTAMP | NOT NULL | Attempt timestamp |

**Note**: Retained for 90 days; older records archived or purged.

### 2.5 patients

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| patient_number | VARCHAR(20) | UNIQUE, NOT NULL | Human-readable patient ID |
| full_name | VARCHAR(100) | NOT NULL | Patient name |
| date_of_birth | DATE | NOT NULL | Date of birth |
| gender | VARCHAR(10) | NOT NULL | Gender |
| phone_number | VARCHAR(20) | NULLABLE | Contact phone |
| address | TEXT | NULLABLE | Patient address |
| next_of_kin_name | VARCHAR(100) | NULLABLE | Next-of-kin name |
| next_of_kin_phone | VARCHAR(20) | NULLABLE | Next-of-kin contact |
| is_active | BOOLEAN | NOT NULL, DEFAULT true | Soft delete flag |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

**Note**: Clinical state (severity, triage, infection status) is NOT stored here. It resides in `clinical_assessments`.

### 2.6 clinical_assessments

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| patient_id | UUID | FK → patients.id, NOT NULL | Assessed patient |
| admission_id | UUID | FK → admissions.id, NULLABLE | Linked admission (nullable for pre-admission assessments) |
| assessed_by | UUID | FK → users.id, NOT NULL | Clinician who performed assessment |
| severity_level | VARCHAR(20) | NOT NULL | Severity classification |
| triage_classification | VARCHAR(20) | NOT NULL | Triage level |
| infection_status | VARCHAR(20) | NOT NULL, DEFAULT 'SUSPECTED' | Lassa fever status |
| clinical_notes | TEXT | NULLABLE | Clinical observations |
| is_reassessment | BOOLEAN | NOT NULL, DEFAULT false | Whether this is a follow-up |
| assessment_timestamp | TIMESTAMP | NOT NULL | When assessment was performed |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

**Note**: This table is append-only for clinical history integrity. Updates to clinical state create new rows, not modify existing ones.

### 2.7 admissions

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| admission_number | VARCHAR(20) | UNIQUE, NOT NULL | Human-readable admission ID |
| patient_id | UUID | FK → patients.id, NOT NULL | Admitted patient |
| ward_id | UUID | FK → wards.id, NOT NULL | Assigned ward |
| bed_id | UUID | FK → beds.id, NULLABLE | Assigned bed |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | Admission status |
| admission_notes | TEXT | NULLABLE | Admission notes |
| discharge_outcome | VARCHAR(30) | NULLABLE | Discharge outcome |
| discharge_notes | TEXT | NULLABLE | Discharge notes |
| admitted_at | TIMESTAMP | NOT NULL | Admission timestamp |
| discharged_at | TIMESTAMP | NULLABLE | Discharge timestamp |
| is_active | BOOLEAN | NOT NULL, DEFAULT true | Active admission flag |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.8 wards

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| name | VARCHAR(50) | UNIQUE, NOT NULL | Ward name |
| ward_type | VARCHAR(30) | NOT NULL | Ward type |
| max_bed_capacity | INTEGER | NOT NULL | Maximum beds allowed |
| isolation_level | VARCHAR(20) | NOT NULL, DEFAULT 'NONE' | Isolation requirement |
| equipment_zone | VARCHAR(50) | NULLABLE | Equipment zone designation |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | Ward status |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.9 beds

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| bed_number | VARCHAR(20) | NOT NULL | Bed number within ward |
| ward_id | UUID | FK → wards.id, NOT NULL | Parent ward |
| bed_type | VARCHAR(40) | NOT NULL | Bed type classification |
| is_isolation_capable | BOOLEAN | NOT NULL, DEFAULT false | Isolation capability |
| status | VARCHAR(30) | NOT NULL, DEFAULT 'AVAILABLE' | Current status |
| current_admission_id | UUID | FK → admissions.id, NULLABLE | Current occupant |
| last_maintenance_at | TIMESTAMP | NULLABLE | Last maintenance timestamp |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

**Unique Constraint**: (bed_number, ward_id)

### 2.10 bed_cleaning

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| bed_id | UUID | FK → beds.id, NOT NULL | Bed to be cleaned |
| admission_id | UUID | FK → admissions.id, NOT NULL | Admission that triggered cleaning |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | Cleaning status |
| assigned_to | UUID | FK → staff.id, NULLABLE | Assigned cleaner |
| assigned_at | TIMESTAMP | NULLABLE | Assignment timestamp |
| started_at | TIMESTAMP | NULLABLE | Cleaning start timestamp |
| completed_at | TIMESTAMP | NULLABLE | Cleaning completion timestamp |
| verified_by | UUID | FK → users.id, NULLABLE | Verification user |
| verified_at | TIMESTAMP | NULLABLE | Verification timestamp |
| cleaning_notes | TEXT | NULLABLE | Cleaning notes |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

### 2.11 resources

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| name | VARCHAR(100) | NOT NULL | Resource name |
| category | VARCHAR(30) | NOT NULL | Resource category |
| unit_of_measure | VARCHAR(20) | NOT NULL | Unit of measure |
| minimum_threshold | INTEGER | NOT NULL, DEFAULT 0 | Minimum stock threshold |
| reorder_point | INTEGER | NOT NULL, DEFAULT 0 | Reorder trigger point |
| criticality_level | VARCHAR(20) | NOT NULL, DEFAULT 'MEDIUM' | Criticality classification |
| default_supplier_id | UUID | FK → resource_suppliers.id, NULLABLE | Default supplier |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.12 resource_inventory

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| resource_id | UUID | FK → resources.id, NOT NULL | Resource type |
| location | VARCHAR(100) | NOT NULL | Storage location |
| current_stock | INTEGER | NOT NULL, DEFAULT 0 | Current quantity |
| expiration_date | DATE | NULLABLE | Batch expiration date |
| batch_number | VARCHAR(50) | NULLABLE | Batch identifier |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |

**Unique Constraint**: (resource_id, location, batch_number)

### 2.13 inventory_transactions

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| resource_inventory_id | UUID | FK → resource_inventory.id, NOT NULL | Inventory record |
| transaction_type | VARCHAR(20) | NOT NULL | Transaction type |
| quantity | INTEGER | NOT NULL | Quantity (+ for inbound, - for outbound) |
| admission_id | UUID | FK → admissions.id, NULLABLE | Linked admission (for patient issues) |
| reference_document | VARCHAR(100) | NULLABLE | Reference document number |
| notes | TEXT | NULLABLE | Transaction notes |
| performed_by | UUID | FK → users.id, NOT NULL | User who performed transaction |
| transaction_timestamp | TIMESTAMP | NOT NULL | Transaction time |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

**Note**: This table is append-only. Stock levels in `resource_inventory` are calculated from transactions.

### 2.14 resource_suppliers

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| name | VARCHAR(100) | NOT NULL | Supplier name |
| contact_person | VARCHAR(100) | NULLABLE | Contact person |
| phone_number | VARCHAR(20) | NULLABLE | Phone number |
| email | VARCHAR(100) | NULLABLE | Email address |
| address | TEXT | NULLABLE | Supplier address |
| lead_time_days | INTEGER | NULLABLE | Average delivery time |
| is_active | BOOLEAN | NOT NULL, DEFAULT true | Active status |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.15 equipment

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| name | VARCHAR(100) | NOT NULL | Equipment name |
| equipment_type | VARCHAR(50) | NOT NULL | Equipment type |
| serial_number | VARCHAR(50) | UNIQUE, NOT NULL | Serial number |
| location | VARCHAR(100) | NULLABLE | Current location |
| status | VARCHAR(30) | NOT NULL, DEFAULT 'AVAILABLE' | Equipment status |
| assigned_admission_id | UUID | FK → admissions.id, NULLABLE | Assigned admission |
| assigned_ward_id | UUID | FK → wards.id, NULLABLE | Assigned ward |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.16 equipment_maintenance

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| equipment_id | UUID | FK → equipment.id, NOT NULL | Equipment being maintained |
| maintenance_type | VARCHAR(20) | NOT NULL | Scheduled, Unscheduled, Repair, Calibration |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'SCHEDULED' | Maintenance status |
| scheduled_date | DATE | NOT NULL | Scheduled maintenance date |
| completed_date | DATE | NULLABLE | Actual completion date |
| performed_by | VARCHAR(100) | NULLABLE | Vendor or staff identifier |
| maintenance_notes | TEXT | NULLABLE | Maintenance notes |
| cost | DECIMAL(10,2) | NULLABLE | Maintenance cost |
| next_maintenance_date | DATE | NULLABLE | Next scheduled maintenance |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.17 staff

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| staff_number | VARCHAR(20) | UNIQUE, NOT NULL | Human-readable staff ID |
| full_name | VARCHAR(100) | NOT NULL | Staff name |
| role | VARCHAR(30) | NOT NULL | Staff role |
| specialization | VARCHAR(50) | NULLABLE | Area of specialization |
| certification_status | VARCHAR(20) | NOT NULL, DEFAULT 'VALID' | Certification status |
| certification_expiry | DATE | NULLABLE | Certification expiry date |
| ward_id | UUID | FK → wards.id, NULLABLE | Assigned ward |
| max_workload_threshold | DECIMAL(5,2) | NOT NULL, DEFAULT 100.00 | Maximum workload |
| availability_status | VARCHAR(20) | NOT NULL, DEFAULT 'AVAILABLE' | Current availability |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

**Note**: `workload_score` is calculated dynamically, not stored.

### 2.18 staff_shifts

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| shift_name | VARCHAR(50) | NOT NULL | Shift name (e.g., "Morning Shift") |
| shift_date | DATE | NOT NULL | Shift date |
| start_time | TIME | NOT NULL | Shift start time |
| end_time | TIME | NOT NULL | Shift end time |
| ward_id | UUID | FK → wards.id, NOT NULL | Ward |
| min_required_staff | INTEGER | NOT NULL | Minimum required staff |
| max_staff | INTEGER | NOT NULL | Maximum staff capacity |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'SCHEDULED' | Shift status |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

### 2.19 shift_assignments

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| staff_id | UUID | FK → staff.id, NOT NULL | Assigned staff |
| shift_id | UUID | FK → staff_shifts.id, NOT NULL | Assigned shift |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'CONFIRMED' | Assignment status |
| assigned_by | UUID | FK → users.id, NOT NULL | Assigning user |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

**Unique Constraint**: (staff_id, shift_id)

### 2.20 staff_admissions (Join Table)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| staff_id | UUID | FK → staff.id, NOT NULL | Assigned staff |
| admission_id | UUID | FK → admissions.id, NOT NULL | Linked admission |
| assigned_at | TIMESTAMP | NOT NULL | Assignment timestamp |
| released_at | TIMESTAMP | NULLABLE | Release timestamp |
| created_by | UUID | FK → users.id | Assigning user |

**Unique Constraint**: (staff_id, admission_id) WHERE released_at IS NULL

### 2.21 resource_allocations

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| resource_id | UUID | FK → resources.id, NOT NULL | Allocated resource |
| admission_id | UUID | FK → admissions.id, NOT NULL | Linked admission |
| quantity | INTEGER | NOT NULL | Quantity allocated |
| allocated_at | TIMESTAMP | NOT NULL | Allocation timestamp |
| allocated_by | UUID | FK → users.id, NOT NULL | Allocating user |
| notes | TEXT | NULLABLE | Allocation notes |

### 2.22 equipment_allocations

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| equipment_id | UUID | FK → equipment.id, NOT NULL | Assigned equipment |
| admission_id | UUID | FK → admissions.id, NOT NULL | Linked admission |
| assigned_at | TIMESTAMP | NOT NULL | Assignment timestamp |
| released_at | TIMESTAMP | NULLABLE | Release timestamp |
| assigned_by | UUID | FK → users.id, NOT NULL | Assigning user |

### 2.23 allocation_recommendations

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| admission_id | UUID | FK → admissions.id, NOT NULL | Linked admission |
| batch_type | VARCHAR(30) | NOT NULL | New Admission, Severity Change, Periodic Refresh |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | Recommendation status |
| generated_at | TIMESTAMP | NOT NULL | Generation timestamp |
| expires_at | TIMESTAMP | NOT NULL | Expiration timestamp |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

### 2.24 recommendation_items

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| recommendation_id | UUID | FK → allocation_recommendations.id, NOT NULL | Parent recommendation |
| item_type | VARCHAR(20) | NOT NULL | Bed, Staff, Equipment, Resource |
| recommended_entity_type | VARCHAR(50) | NOT NULL | Entity table name |
| recommended_entity_id | UUID | NOT NULL | Entity ID |
| rank | INTEGER | NOT NULL, DEFAULT 1 | Recommendation rank (1 = primary) |
| confidence_score | DECIMAL(3,2) | NOT NULL | Confidence score (0.00-1.00) |
| scoring_breakdown | JSONB | NULLABLE | Factor scores and weights |
| rationale | TEXT | NOT NULL | Human-readable explanation |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | Item status |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

### 2.25 recommendation_decisions

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| recommendation_item_id | UUID | FK → recommendation_items.id, NOT NULL | Decision target |
| decision_type | VARCHAR(20) | NOT NULL | Accepted, Overridden |
| overridden_entity_type | VARCHAR(50) | NULLABLE | Alternative entity type |
| overridden_entity_id | UUID | NULLABLE | Alternative entity ID |
| override_justification | TEXT | NULLABLE | Override reason (required if overridden) |
| decided_by | UUID | FK → users.id, NOT NULL | Decision maker |
| decided_at | TIMESTAMP | NOT NULL | Decision timestamp |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

### 2.26 audit_logs

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| timestamp | TIMESTAMP | NOT NULL | Event timestamp |
| user_id | UUID | FK → users.id, NULLABLE | User who performed action |
| action_type | VARCHAR(30) | NOT NULL | Action type |
| entity_type | VARCHAR(50) | NOT NULL | Affected entity type |
| entity_id | UUID | NOT NULL | Affected entity ID |
| before_value | JSONB | NULLABLE | State before change |
| after_value | JSONB | NULLABLE | State after change |
| ip_address | VARCHAR(45) | NULLABLE | Client IP address |
| user_agent | VARCHAR(500) | NULLABLE | Client user agent |
| integrity_hash | VARCHAR(64) | NOT NULL | SHA-256 hash for integrity |

**Note**: This table has no UPDATE or DELETE triggers; records are append-only.

### 2.27 login_audit_logs

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| timestamp | TIMESTAMP | NOT NULL | Event timestamp |
| username_attempted | VARCHAR(50) | NOT NULL | Username used |
| user_id | UUID | FK → users.id, NULLABLE | Matched user |
| event_type | VARCHAR(30) | NOT NULL | Event type |
| ip_address | VARCHAR(45) | NULLABLE | Client IP |
| user_agent | VARCHAR(500) | NULLABLE | Client user agent |
| details | JSONB | NULLABLE | Additional context |

**Note**: Retained for 90 days.

### 2.28 notifications

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| title | VARCHAR(200) | NOT NULL | Notification title |
| message | TEXT | NOT NULL | Notification body |
| notification_type | VARCHAR(20) | NOT NULL | Notification type |
| source_module | VARCHAR(30) | NOT NULL | Source module |
| source_entity_type | VARCHAR(50) | NULLABLE | Source entity type |
| source_entity_id | UUID | NULLABLE | Source entity ID |
| recipient_user_id | UUID | FK → users.id, NOT NULL | Recipient user |
| is_read | BOOLEAN | NOT NULL, DEFAULT false | Read status |
| read_at | TIMESTAMP | NULLABLE | Read timestamp |
| created_at | TIMESTAMP | NOT NULL | Creation timestamp |

### 2.29 forecast_snapshots

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| forecast_type | VARCHAR(30) | NOT NULL | Forecast type |
| forecast_horizon | VARCHAR(10) | NOT NULL | 7-day, 14-day, 30-day |
| target_period_start | DATE | NOT NULL | Forecast period start |
| target_period_end | DATE | NOT NULL | Forecast period end |
| predicted_values | JSONB | NOT NULL | Structured forecast data |
| model_used | VARCHAR(30) | NOT NULL | Model algorithm |
| accuracy_score | DECIMAL(5,2) | NULLABLE | Accuracy (populated later) |
| generated_at | TIMESTAMP | NOT NULL | Generation timestamp |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |

### 2.30 system_configurations

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Unique identifier |
| config_key | VARCHAR(100) | UNIQUE, NOT NULL | Configuration key |
| config_value | TEXT | NOT NULL | Configuration value |
| value_type | VARCHAR(20) | NOT NULL | STRING, INTEGER, DECIMAL, BOOLEAN, JSON |
| description | TEXT | NULLABLE | Parameter description |
| category | VARCHAR(30) | NOT NULL | Recommendation, Notification, Security, Workflow |
| default_value | TEXT | NULLABLE | Default value |
| requires_restart | BOOLEAN | NOT NULL, DEFAULT false | Whether restart is needed |
| created_at | TIMESTAMP | NOT NULL | Record creation timestamp |
| updated_at | TIMESTAMP | NOT NULL | Last update timestamp |
| created_by | UUID | FK → users.id | Creator user ID |
| updated_by | UUID | FK → users.id | Last updater user ID |

---

## 3. Relationship Summary

| Parent Table | Child Table | Relationship | FK Column |
|-------------|-------------|-------------|-----------|
| users | refresh_tokens | One-to-Many | user_id |
| users | password_history | One-to-Many | user_id |
| users | login_history | One-to-Many | user_id |
| users | audit_logs | One-to-Many | user_id |
| users | login_audit_logs | One-to-Many | user_id |
| users | notifications | One-to-Many | recipient_user_id |
| users | allocation_recommendations | One-to-Many | generated_by |
| users | recommendation_decisions | One-to-Many | decided_by |
| patients | clinical_assessments | One-to-Many | patient_id |
| patients | admissions | One-to-Many | patient_id |
| admissions | clinical_assessments | One-to-Many | admission_id |
| admissions | beds | One-to-One | current_admission_id |
| admissions | staff_admissions | One-to-Many | admission_id |
| admissions | resource_allocations | One-to-Many | admission_id |
| admissions | equipment_allocations | One-to-Many | admission_id |
| admissions | allocation_recommendations | One-to-Many | admission_id |
| admissions | bed_cleaning | One-to-Many | admission_id |
| wards | beds | One-to-Many | ward_id |
| wards | staff | One-to-Many | ward_id |
| wards | admissions | One-to-Many | ward_id |
| wards | staff_shifts | One-to-Many | ward_id |
| beds | bed_cleaning | One-to-Many | bed_id |
| resources | resource_inventory | One-to-Many | resource_id |
| resources | resource_allocations | One-to-Many | resource_id |
| resource_inventory | inventory_transactions | One-to-Many | resource_inventory_id |
| resource_suppliers | resources | One-to-Many | default_supplier_id |
| equipment | equipment_allocations | One-to-Many | equipment_id |
| equipment | equipment_maintenance | One-to-Many | equipment_id |
| staff | staff_admissions | One-to-Many | staff_id |
| staff | shift_assignments | One-to-Many | staff_id |
| staff | bed_cleaning | One-to-Many | assigned_to |
| staff_shifts | shift_assignments | One-to-Many | shift_id |
| allocation_recommendations | recommendation_items | One-to-Many | recommendation_id |
| recommendation_items | recommendation_decisions | One-to-One | recommendation_item_id |

---

## 4. Index Strategy

### 4.1 Primary Indexes (Auto-Generated)

All primary keys (UUID) automatically receive B-tree indexes.

### 4.2 Foreign Key Indexes

| Table | Column | Index Purpose |
|-------|--------|---------------|
| refresh_tokens | user_id | User token lookup |
| password_history | user_id | Password reuse check |
| login_history | user_id | User login history |
| login_history | username_attempted | Brute-force detection |
| clinical_assessments | patient_id | Patient assessment history |
| clinical_assessments | admission_id | Admission assessment history |
| admissions | patient_id | Patient admission lookup |
| admissions | ward_id | Ward admission lookup |
| admissions | bed_id | Bed admission lookup |
| beds | ward_id | Ward bed lookup |
| bed_cleaning | bed_id | Bed cleaning history |
| bed_cleaning | assigned_to | Cleaner assignment lookup |
| staff | ward_id | Ward staff lookup |
| staff_admissions | staff_id | Staff assignment lookup |
| staff_admissions | admission_id | Admission staff lookup |
| shift_assignments | staff_id | Staff shift lookup |
| shift_assignments | shift_id | Shift staff lookup |
| resource_inventory | resource_id | Resource inventory lookup |
| inventory_transactions | resource_inventory_id | Inventory history |
| resource_allocations | resource_id | Resource allocation lookup |
| resource_allocations | admission_id | Admission resource lookup |
| equipment_allocations | equipment_id | Equipment allocation lookup |
| equipment_allocations | admission_id | Admission equipment lookup |
| equipment_maintenance | equipment_id | Equipment maintenance history |
| allocation_recommendations | admission_id | Admission recommendation lookup |
| recommendation_items | recommendation_id | Recommendation items lookup |
| recommendation_decisions | recommendation_item_id | Decision lookup |
| audit_logs | user_id | User audit lookup |
| login_audit_logs | user_id | User login audit lookup |
| login_audit_logs | username_attempted | Security analysis |
| notifications | recipient_user_id | User notification lookup |
| staff_shifts | ward_id | Ward shift lookup |
| forecast_snapshots | forecast_type | Forecast lookup |

### 4.3 Query-Driven Indexes

| Table | Columns | Index Type | Query Pattern |
|-------|---------|-----------|---------------|
| patients | (full_name) | GIN (trgm) | Fuzzy name search |
| patients | (patient_number) | B-tree | Exact patient number lookup |
| patients | (is_active, created_at) | B-tree | Active patient listing |
| clinical_assessments | (patient_id, assessment_timestamp) | B-tree | Patient assessment timeline |
| clinical_assessments | (admission_id, is_reassessment) | B-tree | Admission reassessment check |
| beds | (ward_id, status) | B-tree | Available beds per ward |
| beds | (status) | B-tree | Status-based bed filtering |
| admissions | (status, admitted_at) | B-tree | Active admission listing |
| admissions | (patient_id, is_active) | B-tree | Patient active admission check |
| bed_cleaning | (status, bed_id) | B-tree | Pending cleaning tasks |
| audit_logs | (timestamp) | B-tree | Time-range audit queries |
| audit_logs | (entity_type, entity_id) | B-tree | Entity-specific audit lookup |
| audit_logs | (action_type, timestamp) | B-tree | Action-type audit filtering |
| login_audit_logs | (username_attempted, timestamp) | B-tree | Brute-force detection |
| notifications | (recipient_user_id, is_read) | B-tree | Unread notification count |
| inventory_transactions | (resource_inventory_id, transaction_timestamp) | B-tree | Inventory history |
| equipment_maintenance | (equipment_id, status) | B-tree | Overdue maintenance lookup |
| recommendations | (status, expires_at) | B-tree | Pending recommendation lookup |

### 4.4 Composite Indexes

| Table | Columns | Purpose |
|-------|---------|---------|
| beds | (ward_id, bed_type, status) | Bed recommendation query optimization |
| staff | (ward_id, availability_status) | Staff recommendation query optimization |
| admissions | (ward_id, status, admitted_at) | Ward occupancy and reporting |
| staff_shifts | (ward_id, shift_date, start_time) | Shift scheduling queries |
| resource_inventory | (resource_id, location) | Location-based stock lookup |
| recommendation_items | (recommendation_id, item_type, rank) | Recommendation item ranking |

---

## 5. Auditing Strategy

### 5.1 Audit Field Standardization

Every business table includes:
- `created_at` (TIMESTAMP, NOT NULL): Set on record creation.
- `updated_at` (TIMESTAMP, NOT NULL): Set on every update.
- `created_by` (UUID, FK → users.id): User who created the record.
- `updated_by` (UUID, FK → users.id): User who last updated the record.

### 5.2 Audit Log Capture

All data modifications (INSERT, UPDATE, DELETE) on business tables are captured in the `audit_logs` table via:
- JPA EntityListeners (`@PrePersist`, `@PreUpdate`) for application-level capture.
- Database triggers as a backup mechanism for direct database modifications.

### 5.3 Audit Log Integrity

- Each audit record includes a SHA-256 hash computed from: `timestamp + user_id + action_type + entity_type + entity_id + before_value + after_value`.
- Hash chain verification: each record's hash includes the previous record's hash, creating a tamper-evident chain.
- The `audit_logs` table has PostgreSQL triggers that prevent UPDATE and DELETE operations.

### 5.4 Audit Retention

- Audit logs are retained for a minimum of 2 years.
- Login audit logs are retained for 90 days.
- Archived audit logs are compressed and stored in long-term storage.
- Audit log queries older than 90 days are routed to an archive-optimized query path.

---

## 6. Soft Deletes

### 6.1 Strategy

All business entities that represent ongoing or historical data use soft deletes:
- `is_active` BOOLEAN column (default `true`).
- Deactivation sets `is_active = false` without removing the record.
- Queries default to filtering `WHERE is_active = true`.
- Soft-deleted records remain visible in audit trails and historical reports.

### 6.2 Entities Using Soft Deletes

| Entity | Column | Rationale |
|--------|--------|-----------|
| Patient | is_active | Preserve patient history for clinical and audit purposes. |
| Admission | is_active | Preserve admission history across transfers and discharges. |
| ResourceSupplier | is_active | Preserve supplier history. |

### 6.3 Entities Without Soft Deletes

| Entity | Rationale |
|--------|-----------|
| User | Deactivation via `status = 'DEACTIVATED'` preserves login history. |
| Bed | Status field tracks lifecycle; no need for soft delete. |
| Ward | `status = 'INACTIVE'` prevents new admissions; history preserved. |
| Audit Log | Append-only; never deleted or modified. |
| Login Audit Log | Append-only; retained for 90 days then purged. |
| Notification | Retained permanently; read status tracks engagement. |
| ClinicalAssessment | Append-only for clinical history integrity. |
| InventoryTransaction | Append-only for financial audit trail. |
| BedCleaning | Workflow records; retained for compliance. |

---

## 7. Future Scaling

### 7.1 Read Replicas

For reporting and dashboard queries that don't require real-time consistency, PostgreSQL read replicas can offload read traffic from the primary database.

### 7.2 Partitioning

The following tables are candidates for table partitioning by month:
- `audit_logs`: High volume, time-range queries.
- `login_audit_logs`: High volume, time-range queries.
- `inventory_transactions`: High volume, time-range queries.
- `forecast_snapshots`: Time-series data.

Partition strategy:
- Automatic partition creation for upcoming months.
- Archival of old partitions to separate tablespaces.
- Query optimization by partition pruning on timestamp ranges.

### 7.3 Connection Pooling

HikariCP connection pooling is configured with:
- Minimum idle connections: 5
- Maximum pool size: 20
- Connection timeout: 30 seconds
- Idle timeout: 10 minutes
- Max lifetime: 30 minutes

### 7.4 Backup Strategy

| Backup Type | Frequency | Retention | Method |
|-------------|-----------|-----------|--------|
| Full Backup | Daily (2:00 AM) | 30 days | pg_dump |
| WAL Archiving | Continuous | 7 days | pg_basebackup + WAL-E |
| Logical Backup | Weekly | 90 days | pg_dump --format=custom |

### 7.5 Performance Monitoring

- Enable `pg_stat_statements` for query performance analysis.
- Monitor slow queries (> 1 second) via application logging.
- Regular VACUUM and ANALYZE on high-update tables.
- Monitor index usage via `pg_stat_user_indexes`.

---

## 8. Document References

| Document | Reference |
|----------|-----------|
| Domain Model | `docs/planning/06-domain-model.md` |
| System Architecture | `docs/planning/03-system-architecture.md` |
| Technology Stack | `docs/planning/05-technology-stack.md` |
| Recommendation Engine Design | `docs/planning/08-recommendation-engine-design.md` |
