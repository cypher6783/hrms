# 06 — Domain Model

## 1. Business Objects

The domain model defines the core business entities, their attributes, relationships, and behavioral rules. The model follows Domain-Driven Design (DDD) principles to ensure the software accurately represents the hospital resource management domain.

### 1.1 Entity Inventory

| Entity | Aggregate Root | Description |
|--------|---------------|-------------|
| User | Yes | System user with authentication credentials and role assignments. |
| RefreshToken | No | Persistent refresh token for JWT token renewal. |
| PasswordHistory | No | Record of previous passwords to enforce password reuse policies. |
| LoginHistory | No | Record of all login attempts (successful and failed). |
| Patient | Yes | Registered patient with demographics only (no clinical state). |
| ClinicalAssessment | Yes | Encounter-specific clinical observation (severity, triage, infection status). |
| Admission | Yes | Patient admission record linking patient to ward and bed. |
| Bed | Yes | Individual bed with type, status, and location. |
| BedCleaning | Yes | Cleaning workflow task for bed sanitation. |
| Ward | Yes | Organizational unit containing beds and staff. |
| Resource | Yes | Consumable resource definition and metadata. |
| ResourceInventory | Yes | Current stock levels for a resource (may vary by location). |
| InventoryTransaction | Yes | Transactional ledger for all stock movements. |
| ResourceSupplier | Yes | Supplier information for procurement. |
| Equipment | Yes | Medical equipment with status and maintenance tracking. |
| EquipmentMaintenance | Yes | Maintenance record for equipment service history. |
| Staff | Yes | Healthcare worker with role and specialization. |
| StaffShift | Yes | Shift definition (date, start time, end time). |
| ShiftAssignment | No | Assignment of staff to specific shifts. |
| AllocationRecommendation | Yes | A recommendation event containing one or more items. |
| RecommendationItem | No | Individual allocation suggestion within a recommendation. |
| RecommendationDecision | No | User decision (accept/override) on a recommendation item. |
| AuditLog | Yes | Immutable record of system actions. |
| LoginAuditLog | No | Dedicated audit trail for authentication events. |
| Notification | Yes | System-generated alert or message. |
| ForecastSnapshot | Yes | Point-in-time forecast result for historical tracking. |
| SystemConfiguration | Yes | Configurable system parameters. |

---

## 2. Entity Responsibilities

### 2.1 User

**Description**: Represents a system user who interacts with the platform. Users are created and managed by administrators and authenticate to access the system.

**Key Attributes**:
- Unique identifier (UUID)
- Username (unique, immutable)
- Email address (unique)
- Password hash (bcrypt)
- Full name
- Role (Administrator, Ward Manager, Nursing Officer, Resource Manager, Equipment Officer, Medical Doctor, Dashboard Viewer)
- Account status (active, locked, deactivated)
- Failed login attempt counter
- Last login timestamp
- Created and updated timestamps

**Behavioral Rules**:
- Username cannot be changed after creation.
- Password must meet complexity requirements before save.
- Account locks after 5 consecutive failed login attempts.
- Deactivated accounts cannot authenticate.
- Password changes are logged in audit trail.

### 2.2 RefreshToken

**Description**: Stores persistent refresh tokens for JWT token renewal without requiring re-authentication.

**Key Attributes**:
- Unique identifier (UUID)
- Token hash (SHA-256 of the actual token)
- Linked user (foreign key)
- Expiry timestamp
- Revoked flag
- Created timestamp

**Behavioral Rules**:
- Refresh tokens expire after 7 days.
- Revoked tokens cannot be used for renewal.
- Each login generates a new refresh token; previous tokens are revoked.
- Token hash is stored, never the plaintext token.

### 2.3 PasswordHistory

**Description**: Stores previous password hashes to prevent password reuse.

**Key Attributes**:
- Unique identifier (UUID)
- Linked user (foreign key)
- Password hash
- Created timestamp

**Behavioral Rules**:
- Last 5 password hashes are retained.
- New password cannot match any of the last 5 hashes.
- Oldest entry is removed when history exceeds 5 entries.

### 2.4 LoginHistory

**Description**: Records all authentication attempts for security monitoring and audit.

**Key Attributes**:
- Unique identifier (UUID)
- Linked user (foreign key, nullable for unknown usernames)
- Username attempted
- Login timestamp
- Success flag
- IP address
- User agent
- Failure reason (nullable)

**Behavioral Rules**:
- Both successful and failed attempts are recorded.
- Retained for 90 days.
- Used for account lockout decisions and security analysis.

