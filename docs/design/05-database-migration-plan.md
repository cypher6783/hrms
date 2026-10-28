# 05 — Database Migration Plan

## 1. Migration Strategy

The system uses **Flyway** for database schema migration management. All schema changes are versioned, tested, and applied in a controlled manner.

### 1.1 Principles

- Every schema change is a versioned migration file.
- Migrations are forward-only (no rollback scripts in production).
- Migrations are tested against a copy of production schema before application.
- Seed data migrations are separated from schema migrations.
- Naming convention: `V{version}__{description}.sql`

### 1.2 Directory Structure

```
src/main/resources/db/migration/
├── V001__create_users_table.sql
├── V002__create_refresh_tokens_table.sql
├── V003__create_password_history_table.sql
├── ...
├── V027__create_system_configurations_table.sql
├── V028__add_indexes.sql
├── V029__add_audit_triggers.sql
└── R__seed_data.sql
```

---

## 2. Flyway Version Plan

### 2.1 Schema Migrations (V001–V030)

| Version | Description | Tables Created |
|---------|-------------|----------------|
| V001 | Authentication tables | users, refresh_tokens, password_history, login_history |
| V002 | Patient tables | patients, clinical_assessments |
| V003 | Facility tables | wards, beds |
| V004 | Admission tables | admissions |
| V005 | Bed cleaning tables | bed_cleaning |
| V006 | Resource tables | resources, resource_inventory, resource_suppliers |
| V007 | Inventory tables | inventory_transactions |
| V008 | Equipment tables | equipment, equipment_maintenance |
| V009 | Staff tables | staff |
| V010 | Shift tables | staff_shifts, shift_assignments |
| V011 | Allocation tables | staff_admissions, resource_allocations, equipment_allocations |
| V012 | Recommendation tables | allocation_recommendations, recommendation_items, recommendation_decisions |
| V013 | Audit tables | audit_logs, login_audit_logs |
| V014 | Notification tables | notifications |
| V015 | Forecast tables | forecast_snapshots |
| V016 | Configuration tables | system_configurations |
| V017 | Foreign key constraints | All FK constraints |
| V018 | Unique constraints | All unique constraints |
| V019 | Primary key defaults | UUID generation defaults |
| V020 | Audit fields | created_at, updated_at, created_by, updated_by on all business tables |
| V021 | Soft delete flags | is_active on patients, admissions, resource_suppliers |
| V022 | Indexes — foreign key | All FK indexes |
| V023 | Indexes — query-driven | Performance indexes |
| V024 | Indexes — composite | Composite indexes |
| V025 | Partial indexes | Unique partial indexes (staff_admissions) |
| V026 | Audit log triggers | Prevent UPDATE/DELETE on audit_logs |
| V027 | Login audit triggers | Prevent UPDATE/DELETE on login_audit_logs |
| V028 | Inventory triggers | Prevent UPDATE/DELETE on inventory_transactions |
| V029 | Clinical assessment triggers | Prevent UPDATE on clinical_assessments |
| V030 | Database functions | UUID generation, audit field auto-update |

### 2.2 Seed Data Migrations (R prefix)

| Version | Description |
|---------|-------------|
| R__001_seed_roles.sql | Default user roles and permissions |
| R__002_seed_admin_user.sql | Default administrator account |
| R__003_seed_wards.sql | Default ward configurations |
| R__004_seed_resources.sql | Default resource categories and types |
| R__005_seed_system_config.sql | Default system configuration parameters |
| R__006_seed_demo_data.sql | Demo data for development environment |

---

## 3. Migration Ordering

Migrations are applied in version order. Within each version, the order is:

1. Table creation (CREATE TABLE)
2. Column additions (ALTER TABLE ADD COLUMN)
3. Constraint additions (ALTER TABLE ADD CONSTRAINT)
4. Index creation (CREATE INDEX)
5. Trigger creation (CREATE TRIGGER)
6. Function creation (CREATE FUNCTION)

### 3.1 Dependency Order

```
V001 (users) ─────────────────────────────────────┐
V002 (patients, clinical_assessments) ────────────┤
V003 (wards, beds) ───────────────────────────────┤
V004 (admissions) ─── depends on patients, wards, beds
V005 (bed_cleaning) ── depends on beds, admissions
V006 (resources, resource_inventory, suppliers) ──┤
V007 (inventory_transactions) ── depends on resource_inventory
V008 (equipment, equipment_maintenance) ──────────┤
V009 (staff) ── depends on wards
V010 (staff_shifts, shift_assignments) ── depends on staff, wards
V011 (staff_admissions, resource_allocations, equipment_allocations) ── depends on admissions
V012 (allocation_recommendations, recommendation_items, recommendation_decisions) ── depends on admissions
V013 (audit_logs, login_audit_logs) ── depends on users
V014 (notifications) ── depends on users
V015 (forecast_snapshots) ── independent
V016 (system_configurations) ── independent
```

---

## 4. Rollback Strategy

### 4.1 Development Environment

- Flyway undo scripts (V{version}__U{version}__{description}.sql) for development only.
- Not deployed to production.

### 4.2 Production Environment

- Forward-only migrations.
- If a migration fails:
  1. Stop application.
  2. Investigate failure.
  3. Fix migration script.
  4. Apply corrected migration.
  5. Resume application.

### 4.3 Emergency Rollback

- If schema change causes critical issues:
  1. Stop application.
  2. Restore database from last backup.
  3. Investigate and fix migration.
  4. Re-apply from restored point.

---

## 5. Seed Data Strategy

### 5.1 Development Environment

- Full seed data including demo patients, admissions, staff, and equipment.
- Created via R__006_seed_demo_data.sql.
- Allows immediate testing without manual data entry.

### 5.2 Staging Environment

- Minimal seed data (roles, default config, sample wards).
- No demo data.

### 5.3 Production Environment

- Only essential seed data (roles, default config, initial ward structure).
- Admin user created via first-time setup wizard.

### 5.4 Seed Data Rules

- Seed data uses UUIDs that are deterministic (fixed values) to avoid conflicts.
- Seed data does not include passwords in plaintext; only bcrypt hashes.
- Seed data is idempotent (can be run multiple times without duplicates).

---

## 6. Future Migrations

### 6.1 Versioning Convention

- Major schema changes increment version by 10 (V040, V050).
- Minor changes increment by 1 (V031, V032).
- Hotfix migrations use suffix (V031_1__hotfix_description.sql).

### 6.2 Migration Examples

| Future Version | Potential Change |
|---------------|------------------|
| V031 | Add patient photo column |
| V032 | Add equipment photo column |
| V040 | Multi-tenant support (add tenant_id to all tables) |
| V050 | Add laboratory results module |
| V060 | Add billing module |

### 6.3 Migration Testing

- All migrations tested against empty database (fresh install).
- All migrations tested against database with previous version (upgrade).
- Migration test automated in CI/CD pipeline.

---

## 7. Document References

| Document | Reference |
|----------|-----------|
| Database Plan | `docs/planning/07-database-plan.md` |
| Database Design | `docs/design/02-database-design.md` |
| Technology Stack | `docs/planning/05-technology-stack.md` |
