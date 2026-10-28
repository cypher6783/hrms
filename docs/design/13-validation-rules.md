# 13 — Validation Rules

## 1. Validation Strategy

- **API Layer**: Jakarta Bean Validation on DTOs (request validation).
- **Service Layer**: Business rule validation (domain validation).
- **Database Layer**: Constraints (NOT NULL, UNIQUE, CHECK, FK).
- **Cross-Field Validation**: Service-layer validation for complex rules.

---

## 2. Entity Validation Rules

### 2.1 User

| Field | Rule | Type | Message |
|-------|------|------|---------|
| username | @NotBlank, @Size(min=3, max=50), @Pattern([a-zA-Z0-9_]+) | API | Username must be 3-50 alphanumeric/underscore characters |
| email | @NotBlank, @Email | API | Valid email required |
| password | @NotBlank, @Size(min=8, max=100), @Pattern(complexity) | API | Password must meet complexity requirements |
| fullName | @NotBlank, @Size(max=100) | API | Full name required |
| role | @NotBlank, @Pattern(enum values) | API | Valid role required |
| status | @Pattern(enum values) | DB | Valid status value |

**Business Rules**:
- Username cannot be changed after creation.
- Account locks after 5 consecutive failed attempts.
- Password cannot match last 5 in history.

### 2.2 Patient

| Field | Rule | Type | Message |
|-------|------|------|---------|
| fullName | @NotBlank, @Size(max=100) | API | Patient name required |
| dateOfBirth | @NotNull, @Past | API | Date of birth must be in the past |
| gender | @NotBlank, @Pattern(MALE\|FEMALE\|OTHER) | API | Valid gender required |
| phoneNumber | @Size(max=20), @Pattern(phone regex) | API | Invalid phone format |
| address | @Size(max=500) | API | Address too long |
| nextOfKinName | @Size(max=100) | API | Name too long |
| nextOfKinPhone | @Size(max=20) | API | Phone too long |
| patientNumber | @Unique | DB | Patient number already exists |

**Business Rules**:
- Patient number auto-generated (not user-provided).
- Only one active admission at a time.

### 2.3 ClinicalAssessment

| Field | Rule | Type | Message |
|-------|------|------|---------|
| patientId | @NotNull | API | Patient required |
| severityLevel | @NotBlank, @Pattern(MILD\|MODERATE\|SEVERE\|CRITICAL) | API | Valid severity required |
| triageClassification | @NotBlank, @Pattern(EMERGENCY\|URGENT\|SEMI_URGENT\|NON_URGENT) | API | Valid triage required |
| infectionStatus | @NotBlank, @Pattern(SUSPECTED\|CONFIRMED\|RULED_OUT) | API | Valid infection status required |
| clinicalNotes | @Size(max=2000) | API | Notes too long |
| assessmentTimestamp | @NotNull | API | Timestamp required |

**Business Rules**:
- Append-only (no updates).
- Severity must be reassessed within 24 hours of admission.
- Most recent assessment = current clinical state.

### 2.4 Admission

| Field | Rule | Type | Message |
|-------|------|------|---------|
| patientId | @NotNull | API | Patient required |
| wardId | @NotNull | API | Ward required |
| status | @Pattern(enum values) | DB | Valid status required |
| admissionNumber | @Unique | DB | Admission number already exists |

**Business Rules**:
- One active admission per patient.
- Status transitions: PENDING → ADMITTED → (TRANSFERRED → ADMITTED) → DISCHARGED.
- Ward must be active.
- Bed must be available (when assigned).

### 2.5 Bed

| Field | Rule | Type | Message |
|-------|------|------|---------|
| bedNumber | @NotBlank, @Size(max=20) | API | Bed number required |
| wardId | @NotNull | API | Ward required |
| bedType | @NotBlank, @Pattern(enum values) | API | Valid bed type required |
| status | @Pattern(enum values) | DB | Valid status required |
| (bedNumber, wardId) | @Unique | DB | Bed number already exists in ward |

**Business Rules**:
- One occupant at a time.
- Confirmed patients require isolation beds.
- Ward capacity cannot be exceeded.

### 2.6 Ward

| Field | Rule | Type | Message |
|-------|------|------|---------|
| name | @NotBlank, @Size(max=50), @Unique | API | Ward name required and unique |
| wardType | @NotBlank, @Pattern(enum values) | API | Valid ward type required |
| maxBedCapacity | @NotNull, @Min(1) | API | Capacity must be positive |
| isolationLevel | @NotBlank, @Pattern(enum values) | API | Valid isolation level required |