### 2.5 Patient

**Description**: Represents a registered patient within the Lassa Fever Unit. Contains only demographic and registration data. Clinical state (severity, triage, infection status) is captured in ClinicalAssessment records, which are encounter-specific.

**Key Attributes**:
- Unique identifier (UUID)
- Patient number (human-readable, auto-generated)
- Full name
- Date of birth
- Gender
- Phone number
- Address
- Next-of-kin name and contact
- Registration timestamp
- Active flag (soft delete)

**Behavioral Rules**:
- Patient number is auto-generated and cannot be duplicated.
- Patient records are soft-deleted, never physically removed.
- Clinical state is always accessed through ClinicalAssessment records, not directly on Patient.

### 2.6 ClinicalAssessment

**Description**: An encounter-specific clinical observation recorded by a clinician. Each assessment captures the patient's severity, triage classification, and infection status at a specific point in time. A patient may have multiple assessments across admissions and even within a single admission.

**Key Attributes**:
- Unique identifier (UUID)
- Linked patient (foreign key)
- Linked admission (foreign key, nullable — initial assessments may occur before admission)
- Assessed by user (foreign key)
- Severity level (Mild, Moderate, Severe, Critical)
- Triage classification (Emergency, Urgent, Semi-Urgent, Non-Urgent)
- Infection status (Suspected, Confirmed, Ruled-Out)
- Clinical notes
- Assessment timestamp
- Is reassessment flag (whether this is a follow-up assessment)
- Created timestamp

**Behavioral Rules**:
- Severity must be reassessed within 24 hours of admission.
- Infection status of "Confirmed" triggers isolation bed requirement.
- All assessments are versioned in audit trail.
- The most recent assessment for an active admission represents the patient's current clinical state.
- Triage classification is typically assigned at initial assessment and may be updated.

### 2.7 Admission

**Description**: Represents a patient's admission to the Lassa Fever Unit. Each admission links a patient to a ward and bed for a defined period. Clinical state is referenced through ClinicalAssessment records linked to this admission.

**Key Attributes**:
- Unique identifier (UUID)
- Admission number (auto-generated)
- Linked patient (foreign key)
- Assigned ward (foreign key)
- Assigned bed (foreign key)
- Admission status (Pending, Admitted, Transferred, Discharged)
- Admission timestamp
- Discharge timestamp
- Discharge outcome (Recovered, Referred, Deceased, Against Medical Advice)
- Discharge notes
- Admission notes
- Length of stay (calculated)
- Active flag

**Behavioral Rules**:
- An admission must be linked to a registered patient.
- A patient can have only one active admission at a time.
- Bed assignment occurs during admission or shortly after.
- Discharge releases the bed for sanitation workflow.
- Transfer creates a new admission record and releases the previous bed.
- Admission status transitions follow a defined workflow.

### 2.8 Bed

**Description**: Represents an individual bed within a ward. Beds are the primary unit of occupancy tracking.

**Key Attributes**:
- Unique identifier (UUID)
- Bed number (unique within ward)
- Linked ward (foreign key)
- Bed type (General, Isolation-Positive-Pressure, Isolation-Negative-Pressure, ICU)
- Isolation capability (boolean)
- Status (Available, Occupied, Reserved, Under Maintenance, Cleaning Required)
- Current admission (foreign key, nullable)
- Last maintenance timestamp
- Created and updated timestamps

**Behavioral Rules**:
- A bed can be assigned to only one patient at a time.
- Isolation beds are mandatory for confirmed Lassa fever patients.
- Bed status transitions are managed through the BedCleaning workflow (see Section 2.9).
- Reserved beds auto-release after configurable timeout.
- Under-maintenance beds cannot be assigned.

### 2.9 BedCleaning

**Description**: Represents a cleaning workflow task for bed sanitation. This entity tracks the full lifecycle from cleaning assignment to completion, replacing the simple timestamp approach with a real workflow.

**Key Attributes**:
- Unique identifier (UUID)
- Linked bed (foreign key)
- Linked admission (foreign key — the admission that triggered the cleaning)
- Status (Pending, Assigned, In Progress, Completed, Verified)
- Assigned cleaner (foreign key to Staff, nullable)
- Assigned at timestamp
- Started at timestamp
- Completed at timestamp
- Verified by user (foreign key, nullable)
- Verified at timestamp
- Cleaning notes
- Created timestamp

