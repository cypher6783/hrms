# 09 — Service Design

## 1. Application Services

Application services orchestrate workflows. They contain no business rules — they delegate to domain services.

### 1.1 PatientApplicationService

**Package**: `com.hospital.resource.patient.service`

**Responsibilities**:
- Patient registration with auto-generated patient number
- Patient search and retrieval
- Patient demographic updates with audit trail
- Patient soft deletion (deactivation)

**Dependencies**: PatientRepository, AuditService, NumberGenerator

**Transactions**: `@Transactional` on registration, update, deactivation methods

**Methods**:
- `registerPatient(PatientRequest) → PatientResponse`
- `getPatient(UUID id) → PatientResponse`
- `searchPatients(PatientSearchRequest, Pageable) → PagedResponse<PatientSummaryResponse>`
- `updatePatient(UUID id, PatientRequest) → PatientResponse`
- `deactivatePatient(UUID id) → void`

**Exception Handling**: ResourceNotFoundException for missing patients, ValidationException for duplicate patient numbers, ConflictException for active admissions preventing deactivation.

---

### 1.2 ClinicalAssessmentApplicationService

**Package**: `com.hospital.resource.assessment.service`

**Responsibilities**:
- Record clinical assessments (append-only)
- Retrieve assessment timeline per patient
- Retrieve latest assessment per admission
- Enforce reassessment schedule (within 24h of admission)

**Dependencies**: ClinicalAssessmentRepository, AdmissionRepository, AuditService, NotificationService

**Transactions**: `@Transactional` on recording methods

**Methods**:
- `recordAssessment(ClinicalAssessmentRequest) → ClinicalAssessmentResponse`
- `getPatientTimeline(UUID patientId, Pageable) → PagedResponse<ClinicalAssessmentResponse>`
- `getLatestAssessment(UUID admissionId) → ClinicalAssessmentResponse`
- `checkReassessmentDue(UUID admissionId) → boolean`

**Exception Handling**: ResourceNotFoundException for missing patient/admission, ValidationException for incomplete data.

---

### 1.3 AdmissionApplicationService

**Package**: `com.hospital.resource.admission.service`

**Responsibilities**:
- Create admission records
- Assign beds to admissions
- Process patient transfers (new admission record + bed release)
- Process discharges (bed release + cleaning task creation)
- Trigger recommendation engine on admission events

**Dependencies**: AdmissionRepository, BedRepository, BedCleaningRepository, RecommendationApplicationService, NotificationService, AuditService

**Transactions**: `@Transactional` on all workflow methods

**Methods**:
- `createAdmission(AdmissionRequest) → AdmissionResponse`
- `getAdmission(UUID id) → AdmissionResponse`
- `searchAdmissions(...) → PagedResponse<AdmissionSummaryResponse>`
- `assignBed(UUID admissionId, BedAssignmentRequest) → AdmissionResponse`
- `transferPatient(UUID admissionId, TransferRequest) → AdmissionResponse`
- `dischargePatient(UUID admissionId, DischargeRequest) → AdmissionResponse`

**Exception Handling**: ConflictException for active admission, ResourceNotFoundException, ValidationException for invalid bed/ward.

---

### 1.4 BedApplicationService

**Package**: `com.hospital.resource.bed.service`

**Responsibilities**:
- Bed CRUD operations
- Bed availability queries
- Bed status management

**Dependencies**: BedRepository, WardRepository, AuditService

**Transactions**: `@Transactional` on create/update methods

**Methods**:
- `createBed(BedRequest) → BedResponse`
- `getBed(UUID id) → BedResponse`
- `searchBeds(...) → PagedResponse<BedResponse>`
- `getAvailableBeds(...) → List<BedAvailabilityResponse>`
- `updateBed(UUID id, BedRequest) → BedResponse`
- `updateBedStatus(UUID id, BedStatus) → void` (internal)

**Exception Handling**: ConflictException for duplicate bed numbers, ResourceNotFoundException.

---

### 1.5 BedCleaningApplicationService

**Package**: `com.hospital.resource.bedcleaning.service`

**Responsibilities**:
- Create cleaning tasks on discharge
- Assign cleaners to tasks
- Track cleaning workflow status
- Verify cleaning completion
- Update bed status through cleaning workflow

**Dependencies**: BedCleaningRepository, BedRepository, StaffRepository, AuditService, NotificationService

