# 02 — Backend Build Checklist

## 1. Project Setup

- [ ] Maven project initialized with groupId `com.hospital.resource`
- [ ] artifactId `hospital-resource`
- [ ] Java 21 configured
- [ ] Spring Boot 3.2.x parent configured
- [ ] Dependencies added:
  - [ ] spring-boot-starter-web
  - [ ] spring-boot-starter-data-jpa
  - [ ] spring-boot-starter-security
  - [ ] spring-boot-starter-validation
  - [ ] spring-boot-starter-cache
  - [ ] spring-boot-starter-actuator
  - [ ] postgresql driver
  - [ ] flyway-core
  - [ ] jjwt (io.jsonwebtoken)
  - [ ] lombok
  - [ ] mapstruct
  - [ ] springdoc-openapi
  - [ ] spring-boot-starter-test
  - [ ] testcontainers-postgresql
  - [ ] h2 (test scope)
- [ ] Application properties configured
- [ ] Profile-specific configs (dev, staging, prod)
- [ ] Main application class created

**Completion Criteria**: `mvn clean compile` succeeds.

---

## 2. Common Infrastructure

- [ ] GlobalExceptionHandler created
- [ ] Custom exceptions defined:
  - [ ] ResourceNotFoundException
  - [ ] ValidationException
  - [ ] ConflictException
  - [ ] UnauthorizedException
  - [ ] ForbiddenException
  - [ ] BusinessException (base class)
- [ ] ApiResponse<T> wrapper created
- [ ] PagedResponse<T> created
- [ ] ErrorResponse created
- [ ] Audit aspect created
- [ ] Audit log interceptor configured
- [ ] UUID generator utility
- [ ] Number generator utility (patient, admission numbers)

**Completion Criteria**: All common classes compile; exception handling returns standardized responses.

---

## 3. Authentication Module

### 3.1 Entity

- [ ] User entity created
- [ ] RefreshToken entity created
- [ ] PasswordHistory entity created
- [ ] LoginHistory entity created
- [ ] LoginAuditLog entity created

### 3.2 Repository

- [ ] UserRepository with custom queries
- [ ] RefreshTokenRepository with revoke methods
- [ ] PasswordHistoryRepository with history check
- [ ] LoginAuditLogRepository with audit queries

### 3.3 Security

- [ ] JwtTokenProvider created
- [ ] JwtAuthenticationFilter created
- [ ] UserDetailsServiceImpl created
- [ ] SecurityUtils utility created
- [ ] SecurityConfig configured
- [ ] CORS configured

### 3.4 Service

- [ ] AuthService (login, logout, refresh)
- [ ] TokenService (token generation, validation)
- [ ] PasswordService (hashing, history check)

### 3.5 Controller

- [ ] AuthController (login, logout, refresh, change-password)

### 3.6 DTOs

- [ ] LoginRequest
- [ ] LoginResponse
- [ ] RefreshTokenRequest
- [ ] RefreshTokenResponse
- [ ] ChangePasswordRequest
- [ ] UserSummary

**Completion Criteria**: User can login, refresh token, logout. Password policy enforced.

---

## 4. Patient Module

### 4.1 Entity

- [ ] Patient entity created

### 4.2 Repository

- [ ] PatientRepository with search queries

### 4.3 Service

- [ ] PatientApplicationService

### 4.4 Controller

- [ ] PatientController (CRUD + search)

### 4.5 DTOs

- [ ] PatientRequest
- [ ] PatientResponse
- [ ] PatientSummaryResponse
- [ ] PatientSearchRequest

**Completion Criteria**: Patient registration, search, edit, soft delete working.

---

## 5. Clinical Assessment Module

### 5.1 Entity

- [ ] ClinicalAssessment entity created

### 5.2 Repository

- [ ] ClinicalAssessmentRepository with timeline queries

### 5.3 Service

- [ ] ClinicalAssessmentApplicationService

### 5.4 Controller

- [ ] ClinicalAssessmentController (record, timeline, latest)

### 5.5 DTOs

- [ ] ClinicalAssessmentRequest
- [ ] ClinicalAssessmentResponse
- [ ] AssessmentTimelineResponse

**Completion Criteria**: Assessments recorded, timeline viewable, latest assessment queryable.

---

## 6. Ward Module

### 6.1 Entity

- [ ] Ward entity created

### 6.2 Repository

- [ ] WardRepository

### 6.3 Service

- [ ] WardApplicationService

### 6.4 Controller

- [ ] WardController (CRUD + status)

### 6.5 DTOs

- [ ] WardRequest
- [ ] WardResponse
- [ ] WardStatusResponse

**Completion Criteria**: Ward CRUD, occupancy calculation, status display.

---

## 7. Bed Module

### 7.1 Entity

- [ ] Bed entity created

### 7.2 Repository