**Business Rules**:
- Capacity changes require administrator approval.
- Inactive wards cannot receive admissions.

### 2.7 Resource

| Field | Rule | Type | Message |
|-------|------|------|---------|
| name | @NotBlank, @Size(max=100) | API | Resource name required |
| category | @NotBlank, @Pattern(enum values) | API | Valid category required |
| unitOfMeasure | @NotBlank, @Size(max=20) | API | Unit required |
| minimumThreshold | @Min(0) | API | Threshold non-negative |
| reorderPoint | @Min(0) | API | Reorder point non-negative |
| criticalityLevel | @NotBlank, @Pattern(enum values) | API | Valid criticality required |

### 2.8 ResourceInventory

| Field | Rule | Type | Message |
|-------|------|------|---------|
| resourceId | @NotNull | API | Resource required |
| location | @NotBlank, @Size(max=100) | API | Location required |
| currentStock | @Min(0) | DB | Stock cannot be negative |
| (resourceId, location, batchNumber) | @Unique | DB | Duplicate inventory record |

### 2.9 InventoryTransaction

| Field | Rule | Type | Message |
|-------|------|------|---------|
| resourceInventoryId | @NotNull | API | Inventory record required |
| transactionType | @NotBlank, @Pattern(enum values) | API | Valid transaction type required |
| quantity | @NotNull, @NotZero | API | Quantity cannot be zero |
| performedBy | @NotNull | API | Performer required |

**Business Rules**:
- Append-only (no updates or deletes).
- Transaction must not cause negative stock.
- ISSUE transactions require admission_id.

### 2.10 Equipment

| Field | Rule | Type | Message |
|-------|------|------|---------|
| name | @NotBlank, @Size(max=100) | API | Equipment name required |
| equipmentType | @NotBlank, @Pattern(enum values) | API | Valid type required |
| serialNumber | @NotBlank, @Size(max=50), @Unique | API | Serial number required and unique |
| status | @Pattern(enum values) | DB | Valid status required |

**Business Rules**:
- Isolation equipment requires decontamination before reassignment.
- Out-of-service equipment cannot be assigned.

### 2.11 Staff

| Field | Rule | Type | Message |
|-------|------|------|---------|
| fullName | @NotBlank, @Size(max=100) | API | Staff name required |
| role | @NotBlank, @Pattern(enum values) | API | Valid role required |
| specialization | @Size(max=50) | API | Specialization too long |
| staffNumber | @Unique | DB | Staff number already exists |
| maxWorkloadThreshold | @Min(0) | API | Threshold non-negative |

**Business Rules**:
- Expired certification restricts critical-care assignment.
- Workload score calculated dynamically.

### 2.12 StaffShift

| Field | Rule | Type | Message |
|-------|------|------|---------|
| shiftName | @NotBlank, @Size(max=50) | API | Shift name required |
| shiftDate | @NotNull, @FutureOrPresent | API | Date required |
| startTime | @NotNull | API | Start time required |
| endTime | @NotNull | API | End time required |
| wardId | @NotNull | API | Ward required |
| minRequiredStaff | @NotNull, @Min(1) | API | Minimum staff required |
| maxStaff | @NotNull, @Min(1) | API | Max staff required |

**Business Rules**:
- endTime must be after startTime.
- minRequiredStaff ≤ maxStaff.

### 2.13 ShiftAssignment

| Field | Rule | Type | Message |
|-------|------|------|---------|
| staffId | @NotNull | API | Staff required |
| shiftId | @NotNull | API | Shift required |
| (staffId, shiftId) | @Unique | DB | Duplicate assignment |

**Business Rules**:
- No overlapping shifts for same staff.
- Staff must be available.

### 2.14 EquipmentMaintenance

| Field | Rule | Type | Message |
|-------|------|------|---------|
| equipmentId | @NotNull | API | Equipment required |
| maintenanceType | @NotBlank, @Pattern(enum values) | API | Valid type required |
| scheduledDate | @NotNull, @FutureOrPresent | API | Date required |
| status | @Pattern(enum values) | DB | Valid status required |

**Business Rules**:
- Overdue maintenance restricts equipment assignment.

### 2.15 AllocationRecommendation

