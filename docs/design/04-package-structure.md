# 04 — Package Structure

## 1. Root Package

```
com.hospital.resource
```

All Java classes reside under this root package.

---

## 2. Package Hierarchy

```
src/main/java/com/hospital/resource/
├── HospitalResourceApplication.java
│
├── common/
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── CorsConfig.java
│   │   ├── JacksonConfig.java
│   │   └── AsyncConfig.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   ├── BusinessException.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── ValidationException.java
│   │   ├── UnauthorizedException.java
│   │   └── ConflictException.java
│   ├── dto/
│   │   ├── ApiResponse.java
│   │   ├── PagedResponse.java
│   │   └── ErrorResponse.java
│   ├── util/
│   │   ├── UUIDGenerator.java
│   │   ├── NumberGenerator.java
│   │   └── DateUtils.java
│   └── audit/
│       ├── AuditAspect.java
│       ├── AuditLog.java
│       └── AuditInterceptor.java
│
├── auth/
│   ├── controller/
│   │   └── AuthController.java
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── RefreshTokenRequest.java
│   │   ├── RefreshTokenResponse.java
│   │   └── LogoutRequest.java
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── TokenService.java
│   │   └── PasswordService.java
│   ├── security/
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── SecurityUtils.java
│   └── repository/
│       ├── UserRepository.java
│       ├── RefreshTokenRepository.java
│       ├── PasswordHistoryRepository.java
│       └── LoginAuditLogRepository.java
│
├── patient/
│   ├── controller/
│   │   └── PatientController.java
│   ├── dto/
│   │   ├── PatientRequest.java
│   │   ├── PatientResponse.java
│   │   ├── PatientSearchRequest.java
│   │   └── PatientSummaryResponse.java
│   ├── service/
│   │   └── PatientApplicationService.java
│   └── repository/
│       └── PatientRepository.java
│
├── assessment/
│   ├── controller/
│   │   └── ClinicalAssessmentController.java
│   ├── dto/
│   │   ├── ClinicalAssessmentRequest.java
│   │   ├── ClinicalAssessmentResponse.java
│   │   └── AssessmentTimelineResponse.java
│   ├── service/
│   │   └── ClinicalAssessmentApplicationService.java
│   └── repository/
│       └── ClinicalAssessmentRepository.java
│
├── admission/
│   ├── controller/
│   │   └── AdmissionController.java
│   ├── dto/
│   │   ├── AdmissionRequest.java
│   │   ├── AdmissionResponse.java
│   │   ├── DischargeRequest.java
│   │   ├── TransferRequest.java
│   │   └── AdmissionSummaryResponse.java
│   ├── service/
│   │   └── AdmissionApplicationService.java
│   └── repository/
│       └── AdmissionRepository.java
│
├── bed/
│   ├── controller/
│   │   └── BedController.java
│   ├── dto/
│   │   ├── BedRequest.java
│   │   ├── BedResponse.java
│   │   └── BedAvailabilityResponse.java
│   ├── service/
│   │   └── BedApplicationService.java
│   ├── domain/
│   │   └── BedDomainService.java
│   └── repository/
│       └── BedRepository.java
│
├── bedcleaning/
│   ├── controller/
│   │   └── BedCleaningController.java
│   ├── dto/
│   │   ├── CleaningTaskResponse.java
│   │   ├── CleaningAssignmentRequest.java
│   │   └── CleaningCompletionRequest.java
│   ├── service/
│   │   └── BedCleaningApplicationService.java
│   ├── domain/
│   │   └── BedCleaningDomainService.java
│   └── repository/
│       └── BedCleaningRepository.java
│
├── ward/
│   ├── controller/
│   │   └── WardController.java
│   ├── dto/
│   │   ├── WardRequest.java
│   │   ├── WardResponse.java
│   │   └── WardStatusResponse.java
│   ├── service/
│   │   └── WardApplicationService.java
│   └── repository/
│       └── WardRepository.java
│
├── staff/
│   ├── controller/
│   │   ├── StaffController.java
│   │   └── ShiftController.java
│   ├── dto/
│   │   ├── StaffRequest.java
│   │   ├── StaffResponse.java
│   │   ├── StaffWorkloadResponse.java
│   │   ├── ShiftRequest.java
│   │   ├── ShiftResponse.java
│   │   ├── ShiftAssignmentRequest.java
│   │   └── ShiftAssignmentResponse.java
│   ├── service/
│   │   ├── StaffApplicationService.java
│   │   └── ShiftApplicationService.java
│   ├── domain/
│   │   ├── WorkloadCalculator.java
│   │   └── ShiftDomainService.java
│   └── repository/
│       ├── StaffRepository.java
│       ├── StaffShiftRepository.java
│       └── ShiftAssignmentRepository.java
│
├── equipment/
│   ├── controller/
│   │   ├── EquipmentController.java
│   │   └── EquipmentMaintenanceController.java
│   ├── dto/
│   │   ├── EquipmentRequest.java
│   │   ├── EquipmentResponse.java
│   │   ├── MaintenanceRequest.java
│   │   ├── MaintenanceResponse.java
│   │   └── EquipmentAllocationRequest.java
│   ├── service/
│   │   ├── EquipmentApplicationService.java
│   │   └── EquipmentMaintenanceApplicationService.java
│   └── repository/
│       ├── EquipmentRepository.java
│       └── EquipmentMaintenanceRepository.java
│
├── resource/
│   ├── controller/
│   │   ├── ResourceController.java
│   │   ├── InventoryController.java
│   │   └── SupplierController.java
│   ├── dto/
│   │   ├── ResourceRequest.java
│   │   ├── ResourceResponse.java
│   │   ├── InventoryTransactionRequest.java
│   │   ├── InventoryTransactionResponse.java
│   │   ├── InventoryStockResponse.java
│   │   ├── SupplierRequest.java
│   │   └── SupplierResponse.java
│   ├── service/
│   │   ├── ResourceApplicationService.java
│   │   └── InventoryApplicationService.java
│   ├── domain/
│   │   └── InventoryDomainService.java
│   └── repository/
│       ├── ResourceRepository.java
│       ├── ResourceInventoryRepository.java
│       ├── InventoryTransactionRepository.java
│       └── ResourceSupplierRepository.java
│
├── recommendation/
│   ├── controller/
│   │   └── RecommendationController.java
│   ├── dto/
│   │   ├── RecommendationResponse.java
│   │   ├── RecommendationItemResponse.java
│   │   ├── RecommendationDecisionRequest.java
│   │   └── RecommendationDecisionResponse.java
│   ├── service/
│   │   └── RecommendationApplicationService.java
│   ├── domain/
│   │   ├── CdsEngine.java
│   │   ├── BedScoringService.java
│   │   ├── StaffScoringService.java
│   │   ├── EquipmentScoringService.java
│   │   ├── ResourceScoringService.java
│   │   └── ScoringFactors.java
│   └── repository/
│       ├── AllocationRecommendationRepository.java
│       ├── RecommendationItemRepository.java
│       └── RecommendationDecisionRepository.java
│
├── forecast/
│   ├── controller/
│   │   └── ForecastController.java
│   ├── dto/
│   │   ├── ForecastRequest.java
│   │   ├── ForecastResponse.java
│   │   └── ForecastAccuracyResponse.java
│   ├── service/
│   │   └── ForecastApplicationService.java
│   ├── domain/
│   │   ├── MovingAverageModel.java
│   │   ├── WeightedMovingAverageModel.java
│   │   └── ForecastCalculator.java
│   └── repository/
│       └── ForecastSnapshotRepository.java
│
├── notification/
│   ├── controller/
│   │   └── NotificationController.java
│   ├── dto/
│   │   ├── NotificationResponse.java
│   │   └── NotificationPreferenceRequest.java
│   ├── service/
│   │   └── NotificationApplicationService.java
│   └── repository/
│       └── NotificationRepository.java
│
├── report/
│   ├── controller/
│   │   └── ReportController.java
│   ├── dto/
│   │   ├── ReportRequest.java
│   │   ├── ReportResponse.java
│   │   ├── OccupancyReportResponse.java
│   │   ├── ResourceReportResponse.java
│   │   ├── StaffReportResponse.java
│   │   └── CdsPerformanceReportResponse.java
│   └── service/
│       └── ReportApplicationService.java
│
├── admin/
│   ├── controller/
│   │   ├── AdminUserController.java
│   │   └── SystemConfigController.java
│   ├── dto/
│   │   ├── UserManagementRequest.java
│   │   ├── UserManagementResponse.java
│   │   ├── SystemConfigRequest.java
│   │   └── SystemConfigResponse.java
│   └── service/
│       ├── AdminUserService.java
│       └── SystemConfigService.java
│
└── audit/
    ├── controller/
    │   └── AuditController.java
    ├── dto/
    │   ├── AuditLogResponse.java
    │   └── AuditSearchRequest.java
    ├── service/
    │   └── AuditApplicationService.java
    └── repository/
        └── AuditLogRepository.java
```