- [ ] BedRepository with availability queries

### 7.3 Service

- [ ] BedApplicationService
- [ ] BedDomainService

### 7.4 Controller

- [ ] BedController (CRUD + availability)

### 7.5 DTOs

- [ ] BedRequest
- [ ] BedResponse
- [ ] BedAvailabilityResponse

**Completion Criteria**: Bed CRUD, status tracking, availability queries.

---

## 8. Admission Module

### 8.1 Entity

- [ ] Admission entity created

### 8.2 Repository

- [ ] AdmissionRepository with active admission checks

### 8.3 Service

- [ ] AdmissionApplicationService

### 8.4 Controller

- [ ] AdmissionController (CRUD + lifecycle operations)

### 8.5 DTOs

- [ ] AdmissionRequest
- [ ] AdmissionResponse
- [ ] AdmissionSummaryResponse
- [ ] DischargeRequest
- [ ] TransferRequest

**Completion Criteria**: Full admission lifecycle (create → assign bed → transfer → discharge).

---

## 9. Bed Cleaning Module

### 9.1 Entity

- [ ] BedCleaning entity created

### 9.2 Repository

- [ ] BedCleaningRepository

### 9.3 Service

- [ ] BedCleaningApplicationService
- [ ] BedCleaningDomainService

### 9.4 Controller

- [ ] BedCleaningController (tasks, assign, complete, verify)

### 9.5 DTOs

- [ ] CleaningTaskResponse
- [ ] CleaningAssignmentRequest
- [ ] CleaningCompletionRequest

**Completion Criteria**: Cleaning workflow from creation through verification.

---

## 10. Staff Module

### 10.1 Entity

- [ ] Staff entity created

### 10.2 Repository

- [ ] StaffRepository

### 10.3 Service

- [ ] StaffApplicationService
- [ ] WorkloadCalculator

### 10.4 Controller

- [ ] StaffController (CRUD + workload)

### 10.5 DTOs

- [ ] StaffRequest
- [ ] StaffResponse
- [ ] StaffSummary
- [ ] StaffWorkloadResponse

**Completion Criteria**: Staff CRUD, dynamic workload calculation.

---

## 11. Shift Module

### 11.1 Entity

- [ ] StaffShift entity created
- [ ] ShiftAssignment entity created

### 11.2 Repository

- [ ] StaffShiftRepository
- [ ] ShiftAssignmentRepository

### 11.3 Service

- [ ] ShiftApplicationService
- [ ] ShiftDomainService

### 11.4 Controller

- [ ] ShiftController (CRUD + assignment)

### 11.5 DTOs

- [ ] ShiftRequest
- [ ] ShiftResponse
- [ ] ShiftAssignmentRequest
- [ ] ShiftAssignmentResponse

**Completion Criteria**: Shift CRUD, assignment with overlap prevention.

---

## 12. Resource Module

### 12.1 Entity

- [ ] Resource entity created
- [ ] ResourceInventory entity created
- [ ] ResourceSupplier entity created

### 12.2 Repository

- [ ] ResourceRepository
- [ ] ResourceInventoryRepository
- [ ] ResourceSupplierRepository

### 12.3 Service

- [ ] ResourceApplicationService

### 12.4 Controller

- [ ] ResourceController
- [ ] SupplierController

### 12.5 DTOs

- [ ] ResourceRequest
- [ ] ResourceResponse
- [ ] SupplierRequest
- [ ] SupplierResponse

**Completion Criteria**: Resource and supplier CRUD.

---

## 13. Inventory Module

### 13.1 Entity

- [ ] InventoryTransaction entity created

### 13.2 Repository

- [ ] InventoryTransactionRepository

### 13.3 Service

- [ ] InventoryApplicationService
- [ ] InventoryDomainService

### 13.4 Controller

- [ ] InventoryController (transactions, stock)

### 13.5 DTOs

- [ ] InventoryTransactionRequest
- [ ] InventoryTransactionResponse
- [ ] InventoryStockResponse

**Completion Criteria**: Inventory transactions recorded, stock calculated, low-stock alerts.

---

## 14. Equipment Module

### 14.1 Entity

- [ ] Equipment entity created
- [ ] EquipmentMaintenance entity created

### 14.2 Repository

- [ ] EquipmentRepository
- [ ] EquipmentMaintenanceRepository
- [ ] EquipmentAllocationRepository

### 14.3 Service

- [ ] EquipmentApplicationService
- [ ] EquipmentMaintenanceApplicationService

### 14.4 Controller

- [ ] EquipmentController
- [ ] EquipmentMaintenanceController

### 14.5 DTOs

- [ ] EquipmentRequest
- [ ] EquipmentResponse
- [ ] EquipmentSummary
- [ ] MaintenanceRequest
- [ ] MaintenanceResponse
- [ ] EquipmentAllocationRequest

