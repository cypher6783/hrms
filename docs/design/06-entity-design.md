# 06 — Entity Design

## 1. User

**Purpose**: System user with authentication credentials and role assignments.

**Fields**: id (UUID PK), username (VARCHAR 50 UNIQUE), email (VARCHAR 100 UNIQUE), password_hash (VARCHAR 255), full_name (VARCHAR 100), role (VARCHAR 30), status (VARCHAR 20), failed_login_attempts (INT), locked_until (TIMESTAMP), last_login_at (TIMESTAMP), created_at, updated_at, created_by, updated_by.

**Relationships**:
- One-to-Many: RefreshToken, PasswordHistory, LoginHistory
- Referenced by: AuditLog, Notification

**Validation**: Username alphanumeric + underscore, 3-50 chars. Email valid format. Password min 8 chars with complexity.

**Lifecycle**: Created by admin → Active → (Locked after 5 failures) → Deactivated.

**Business Rules**: Username immutable after creation. Account locks 15 min after 5 failures.

**Indexes**: username (unique), email (unique), status.

**Soft Delete**: No. Uses status field ('DEACTIVATED').

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 2. RefreshToken

**Purpose**: Persistent refresh token for JWT renewal.

**Fields**: id (UUID PK), token_hash (VARCHAR 64 UNIQUE), user_id (UUID FK), expires_at (TIMESTAMP), revoked (BOOLEAN), created_at.

**Relationships**: Many-to-One: User.

**Validation**: Token hash must be SHA-256. Expiry must be in future.

**Lifecycle**: Created on login → Active → Used (rotated) → Revoked. Expires after 7 days.

**Indexes**: user_id, token_hash, expires_at.

**Soft Delete**: No. Revoked flag used.

**Audit**: created_at only.

---

## 3. PasswordHistory

**Purpose**: Prevent password reuse (last 5 passwords).

**Fields**: id (UUID PK), user_id (UUID FK), password_hash (VARCHAR 255), created_at.

**Relationships**: Many-to-One: User.

**Validation**: Password hash required.

**Lifecycle**: Created on password change. Max 5 per user; oldest deleted.

**Indexes**: user_id.

**Soft Delete**: No. Records retained for policy enforcement.

**Audit**: created_at only.

---

## 4. LoginHistory

**Purpose**: Record all authentication attempts.

**Fields**: id (UUID PK), username_attempted (VARCHAR 50), user_id (UUID FK NULLABLE), success (BOOLEAN), ip_address (VARCHAR 45), user_agent (VARCHAR 500), failure_reason (VARCHAR 100), created_at.

**Relationships**: Many-to-One: User (nullable).

**Validation**: Username required. Success boolean required.

**Lifecycle**: Created on each login attempt. Retained 90 days then purged.

**Indexes**: user_id, username_attempted, created_at.

**Soft Delete**: No. Append-only, time-based retention.

**Audit**: created_at only.

---

## 5. Patient

**Purpose**: Registered patient demographics (no clinical state).

**Fields**: id (UUID PK), patient_number (VARCHAR 20 UNIQUE), full_name (VARCHAR 100), date_of_birth (DATE), gender (VARCHAR 10), phone_number (VARCHAR 20), address (TEXT), next_of_kin_name (VARCHAR 100), next_of_kin_phone (VARCHAR 20), is_active (BOOLEAN), created_at, updated_at, created_by, updated_by.

**Relationships**:
- One-to-Many: ClinicalAssessment, Admission
- Referenced by: Admission, ClinicalAssessment

**Validation**: Full name required. DOB not in future. Gender in allowed values.

**Lifecycle**: Registered → Active → (Multiple Admissions) → Deactivated.

**Business Rules**: Patient number auto-generated. Soft-deleted only.

**Indexes**: patient_number (unique), full_name (GIN trgm), is_active+created_at, phone_number.

**Soft Delete**: Yes (is_active flag).

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 6. ClinicalAssessment

**Purpose**: Encounter-specific clinical observation (severity, triage, infection status).

**Fields**: id (UUID PK), patient_id (UUID FK), admission_id (UUID FK NULLABLE), assessed_by (UUID FK), severity_level (VARCHAR 20), triage_classification (VARCHAR 20), infection_status (VARCHAR 20), clinical_notes (TEXT), is_reassessment (BOOLEAN), assessment_timestamp (TIMESTAMP), created_at.

**Relationships**:
- Many-to-One: Patient, Admission (optional), User (assessed_by)
- Referenced by: CDS Engine (latest assessment)

**Validation**: Severity, triage, infection status in enum values. Timestamp required.

**Lifecycle**: Created on assessment. Append-only (no updates).

