# 15 — Testing Design

## 1. Testing Strategy

### 1.1 Testing Pyramid

```
        ┌─────────────┐
        │   E2E Tests  │  (Playwright)
        │   10%        │
        ├─────────────┤
        │ Integration  │  (Spring Boot Test)
        │   20%        │
        ├─────────────┤
        │   Unit Tests │  (JUnit 5 + Mockito)
        │   70%        │
        └─────────────┘
```

### 1.2 Coverage Goals

| Layer | Target Coverage |
|-------|----------------|
| Domain Services | ≥ 90% |
| Application Services | ≥ 80% |
| Repositories | ≥ 70% |
| Controllers | ≥ 70% |
| DTOs | ≥ 60% |
| Overall | ≥ 75% |

---

## 2. Unit Tests

### 2.1 Domain Service Tests

**Framework**: JUnit 5 + Mockito

**Scope**: Business logic, scoring algorithms, validation rules.

**Examples**:
- `WorkloadCalculatorTest`: Verify workload formula calculation.
- `CdsEngineTest`: Verify bed/staff/equipment/resource scoring.
- `InventoryDomainServiceTest`: Verify stock validation.
- `BedCleaningDomainServiceTest`: Verify status transitions.
- `ShiftDomainServiceTest`: Verify overlap detection.
- `ForecastCalculatorTest`: Verify moving average calculations.

**Mocking**: Repository interfaces mocked. No database access.

### 2.2 Application Service Tests

**Framework**: JUnit 5 + Mockito

**Scope**: Workflow orchestration, transaction boundaries, exception handling.

**Examples**:
- `AdmissionApplicationServiceTest`: Verify admission lifecycle.
- `BedCleaningApplicationServiceTest`: Verify cleaning workflow.
- `RecommendationApplicationServiceTest`: Verify recommendation generation.
- `AuthApplicationServiceTest`: Verify login, logout, token refresh.

**Mocking**: Domain services and repositories mocked.

### 2.3 Scoring Factor Tests

**Framework**: JUnit 5 + Parameterized Tests

**Scope**: Individual scoring factors for each recommendation type.

**Examples**:
- `BedScoringServiceTest`: Test isolation match, bed type match, occupancy scoring.
- `StaffScoringServiceTest`: Test specialization match, workload balance scoring.
- `EquipmentScoringServiceTest`: Test equipment type match, maintenance scoring.
- `ResourceScoringServiceTest`: Test severity priority, stock availability scoring.

---

## 3. Integration Tests

### 3.1 Repository Tests

**Framework**: Spring Boot Test + Testcontainers (PostgreSQL)

**Scope**: Query correctness, constraint enforcement, soft delete behavior.

**Examples**:
- `PatientRepositoryTest`: Test search queries, soft delete filtering.
- `AdmissionRepositoryTest`: Test active admission check, ward occupancy.
- `BedRepositoryTest`: Test available bed queries, status filtering.
- `InventoryTransactionRepositoryTest`: Test append-only behavior, stock calculation.
- `AuditLogRepositoryTest`: Test immutability, query patterns.

**Database**: Testcontainers spins up PostgreSQL instance.

### 3.2 Service Integration Tests

**Framework**: Spring Boot Test + Testcontainers

**Scope**: End-to-end service workflows with real database.

**Examples**:
- `AdmissionWorkflowTest`: Create patient → Create admission → Assign bed → Discharge.
- `CleaningWorkflowTest`: Discharge → Create task → Assign → Complete → Verify.
- `InventoryWorkflowTest`: Create resource → Record purchase → Issue → Verify stock.
- `RecommendationWorkflowTest`: Admission → Generate → Accept/Override.

### 3.3 Security Tests

**Framework**: Spring Security Test

**Scope**: Authentication, authorization, role-based access.

**Examples**:
- `JwtAuthenticationFilterTest`: Test token validation, expiry handling.
- `RoleBasedAccessTest`: Test endpoint access per role.
- `PasswordPolicyTest`: Test complexity, history enforcement.
- `AccountLockoutTest`: Test lockout after 5 failures.

---

## 4. API Tests

### 4.1 Controller Tests

**Framework**: Spring MockMvc

**Scope**: Request/response validation, HTTP status codes, DTO mapping.