**Completion Criteria**: Equipment CRUD, maintenance scheduling, allocation tracking.

---

## 15. CDS Engine

### 15.1 Domain Services

- [ ] CdsEngineService
- [ ] BedScoringService
- [ ] StaffScoringService
- [ ] EquipmentScoringService
- [ ] ResourceScoringService
- [ ] ScoringFactors

### 15.2 Integration

- [ ] Triggers on admission creation
- [ ] Triggers on severity change
- [ ] Triggers on bed release
- [ ] Recommendation generation tested

**Completion Criteria**: CDS engine generates recommendations for all resource types.

---

## 16. Recommendation Module

### 16.1 Entity

- [ ] AllocationRecommendation entity
- [ ] RecommendationItem entity
- [ ] RecommendationDecision entity

### 16.2 Repository

- [ ] AllocationRecommendationRepository
- [ ] RecommendationItemRepository
- [ ] RecommendationDecisionRepository

### 16.3 Service

- [ ] RecommendationApplicationService

### 16.4 Controller

- [ ] RecommendationController (generate, pending, accept, override)

### 16.5 DTOs

- [ ] RecommendationResponse
- [ ] RecommendationItemResponse
- [ ] RecommendationDecisionRequest
- [ ] RecommendationDecisionResponse

**Completion Criteria**: Recommendations generated, accepted, overridden with justification.

---

## 17. Forecast Module

### 17.1 Entity

- [ ] ForecastSnapshot entity

### 17.2 Repository

- [ ] ForecastSnapshotRepository

### 17.3 Domain Services

- [ ] ForecastCalculator
- [ ] MovingAverageModel
- [ ] WeightedMovingAverageModel

### 17.4 Service

- [ ] ForecastApplicationService

### 17.5 Controller

- [ ] ForecastController (generate, list)

### 17.6 DTOs

- [ ] ForecastRequest
- [ ] ForecastResponse

**Completion Criteria**: Forecasts generated using Moving Average models.

---

## 18. Notification Module

### 18.1 Entity

- [ ] Notification entity

### 18.2 Repository

- [ ] NotificationRepository

### 18.3 Service

- [ ] NotificationApplicationService

### 18.4 Controller

- [ ] NotificationController (list, mark-read)

### 18.5 DTOs

- [ ] NotificationResponse

**Completion Criteria**: Notifications created, listed, marked as read.

---

## 19. Report Module

### 19.1 Service

- [ ] ReportApplicationService

### 19.2 Controller

- [ ] ReportController (occupancy, resource, CDS performance, audit)

### 19.3 DTOs

- [ ] ReportRequest
- [ ] OccupancyReportResponse
- [ ] ResourceReportResponse
- [ ] CdsPerformanceReportResponse

**Completion Criteria**: Reports generated in JSON, PDF, CSV formats.

---

## 20. Audit Module

### 20.1 Repository

- [ ] AuditLogRepository

### 20.2 Service

- [ ] AuditApplicationService

### 20.3 Controller

- [ ] AuditController (search)

### 20.4 DTOs

- [ ] AuditLogResponse
- [ ] AuditSearchRequest

**Completion Criteria**: Audit logs searchable by entity, user, date range.

---

## 21. Admin Module

### 21.1 Entity

- [ ] SystemConfiguration entity

### 21.2 Repository

- [ ] SystemConfigurationRepository

### 21.3 Service

- [ ] AdminUserService
- [ ] SystemConfigService

### 21.4 Controller

- [ ] AdminUserController (CRUD, unlock)
- [ ] SystemConfigController (get, update)

### 21.5 DTOs

- [ ] UserManagementRequest
- [ ] UserManagementResponse
- [ ] SystemConfigRequest
- [ ] SystemConfigResponse

**Completion Criteria**: User management, system configuration, account unlock.

---

## 22. Validation Checklist

| Check | Status |
|-------|--------|
| All entities have UUID primary keys | [ ] |
| All entities have audit fields | [ ] |
| Soft deletes implemented for Patient, Admission, ResourceSupplier | [ ] |
| Append-only for clinical_assessments, inventory_transactions, audit_logs | [ ] |
| All DTOs have validation annotations | [ ] |
| All controllers return ApiResponse<T> | [ ] |
| All list endpoints support pagination | [ ] |
| All endpoints have authorization checks | [ ] |
| All exceptions handled by GlobalExceptionHandler | [ ] |
| All services have @Transactional where needed | [ ] |
| All repositories have required custom queries | [ ] |

---

## 23. Document References

| Document | Reference |
|----------|-----------|
| Package Structure | `docs/design/04-package-structure.md` |
| Entity Design | `docs/design/06-entity-design.md` |
| Service Design | `docs/design/09-service-design.md` |
| Repository Design | `docs/design/08-repository-design.md` |