**Behavioral Rules**:
- A new BedCleaning record is created when a patient is discharged.
- Bed status transitions to "Cleaning Required" when BedCleaning is created.
- Bed status transitions to "Available" only when Cleaning is completed AND verified.
- Only verified beds can be reassigned to new patients.
- Cleaning must be completed within 2 hours of discharge for isolation beds.

### 2.10 Ward

**Description**: Represents an organizational unit within the Lassa Fever Unit. Wards contain beds and staff.

**Key Attributes**:
- Unique identifier (UUID)
- Ward name (unique)
- Ward type (General, Isolation, ICU, Step-Down)
- Maximum bed capacity
- Current bed count (calculated)
- Isolation level (None, Contact, Droplet, Airborne)
- Equipment zone designation
- Status (Active, Inactive)
- Created and updated timestamps

**Behavioral Rules**:
- Ward capacity cannot be exceeded by active bed assignments.
- Isolation level dictates PPE requirements for staff entering the ward.
- Inactive wards cannot receive new admissions.
- Capacity changes require administrator approval.

### 2.11 Resource

**Description**: Defines a consumable resource type. Actual stock levels are tracked in ResourceInventory, and all movements are recorded in InventoryTransaction.

**Key Attributes**:
- Unique identifier (UUID)
- Resource name
- Resource category (PPE, Medication, IV Fluid, Laboratory, Sanitization, Other)
- Unit of measure
- Minimum threshold (triggers low-stock alert)
- Reorder point (triggers reorder recommendation)
- Criticality level (Critical, High, Medium, Low)
- Default supplier (foreign key to ResourceSupplier, nullable)
- Created and updated timestamps

**Behavioral Rules**:
- Resource definitions are managed by administrators.
- Falling below minimum threshold triggers notification.
- Critical resources trigger escalation notifications.

### 2.12 ResourceInventory

**Description**: Tracks current stock levels for a resource, potentially at different locations (e.g., central store, ward store).

**Key Attributes**:
- Unique identifier (UUID)
- Linked resource (foreign key)
- Location (e.g., "Central Store", "Ward A Store")
- Current stock quantity
- Expiration date (nullable)
- Batch number (nullable)
- Created and updated timestamps

**Behavioral Rules**:
- Stock quantity cannot go below zero.
- Expiration tracking supports FEFO (First Expired, First Out) allocation.
- Each location maintains independent stock levels.

### 2.13 InventoryTransaction

**Description**: Transactional ledger recording every stock movement. This provides a complete audit trail for inventory changes.

**Key Attributes**:
- Unique identifier (UUID)
- Linked resource inventory (foreign key)
- Transaction type (Purchase, Issue, Return, Adjustment, Transfer, Disposal)
- Quantity (positive for inbound, negative for outbound)
- Linked admission (foreign key, nullable — for issues to patients)
- Reference document (e.g., purchase order number, return receipt)
- Notes
- Performed by user (foreign key)
- Transaction timestamp
- Created timestamp

**Behavioral Rules**:
- Every stock change MUST be recorded as a transaction.
- Transactions are append-only; no updates or deletes permitted.
- Current stock in ResourceInventory is calculated as: SUM(all transaction quantities).
- Negative stock is prevented by validation before transaction creation.
- Returns are recorded as positive-quantity transactions linked to the original admission.

### 2.14 ResourceSupplier

**Description**: Stores supplier information for procurement and reorder processes.

**Key Attributes**:
- Unique identifier (UUID)
- Supplier name
- Contact person
- Phone number
- Email address
- Address
- Lead time days (average delivery time)
- Active flag
- Created and updated timestamps

**Behavioral Rules**:
- Suppliers are managed by Resource Managers.
- Inactive suppliers cannot be linked to new resource orders.

### 2.15 Equipment

**Description**: Represents medical equipment tracked by the system. Equipment includes ventilators, monitors, oxygen concentrators, and specialized Lassa fever treatment devices.

**Key Attributes**:
- Unique identifier (UUID)
- Equipment name
- Equipment type (Ventilator, Monitor, Oxygen Concentrator, Infusion Pump, Other)
- Serial number (unique)
- Current location (ward or standalone)
- Status (Available, In-Use, Under Maintenance, Out of Service)
- Assigned admission (foreign key, nullable)
- Assigned ward (foreign key, nullable)
- Created and updated timestamps

**Behavioral Rules**:
- Equipment assigned to isolation patients cannot be reassigned without decontamination.
- Overdue maintenance triggers notification and may restrict assignment.
- Out-of-service equipment cannot be assigned.
- Equipment status changes are logged in audit trail.

### 2.16 EquipmentMaintenance

**Description**: Records maintenance history for equipment, including scheduled and unscheduled maintenance events.