---

## 3. Package Responsibilities

### 3.1 common/

Shared infrastructure, exception handling, DTOs, utilities, and cross-cutting concerns.

| Sub-package | Responsibility |
|-------------|---------------|
| config | Spring configuration classes (Security, CORS, Jackson, Async) |
| exception | Global exception handler, custom exceptions |
| dto | API response wrappers, pagination DTOs |
| util | UUID generation, number generation, date utilities |
| audit | AOP-based audit logging aspect |

### 3.2 auth/

Authentication, authorization, JWT token management, and password policies.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Login, logout, token refresh endpoints |
| dto | Authentication request/response DTOs |
| service | Token generation, password hashing, session management |
| security | JWT filter, token provider, user details service |
| repository | User, token, password history, login audit data access |

### 3.3 patient/

Patient registration, demographics management, and search.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Patient CRUD and search endpoints |
| dto | Patient request/response DTOs |
| service | Patient registration, search, soft deletion |
| repository | Patient data access |

### 3.4 assessment/

Clinical assessment recording and history management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Assessment recording and timeline endpoints |
| dto | Assessment request/response DTOs |
| service | Assessment recording, timeline retrieval |
| repository | Assessment data access |

### 3.5 admission/

Admission lifecycle management (create, admit, transfer, discharge).

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Admission CRUD and workflow endpoints |
| dto | Admission request/response DTOs |
| service | Admission workflow orchestration |
| repository | Admission data access |

