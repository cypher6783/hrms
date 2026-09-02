# 12 — Workflow Design

## 1. Admission Workflow

### 1.1 Standard Admission

```
Nursing Officer registers patient (if new)
    ↓
Nursing Officer creates admission record (Patient + Ward)
    ↓
System creates pending admission
    ↓
System triggers CDS Engine for bed recommendation
    ↓
Ward Manager reviews recommendation
    ├── Accept → System assigns bed → Admission status = ADMITTED
    └── Override → Ward Manager selects alternative bed → System assigns → ADMITTED
    ↓
Bed status = OCCUPIED
    ↓
Clinical Assessment recorded by Doctor
    ↓
Admission in progress
```

### 1.2 Transfer

```
Ward Manager initiates transfer
    ↓
System creates transfer request (Target Ward + Bed)
    ↓
System triggers CDS Engine for new bed recommendation
    ↓
Ward Manager reviews → Accept/Override
    ↓
Current bed released (status = CLEANING_REQUIRED)
    ↓
BedCleaning task created
    ↓
New admission record created (linked to patient)
    ↓
New bed assigned → Status = OCCUPIED
    ↓
Original admission status = TRANSFERRED
    ↓
Original admission is_active = false
```

### 1.3 Discharge

```
Doctor initiates discharge
    ↓
Doctor records discharge outcome and notes
    ↓
Admission status = DISCHARGED
    ↓
Admission discharged_at = NOW()
    ↓
Bed status = CLEANING_REQUIRED
    ↓
BedCleaning task created (status = PENDING)
    ↓
Admission is_active = false
    ↓
CDS Engine checks for pending admissions needing beds
```

---

## 2. Clinical Assessment Workflow

### 2.1 Initial Assessment

```
Doctor opens assessment form for patient/admission
    ↓
Doctor records severity, triage, infection status, notes
    ↓
System creates assessment record (is_reassessment = false)
    ↓
Assessment linked to patient and admission
    ↓
System updates patient's current clinical state
    ↓
If admission linked → CDS Engine triggered for re-evaluation
    ↓
If infection_status = CONFIRMED → Isolation bed required
    ↓
Reassessment timer started (24 hours)
```

### 2.2 Reassessment

```
Doctor opens reassessment form
    ↓
System pre-fills with previous assessment
    ↓
Doctor updates severity/triage/infection status
    ↓
System creates NEW assessment record (is_reassessment = true)
    ↓
Previous assessment preserved (append-only)
    ↓
CDS Engine triggered for re-evaluation
    ↓
Reassessment timer reset (24 hours)
```

---

## 3. Recommendation Workflow

### 3.1 Recommendation Generation

```
Trigger event (admission, severity change, bed release)
    ↓
CDS Engine gathers context:
    - Latest ClinicalAssessment
    - Available beds (type, isolation)
    - Ward occupancy
    - Equipment availability
    - Staff workload/availability
    - Resource stock levels
    ↓
Hard constraint filtering (eliminate invalid options)
    ↓
Weighted scoring of remaining options
    ↓
Ranking by composite score
    ↓
Confidence score calculation
    ↓
Rationale generation
    ↓
AllocationRecommendation + RecommendationItems created
    ↓
Notification sent to Ward Manager
    ↓
Recommendation expiry timer started (30 minutes)
```

### 3.2 Recommendation Action

```
Ward Manager views recommendation
    ↓
Option A: Accept
    ↓
System creates RecommendationDecision (type = ACCEPTED)
    ↓
RecommendationItem status = ACCEPTED
    ↓
Allocation executed (bed/staff/equipment/resource assigned)
    ↓
Audit log recorded

Option B: Override
    ↓
Ward Manager selects alternative
    ↓
Ward Manager provides mandatory justification
    ↓
System creates RecommendationDecision (type = OVERRIDDEN)
    ↓
RecommendationItem status = OVERRIDDEN
    ↓
Alternative allocation executed
    ↓
Audit log recorded

Option C: No action (timeout)
    ↓
Recommendation expires after 30 minutes
    ↓
RecommendationItem status = EXPIRED
    ↓
Re-evaluation triggered
    ↓
Escalation notification sent
```

---

## 4. Bed Cleaning Workflow

### 4.1 Cleaning Process

```
Patient discharged
    ↓
BedCleaning task created (status = PENDING)
    ↓
Bed status = CLEANING_REQUIRED
    ↓
Ward Manager assigns cleaner
    ↓
BedCleaning status = ASSIGNED
    ↓
BedCleaning.assigned_at = NOW()
    ↓
Cleaner starts cleaning
    ↓
BedCleaning status = IN_PROGRESS
    ↓
BedCleaning.started_at = NOW()
    ↓
Cleaner completes cleaning
    ↓
BedCleaning status = COMPLETED
    ↓
BedCleaning.completed_at = NOW()
    ↓
Ward Manager verifies cleaning
    ↓
BedCleaning status = VERIFIED
    ↓
BedCleaning.verified_by = user
    ↓
BedCleaning.verified_at = NOW()
    ↓
Bed status = AVAILABLE
    ↓
CDS Engine checks for pending admissions
```

### 4.2 Isolation Bed Cleaning (2-Hour Target)

```
Discharge from isolation bed
    ↓
BedCleaning task created
    ↓
Timer started (2 hours)
    ↓
If not completed within 2 hours:
    ↓
Escalation notification to Ward Manager
    ↓
Bed remains unavailable until verified
```