**Key Attributes**:
- Unique identifier (UUID)
- Linked equipment (foreign key)
- Maintenance type (Scheduled, Unscheduled, Repair, Calibration)
- Status (Scheduled, In Progress, Completed, Overdue)
- Scheduled date
- Completed date (nullable)
- Performed by (external vendor or internal staff)
- Maintenance notes
- Cost (nullable)
- Next maintenance date
- Created and updated timestamps

**Behavioral Rules**:
- Overdue maintenance triggers notification to Equipment Officer.
- Equipment with overdue maintenance cannot be assigned to new patients.
- Maintenance records are retained for equipment lifecycle.
- Completed maintenance updates the equipment's last_maintenance_at timestamp.

### 2.17 Staff

**Description**: Represents a healthcare worker within the Lassa Fever Unit. Staff records support workload tracking and assignment optimization.

**Key Attributes**:
- Unique identifier (UUID)
- Staff number (auto-generated)
- Full name
- Role (Doctor, Nurse, Lab Technician, Pharmacist, Support Staff)
- Specialization (Infectious Disease, Critical Care, General, Laboratory, Pharmacy)
- Certification status and expiry date
- Assigned ward (foreign key, nullable)
- Maximum workload threshold
- Availability status (Available, On Leave, Off Duty)
- Created and updated timestamps

**Behavioral Rules**:
- Staff with expired certifications cannot be assigned to critical-care wards.
- Availability status affects recommendation engine scoring.

### 2.18 StaffShift

**Description**: Defines a shift period (e.g., Morning 7AM-3PM, Afternoon 3PM-11PM, Night 11PM-7AM) for a specific date.

**Key Attributes**:
- Unique identifier (UUID)
- Shift name (e.g., "Morning Shift")
- Shift date
- Start time
- End time
- Ward (foreign key)
- Minimum required staff count
- Maximum staff count
- Status (Scheduled, In Progress, Completed)
- Created and updated timestamps

**Behavioral Rules**:
- Shifts are created by Ward Managers.
- Staff count cannot fall below minimum required during a shift.

### 2.19 ShiftAssignment

**Description**: Assigns a staff member to a specific shift.

**Key Attributes**:
- Unique identifier (UUID)
- Linked staff (foreign key)
- Linked staff shift (foreign key)
- Status (Confirmed, Completed, Absent, Swapped)
- Assigned by user (foreign key)
- Created timestamp

**Behavioral Rules**:
- A staff member cannot be assigned to overlapping shifts.
- Absent staff trigger workload redistribution alerts.
- Swap requests require Ward Manager approval.

### 2.20 AllocationRecommendation

**Description**: Represents a recommendation event generated by the Rule-Based Clinical Decision Support Engine. Each event may contain multiple recommendation items for different resource types (bed, staff, equipment, resource).

**Key Attributes**:
- Unique identifier (UUID)
- Linked admission (foreign key)
- Recommendation batch type (New Admission, Severity Change, Periodic Refresh)
- Status (Pending, Fully Actioned, Partially Actioned, Expired)
- Generated timestamp
- Expiry timestamp
- Created timestamp

**Behavioral Rules**:
- A recommendation event expires after a configurable time window (default 30 minutes).
- Expired recommendations trigger re-evaluation.
- All items within a recommendation event are presented together.

### 2.21 RecommendationItem

**Description**: An individual allocation suggestion within a recommendation event. Each item recommends a specific resource (bed, staff, equipment, or consumable).

**Key Attributes**:
- Unique identifier (UUID)
- Linked allocation recommendation (foreign key)
- Item type (Bed, Staff, Equipment, Resource)
- Recommended entity type (table name)
- Recommended entity ID (foreign key to the recommended entity)
- Rank (1 = primary recommendation, 2+ = alternatives)
- Confidence score (0.00 to 1.00)
- Scoring breakdown (JSON — factor scores and weights)
- Rationale (human-readable explanation)
- Status (Pending, Accepted, Overridden, Expired)
- Created timestamp

**Behavioral Rules**:
- Rank 1 is the primary recommendation; others are alternatives.
- Confidence score below 0.30 is not presented to users.
- Scoring breakdown is stored for audit and engine tuning.

### 2.22 RecommendationDecision

**Description**: Records the user's decision on a recommendation item (accept or override).

**Key Attributes**:
- Unique identifier (UUID)
- Linked recommendation item (foreign key)
- Decision type (Accepted, Overridden)
- Overridden entity type (nullable — the alternative entity selected)
- Overridden entity ID (nullable)
- Override justification (required if decision type is Overridden)
- Decision made by user (foreign key)
- Decision timestamp
- Created timestamp

