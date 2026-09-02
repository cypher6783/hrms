# 08 — Testing Execution Plan

## 1. Testing Strategy

### 1.1 Testing Pyramid

| Level | Framework | Scope | Coverage |
|-------|-----------|-------|----------|
| Unit Tests | JUnit 5 + Mockito | Domain services, utilities | ≥ 90% |
| Integration Tests | Spring Boot Test + Testcontainers | Repository, service integration | ≥ 70% |
| API Tests | Spring MockMvc | Controller endpoints | ≥ 80% |
| Security Tests | Spring Security Test | Auth, authorization | 100% |
| E2E Tests | Playwright | Complete user workflows | Critical paths |

### 1.2 Execution Order

1. Unit tests (run continuously during development)
2. Integration tests (run after unit tests pass)
3. API tests (run after integration tests pass)
4. Security tests (run after API tests pass)
5. E2E tests (run after all backend tests pass)

---

## 2. Unit Tests

### 2.1 Domain Service Tests

| Test Class | Methods | Priority | Status |
|------------|---------|----------|--------|
| WorkloadCalculatorTest | calculateWorkload, calculatePatientFactor, calculateShiftFactor | High | [ ] |
| CdsEngineTest | generateBedRecommendations, generateStaffRecommendations, generateEquipmentRecommendations, generateResourceRecommendations | High | [ ] |
| BedScoringServiceTest | scoreIsolationMatch, scoreBedTypeMatch, scoreWardOccupancy, scoreProximity, scoreCleaningRecency, scoreEquipmentAvailability | High | [ ] |
| StaffScoringServiceTest | scoreSpecializationMatch, scoreWorkloadBalance, scoreAvailability, scoreCertification, scoreWardFamiliarity | High | [ ] |
| EquipmentScoringServiceTest | scoreEquipmentTypeMatch, scoreEquipmentStatus, scoreMaintenanceRecency, scoreLocationProximity, scoreUtilizationHistory | High | [ ] |
| ResourceScoringServiceTest | scoreSeverityPriority, scoreStockAvailability, scoreCriticalityMatch, scoreExpirationProximity, scoreHistoricalConsumption | High | [ ] |
| InventoryDomainServiceTest | validateTransaction, calculateCurrentStock, checkLowStock | High | [ ] |
| BedCleaningDomainServiceTest | canTransitionStatus, isCleaningOverdue, getRequiredStatusForBedAvailability | Medium | [ ] |
| ShiftDomainServiceTest | hasOverlappingShift, canAssignToShift | Medium | [ ] |
| ForecastCalculatorTest | calculateMovingAverage, calculateWeightedMovingAverage, calculateMAPE | Medium | [ ] |

### 2.2 Application Service Tests

| Test Class | Methods | Priority | Status |
|------------|---------|----------|--------|
| PatientApplicationServiceTest | createPatient, getPatient, searchPatients, updatePatient, deactivatePatient | High | [ ] |
| ClinicalAssessmentApplicationServiceTest | recordAssessment, getPatientTimeline, getLatestAssessment, checkReassessmentDue | High | [ ] |
| AdmissionApplicationServiceTest | createAdmission, assignBed, transferPatient, dischargePatient, getAdmission | High | [ ] |
| BedCleaningApplicationServiceTest | createCleaningTask, assignCleaner, startCleaning, completeCleaning, verifyCleaning | High | [ ] |
| RecommendationApplicationServiceTest | generateRecommendations, getPendingRecommendations, acceptItem, overrideItem | High | [ ] |
| AuthApplicationServiceTest | login, refreshToken, logout, changePassword | High | [ ] |

### 2.3 Test Naming Convention

```
test_{method}_{scenario}_{expectedResult}
```

**Examples**:
- `test_createPatient_validInput_returnsPatientResponse`
- `test_createAdmission_patientHasActiveAdmission_throwsConflictException`
- `test_calculateWorkload_criticalPatient_returnsHigherScore`
- `test_bedScoring_confirmedPatient_returnsIsolationBed`
- `test_login_validCredentials_returnsLoginResponse`
- `test_login_accountLocked_throwsUnauthorizedException`