**Transactions**: `@Transactional` on all workflow transitions

**Methods**:
- `createCleaningTask(UUID bedId, UUID admissionId) → CleaningTaskResponse`
- `assignCleaner(UUID taskId, CleaningAssignmentRequest) → CleaningTaskResponse`
- `startCleaning(UUID taskId) → CleaningTaskResponse`
- `completeCleaning(UUID taskId, CleaningCompletionRequest) → CleaningTaskResponse`
- `verifyCleaning(UUID taskId) → CleaningTaskResponse`
- `getPendingTasks(...) → PagedResponse<CleaningTaskResponse>`

**Exception Handling**: IllegalStateException for invalid workflow transitions, ResourceNotFoundException.

---

### 1.6 WardApplicationService

**Package**: `com.hospital.resource.ward.service`

**Responsibilities**:
- Ward CRUD operations
- Ward occupancy calculations
- Ward configuration management

**Dependencies**: WardRepository, BedRepository, AdmissionRepository, AuditService

**Transactions**: `@Transactional` on create/update methods

**Methods**:
- `createWard(WardRequest) → WardResponse`
- `getWard(UUID id) → WardResponse`
- `listWards(...) → PagedResponse<WardResponse>`
- `updateWard(UUID id, WardRequest) → WardResponse`
- `getWardOccupancy(UUID wardId) → WardStatusResponse`

**Exception Handling**: ConflictException for duplicate ward names, ResourceNotFoundException.

---

### 1.7 StaffApplicationService

**Package**: `com.hospital.resource.staff.service`

**Responsibilities**:
- Staff CRUD operations
- Staff workload retrieval (delegates to WorkloadCalculator)
- Staff availability management

**Dependencies**: StaffRepository, WorkloadCalculator, AuditService

**Transactions**: `@Transactional` on create/update methods

**Methods**:
- `registerStaff(StaffRequest) → StaffResponse`
- `getStaff(UUID id) → StaffResponse`
- `listStaff(...) → PagedResponse<StaffResponse>`
- `updateStaff(UUID id, StaffRequest) → StaffResponse`
- `getStaffWorkload(UUID staffId) → StaffWorkloadResponse`

**Exception Handling**: ConflictException for duplicate staff numbers, ResourceNotFoundException.

---

### 1.8 ShiftApplicationService

**Package**: `com.hospital.resource.staff.service`

**Responsibilities**:
- Shift CRUD operations
- Shift assignment management
- Overlap prevention

**Dependencies**: StaffShiftRepository, ShiftAssignmentRepository, StaffRepository, AuditService

**Transactions**: `@Transactional` on create/assign methods

**Methods**:
- `createShift(ShiftRequest) → ShiftResponse`
- `listShifts(...) → PagedResponse<ShiftResponse>`
- `assignStaffToShift(UUID shiftId, ShiftAssignmentRequest) → ShiftAssignmentResponse`
- `checkOverlap(UUID staffId, ShiftRequest) → boolean`

**Exception Handling**: ConflictException for overlapping shifts, ResourceNotFoundException.

---

### 1.9 EquipmentApplicationService

**Package**: `com.hospital.resource.equipment.service`

**Responsibilities**:
- Equipment CRUD operations
- Equipment assignment to admissions/wards
- Equipment availability queries

**Dependencies**: EquipmentRepository, AuditService

**Transactions**: `@Transactional` on create/update/assign methods

**Methods**:
- `registerEquipment(EquipmentRequest) → EquipmentResponse`
- `getEquipment(UUID id) → EquipmentResponse`
- `listEquipment(...) → PagedResponse<EquipmentResponse>`
- `assignEquipment(UUID id, EquipmentAllocationRequest) → EquipmentResponse`
- `updateStatus(UUID id, EquipmentStatus) → void`

**Exception Handling**: ConflictException for duplicate serial numbers, ValidationException for status transitions.

---

### 1.10 EquipmentMaintenanceApplicationService

**Package**: `com.hospital.resource.equipment.service`

**Responsibilities**:
- Maintenance scheduling
- Maintenance completion tracking
- Overdue maintenance detection

**Dependencies**: EquipmentMaintenanceRepository, EquipmentRepository, NotificationService, AuditService

**Transactions**: `@Transactional` on schedule/complete methods