**Business Rules**: Severity reassessed within 24h of admission. Most recent = current state.

**Indexes**: patient_id, admission_id, patient_id+assessment_timestamp, admission_id+is_reassessment.

**Soft Delete**: No. Append-only for clinical integrity.

**Audit**: created_at only (immutable record).

---

## 7. Admission

**Purpose**: Patient admission record linking patient to ward and bed.

**Fields**: id (UUID PK), admission_number (VARCHAR 20 UNIQUE), patient_id (UUID FK), ward_id (UUID FK), bed_id (UUID FK NULLABLE), status (VARCHAR 20), admission_notes (TEXT), discharge_outcome (VARCHAR 30), discharge_notes (TEXT), admitted_at (TIMESTAMP), discharged_at (TIMESTAMP), is_active (BOOLEAN), created_at, updated_at, created_by, updated_by.

**Relationships**:
- Many-to-One: Patient, Ward, Bed (optional)
- One-to-Many: ClinicalAssessment, StaffAdmission, ResourceAllocation, EquipmentAllocation, AllocationRecommendation, BedCleaning
- Referenced by: Bed (current_admission_id)

**Validation**: Patient required. Ward required. Status in enum values.

**Lifecycle**: Pending → Admitted → (Transferred → Admitted) → Discharged.

**Business Rules**: One active admission per patient. Transfer creates new record. Discharge triggers cleaning.

**Indexes**: patient_id, ward_id, bed_id, status+admitted_at, patient_id+is_active, ward_id+status+admitted_at.

**Soft Delete**: Yes (is_active flag).

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 8. Bed

**Purpose**: Individual bed within a ward.

**Fields**: id (UUID PK), bed_number (VARCHAR 20), ward_id (UUID FK), bed_type (VARCHAR 40), is_isolation_capable (BOOLEAN), status (VARCHAR 30), current_admission_id (UUID FK NULLABLE), last_maintenance_at (TIMESTAMP), created_at, updated_at, created_by, updated_by.

**Relationships**:
- Many-to-One: Ward, Admission (optional)
- One-to-Many: BedCleaning
- Referenced by: Admission (bed_id)

**Validation**: Bed number unique within ward. Status in enum values.

**Lifecycle**: Available → Occupied → Cleaning Required → Available. Or Under Maintenance.

**Business Rules**: One occupant at a time. Confirmed patients require isolation beds. Reserved beds auto-release.

**Indexes**: ward_id+status, status, ward_id+bed_type+status, current_admission_id.

**Unique Constraint**: (bed_number, ward_id).

**Soft Delete**: No. Status field tracks lifecycle.

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 9. BedCleaning

**Purpose**: Cleaning workflow task for bed sanitation.

**Fields**: id (UUID PK), bed_id (UUID FK), admission_id (UUID FK), status (VARCHAR 20), assigned_to (UUID FK NULLABLE), assigned_at (TIMESTAMP), started_at (TIMESTAMP), completed_at (TIMESTAMP), verified_by (UUID FK NULLABLE), verified_at (TIMESTAMP), cleaning_notes (TEXT), created_at, updated_at.

**Relationships**: Many-to-One: Bed, Admission, Staff (assigned_to), User (verified_by).

**Validation**: Status in workflow states. Timestamps in logical order.

**Lifecycle**: Pending → Assigned → In Progress → Completed → Verified → Bed Available.

**Business Rules**: Created on discharge. Bed status = Cleaning Required. Available only after verification.

**Indexes**: bed_id, admission_id, status+bed_id, assigned_to.

**Soft Delete**: No. Workflow records retained.

**Audit**: created_at, updated_at.

---

## 10. Ward

**Purpose**: Organizational unit containing beds and staff.

**Fields**: id (UUID PK), name (VARCHAR 50 UNIQUE), ward_type (VARCHAR 30), max_bed_capacity (INT), isolation_level (VARCHAR 20), equipment_zone (VARCHAR 50), status (VARCHAR 20), created_at, updated_at, created_by, updated_by.

**Relationships**: One-to-Many: Bed, Staff, StaffShift.

**Validation**: Name unique. Capacity positive. Isolation level in enum values.

**Lifecycle**: Active → (Inactive).

**Business Rules**: Capacity cannot be exceeded. Inactive wards cannot receive admissions.

**Indexes**: name (unique), status.

**Soft Delete**: No. Uses status field ('INACTIVE').

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 11. Resource

**Purpose**: Consumable resource definition and metadata.

**Fields**: id (UUID PK), name (VARCHAR 100), category (VARCHAR 30), unit_of_measure (VARCHAR 20), minimum_threshold (INT), reorder_point (INT), criticality_level (VARCHAR 20), default_supplier_id (UUID FK NULLABLE), created_at, updated_at, created_by, updated_by.