---

## 3. Integration Tests

### 3.1 Repository Tests

| Test Class | Focus | Status |
|------------|-------|--------|
| PatientRepositoryTest | Search queries, soft delete filtering | [ ] |
| AdmissionRepositoryTest | Active admission check, ward occupancy | [ ] |
| BedRepositoryTest | Available bed queries, status filtering | [ ] |
| ClinicalAssessmentRepositoryTest | Timeline queries, latest assessment | [ ] |
| InventoryTransactionRepositoryTest | Append-only behavior, stock calculation | [ ] |
| AuditLogRepositoryTest | Immutability, query patterns | [ ] |
| NotificationRepositoryTest | Unread count, mark all read | [ ] |

### 3.2 Service Integration Tests

| Test Class | Workflow | Status |
|------------|----------|--------|
| AdmissionWorkflowTest | Create patient → Create admission → Assign bed → Discharge | [ ] |
| CleaningWorkflowTest | Discharge → Create task → Assign → Complete → Verify | [ ] |
| InventoryWorkflowTest | Create resource → Record purchase → Issue → Verify stock | [ ] |
| RecommendationWorkflowTest | Admission → Generate → Accept/Override | [ ] |
| AuthenticationWorkflowTest | Register → Login → Refresh → Logout | [ ] |

---

## 4. API Tests

### 4.1 Controller Tests

| Test Class | Endpoints | Status |
|------------|-----------|--------|
| AuthControllerTest | login, refresh, logout, change-password | [ ] |
| PatientControllerTest | CRUD, search, pagination | [ ] |
| ClinicalAssessmentControllerTest | create, timeline, latest | [ ] |
| AdmissionControllerTest | CRUD, assign-bed, transfer, discharge | [ ] |
| BedControllerTest | CRUD, available | [ ] |
| BedCleaningControllerTest | tasks, assign, start, complete, verify | [ ] |
| WardControllerTest | CRUD, status | [ ] |
| StaffControllerTest | CRUD, workload | [ ] |
| ShiftControllerTest | CRUD, assign | [ ] |
| EquipmentControllerTest | CRUD, assign, maintenance | [ ] |
| ResourceControllerTest | CRUD | [ ] |
| InventoryControllerTest | transactions, stock | [ ] |
| SupplierControllerTest | CRUD | [ ] |
| RecommendationControllerTest | generate, pending, accept, override | [ ] |
| ForecastControllerTest | generate, list | [ ] |
| NotificationControllerTest | list, mark-read | [ ] |
| ReportControllerTest | occupancy, resources, cds, audit | [ ] |
| AdminUserControllerTest | CRUD, unlock | [ ] |
| SystemConfigControllerTest | get, update | [ ] |
| AuditControllerTest | search | [ ] |

### 4.2 Contract Tests

| Check | Status |
|-------|--------|
| All endpoints return ApiResponse<T> envelope | [ ] |
| Error responses match ErrorResponse format | [ ] |
| Pagination responses match PagedResponse<T> format | [ ] |
| HTTP status codes correct for all scenarios | [ ] |

---

## 5. Security Tests

| Test | Scenario | Expected | Status |
|------|----------|----------|--------|
| JWT Validation | Valid token | Request proceeds | [ ] |
| JWT Validation | Expired token | 401 returned | [ ] |
| JWT Validation | Invalid signature | 401 returned | [ ] |
| JWT Validation | Missing token | 401 returned | [ ] |
| Refresh Token | Valid refresh | New tokens issued | [ ] |
| Refresh Token | Revoked refresh | 401 returned | [ ] |
| Refresh Token | Expired refresh | 401 returned | [ ] |
| Role Access | User with correct role | Access granted | [ ] |
| Role Access | User without required role | 403 returned | [ ] |
| Account Lockout | 5 failed attempts | Account locked | [ ] |
| Account Lockout | Login while locked | 423 returned | [ ] |
| Password History | Reuse last 5 passwords | Rejected | [ ] |
| Password Complexity | Weak password | Rejected | [ ] |
| Input Validation | Invalid input | 400 with errors | [ ] |