**Methods**:
- `scheduleMaintenance(UUID equipmentId, MaintenanceRequest) → MaintenanceResponse`
- `completeMaintenance(UUID maintenanceId) → MaintenanceResponse`
- `getOverdueMaintenance(...) → List<MaintenanceResponse>`

**Exception Handling**: ResourceNotFoundException, ValidationException.

---

### 1.11 ResourceApplicationService

**Package**: `com.hospital.resource.resource.service`

**Responsibilities**:
- Resource definition CRUD
- Supplier management

**Dependencies**: ResourceRepository, ResourceSupplierRepository, AuditService

**Transactions**: `@Transactional` on create/update methods

**Methods**:
- `defineResource(ResourceRequest) → ResourceResponse`
- `listResources(...) → PagedResponse<ResourceResponse>`
- `createSupplier(SupplierRequest) → SupplierResponse`
- `listSuppliers(...) → PagedResponse<SupplierResponse>`

**Exception Handling**: ResourceNotFoundException, ConflictException.

---

### 1.12 InventoryApplicationService

**Package**: `com.hospital.resource.resource.service`

**Responsibilities**:
- Record inventory transactions (append-only)
- Stock level queries
- Low-stock alert generation

**Dependencies**: InventoryTransactionRepository, ResourceInventoryRepository, NotificationService, AuditService

**Transactions**: `@Transactional` on transaction recording

**Methods**:
- `recordTransaction(InventoryTransactionRequest) → InventoryTransactionResponse`
- `getTransactions(...) → PagedResponse<InventoryTransactionResponse>`
- `getStockLevels(UUID resourceId, String location) → List<InventoryStockResponse>`
- `checkLowStock() → List<ResourceInventory>`

**Exception Handling**: ValidationException for negative stock, ResourceNotFoundException.

---

### 1.13 RecommendationApplicationService

**Package**: `com.hospital.resource.recommendation.service`

**Responsibilities**:
- Generate recommendations (delegates to CDS Engine)
- Retrieve pending recommendations
- Process accept/override decisions
- Expiry management

**Dependencies**: AllocationRecommendationRepository, RecommendationItemRepository, RecommendationDecisionRepository, CdsEngine, NotificationService, AuditService

**Transactions**: `@Transactional` on generate/decision methods

**Methods**:
- `generateRecommendations(UUID admissionId, BatchType) → RecommendationResponse`
- `getPendingRecommendations(UUID admissionId) → RecommendationResponse`
- `acceptItem(UUID recommendationId, UUID itemId) → RecommendationDecisionResponse`
- `overrideItem(UUID recommendationId, UUID itemId, RecommendationDecisionRequest) → RecommendationDecisionResponse`
- `expireRecommendations() → void` (scheduled)

**Exception Handling**: ValidationException for override without justification, ResourceNotFoundException, UnauthorizedException for override permissions.

---

### 1.14 ForecastApplicationService

**Package**: `com.hospital.resource.forecast.service`

**Responsibilities**:
- Generate forecasts (delegates to domain models)
- Retrieve forecast history
- Calculate accuracy scores

**Dependencies**: ForecastSnapshotRepository, AuditService

**Transactions**: `@Transactional` on generate methods

**Methods**:
- `generateForecast(ForecastRequest) → ForecastResponse`
- `listForecasts(...) → PagedResponse<ForecastResponse>`
- `calculateAccuracy(UUID forecastId) → ForecastAccuracyResponse`

**Exception Handling**: ResourceNotFoundException, ValidationException.

---

### 1.15 NotificationApplicationService

**Package**: `com.hospital.resource.notification.service`

**Responsibilities**:
- Create and deliver notifications
- Retrieve user notifications
- Mark notifications as read

**Dependencies**: NotificationRepository, AuditService

**Transactions**: `@Transactional` on create/read methods

**Methods**:
- `createNotification(NotificationRequest) → NotificationResponse`
- `getUserNotifications(UUID userId, ...) → PagedResponse<NotificationResponse>`
- `markAsRead(UUID notificationId) → NotificationResponse`
- `markAllAsRead(UUID userId) → void`
- `getUnreadCount(UUID userId) → long`

**Exception Handling**: ResourceNotFoundException.

---

### 1.16 ReportApplicationService

**Package**: `com.hospital.resource.report.service`

