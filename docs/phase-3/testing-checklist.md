# Testing Checklist – Phase 3

## Repository Tests

### AdmissionRepository
- [ ] Find by admission number
- [ ] Find active admission by patient
- [ ] Find admissions by ward
- [ ] Count active admissions
- [ ] Pagination and sorting

### BedCleaningRepository
- [ ] Find by status
- [ ] Find by bed and status
- [ ] Find by assigned staff
- [ ] Find pending tasks
- [ ] Pagination and sorting

### StaffRepository
- [ ] Find by ward and availability
- [ ] Find by role
- [ ] Count by availability status
- [ ] Pagination and sorting

### StaffShiftRepository
- [ ] Find by ward and date
- [ ] Find by date
- [ ] Pagination and sorting

### ShiftAssignmentRepository
- [ ] Find by staff
- [ ] Find by shift
- [ ] Count by shift
- [ ] Check exists by staff and shift
- [ ] Find overlapping assignments

---

## Service Tests

### AdmissionApplicationService
- [ ] Create admission – success
- [ ] Create admission – duplicate active admission
- [ ] Get admission – success
- [ ] Get admission – not found
- [ ] Get active admission by patient
- [ ] Transfer admission – success
- [ ] Transfer admission – invalid bed
- [ ] Discharge admission – success
- [ ] Discharge admission – not active
- [ ] Get active admission count

### BedCleaningApplicationService
- [ ] Get pending tasks
- [ ] Assign task – success
- [ ] Assign task – invalid status
- [ ] Start cleaning – success
- [ ] Complete cleaning – success
- [ ] Verify cleaning – bed becomes available
- [ ] Get pending cleaning count

### StaffApplicationService
- [ ] Create staff – success
- [ ] Get staff – success
- [ ] Get staff – not found
- [ ] Get all staff – paginated
- [ ] Get staff by ward
- [ ] Update staff – success
- [ ] Get active staff count

### WorkloadCalculator
- [ ] Calculate workload – no admissions
- [ ] Calculate workload – with admissions
- [ ] Calculate workload – with severity factors
- [ ] Calculate workload percentage
- [ ] Check if overloaded

### ShiftApplicationService
- [ ] Create shift – success
- [ ] Get shift – success
- [ ] Get shift – not found
- [ ] Get shifts by ward and date
- [ ] Assign staff – success
- [ ] Assign staff – duplicate assignment
- [ ] Assign staff – shift at capacity
- [ ] Get shift assignments

### ShiftDomainService
- [ ] Validate overlap – no overlap
- [ ] Validate overlap – has overlap
- [ ] Validate staff availability – active
- [ ] Validate staff availability – inactive
- [ ] Calculate staffing level – fully staffed
- [ ] Calculate staffing level – understaffed

---

## Controller Tests

### AdmissionController
- [ ] POST /api/v1/admissions – 201 Created
- [ ] POST /api/v1/admissions – 409 Conflict
- [ ] GET /api/v1/admissions/{id} – 200 OK
- [ ] GET /api/v1/admissions/{id} – 404 Not Found
- [ ] GET /api/v1/admissions/patient/{id}/active – 200 OK
- [ ] POST /api/v1/admissions/{id}/transfer – 200 OK
- [ ] POST /api/v1/admissions/{id}/discharge – 200 OK

### BedCleaningController
- [ ] GET /api/v1/bed-cleaning/pending – 200 OK
- [ ] POST /api/v1/bed-cleaning/{id}/assign – 200 OK
- [ ] POST /api/v1/bed-cleaning/{id}/start – 200 OK
- [ ] POST /api/v1/bed-cleaning/{id}/complete – 200 OK
- [ ] POST /api/v1/bed-cleaning/{id}/verify – 200 OK

### StaffController
- [ ] POST /api/v1/staff – 201 Created
- [ ] GET /api/v1/staff/{id} – 200 OK
- [ ] GET /api/v1/staff/{id} – 404 Not Found
- [ ] GET /api/v1/staff – 200 OK (paginated)
- [ ] PUT /api/v1/staff/{id} – 200 OK

### ShiftController
- [ ] POST /api/v1/shifts – 201 Created
- [ ] GET /api/v1/shifts/{id} – 200 OK
- [ ] POST /api/v1/shifts/assign – 200 OK
- [ ] POST /api/v1/shifts/assign – 409 Conflict

---

## Workflow Integration Tests

### Admission → Bed → Cleaning Workflow
- [ ] Full admission lifecycle (admit → discharge → cleaning → available)
- [ ] Transfer workflow (admit → transfer → old bed cleaning → new bed occupied)
- [ ] Multiple admissions for different patients

### Shift Management Workflow
- [ ] Create shift → assign staff → check staffing level
- [ ] Overlap prevention across shifts
- [ ] Staff availability validation

---

## Acceptance Tests

### E2E Admission Flow
- [ ] Patient admitted to ward with bed
- [ ] Patient transferred to different ward
- [ ] Patient discharged
- [ ] Bed cleaned and becomes available

### E2E Staff Management Flow
- [ ] Staff created and assigned to ward
- [ ] Workload calculated correctly
- [ ] Staff assigned to shifts
- [ ] Overlapping shift prevented
