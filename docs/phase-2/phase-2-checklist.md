# Phase 2 Checklist

## Pre-Implementation

- [ ] Phase 1 foundation verified and complete
- [ ] All planning documents reviewed
- [ ] Design documents reviewed
- [ ] Package structure confirmed

## Module 1: Patient

### Entity
- [ ] Patient entity with JPA annotations
- [ ] Audit fields (createdAt, updatedAt, createdBy, updatedBy)
- [ ] Soft delete flag (isActive)
- [ ] UUID primary key with Hibernate generator
- [ ] Lifecycle hooks (@PrePersist, @PreUpdate)

### Repository
- [ ] PatientRepository extends JpaRepository
- [ ] findByPatientNumber query
- [ ] existsByPatientNumber query
- [ ] searchPatients JPQL query
- [ ] countByIsActiveTrue query

### DTOs
- [ ] PatientRequest with validation
- [ ] PatientResponse record
- [ ] PatientSearchRequest record
- [ ] PatientSummaryResponse record

### Mapper
- [ ] PatientMapper interface (MapStruct)
- [ ] toEntity method
- [ ] toResponse method
- [ ] toSummary method
- [ ] toResponseList method

### Service
- [ ] PatientApplicationService
- [ ] createPatient operation
- [ ] getPatient operation
- [ ] getPatientByNumber operation
- [ ] searchPatients operation
- [ ] updatePatient operation
- [ ] deactivatePatient operation
- [ ] getActivePatientCount operation

### Controller
- [ ] PatientController with REST endpoints
- [ ] POST /api/v1/patients
- [ ] GET /api/v1/patients/{id}
- [ ] GET /api/v1/patients/number/{patientNumber}
- [ ] GET /api/v1/patients
- [ ] PUT /api/v1/patients/{id}
- [ ] DELETE /api/v1/patients/{id}
- [ ] ApiResponse<T> wrapping

### Testing
- [ ] Repository tests
- [ ] Service tests
- [ ] Controller tests

---

## Module 2: Clinical Assessment

### Entity
- [ ] ClinicalAssessment entity
- [ ] Append-only design (no updatedAt)
- [ ] Audit fields (createdAt)
- [ ] UUID primary key
- [ ] isReassessment flag

### Repository
- [ ] ClinicalAssessmentRepository
- [ ] findByPatientIdOrderByAssessmentTimestampDesc
- [ ] findByAdmissionIdOrderByAssessmentTimestampDesc
- [ ] findTopByAdmissionIdOrderByAssessmentTimestampDesc

### DTOs
- [ ] ClinicalAssessmentRequest
- [ ] ClinicalAssessmentResponse
- [ ] ClinicalAssessmentSummaryResponse

### Mapper
- [ ] ClinicalAssessmentMapper interface
- [ ] toEntity method
- [ ] toResponse method
- [ ] toSummary method

### Service
- [ ] ClinicalAssessmentApplicationService
- [ ] createAssessment (with isReassessment logic)
- [ ] getPatientTimeline
- [ ] getAdmissionTimeline
- [ ] getLatestByAdmission
- [ ] NO update operations (append-only)

### Controller
- [ ] ClinicalAssessmentController
- [ ] POST /api/v1/assessments
- [ ] GET /api/v1/assessments/patient/{patientId}
- [ ] GET /api/v1/assessments/admission/{admissionId}
- [ ] GET /api/v1/assessments/admission/{admissionId}/latest
- [ ] NO PUT or DELETE endpoints

### Testing
- [ ] Repository tests
- [ ] Service tests
- [ ] Controller tests

---

## Module 3: Ward

### Entity
- [ ] Ward entity
- [ ] Audit fields
- [ ] UUID primary key
- [ ] isActive() helper method

### Repository
- [ ] WardRepository
- [ ] findByStatus query
- [ ] findByIsActiveTrue query

### DTOs
- [ ] WardRequest
- [ ] WardResponse
- [ ] WardStatusResponse (with capacity info)

### Mapper
- [ ] WardMapper interface
- [ ] toEntity method
- [ ] toResponse method
- [ ] toStatusResponse method (with capacity parameters)

### Service
- [ ] WardApplicationService
- [ ] createWard
- [ ] getWard
- [ ] getAllActiveWards
- [ ] updateWard
- [ ] deactivateWard
- [ ] getActiveWardCount
- [ ] getWardStatus (with capacity)

### Domain Service
- [ ] WardDomainService
- [ ] Capacity calculation
- [ ] Occupancy rate calculation
- [ ] Active bed count

### Controller
- [ ] WardController
- [ ] POST /api/v1/wards
- [ ] GET /api/v1/wards/{id}
- [ ] GET /api/v1/wards
- [ ] GET /api/v1/wards/{id}/status
- [ ] PUT /api/v1/wards/{id}
- [ ] DELETE /api/v1/wards/{id}

### Testing
- [ ] Repository tests
- [ ] Service tests
- [ ] Controller tests

---

## Module 4: Bed

### Entity
- [ ] Bed entity
- [ ] Audit fields
- [ ] UUID primary key
- [ ] isAvailable() helper method
- [ ] Unique constraint on (bed_number, ward_id)

### Repository
- [ ] BedRepository
- [ ] findByWardIdAndStatus
- [ ] countByWardIdAndStatus
- [ ] countAvailableByWardId (JPQL)
- [ ] countOccupiedByWardId (JPQL)
- [ ] findByStatusAndIsIsolationCapable
- [ ] findByWardId
- [ ] findByBedType

### DTOs
- [ ] BedRequest
- [ ] BedResponse
- [ ] BedAvailabilityResponse
- [ ] BedFilterRequest

### Mapper
- [ ] BedMapper interface
- [ ] toEntity method
- [ ] toResponse method
- [ ] toAvailabilityResponse method
- [ ] toResponseList method

### Service
- [ ] BedApplicationService
- [ ] createBed
- [ ] getBed
- [ ] getBedsByWard
- [ ] getAvailableIsolationBeds
- [ ] getBedAvailability
- [ ] updateBed
- [ ] updateBedStatus
- [ ] filterBeds

### Domain Service
- [ ] BedDomainService
- [ ] Availability query logic
- [ ] Status transition validation
- [ ] Ward lookup with aggregation

### Controller
- [ ] BedController
- [ ] POST /api/v1/beds
- [ ] GET /api/v1/beds/{id}
- [ ] GET /api/v1/beds/ward/{wardId}
- [ ] GET /api/v1/beds/available/isolation
- [ ] GET /api/v1/beds/availability/{wardId}
- [ ] GET /api/v1/beds/filter
- [ ] PUT /api/v1/beds/{id}
- [ ] PUT /api/v1/beds/{id}/status

### Testing
- [ ] Repository tests
- [ ] Service tests
- [ ] Controller tests

---

## Cross-Cutting Concerns

- [ ] GlobalExceptionHandler handles all module exceptions
- [ ] Validation errors properly formatted
- [ ] ResourceNotFoundException for missing entities
- [ ] BusinessException for business rule violations
- [ ] Proper HTTP status codes (201 Created, 204 No Content, etc.)
- [ ] SLF4J logging in all services
- [ ] @Transactional readOnly for queries
- [ ] No entity exposure in responses