**Relationships**: One-to-Many: ResourceInventory, ResourceAllocation. Many-to-One: ResourceSupplier.

**Validation**: Name required. Category in enum values. Thresholds non-negative.

**Lifecycle**: Created → Active.

**Business Rules**: Falling below threshold triggers notification.

**Indexes**: category, criticality_level, default_supplier_id.

**Soft Delete**: No. Resources are reference data.

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 12. ResourceInventory

**Purpose**: Current stock levels for a resource at a location.

**Fields**: id (UUID PK), resource_id (UUID FK), location (VARCHAR 100), current_stock (INT), expiration_date (DATE NULLABLE), batch_number (VARCHAR 50 NULLABLE), created_at, updated_at.

**Relationships**: Many-to-One: Resource. One-to-Many: InventoryTransaction.

**Validation**: Stock non-negative. Location required.

**Lifecycle**: Created → Stock updated via transactions.

**Business Rules**: FEFO allocation. Independent stock per location.

**Indexes**: resource_id, resource_id+location, current_stock.

**Unique Constraint**: (resource_id, location, batch_number).

**Soft Delete**: No. Stock managed via transactions.

**Audit**: created_at, updated_at.

---

## 13. InventoryTransaction

**Purpose**: Transactional ledger for all stock movements.

**Fields**: id (UUID PK), resource_inventory_id (UUID FK), transaction_type (VARCHAR 20), quantity (INT), admission_id (UUID FK NULLABLE), reference_document (VARCHAR 100), notes (TEXT), performed_by (UUID FK), transaction_timestamp (TIMESTAMP), created_at.

**Relationships**: Many-to-One: ResourceInventory, Admission (optional), User (performed_by).

**Validation**: Quantity non-zero. Transaction type in enum values. Would not cause negative stock.

**Lifecycle**: Created → Immutable.

**Business Rules**: Append-only. Stock = SUM(all quantities). No updates or deletes.

**Indexes**: resource_inventory_id+transaction_timestamp, admission_id, transaction_type.

**Soft Delete**: No. Append-only for audit trail.

**Audit**: created_at only (immutable).

---

## 14. ResourceSupplier

**Purpose**: Supplier information for procurement.

**Fields**: id (UUID PK), name (VARCHAR 100), contact_person (VARCHAR 100), phone_number (VARCHAR 20), email (VARCHAR 100), address (TEXT), lead_time_days (INT), is_active (BOOLEAN), created_at, updated_at, created_by, updated_by.

**Relationships**: One-to-Many: Resource.

**Validation**: Name required. Email valid format if provided.

**Lifecycle**: Active → Inactive.

**Business Rules**: Inactive suppliers cannot be linked to new orders.

**Indexes**: name, is_active.

**Soft Delete**: Yes (is_active flag).

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 15. Equipment

**Purpose**: Medical equipment with status and tracking.

**Fields**: id (UUID PK), name (VARCHAR 100), equipment_type (VARCHAR 50), serial_number (VARCHAR 50 UNIQUE), location (VARCHAR 100), status (VARCHAR 30), assigned_admission_id (UUID FK NULLABLE), assigned_ward_id (UUID FK NULLABLE), created_at, updated_at, created_by, updated_by.

**Relationships**: Many-to-One: Admission (optional), Ward (optional). One-to-Many: EquipmentMaintenance, EquipmentAllocation.

**Validation**: Serial number unique. Status in enum values.

**Lifecycle**: Available → In-Use → Under Maintenance → Out of Service.

**Business Rules**: Isolation equipment needs decontamination before reassignment. Overdue maintenance restricts assignment.

**Indexes**: equipment_type, status, assigned_admission_id, assigned_ward_id, serial_number.

**Soft Delete**: No. Status field tracks lifecycle.

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 16. EquipmentMaintenance

**Purpose**: Maintenance history for equipment.

**Fields**: id (UUID PK), equipment_id (UUID FK), maintenance_type (VARCHAR 20), status (VARCHAR 20), scheduled_date (DATE), completed_date (DATE NULLABLE), performed_by (VARCHAR 100), maintenance_notes (TEXT), cost (DECIMAL 10,2), next_maintenance_date (DATE NULLABLE), created_at, updated_at, created_by, updated_by.

**Relationships**: Many-to-One: Equipment.

**Validation**: Scheduled date required. Status in enum values.

**Lifecycle**: Scheduled → In Progress → Completed/Overdue.

**Business Rules**: Overdue maintenance restricts equipment assignment.

