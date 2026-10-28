# Testing Checklist

## Unit Testing Strategy

### Repository Tests
- Test custom query methods
- Test pagination and sorting
- Test JPQL queries
- Test edge cases (empty results, null parameters)

### Service Tests
- Test CRUD operations
- Test business logic validation
- Test exception handling
- Test transactional behavior
- Mock repository dependencies

### Controller Tests
- Test endpoint mapping
- Test request validation
- Test response wrapping (ApiResponse)
- Test HTTP status codes
- Mock service dependencies

---

## Module-Specific Tests

### Patient Module

**Repository Tests**
- [ ] testFindByPatientNumber_Exists
- [ ] testFindByPatientNumber_NotExists
- [ ] testExistsByPatientNumber
- [ ] testSearchPatients_ByName
- [ ] testSearchPatients_ByPatientNumber
- [ ] testSearchPatients_ByPhone
- [ ] testSearchPatients_NoResults
- [ ] testSearchPatients_Pagination
- [ ] testCountByIsActiveTrue

**Service Tests**
- [ ] testCreatePatient_Success
- [ ] testGetPatient_Found
- [ ] testGetPatient_NotFound_ThrowsException
- [ ] testGetPatientByNumber_Found
- [ ] testGetPatientByNumber_NotFound
- [ ] testSearchPatients_WithResults
- [ ] testSearchPatients_EmptyResults
- [ ] testUpdatePatient_Success
- [ ] testUpdatePatient_NotFound
- [ ] testDeactivatePatient_Success
- [ ] testGetActivePatientCount

**Controller Tests**
- [ ] testCreatePatient_Returns201
- [ ] testCreatePatient_InvalidRequest_Returns400
- [ ] testGetPatient_Returns200
- [ ] testGetPatient_NotFound_Returns404
- [ ] testSearchPatients_ReturnsPagedResponse
- [ ] testUpdatePatient_Returns200
- [ ] testDeactivatePatient_ReturnsSuccess

---

### Clinical Assessment Module

**Repository Tests**
- [ ] testFindByPatientIdOrderByAssessmentTimestampDesc
- [ ] testFindByAdmissionIdOrderByAssessmentTimestampDesc
- [ ] testFindTopByAdmissionIdOrderByAssessmentTimestampDesc_Exists
- [ ] testFindTopByAdmissionIdOrderByAssessmentTimestampDesc_NotExists

**Service Tests**
- [ ] testCreateAssessment_FirstAssessment
- [ ] testCreateAssessment_Reassessment
- [ ] testGetPatientTimeline_WithAssessments
- [ ] testGetPatientTimeline_Empty
- [ ] testGetAdmissionTimeline
- [ ] testGetLatestByAdmission_Found
- [ ] testGetLatestByAdmission_NotFound

**Controller Tests**
- [ ] testCreateAssessment_Returns201
- [ ] testGetPatientTimeline_Returns200
- [ ] testGetAdmissionTimeline_Returns200
- [ ] testGetLatestByAdmission_Returns200
- [ ] testGetLatestByAdmission_NotFound_Returns404
- [ ] testNoPutEndpoint_Exists
- [ ] testNoDeleteEndpoint_Exists

---

### Ward Module

**Repository Tests**
- [ ] testFindByStatus
- [ ] testFindByIsActiveTrue

**Service Tests**
- [ ] testCreateWard_Success
- [ ] testCreateWard_DuplicateName_ThrowsException
- [ ] testGetWard_Found
- [ ] testGetWard_NotFound
- [ ] testGetAllActiveWards
- [ ] testUpdateWard_Success
- [ ] testDeactivateWard_Success
- [ ] testGetActiveWardCount
- [ ] testGetWardStatus_CapacityCalculation

**Domain Service Tests**
- [ ] testCalculateCapacity
- [ ] testCalculateOccupancyRate
- [ ] testGetActiveBedCount

**Controller Tests**
- [ ] testCreateWard_Returns201
- [ ] testGetWard_Returns200
- [ ] testGetAllActiveWards_Returns200
- [ ] testGetWardStatus_ReturnsCapacityInfo
- [ ] testUpdateWard_Returns200
- [ ] testDeactivateWard_ReturnsSuccess

---

### Bed Module

**Repository Tests**
- [ ] testFindByWardIdAndStatus
- [ ] testCountByWardIdAndStatus
- [ ] testCountAvailableByWardId
- [ ] testCountOccupiedByWardId
- [ ] testFindByStatusAndIsIsolationCapable
- [ ] testFindByWardId
- [ ] testFindByBedType

**Service Tests**
- [ ] testCreateBed_Success
- [ ] testCreateBed_DuplicateNumberInWard_ThrowsException
- [ ] testGetBed_Found
- [ ] testGetBed_NotFound
- [ ] testGetBedsByWard
- [ ] testGetAvailableIsolationBeds
- [ ] testGetBedAvailability
- [ ] testUpdateBed_Success
- [ ] testUpdateBedStatus_ValidTransition
- [ ] testUpdateBedStatus_InvalidTransition_ThrowsException
- [ ] testFilterBeds_ByWard
- [ ] testFilterBeds_ByType
- [ ] testFilterBeds_ByStatus
- [ ] testFilterBeds_Combined

**Domain Service Tests**
- [ ] testValidateStatusTransition_Valid
- [ ] testValidateStatusTransition_Invalid
- [ ] testGetAvailabilitySummary
- [ ] testWardLookup_WithAggregation

**Controller Tests**
- [ ] testCreateBed_Returns201
- [ ] testGetBed_Returns200
- [ ] testGetBedsByWard_Returns200
- [ ] testGetAvailableIsolationBeds_Returns200
- [ ] testGetBedAvailability_Returns200
- [ ] testFilterBeds_Returns200
- [ ] testUpdateBed_Returns200
- [ ] testUpdateBedStatus_Returns200
- [ ] testUpdateBedStatus_InvalidTransition_Returns400

---

## Integration Tests

- [ ] Patient creation and assessment flow
- [ ] Ward creation with bed assignment
- [ ] Bed status transitions with ward occupancy
- [ ] Search and filtering across modules

---

## Test Configuration

- [ ] Test profiles configured
- [ ] Test containers for PostgreSQL
- [ ] Test data builders/factories
- [ ] MockMvc configuration
- [ ] Service layer mocks