**Responsibilities**:
- Generate occupancy reports
- Generate resource utilization reports
- Generate CDS performance reports
- Generate audit trail reports

**Dependencies**: AdmissionRepository, BedRepository, ResourceInventoryRepository, AllocationRecommendationRepository, AuditLogRepository

**Transactions**: Read-only transactions

**Methods**:
- `generateOccupancyReport(ReportRequest) → ReportResponse`
- `generateResourceReport(ReportRequest) → ReportResponse`
- `generateCdsPerformanceReport(ReportRequest) → ReportResponse`
- `generateAuditReport(ReportRequest) → ReportResponse`

**Exception Handling**: ValidationException for invalid date ranges.

---

### 1.17 AdminUserService

**Package**: `com.hospital.resource.admin.service`

**Responsibilities**:
- User account CRUD
- Account lock/unlock
- Password reset

**Dependencies**: UserRepository, PasswordHistoryRepository, AuditService, NotificationService

**Transactions**: `@Transactional` on all mutation methods

**Methods**:
- `createUser(UserManagementRequest) → UserManagementResponse`
- `listUsers(...) → PagedResponse<UserManagementResponse>`
- `updateUser(UUID id, UserManagementRequest) → UserManagementResponse`
- `unlockAccount(UUID userId) → void`
- `resetPassword(UUID userId) → void`

**Exception Handling**: ConflictException for duplicate username/email, ResourceNotFoundException.

---

### 1.18 SystemConfigService

**Package**: `com.hospital.resource.admin.service`

**Responsibilities**:
- Configuration CRUD
- Configuration validation

**Dependencies**: SystemConfigurationRepository, AuditService

**Transactions**: `@Transactional` on update methods

**Methods**:
- `listConfigurations(String category) → List<SystemConfigResponse>`
- `updateConfiguration(String key, SystemConfigRequest) → SystemConfigResponse`
- `getConfiguration(String key) → SystemConfigResponse`

**Exception Handling**: ValidationException for invalid values, ResourceNotFoundException.

---

### 1.19 AuditApplicationService

**Package**: `com.hospital.resource.audit.service`

**Responsibilities**:
- Query audit logs
- Generate audit reports

**Dependencies**: AuditLogRepository, LoginAuditLogRepository

**Transactions**: Read-only

**Methods**:
- `queryAuditLogs(AuditSearchRequest, Pageable) → PagedResponse<AuditLogResponse>`
- `getEntityAuditHistory(String entityType, UUID entityId) → List<AuditLogResponse>`

**Exception Handling**: UnauthorizedException for non-admin access.

---

## 2. Domain Services

Domain services contain business rules and computation. They have no infrastructure dependencies.

### 2.1 CdsEngine

**Package**: `com.hospital.resource.recommendation.domain`

**Responsibilities**:
- Orchestrate the multi-factor scoring algorithm
- Apply hard constraints to eliminate invalid options
- Rank options by composite score
- Generate rationale for each recommendation
- Handle fallback when no options meet threshold

**Dependencies**: BedScoringService, StaffScoringService, EquipmentScoringService, ResourceScoringService, ScoringFactors

**Methods**:
- `evaluateBedRecommendation(ClinicalAssessment, List<Bed>, List<Ward>, Admission) → List<RecommendationItem>`
- `evaluateStaffRecommendation(ClinicalAssessment, List<Staff>, Admission) → List<RecommendationItem>`
- `evaluateEquipmentRecommendation(ClinicalAssessment, List<Equipment>, Admission) → List<RecommendationItem>`
- `evaluateResourceRecommendation(ClinicalAssessment, List<ResourceInventory>, Admission) → List<RecommendationItem>`

---

### 2.2 BedScoringService

**Package**: `com.hospital.resource.recommendation.domain`

**Responsibilities**:
- Score beds against patient requirements
- Apply isolation match scoring (weight: 0.30)
- Apply bed type match scoring (weight: 0.25)
- Apply ward occupancy scoring (weight: 0.20)
- Apply proximity scoring (weight: 0.10)
- Apply cleaning recency scoring (weight: 0.10)
- Apply equipment availability scoring (weight: 0.05)

**Methods**:
- `scoreBed(Bed, ClinicalAssessment, Ward, List<Equipment>) → ScoringResult`

---

### 2.3 StaffScoringService

**Package**: `com.hospital.resource.recommendation.domain`