**Indexes**: equipment_id, equipment_id+status, scheduled_date.

**Soft Delete**: No. Maintenance records retained.

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 17. Staff

**Purpose**: Healthcare worker with profile and availability.

**Fields**: id (UUID PK), staff_number (VARCHAR 20 UNIQUE), full_name (VARCHAR 100), role (VARCHAR 30), specialization (VARCHAR 50), certification_status (VARCHAR 20), certification_expiry (DATE), ward_id (UUID FK NULLABLE), max_workload_threshold (DECIMAL 5,2), availability_status (VARCHAR 20), created_at, updated_at, created_by, updated_by.

**Relationships**: Many-to-One: Ward. One-to-Many: StaffAdmission, ShiftAssignment, BedCleaning.

**Validation**: Staff number unique. Role in enum values. Workload threshold positive.

**Lifecycle**: Active → (On Leave, Off Duty).

**Business Rules**: Expired certification restricts critical-care assignment.

**Indexes**: ward_id, role, ward_id+availability_status, staff_number, specialization.

**Soft Delete**: No. Staff are reference data.

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 18. StaffShift

**Purpose**: Shift definition for a ward and date.

**Fields**: id (UUID PK), shift_name (VARCHAR 50), shift_date (DATE), start_time (TIME), end_time (TIME), ward_id (UUID FK), min_required_staff (INT), max_staff (INT), status (VARCHAR 20), created_at, updated_at, created_by, updated_by.

**Relationships**: Many-to-One: Ward. One-to-Many: ShiftAssignment.

**Validation**: End time after start time. Min ≤ Max. Status in enum values.

**Lifecycle**: Scheduled → In Progress → Completed.

**Business Rules**: Staff count cannot fall below minimum.

**Indexes**: ward_id, shift_date, ward_id+shift_date+start_time.

**Soft Delete**: No. Shifts are reference data.

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 19. ShiftAssignment

**Purpose**: Assigns staff to a specific shift.

**Fields**: id (UUID PK), staff_id (UUID FK), shift_id (UUID FK), status (VARCHAR 20), assigned_by (UUID FK), created_at.

**Relationships**: Many-to-One: Staff, StaffShift, User (assigned_by).

**Validation**: No overlapping shifts for same staff. Status in enum values.

**Lifecycle**: Confirmed → Completed/Absent/Swapped.

**Business Rules**: Overlapping shifts prevented. Absent triggers redistribution.

**Unique Constraint**: (staff_id, shift_id).

**Indexes**: staff_id, shift_id.

**Soft Delete**: No. Assignment records retained.

**Audit**: created_at only.

---

## 20. AllocationRecommendation

**Purpose**: A recommendation event containing multiple items.

**Fields**: id (UUID PK), admission_id (UUID FK), batch_type (VARCHAR 30), status (VARCHAR 20), generated_at (TIMESTAMP), expires_at (TIMESTAMP), created_at.

**Relationships**: Many-to-One: Admission. One-to-Many: RecommendationItem.

**Validation**: Batch type in enum values. Expiry after generation.

**Lifecycle**: Generated → Pending → Fully Actioned/Partially Actioned/Expired.

**Business Rules**: Expires after configurable window (default 30 min). Expired triggers re-evaluation.

**Indexes**: admission_id, status+expires_at.

**Soft Delete**: No. Recommendation history retained.

**Audit**: created_at only.

---

## 21. RecommendationItem

**Purpose**: Individual allocation suggestion within a recommendation.

**Fields**: id (UUID PK), recommendation_id (UUID FK), item_type (VARCHAR 20), recommended_entity_type (VARCHAR 50), recommended_entity_id (UUID), rank (INT), confidence_score (DECIMAL 3,2), scoring_breakdown (JSONB), rationale (TEXT), status (VARCHAR 20), created_at.

**Relationships**: Many-to-One: AllocationRecommendation. One-to-One: RecommendationDecision.

**Validation**: Confidence 0.00-1.00. Rank ≥ 1. Status in enum values.

**Lifecycle**: Pending → Accepted/Overridden/Expired.

**Business Rules**: Rank 1 = primary. Below 0.30 not presented.

**Indexes**: recommendation_id, recommendation_id+item_type+rank, status.

**Soft Delete**: No. Item history retained.

**Audit**: created_at only.

---

## 22. RecommendationDecision

**Purpose**: User decision on a recommendation item.

**Fields**: id (UUID PK), recommendation_item_id (UUID FK), decision_type (VARCHAR 20), overridden_entity_type (VARCHAR 50), overridden_entity_id (UUID), override_justification (TEXT), decided_by (UUID FK), decided_at (TIMESTAMP), created_at.