**Examples**:
- `PatientControllerTest`: Test CRUD endpoints, search, pagination.
- `AdmissionControllerTest`: Test admission lifecycle endpoints.
- `BedControllerTest`: Test bed availability endpoints.
- `AuthControllerTest`: Test login, refresh, logout endpoints.
- `RecommendationControllerTest`: Test generate, accept, override endpoints.

### 4.2 Contract Tests

**Framework**: Spring MockMvc

**Scope**: API contract compliance (request/response format).

**Examples**:
- Test all endpoints return `ApiResponse<T>` envelope.
- Test error responses match `ErrorResponse` format.
- Test pagination responses match `PagedResponse<T>` format.

---

## 5. Acceptance Tests

### 5.1 End-to-End Tests

**Framework**: Playwright (Phase 5)

**Scope**: Complete user workflows through the UI.

**Examples**:
- `PatientRegistrationE2E`: Register patient → View in list → Edit.
- `AdmissionE2E`: Register patient → Create admission → Assign bed → View recommendations.
- `DischargeE2E`: Discharge patient → Verify cleaning task → Verify bed available.
- `InventoryE2E`: Add resource → Record purchase → Issue to patient → View history.

### 5.2 Critical Path Tests

| Path | Description |
|------|-------------|
| Login → Dashboard | Authentication and main view |
| Patient → Admission → Bed | Core admission workflow |
| Assessment → Recommendation → Action | CDS engine workflow |
| Discharge → Cleaning → Available | Bed cleaning workflow |
| Resource → Transaction → Stock | Inventory workflow |

---

## 6. Performance Tests

### 6.1 Load Tests

**Framework**: JMeter or Gatling

**Scenarios**:
- 50 concurrent users performing admission operations.
- Dashboard load with 10,000 patient records.
- Recommendation generation under load.
- Report generation with large datasets.

### 6.2 Benchmarks

| Operation | Target |
|-----------|--------|
| API response time (95th percentile) | ≤ 2 seconds |
| Dashboard load time | ≤ 3 seconds |
| Recommendation generation | ≤ 5 seconds |
| Report generation | ≤ 10 seconds |
| Concurrent users | ≥ 50 |

---

## 7. Test Data Strategy

### 7.1 Unit Tests

- Fixtures defined in test classes.
- Builder pattern for test data creation.
- No database dependency.

### 7.2 Integration Tests

- Test data created in `@BeforeEach`.
- Cleaned up in `@AfterEach`.
- Testcontainers provide isolated database per test class.

### 7.3 Acceptance Tests

- Demo seed data (R__006_seed_demo_data.sql).
- Test scenarios reference seed data IDs.

---

## 8. Test Naming Convention

```
test_{method}_{scenario}_{expectedResult}
```

Examples:
- `test_createAdmission_validInput_returnsAdmissionResponse`
- `test_createAdmission_patientHasActiveAdmission_throwsConflictException`
- `test_calculateWorkload_criticalPatient_returnsHigherScore`
- `test_bedScoring_confirmedPatient_returnsIsolationBed`

---

## 9. Test Organization

```
src/test/java/com/hospital/resource/
├── auth/
│   ├── service/
│   │   └── AuthApplicationServiceTest.java
│   └── security/
│       └── JwtAuthenticationFilterTest.java
├── patient/
│   ├── controller/
│   │   └── PatientControllerTest.java
│   ├── service/
│   │   └── PatientApplicationServiceTest.java
│   └── repository/
│       └── PatientRepositoryTest.java
├── admission/
│   ├── service/
│   │   ├── AdmissionApplicationServiceTest.java
│   │   └── AdmissionWorkflowIntegrationTest.java
│   └── repository/
│       └── AdmissionRepositoryTest.java
├── recommendation/
│   ├── domain/
│   │   ├── CdsEngineTest.java
│   │   ├── BedScoringServiceTest.java
│   │   ├── StaffScoringServiceTest.java
│   │   └── EquipmentScoringServiceTest.java
│   └── service/
│       └── RecommendationApplicationServiceTest.java
├── common/
│   └── exception/
│       └── GlobalExceptionHandlerTest.java
└── e2e/
    └── AdmissionE2ETest.java
```

---

## 10. Document References

| Document | Reference |
|----------|-----------|
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
| Technology Stack | `docs/planning/05-technology-stack.md` |
| Testing Strategy | `docs/planning/09-development-roadmap.md` §6 |