---

## 5. Equipment Workflow

### 5.1 Equipment Assignment

```
CDS Engine recommends equipment for admission
    ↓
Ward Manager accepts recommendation
    ↓
Equipment.status = IN_USE
    ↓
Equipment.assigned_admission_id = admission
    ↓
EquipmentAllocation record created
    ↓
Audit log recorded
```

### 5.2 Equipment Release

```
Patient discharged or transfer
    ↓
EquipmentAllocation.released_at = NOW()
    ↓
Equipment.status = AVAILABLE
    ↓
Equipment.assigned_admission_id = NULL
    ↓
Audit log recorded
```

### 5.3 Equipment Maintenance

```
Equipment Officer schedules maintenance
    ↓
EquipmentMaintenance record created (status = SCHEDULED)
    ↓
Equipment.status = UNDER_MAINTENANCE
    ↓
Equipment cannot be assigned to new patients
    ↓
Maintenance completed
    ↓
EquipmentMaintenance.status = COMPLETED
    ↓
EquipmentMaintenance.completed_date = NOW()
    ↓
Equipment.status = AVAILABLE
    ↓
Next maintenance date calculated
    ↓
Audit log recorded
```

---

## 6. Inventory Workflow

### 6.1 Stock Receipt (Purchase)

```
Resource Manager records purchase
    ↓
InventoryTransaction created:
    - transaction_type = PURCHASE
    - quantity = +N (positive)
    ↓
ResourceInventory.current_stock updated
    ↓
Stock level = SUM(all transactions)
    ↓
Audit log recorded
```

### 6.2 Stock Issue (Patient Allocation)

```
Nursing Officer issues resources to patient
    ↓
InventoryTransaction created:
    - transaction_type = ISSUE
    - quantity = -N (negative)
    - admission_id = linked admission
    ↓
ResourceInventory.current_stock updated
    ↓
If stock < minimum_threshold:
    ↓
Low-stock notification to Resource Manager
    ↓
If critical resource AND stock < threshold:
    ↓
Escalation notification to Administrator
```

### 6.3 Stock Return

```
Unused resources returned
    ↓
InventoryTransaction created:
    - transaction_type = RETURN
    - quantity = +N (positive)
    - admission_id = original admission
    ↓
ResourceInventory.current_stock updated
```

---

## 7. Notification Workflow

### 7.1 Notification Triggering

```
Event occurs in any module
    ↓
Module calls NotificationApplicationService.sendNotification()
    ↓
Notification record created
    ↓
In-app notification delivered (real-time via WebSocket or polling)
    ↓
If notification type = ALERT or ESCALATION:
    ↓
Email notification sent (if configured)
```

### 7.2 Notification Types

| Type | Trigger | Recipient |
|------|---------|-----------|
| INFO | Admission created, discharge completed | Ward Manager |
| WARNING | Low stock, overdue maintenance | Resource/Equipment Manager |
| ALERT | Critical stock, bed critical occupancy | Ward Manager, Administrator |
| ESCALATION | Recommendation expired, cleaning overdue | Administrator, Ward Manager |

---

## 8. Reporting Workflow

### 8.1 Report Generation

```
User requests report
    ↓
User selects report type and parameters
    ↓
System validates parameters
    ↓
ReportApplicationService queries data
    ↓
Report data compiled
    ↓
If format = JSON → Return JSON response
If format = PDF → Generate PDF file → Return file
If format = CSV → Generate CSV file → Return file
```

### 8.2 Scheduled Reports (Future)

```
Scheduled task runs daily/weekly
    ↓
ReportApplicationService generates report
    ↓
Report saved to storage
    ↓
Email notification sent to Administrator with report link
```

---

## 9. Failure Scenarios

### 9.1 Bed Assignment Failure

```
Ward Manager attempts bed assignment
    ↓
System checks bed availability
    ↓
If bed no longer available (race condition):
    ↓
Return error: "Bed is no longer available"
    ↓
System re-triggers CDS Engine for new recommendation
```

### 9.2 Inventory Transaction Failure

```
Resource Manager records transaction
    ↓
System validates stock level
    ↓
If transaction would cause negative stock:
    ↓
Return error: "Insufficient stock. Available: N"
    ↓
Transaction not created
```

### 9.3 Shift Assignment Failure

```
Ward Manager assigns staff to shift
    ↓
System checks for overlapping shifts
    ↓
If overlap detected:
    ↓
Return error: "Staff member has overlapping shift"
    ↓
Assignment not created
```

### 9.4 Recommendation Timeout

```
Recommendation generated
    ↓
30 minutes pass without action
    ↓
Recommendation status = EXPIRED
    ↓
All items status = EXPIRED
    ↓
Re-evaluation triggered
    ↓
New recommendation generated
    ↓
Escalation notification sent
```

### 9.5 Cleaning Timeout (Isolation Beds)

```
Isolation bed discharged
    ↓
2 hours pass without verification
    ↓
Escalation notification to Ward Manager
    ↓
Bed remains CLEANING_REQUIRED
    ↓
Bed unavailable for assignment until verified
```

---

## 10. Document References

| Document | Reference |
|----------|-----------|
| Domain Model | `docs/planning/06-domain-model.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Recommendation Engine Design | `docs/planning/08-recommendation-engine-design.md` |
| Service Design | `docs/design/09-service-design.md` |