| Field | Rule | Type | Message |
|-------|------|------|---------|
| admissionId | @NotNull | API | Admission required |
| batchType | @NotBlank, @Pattern(enum values) | API | Valid batch type required |
| expiresAt | @NotNull, @After(generatedAt) | API | Expiry must be after generation |

### 2.16 RecommendationItem

| Field | Rule | Type | Message |
|-------|------|------|---------|
| recommendationId | @NotNull | API | Recommendation required |
| itemType | @NotBlank, @Pattern(enum values) | API | Valid item type required |
| recommendedEntityId | @NotNull | API | Entity required |
| rank | @NotNull, @Min(1) | API | Rank must be positive |
| confidenceScore | @NotNull, @DecimalMin("0.00"), @DecimalMax("1.00") | API | Score must be 0.00-1.00 |
| rationale | @NotBlank | API | Rationale required |

### 2.17 RecommendationDecision

| Field | Rule | Type | Message |
|-------|------|------|---------|
| recommendationItemId | @NotNull | API | Item required |
| decisionType | @NotBlank, @Pattern(ACCEPTED\|OVERRIDDEN) | API | Valid decision required |
| overrideJustification | @NotBlank(condition: if overridden) | API | Justification required for override |

### 2.18 Notification

| Field | Rule | Type | Message |
|-------|------|------|---------|
| title | @NotBlank, @Size(max=200) | API | Title required |
| message | @NotBlank | API | Message required |
| notificationType | @NotBlank, @Pattern(enum values) | API | Valid type required |
| recipientUserId | @NotNull | API | Recipient required |

### 2.19 SystemConfiguration

| Field | Rule | Type | Message |
|-------|------|------|---------|
| configKey | @NotBlank, @Size(max=100), @Unique | API | Key required and unique |
| configValue | @NotBlank | API | Value required |
| valueType | @NotBlank, @Pattern(STRING\|INTEGER\|DECIMAL\|BOOLEAN\|JSON) | API | Valid type required |
| category | @NotBlank, @Pattern(enum values) | API | Valid category required |

**Business Rules**:
- Value must match valueType (integer value for INTEGER type, etc.).

---

## 3. Cross-Field Validation Rules

| Rule | Entity | Condition | Validation |
|------|--------|-----------|------------|
| One active admission | Admission | Create/Update | Check no active admission for patient |
| Ward capacity | Bed | Assign bed | Check ward bed count < max capacity |
| Bed availability | Bed | Assign bed | Check bed status = AVAILABLE |
| Isolation requirement | Bed | Assign bed | If infection_status = CONFIRMED, bed must be isolation |
| Staff certification | Staff | Assign to critical ward | Check certification_status = VALID and not expired |
| Staff workload | Staff | Assign to admission | Check workload < max threshold |
| Shift overlap | ShiftAssignment | Create | Check no overlapping shifts for staff |
| Stock sufficiency | InventoryTransaction | Create ISSUE | Check current_stock >= quantity |
| Maintenance status | Equipment | Assign | Check no overdue maintenance |
| Cleaning verification | Bed | Assign to patient | Check cleaning task verified |
| Override justification | RecommendationDecision | Override | Check justification provided |

---

## 4. Database Constraints

| Constraint Type | Tables | Rules |
|----------------|--------|-------|
| NOT NULL | All | Required fields enforced |
| UNIQUE | users.username, users.email, patients.patient_number, admissions.admission_number, beds.(bed_number, ward_id), equipment.serial_number, resources.inventory.(resource_id, location, batch_number), shift_assignments.(staff_id, shift_id), system_configurations.config_key | Prevent duplicates |
| CHECK | resource_inventory.current_stock ≥ 0 | Non-negative stock |
| FK | All foreign keys | Referential integrity |
| DEFAULT | created_at = NOW(), updated_at = NOW(), is_active = true, status = 'PENDING' | Default values |

---

## 5. Security Validation

| Rule | Implementation |
|------|----------------|
| Input sanitization | Jakarta Bean Validation + HTML escaping |
| SQL injection prevention | JPA parameterized queries |
| XSS prevention | React JSX escaping + CSP header |
| CSRF prevention | SameSite cookies + state-changing token |
| Rate limiting | Application-level (5 attempts/min per IP) |
| File upload (future) | Type whitelist + size limit |

---

## 6. Document References

| Document | Reference |
|----------|-----------|
| Entity Design | `docs/design/06-entity-design.md` |
| DTO Design | `docs/design/07-dto-design.md` |
| Error Handling | `docs/design/14-error-handling.md` |