**Behavioral Rules**:
- Override justification is mandatory when decision is Overridden.
- Only Ward Manager, Medical Doctor, or Administrator roles may override.
- Decision records are used for engine performance analysis.

### 2.23 AuditLog

**Description**: Represents an immutable record of a system action. Audit logs provide a complete traceability trail for all operations.

**Key Attributes**:
- Unique identifier (UUID)
- Timestamp
- User (foreign key)
- Action type (Create, Read, Update, Delete, Login, Logout, Override, etc.)
- Entity type (Patient, Admission, Bed, etc.)
- Entity identifier
- Before value (JSON, nullable)
- After value (JSON, nullable)
- IP address
- User agent

**Behavioral Rules**:
- Audit records are append-only; no updates or deletes are permitted.
- Audit records include cryptographic hash for integrity verification.
- Audit logs are retained for a minimum of 2 years.
- Only Administrators can query audit logs.

### 2.24 LoginAuditLog

**Description**: Dedicated audit trail for authentication events, separate from general audit logs for security monitoring efficiency.

**Key Attributes**:
- Unique identifier (UUID)
- Timestamp
- Username attempted
- User ID (nullable — for failed attempts with unknown usernames)
- Event type (LOGIN_SUCCESS, LOGIN_FAILURE, LOGOUT, PASSWORD_CHANGE, PASSWORD_RESET, ACCOUNT_LOCKED, ACCOUNT_UNLOCKED)
- IP address
- User agent
- Details (JSON — additional context)

**Behavioral Rules**:
- Append-only; no modifications permitted.
- Retained for 90 days for security analysis.
- Used for brute-force detection and account lockout decisions.

### 2.25 Notification

**Description**: Represents a system-generated notification delivered to one or more users.

**Key Attributes**:
- Unique identifier (UUID)
- Title
- Message body
- Notification type (Info, Warning, Alert, Escalation)
- Source module (Bed, Resource, Equipment, etc.)
- Source entity (foreign key, nullable)
- Recipient user (foreign key)
- Read status (boolean)
- Read timestamp (nullable)
- Created timestamp

**Behavioral Rules**:
- Notifications are delivered via in-app and optionally email.
- Escalation notifications are sent when recommendations are not actioned within timeframe.
- Read notifications are retained but marked as read.

### 2.26 ForecastSnapshot

**Description**: Stores point-in-time forecast results for historical tracking and accuracy analysis.

**Key Attributes**:
- Unique identifier (UUID)
- Forecast type (Admission Demand, Bed Demand, Resource Demand, Staff Demand)
- Forecast horizon (7-day, 14-day, 30-day)
- Target period start date
- Target period end date
- Predicted values (JSON — structured forecast data)
- Model used (Moving Average, Linear Regression)
- Generated timestamp
- Accuracy score (nullable — populated after actual data is available)

**Behavioral Rules**:
- Forecasts are generated nightly and on-demand.
- Historical snapshots enable accuracy analysis.
- Accuracy is calculated when actual data becomes available for the forecast period.

### 2.27 SystemConfiguration

**Description**: Stores configurable system parameters that can be adjusted by administrators without code changes.

**Key Attributes**:
- Unique identifier (UUID)
- Configuration key (unique)
- Configuration value (text)
- Value type (STRING, INTEGER, DECIMAL, BOOLEAN, JSON)
- Description
- Category (Recommendation, Notification, Security, Workflow)
- Default value
- Created and updated timestamps

**Behavioral Rules**:
- Configuration changes are logged in audit trail.
- Invalid values are rejected with validation error.
- Some configurations require system restart to take effect (noted in description).

---

## 3. Relationships

### 3.1 Relationship Summary