---

## 6. End-to-End Tests

### 6.1 Critical Path Tests

| Test | Steps | Status |
|------|-------|--------|
| Login → Dashboard | Navigate to login → Enter credentials → Verify dashboard loads | [ ] |
| Patient Registration | Navigate to patients → Click new → Fill form → Submit → Verify in list | [ ] |
| Admission Workflow | Register patient → Create admission → View recommendations → Accept bed → Verify admission | [ ] |
| Discharge Workflow | Open admission → Click discharge → Select outcome → Submit → Verify cleaning task created | [ ] |
| Bed Cleaning | Open cleaning tasks → Assign cleaner → Mark complete → Verify → Verify bed available | [ ] |
| Inventory Transaction | Navigate to inventory → Record purchase → Verify stock updated → Issue to patient → Verify stock decremented | [ ] |
| Recommendation Override | View recommendation → Click override → Enter justification → Submit → Verify override recorded | [ ] |
| Report Generation | Navigate to reports → Select occupancy report → Set date range → Generate → Verify download | [ ] |

### 6.2 Role-Based Tests

| Role | Verify Access To | Verify Denied From |
|------|------------------|---------------------|
| ADMINISTRATOR | All pages | None |
| WARD_MANAGER | Ward, Bed, Cleaning, Admission, Recommendation | Admin config, User management |
| NURSING_OFFICER | Patient, Assessment, Admission | Bed assignment, Cleaning verify |
| RESOURCE_MANAGER | Resource, Inventory, Supplier | Bed, Ward config, Recommendation |
| EQUIPMENT_OFFICER | Equipment, Maintenance | Resource, Inventory, Recommendation |
| MEDICAL_DOCTOR | Assessment, Admission, Recommendation | Bed assignment, Cleaning |
| DASHBOARD_VIEWER | Dashboard, Reports (read-only) | All write operations |

---

## 7. Performance Tests

| Scenario | Target | Tool | Status |
|----------|--------|------|--------|
| 50 concurrent logins | ≤ 2 seconds response | JMeter | [ ] |
| Dashboard load | ≤ 3 seconds | JMeter | [ ] |
| Patient list with 10,000 records | ≤ 2 seconds | JMeter | [ ] |
| Recommendation generation | ≤ 5 seconds | JMeter | [ ] |
| Report generation | ≤ 10 seconds | JMeter | [ ] |
| Concurrent admission operations | No data corruption | JMeter | [ ] |

---

## 8. Test Data Strategy

### 8.1 Unit Tests

- Fixtures defined in test classes
- Builder pattern for test data
- No database dependency

### 8.2 Integration Tests

- Test data created in @BeforeEach
- Cleaned up in @AfterEach
- Testcontainers provide isolated database

### 8.3 E2E Tests

- Demo seed data (R__006_seed_demo_data.sql)
- Test scenarios reference seed data IDs

---

## 9. Test Execution Order

```
Phase 1: Unit Tests
    ├── Domain Service Tests
    └── Application Service Tests

Phase 2: Integration Tests
    ├── Repository Tests
    └── Service Integration Tests

Phase 3: API Tests
    ├── Controller Tests
    └── Contract Tests

Phase 4: Security Tests
    ├── JWT Tests
    ├── Authorization Tests
    └── Password Policy Tests

Phase 5: E2E Tests
    ├── Critical Path Tests
    └── Role-Based Tests

Phase 6: Performance Tests
    └── Load Tests
```

---

## 10. Document References

| Document | Reference |
|----------|-----------|
| Testing Design | `docs/design/15-testing-design.md` |
| Service Design | `docs/design/09-service-design.md` |
| Security Design | `docs/design/10-security-design.md` |
