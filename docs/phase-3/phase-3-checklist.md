# Phase 3 Implementation Checklist

## Module 1: Admission

### Entity
- [x] Admission entity exists with UUID PK
- [x] Audit fields (createdAt, updatedAt, createdBy, updatedBy)
- [x] Status field with lifecycle states
- [x] Soft delete via isActive flag
- [x] Admission number unique constraint

### Repository
- [x] JpaRepository with UUID
- [x] findByAdmissionNumber
- [x] findByPatientIdAndIsActiveTrue
- [x] findByWardIdAndIsActiveTrue
- [x] countActiveAdmissions
- [ ] Pagination and sorting support
- [ ] Search specification

### DTOs
- [x] AdmissionRequest (create)
- [x] AdmissionResponse
- [x] DischargeRequest
- [ ] TransferRequest
- [ ] AdmissionSearchRequest
- [ ] AdmissionSummaryResponse

### Mapper
- [ ] AdmissionMapper (MapStruct)

### Application Service
- [x] createAdmission
- [x] getAdmission
- [x] getActiveAdmissionByPatient
- [x] transferAdmission
- [x] dischargeAdmission
- [x] getActiveAdmissionCount
- [ ] searchAdmissions (paginated)
- [ ] getAdmissionsByWard
- [ ] getAdmissionStats

### Domain Service
- [ ] AdmissionDomainService
  - [ ] validateAdmission
  - [ ] validateTransfer
  - [ ] validateDischarge
  - [ ] checkActiveAdmission

### Controller
- [x] POST /api/v1/admissions
- [x] GET /api/v1/admissions/{id}
- [x] GET /api/v1/admissions/patient/{patientId}/active
- [x] POST /api/v1/admissions/{id}/transfer
- [x] POST /api/v1/admissions/{id}/discharge
- [ ] GET /api/v1/admissions (search)
- [ ] GET /api/v1/admissions/ward/{wardId}
- [ ] GET /api/v1/admissions/stats

---

## Module 2: Bed Cleaning

### Entity
- [x] BedCleaning entity with UUID PK
- [x] Audit fields
- [x] Status field with lifecycle states

### Repository
- [x] JpaRepository with UUID
- [x] findByStatus
- [x] findByBedIdAndStatus
- [x] findByAssignedTo
- [ ] Pagination and sorting support
- [ ] Search specification

### DTOs
- [x] CleaningAssignmentRequest
- [x] CleaningCompletionRequest
- [x] CleaningTaskResponse
- [ ] CleaningSearchRequest
- [ ] CleaningTaskSummaryResponse

### Mapper
- [ ] BedCleaningMapper (MapStruct)

### Application Service
- [x] getPendingTasks
- [x] getTasksByBed
- [x] assignTask
- [x] startCleaning
- [x] completeCleaning
- [x] verifyCleaning
- [x] getPendingCleaningCount
- [ ] searchTasks (paginated)
- [ ] getCleaningStats

### Domain Service
- [ ] BedCleaningDomainService
  - [ ] validateStatusTransition
  - [ ] validateAssignment
  - [ ] validateVerification

### Controller
- [x] GET /api/v1/bed-cleaning/pending
- [x] GET /api/v1/bed-cleaning/bed/{bedId}
- [x] POST /api/v1/bed-cleaning/{id}/assign
- [x] POST /api/v1/bed-cleaning/{id}/start
- [x] POST /api/v1/bed-cleaning/{id}/complete
- [x] POST /api/v1/bed-cleaning/{id}/verify
- [ ] GET /api/v1/bed-cleaning (search)
- [ ] GET /api/v1/bed-cleaning/stats

---

## Module 3: Staff

### Entity
- [x] Staff entity with UUID PK
- [x] Audit fields
- [x] Availability status
- [x] Certification tracking

### Repository
- [x] JpaRepository with UUID
- [x] findByWardIdAndAvailabilityStatus
- [x] countByAvailabilityStatus
- [ ] Pagination and sorting support
- [ ] Search specification

### DTOs
- [x] StaffRequest
- [x] StaffResponse
- [ ] StaffSearchRequest
- [ ] StaffSummaryResponse
- [ ] StaffWorkloadResponse

### Mapper
- [ ] StaffMapper (MapStruct)

### Application Service
- [x] createStaff
- [x] getStaff
- [x] getAllStaff
- [x] getStaffByWard
- [x] updateStaff
- [x] getActiveStaffCount
- [ ] searchStaff (paginated)
- [ ] getStaffStats

### Domain Service
- [ ] WorkloadCalculator
  - [ ] calculateWorkload
  - [ ] calculateWorkloadPercentage
  - [ ] isOverloaded

### Controller
- [x] POST /api/v1/staff
- [x] GET /api/v1/staff/{id}
- [x] GET /api/v1/staff
- [x] GET /api/v1/staff/ward/{wardId}
- [x] PUT /api/v1/staff/{id}
- [ ] GET /api/v1/staff/{id}/workload
- [ ] GET /api/v1/staff/stats

---

## Module 4: Shift Management

### Entities
- [x] StaffShift entity with UUID PK
- [x] ShiftAssignment entity with UUID PK
- [x] Audit fields
- [x] Unique constraint on (staff_id, shift_id)

### Repositories
- [x] StaffShiftRepository
- [x] ShiftAssignmentRepository
- [ ] Pagination support
- [ ] Search specification

### DTOs
- [x] ShiftRequest
- [x] ShiftResponse
- [x] ShiftAssignmentRequest
- [x] ShiftAssignmentResponse
- [ ] ShiftSearchRequest
- [ ] ShiftSummaryResponse
- [ ] StaffingLevelResponse

### Mappers
- [ ] ShiftMapper (MapStruct)
- [ ] ShiftAssignmentMapper (MapStruct)

### Application Service
- [x] createShift
- [x] getShift
- [x] getShiftsByWardAndDate
- [x] assignStaff
- [x] getShiftAssignments
- [ ] searchShifts (paginated)
- [ ] removeAssignment
- [ ] getStaffingLevel
- [ ] getShiftCalendar

### Domain Service
- [ ] ShiftDomainService
  - [ ] validateOverlap
  - [ ] validateStaffAvailability
  - [ ] calculateStaffingLevel
  - [ ] validateShiftCreation

### Controller
- [x] POST /api/v1/shifts
- [x] GET /api/v1/shifts/{id}
- [x] GET /api/v1/shifts/ward/{wardId}/date/{date}
- [x] POST /api/v1/shifts/assign
- [x] GET /api/v1/shifts/{shiftId}/assignments
- [ ] GET /api/v1/shifts (search)
- [ ] DELETE /api/v1/shifts/assignments/{id}
- [ ] GET /api/v1/shifts/{shiftId}/staffing-level
- [ ] GET /api/v1/shifts/calendar

---

## Cross-Cutting

### Domain Events
- [ ] AdmissionCreated
- [ ] AdmissionTransferred
- [ ] AdmissionDischarged
- [ ] BedAssigned
- [ ] BedReleased
- [ ] BedCleaningCreated
- [ ] BedCleaningStarted
- [ ] BedCleaningCompleted
- [ ] BedCleaningVerified
- [ ] StaffAssigned
- [ ] ShiftAssigned

### Testing
- [ ] Repository tests for each module
- [ ] Service tests for each module
- [ ] Controller tests for each module
- [ ] Workflow integration tests
- [ ] Acceptance tests