| Relationship | Cardinality | Description |
|-------------|-------------|-------------|
| User → RefreshToken | One-to-Many | Each user can have multiple refresh tokens. |
| User → PasswordHistory | One-to-Many | Each user has password history entries. |
| User → LoginHistory | One-to-Many | Each user has login attempt records. |
| Patient → ClinicalAssessment | One-to-Many | A patient can have multiple clinical assessments over time. |
| Patient → Admission | One-to-Many | A patient can have multiple admissions over time. |
| Admission → ClinicalAssessment | One-to-Many | An admission can have multiple clinical assessments during the stay. |
| Admission → Patient | Many-to-One | Each admission belongs to one patient. |
| Admission → Ward | Many-to-One | Each admission is assigned to one ward. |
| Admission → Bed | One-to-One | Each admission occupies one bed (at a time). |
| Bed → Ward | Many-to-One | Each bed belongs to one ward. |
| Ward → Bed | One-to-Many | A ward contains multiple beds. |
| Bed → BedCleaning | One-to-Many | A bed can have multiple cleaning records over time. |
| Staff → Ward | Many-to-One | Staff are assigned to one ward. |
| Ward → Staff | One-to-Many | A ward has multiple staff members. |
| Admission → Staff | Many-to-Many | Multiple staff can be assigned to an admission; staff can have multiple admissions. |
| Admission → Equipment | One-to-Many | An admission can have multiple equipment assignments. |
| Equipment → Admission | Many-to-One | Equipment is assigned to one admission at a time. |
| Resource → ResourceInventory | One-to-Many | A resource can have inventory at multiple locations. |
| ResourceInventory → InventoryTransaction | One-to-Many | Each inventory location has multiple transactions. |
| Resource → ResourceSupplier | Many-to-One | A resource has a default supplier. |
| AllocationRecommendation → Admission | Many-to-One | A recommendation event is for one admission. |
| RecommendationItem → AllocationRecommendation | Many-to-One | A recommendation event contains multiple items. |
| RecommendationDecision → RecommendationItem | One-to-One | Each recommendation item has one decision. |
| AuditLog → User | Many-to-One | Each audit entry is attributed to one user. |
| LoginAuditLog → User | Many-to-One | Each login audit entry is attributed to one user. |
| Notification → User | Many-to-One | Each notification is delivered to one user. |
| StaffShift → Ward | Many-to-One | Shifts are defined per ward. |
| ShiftAssignment → Staff | Many-to-One | Staff are assigned to shifts. |
| ShiftAssignment → StaffShift | Many-to-One | Shifts have multiple staff assignments. |
| EquipmentMaintenance → Equipment | Many-to-One | Equipment has multiple maintenance records. |
| ForecastSnapshot | Independent | Stored forecast results. |
| SystemConfiguration | Independent | System-wide configuration. |

### 3.2 Entity Relationship Diagram Description

```
User (1) ──────── (N) RefreshToken
User (1) ──────── (N) PasswordHistory
User (1) ──────── (N) LoginHistory
User (1) ──────── (N) AuditLog
User (1) ──────── (N) LoginAuditLog
User (1) ──────── (N) Notification

Patient (1) ────── (N) ClinicalAssessment
Patient (1) ────── (N) Admission
Admission (1) ──── (N) ClinicalAssessment
Admission (N) ──── (1) Ward
Admission (1) ──── (1) Bed
Admission (N) ──── (M) Staff
Admission (N) ──── (M) Equipment
Admission (1) ──── (N) AllocationRecommendation

Bed (N) ────────── (1) Ward
Bed (1) ────────── (N) BedCleaning

Ward (1) ──────── (N) Bed
Ward (1) ──────── (N) Staff
Ward (1) ──────── (N) StaffShift

Resource (1) ──── (N) ResourceInventory
Resource (N) ──── (1) ResourceSupplier
ResourceInventory (1) ── (N) InventoryTransaction

Staff (1) ─────── (N) ShiftAssignment
StaffShift (1) ── (N) ShiftAssignment

Equipment (1) ──── (N) EquipmentMaintenance

AllocationRecommendation (1) ── (N) RecommendationItem
RecommendationItem (1) ── (1) RecommendationDecision
```

---

## 4. Aggregates

### 4.1 Patient Aggregate

**Aggregate Root**: Patient
**Members**: Patient, ClinicalAssessment (collection)
**Boundary**: Patient demographic data and their clinical assessments.
**Rules**: Patient records persist across admissions; clinical assessments are encounter-specific.

### 4.2 Admission Aggregate

**Aggregate Root**: Admission
**Members**: Admission, ClinicalAssessment (collection for this admission)
**Boundary**: The admission lifecycle from creation to discharge.
**Rules**: Admission links patient to ward and bed; status transitions are managed within this aggregate.

### 4.3 Bed Aggregate

**Aggregate Root**: Bed
**Members**: Bed, BedCleaning (collection)
**Boundary**: Individual bed lifecycle and cleaning workflow.
**Rules**: Bed status is managed through the cleaning workflow; assignment is exclusive.

### 4.4 Ward Aggregate

**Aggregate Root**: Ward
**Members**: Ward, Bed (collection)
**Boundary**: Ward configuration and its contained beds.
**Rules**: Ward capacity is enforced as the sum of its beds' status.