### 3.6 bed/

Bed registry, status management, and availability queries.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Bed CRUD and availability endpoints |
| dto | Bed request/response DTOs |
| service | Bed status management |
| domain | Bed business rules (status transitions) |
| repository | Bed data access |

### 3.7 bedcleaning/

Bed cleaning workflow management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Cleaning task endpoints |
| dto | Cleaning request/response DTOs |
| service | Cleaning workflow orchestration |
| domain | Cleaning business rules |
| repository | Cleaning data access |

### 3.8 ward/

Ward configuration and management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Ward CRUD endpoints |
| dto | Ward request/response DTOs |
| service | Ward management |
| repository | Ward data access |

### 3.9 staff/

Staff profiles, workload calculation, and shift management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Staff and shift endpoints |
| dto | Staff/shift request/response DTOs |
| service | Staff and shift orchestration |
| domain | Workload calculation, shift business rules |
| repository | Staff, shift, assignment data access |

### 3.10 equipment/

Equipment registry and maintenance management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Equipment and maintenance endpoints |
| dto | Equipment/maintenance request/response DTOs |
| service | Equipment and maintenance orchestration |
| repository | Equipment and maintenance data access |

### 3.11 resource/

Resource definitions, inventory management, and supplier management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Resource, inventory, supplier endpoints |
| dto | Resource/inventory/supplier request/response DTOs |
| service | Resource and inventory orchestration |
| domain | Inventory business rules (stock validation) |
| repository | Resource, inventory, transaction, supplier data access |

### 3.12 recommendation/

CDS engine and recommendation management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Recommendation and decision endpoints |
| dto | Recommendation request/response DTOs |
| service | Recommendation orchestration |
| domain | CDS engine, scoring services for bed/staff/equipment/resource |
| repository | Recommendation, item, decision data access |

### 3.13 forecast/

Forecasting models and forecast management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Forecast generation and retrieval endpoints |
| dto | Forecast request/response DTOs |
| service | Forecast orchestration |
| domain | Moving average, weighted moving average models |
| repository | Forecast snapshot data access |

### 3.14 notification/

Notification delivery and management.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Notification endpoints |
| dto | Notification request/response DTOs |
| service | Notification delivery and history |
| repository | Notification data access |

### 3.15 report/

Report generation and export.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Report generation endpoints |
| dto | Report request/response DTOs |
| service | Report generation logic |

### 3.16 admin/

User management and system configuration.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | User management and config endpoints |
| dto | User/config request/response DTOs |
| service | User lifecycle and config management |

### 3.17 audit/

Audit log querying and reporting.

| Sub-package | Responsibility |
|-------------|---------------|
| controller | Audit log query endpoints |
| dto | Audit log request/response DTOs |
| service | Audit log querying |
| repository | Audit log data access |

---

## 4. Document References

| Document | Reference |
|----------|-----------|
| System Architecture | `docs/planning/03-system-architecture.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Technology Stack | `docs/planning/05-technology-stack.md` |
