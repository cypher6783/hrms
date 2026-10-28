# Operational Workflows – Phase 3

## Admission Lifecycle Workflow

### Standard Admission

```
1. Patient arrives → POST /api/v1/admissions
   ├─ Validate: No active admission for patient
   ├─ Generate admission number (ADM-yyyyMMdd-NNNN)
   ├─ Set status: ADMITTED
   ├─ Set admittedAt timestamp
   └─ If bedId provided:
       ├─ Validate: Bed exists and is AVAILABLE
       ├─ Update bed status → OCCUPIED
       └─ Publish BedAssigned event

2. Patient admitted → GET /api/v1/admissions/{id}
   └─ Admission details returned

3. Clinical care phase
   └─ Patient receives treatment in assigned ward/bed
```

### Transfer Workflow

```
1. Transfer request → PUT /api/v1/admissions/{id}/transfer
   ├─ Validate: Admission is active
   ├─ If current bed exists:
   │   ├─ Update old bed status → CLEANING_REQUIRED
   │   ├─ Create BedCleaning task (PENDING)
   │   ├─ Publish BedReleased event
   │   └─ Publish BedCleaningCreated event
   ├─ Update admission: wardId, bedId
   ├─ If new bed provided:
   │   ├─ Validate: New bed is AVAILABLE
   │   ├─ Update new bed status → OCCUPIED
   │   └─ Publish BedAssigned event
   ├─ Set transferNotes
   └─ Publish AdmissionTransferred event
```

### Discharge Workflow

```
1. Discharge request → PUT /api/v1/admissions/{id}/discharge
   ├─ Validate: Admission is active
   ├─ Set status: DISCHARGED
   ├─ Set dischargeOutcome, dischargeNotes, dischargedAt
   ├─ Set isActive: false
   ├─ If bed exists:
   │   ├─ Update bed status → CLEANING_REQUIRED
   │   ├─ Create BedCleaning task (PENDING)
   │   ├─ Publish BedReleased event
   │   └─ Publish BedCleaningCreated event
   ├─ Admission timestamps are immutable
   └─ Publish AdmissionDischarged event
```

## Bed Cleaning Workflow

### Cleaning Lifecycle

```
1. Task created (automatic on discharge/transfer)
   ├─ Status: PENDING
   └─ Bed status: CLEANING_REQUIRED

2. Staff assignment → POST /api/v1/bed-cleaning/{id}/assign
   ├─ Validate: Status is PENDING
   ├─ Set assignedTo, assignedAt
   ├─ Status: ASSIGNED
   └─ Publish BedCleaningAssigned event

3. Cleaning starts → POST /api/v1/bed-cleaning/{id}/start
   ├─ Validate: Status is ASSIGNED
   ├─ Set startedAt
   ├─ Status: IN_PROGRESS
   └─ Publish BedCleaningStarted event

4. Cleaning completes → POST /api/v1/bed-cleaning/{id}/complete
   ├─ Validate: Status is IN_PROGRESS
   ├─ Set completedAt, cleaningNotes
   ├─ Status: COMPLETED
   └─ Publish BedCleaningCompleted event

5. Cleaning verified → POST /api/v1/bed-cleaning/{id}/verify
   ├─ Validate: Status is COMPLETED
   ├─ Set verifiedBy, verifiedAt
   ├─ Status: VERIFIED
   ├─ Update bed status → AVAILABLE
   └─ Publish BedCleaningVerified event
```

## Staff Workload Workflow

```
1. Calculate workload → GET /api/v1/staff/{id}/workload
   ├─ Count active admissions assigned to staff
   ├─ Apply severity weights:
   │   ├─ CRITICAL: 1.5x
   │   ├─ HIGH: 1.2x
   │   ├─ MODERATE: 1.0x
   │   └─ LOW: 0.8x
   ├─ Add isolation assignment weight
   ├─ Calculate workload percentage vs threshold
   └─ Return StaffWorkloadResponse
```

## Shift Management Workflow

### Create and Staff Shift

```
1. Create shift → POST /api/v1/shifts
   ├─ Validate: No overlapping shift for same ward
   ├─ Set status: SCHEDULED
   └─ Publish ShiftCreated event

2. Assign staff → POST /api/v1/shifts/assign
   ├─ Validate: Staff is ACTIVE and available
   ├─ Validate: No overlapping shift assignment for staff
   ├─ Validate: Shift not at max capacity
   ├─ Create ShiftAssignment (CONFIRMED)
   └─ Publish ShiftAssigned event

3. Check staffing level → GET /api/v1/shifts/{id}/staffing-level
   ├─ Count CONFIRMED assignments
   ├─ Compare to minRequiredStaff
   └─ Return staffing status and deficit
```

### Overlap Prevention

```
For staff member S, check existing assignments:
  FOR EACH existing shift E where E.staffId = S:
    IF E.startTime < newShift.endTime AND E.endTime > newShift.startTime:
      REJECT: "Staff has overlapping shift"
```

## Event Flow Diagram

```
┌─────────────┐    ┌──────────────┐    ┌────────────────┐
│  Admission   │───>│ BedCleaning  │───>│     Bed        │
│  Service     │    │  Service     │    │   Service      │
└──────┬──────┘    └──────────────┘    └────────────────┘
       │
       │ AdmissionCreated
       │ AdmissionTransferred
       │ AdmissionDischarged
       │ BedAssigned
       │ BedReleased
       v
┌──────────────────────────────────────┐
│         Event Publisher              │
│  (Spring ApplicationEventPublisher)  │
└──────────────────────────────────────┘
```