### 4.5 Resource Aggregate

**Aggregate Root**: Resource
**Members**: Resource, ResourceInventory (collection)
**Boundary**: Consumable resource definition and inventory.
**Rules**: Stock levels are managed through InventoryTransaction records.

### 4.6 Equipment Aggregate

**Aggregate Root**: Equipment
**Members**: Equipment, EquipmentMaintenance (collection)
**Boundary**: Equipment lifecycle and maintenance history.
**Rules**: Equipment status and assignment are managed atomically.

### 4.7 Staff Aggregate

**Aggregate Root**: Staff
**Members**: Staff
**Boundary**: Staff profile, availability, and assignment.
**Rules**: Staff workload is calculated from shift assignments and patient assignments.

### 4.8 Recommendation Aggregate

**Aggregate Root**: AllocationRecommendation
**Members**: AllocationRecommendation, RecommendationItem (collection), RecommendationDecision (collection)
**Boundary**: Recommendation lifecycle from generation to user decision.
**Rules**: Recommendations are generated, presented, and actioned (accepted/overridden/expired).

---

## 5. Value Objects

| Value Object | Attributes | Immutability |
|-------------|------------|--------------|
| SeverityLevel | level (Mild, Moderate, Severe, Critical) | Yes |
| TriageClassification | level (Emergency, Urgent, Semi-Urgent, Non-Urgent) | Yes |
| InfectionStatus | status (Suspected, Confirmed, Ruled-Out) | Yes |
| BedType | type (General, Isolation-Positive-Pressure, Isolation-Negative-Pressure, ICU) | Yes |
| IsolationLevel | level (None, Contact, Droplet, Airborne) | Yes |
| BedStatus | status (Available, Occupied, Reserved, Under-Maintenance, Cleaning-Required) | Yes |
| AdmissionStatus | status (Pending, Admitted, Transferred, Discharged) | Yes |
| DischargeOutcome | outcome (Recovered, Referred, Deceased, Against-Medical-Advice) | Yes |
| EquipmentStatus | status (Available, In-Use, Under-Maintenance, Out-of-Service) | Yes |
| StaffAvailability | status (Available, On-Leave, Off-Duty) | Yes |
| ResourceCategory | category (PPE, Medication, IV-Fluid, Laboratory, Sanitization, Other) | Yes |
| CriticalityLevel | level (Critical, High, Medium, Low) | Yes |
| NotificationType | type (Info, Warning, Alert, Escalation) | Yes |
| ConfidenceScore | score (0.00 to 1.00) | Yes |
| InventoryTransactionType | type (Purchase, Issue, Return, Adjustment, Transfer, Disposal) | Yes |
| CleaningStatus | status (Pending, Assigned, In-Progress, Completed, Verified) | Yes |

---

## 6. Enumerations

### 6.1 UserRole

| Value | Description | Key Permissions |
|-------|-------------|-----------------|
| ADMINISTRATOR | System administrator | Full system access, user management, configuration |
| WARD_MANAGER | Ward operations manager | Ward management, recommendation review, override authority |
| NURSING_OFFICER | Nursing staff | Patient registration, triage, admission processing |
| RESOURCE_MANAGER | Inventory manager | Resource management, stock monitoring |
| EQUIPMENT_OFFICER | Equipment custodian | Equipment management, maintenance tracking |
| MEDICAL_DOCTOR | Physician | Clinical assessment, severity classification |
| DASHBOARD_VIEWER | Read-only viewer | Dashboard and report viewing only |

### 6.2 PatientSeverity

| Value | Description | Typical Resource Implication |
|-------|-------------|------------------------------|
| MILD | Minimal symptoms, low risk | General bed, standard monitoring |
| MODERATE | Significant symptoms, moderate risk | General bed, enhanced monitoring, basic equipment |
| SEVERE | Serious condition, high risk | Isolation bed, priority equipment, increased staffing |
| CRITICAL | Life-threatening, immediate intervention required | ICU bed, maximum equipment, dedicated staffing |

### 6.3 TriageClassification

| Value | Description | Response Time Requirement |
|-------|-------------|---------------------------|
| EMERGENCY | Immediate life-threatening condition | Within 5 minutes |
| URGENT | Serious condition requiring prompt attention | Within 15 minutes |
| SEMI_URGENT | Condition requiring timely but not immediate care | Within 1 hour |
| NON_URGENT | Condition allowing delayed care | Within 4 hours |

### 6.4 InfectionStatus