**Relationships**: One-to-One: RecommendationItem. Many-to-One: User (decided_by).

**Validation**: Decision type in enum values. Justification required if overridden.

**Lifecycle**: Created → Immutable.

**Business Rules**: Override requires justification. Only authorized roles can override.

**Indexes**: recommendation_item_id, decision_type.

**Soft Delete**: No. Decision history retained.

**Audit**: created_at only.

---

## 23. AuditLog

**Purpose**: Immutable record of system actions.

**Fields**: id (UUID PK), timestamp (TIMESTAMP), user_id (UUID FK NULLABLE), action_type (VARCHAR 30), entity_type (VARCHAR 50), entity_id (UUID), before_value (JSONB), after_value (JSONB), ip_address (VARCHAR 45), user_agent (VARCHAR 500), integrity_hash (VARCHAR 64).

**Relationships**: Many-to-One: User (nullable).

**Validation**: Action type required. Entity type required. Integrity hash required.

**Lifecycle**: Created → Immutable (append-only).

**Business Rules**: No updates or deletes. Hash chain integrity. Retained 2+ years.

**Indexes**: timestamp, user_id, entity_type+entity_id, action_type+timestamp.

**Soft Delete**: No. Append-only.

**Audit**: Immutable by design.

---

## 24. LoginAuditLog

**Purpose**: Dedicated authentication event audit trail.

**Fields**: id (UUID PK), timestamp (TIMESTAMP), username_attempted (VARCHAR 50), user_id (UUID FK NULLABLE), event_type (VARCHAR 30), ip_address (VARCHAR 45), user_agent (VARCHAR 500), details (JSONB).

**Relationships**: Many-to-One: User (nullable).

**Validation**: Event type in enum values. Username required.

**Lifecycle**: Created → Retained 90 days → Purged.

**Business Rules**: Append-only. Used for brute-force detection.

**Indexes**: username_attempted+timestamp, user_id, event_type.

**Soft Delete**: No. Time-based retention.

**Audit**: Immutable by design.

---

## 25. Notification

**Purpose**: System-generated alert or message.

**Fields**: id (UUID PK), title (VARCHAR 200), message (TEXT), notification_type (VARCHAR 20), source_module (VARCHAR 30), source_entity_type (VARCHAR 50), source_entity_id (UUID), recipient_user_id (UUID FK), is_read (BOOLEAN), read_at (TIMESTAMP), created_at.

**Relationships**: Many-to-One: User (recipient).

**Validation**: Title required. Message required. Type in enum values.

**Lifecycle**: Created → Delivered → Read → Retained.

**Business Rules**: Escalation for unactioned recommendations.

**Indexes**: recipient_user_id, recipient_user_id+is_read, notification_type.

**Soft Delete**: No. Notifications retained permanently.

**Audit**: created_at only.

---

## 26. ForecastSnapshot

**Purpose**: Point-in-time forecast result for tracking.

**Fields**: id (UUID PK), forecast_type (VARCHAR 30), forecast_horizon (VARCHAR 10), target_period_start (DATE), target_period_end (DATE), predicted_values (JSONB), model_used (VARCHAR 30), accuracy_score (DECIMAL 5,2), generated_at (TIMESTAMP), created_at.

**Relationships**: Independent (no foreign keys).

**Validation**: Dates valid. Predicted values JSON required. Model in enum values.

**Lifecycle**: Generated → Accuracy populated later → Retained.

**Business Rules**: Accuracy calculated when actual data available.

**Indexes**: forecast_type, target_period_start+target_period_end.

**Soft Delete**: No. Historical snapshots retained.

**Audit**: created_at only.

---

## 27. SystemConfiguration

**Purpose**: Configurable system parameters.

**Fields**: id (UUID PK), config_key (VARCHAR 100 UNIQUE), config_value (TEXT), value_type (VARCHAR 20), description (TEXT), category (VARCHAR 30), default_value (TEXT), requires_restart (BOOLEAN), created_at, updated_at, created_by, updated_by.

**Relationships**: Independent.

**Validation**: Key unique. Value type matches actual value. Category in enum values.

**Lifecycle**: Created → Updated.

**Business Rules**: Changes logged in audit trail. Some require restart.

**Indexes**: config_key (unique), category.

**Soft Delete**: No. Configuration history retained.

**Audit**: created_at, updated_at, created_by, updated_by.

---

## 28. Document References

| Document | Reference |
|----------|-----------|
| Domain Model | `docs/planning/06-domain-model.md` |
| Database Plan | `docs/planning/07-database-plan.md` |
| Database Design | `docs/design/02-database-design.md` |