**Responsibilities**:
- Score staff against patient requirements
- Apply specialization match (weight: 0.30)
- Apply workload balance (weight: 0.25)
- Apply availability (weight: 0.20)
- Apply certification status (weight: 0.15)
- Apply ward familiarity (weight: 0.10)

**Methods**:
- `scoreStaff(Staff, ClinicalAssessment, Admission) → ScoringResult`

---

### 2.4 EquipmentScoringService

**Package**: `com.hospital.resource.recommendation.domain`

**Responsibilities**:
- Score equipment against patient requirements
- Apply type match (weight: 0.35)
- Apply status (weight: 0.25)
- Apply maintenance recency (weight: 0.15)
- Apply location proximity (weight: 0.15)
- Apply age factor (weight: 0.10)

**Methods**:
- `scoreEquipment(Equipment, ClinicalAssessment, Admission) → ScoringResult`

---

### 2.5 ResourceScoringService

**Package**: `com.hospital.resource.recommendation.domain`

**Responsibilities**:
- Score resource inventory locations against patient requirements
- Apply stock level (weight: 0.30)
- Apply expiration proximity (weight: 0.25)
- Apply location proximity (weight: 0.20)
- Apply criticality match (weight: 0.15)
- Apply historical consumption (weight: 0.10)

**Methods**:
- `scoreResource(ResourceInventory, ClinicalAssessment, Admission) → ScoringResult`

---

### 2.6 BedDomainService

**Package**: `com.hospital.resource.bed.domain`

**Responsibilities**:
- Enforce bed status transition rules
- Validate bed assignment constraints

**Methods**:
- `canTransitionTo(BedStatus current, BedStatus target) → boolean`
- `validateAssignment(Bed, Admission) → ValidationResult`

---

### 2.7 BedCleaningDomainService

**Package**: `com.hospital.resource.bedcleaning.domain`

**Responsibilities**:
- Enforce cleaning workflow state machine
- Validate cleaning completion requirements

**Methods**:
- `canTransitionTo(CleaningStatus current, CleaningStatus target) → boolean`
- `validateCompletion(BedCleaning) → ValidationResult`

---

### 2.8 InventoryDomainService

**Package**: `com.hospital.resource.resource.domain`

**Responsibilities**:
- Validate stock levels before transactions
- Prevent negative stock
- Calculate current stock from transactions

**Methods**:
- `validateTransaction(ResourceInventory, int quantity) → ValidationResult`
- `calculateCurrentStock(UUID resourceInventoryId) → int`

---

### 2.9 WorkloadCalculator

**Package**: `com.hospital.resource.staff.domain`

**Responsibilities**:
- Calculate staff workload score using defined formula
- Apply severity weights and time factors
- Check against role-specific thresholds

**Methods**:
- `calculateWorkload(Staff, List<Admission>, StaffShift) → WorkloadResult`
- `isOverThreshold(WorkloadResult) → boolean`
- `isAlertThreshold(WorkloadResult) → boolean`

---

### 2.10 ShiftDomainService

**Package**: `com.hospital.resource.staff.domain`

**Responsibilities**:
- Validate shift overlap constraints
- Validate minimum staffing requirements

**Methods**:
- `hasOverlap(Staff, StaffShift, List<ShiftAssignment>) → boolean`
- `meetsMinimumStaffing(StaffShift, List<ShiftAssignment>) → boolean`

---

### 2.11 ForecastCalculator

**Package**: `com.hospital.resource.forecast.domain`

**Responsibilities**:
- Apply forecasting models (SMA, WMA)
- Calculate MAPE for accuracy measurement

**Methods**:
- `calculateSMA(List<Integer> historicalData, int period) → double`
- `calculateWMA(List<Integer> historicalData, List<Double> weights) → double`
- `calculateMAPE(List<Double> predicted, List<Double> actual) → double`

---

## 3. Service Interaction Diagram

```
Controller → ApplicationService → DomainService → Repository
                ↓                      ↓
          AuditService            Business Rules
          NotificationService     Calculations
          RecommendationService   Validation
```

---

## 4. Document References

| Document | Reference |
|----------|-----------|
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| System Architecture | `docs/planning/03-system-architecture.md` |
| Domain Model | `docs/planning/06-domain-model.md` |
| Package Structure | `docs/design/04-package-structure.md` |