| Value | Description | Isolation Requirement |
|-------|-------------|----------------------|
| SUSPECTED | Lassa fever suspected, awaiting confirmation | Precautionary isolation |
| CONFIRMED | Laboratory-confirmed Lassa fever | Mandatory isolation bed |
| RULED_OUT | Lassa fever ruled out by laboratory testing | Standard bed allocation |

---

## 7. Lifecycle

### 7.1 Patient Lifecycle

```
Registration → Active → [Multiple Admissions] → Deactivated
```

1. **Registration**: Patient demographics recorded.
2. **Active**: Patient is eligible for admission.
3. **Admission(s)**: Patient may have multiple admissions over time.
4. **Deactivated**: Patient record soft-deleted (no longer active).

### 7.2 Clinical Assessment Lifecycle

```
Initial Assessment → [Reassessments] → Discharge Assessment
```

1. **Initial Assessment**: Severity, triage, and infection status recorded at admission.
2. **Reassessments**: Periodic reassessments during the stay (required within 24 hours).
3. **Discharge Assessment**: Final assessment before discharge.

### 7.3 Admission Lifecycle

```
Pending → Admitted → [Transferred → Admitted] → Discharged
```

1. **Pending**: Admission record created, awaiting bed assignment.
2. **Admitted**: Patient assigned to bed and ward; care in progress.
3. **Transferred**: Patient moved to different ward/bed (new admission record).
4. **Discharged**: Patient discharged; bed released for cleaning workflow.

### 7.4 Bed Lifecycle

```
Available → Occupied → Cleaning Required → Available
                ↓
         Under Maintenance → Available
                ↓
          Out of Service (permanent)
```

1. **Available**: Bed is ready for patient assignment.
2. **Occupied**: Bed is assigned to a patient.
3. **Cleaning Required**: Patient discharged; BedCleaning record created.
4. **Under Maintenance**: Bed is undergoing maintenance or repair.
5. **Out of Service**: Bed is permanently retired.

### 7.5 Bed Cleaning Workflow

```
Discharge → Pending → Assigned → In Progress → Completed → Verified → Available
```

1. **Pending**: Cleaning task created; bed status set to "Cleaning Required".
2. **Assigned**: Cleaner staff member assigned to the task.
3. **In Progress**: Cleaning is underway.
4. **Completed**: Cleaning finished; awaiting verification.
5. **Verified**: Supervisor verifies cleaning quality; bed status set to "Available".

### 7.6 Recommendation Lifecycle

```
Generated → Pending → [Fully Actioned | Partially Actioned | Expired]
```

1. **Generated**: Recommendation event created with multiple items.
2. **Pending**: Awaiting user action on individual items.
3. **Fully Actioned**: All recommendation items have been accepted or overridden.
4. **Partially Actioned**: Some items actioned, some expired.
5. **Expired**: Recommendation not actioned within timeout; re-evaluation triggered.

---

## 8. Workload Calculation Formula

Staff workload is calculated using a weighted formula based on multiple factors:

### 8.1 Formula

```
Workload Score = Σ (Patient Factor × Severity Weight × Time Factor)
```

Where:

**Patient Factor** (per assigned patient):
- Mild patient: 1.0 points
- Moderate patient: 1.5 points
- Severe patient: 2.5 points
- Critical patient: 4.0 points
- Isolation patient: +1.0 point (additional for PPE requirements)
- ICU patient: +1.5 points (additional for intensive monitoring)

**Severity Weight**:
- Ward Manager review: 1.0 (base)
- Night shift: 1.2 (higher weight due to reduced staffing)
- Weekend/Holiday: 1.1 (higher weight due to reduced support)

**Time Factor**:
- Per 8-hour shift period
- Cumulative across shifts in a 24-hour period

### 8.2 Maximum Thresholds

| Role | Maximum Workload Score | Alert Threshold |
|------|----------------------|-----------------|
| Nurse | 12.0 | 10.0 (80%) |
| Doctor | 15.0 | 12.0 (80%) |
| Lab Technician | 10.0 | 8.0 (80%) |
| Support Staff | 8.0 | 6.0 (80%) |

### 8.3 Recalculation Triggers

Workload score is recalculated when:
- A new patient is assigned to the staff member.
- A patient is discharged or transferred.
- A patient's severity level changes.
- A shift assignment changes.
- The staff member starts or ends a shift.

---

## 9. Document References

| Document | Reference |
|----------|-----------|
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Database Plan | `docs/planning/07-database-plan.md` |
| Recommendation Engine Design | `docs/planning/08-recommendation-engine-design.md` |
