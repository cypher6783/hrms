# 07 — DTO Design

## 1. DTO Policy

- All DTOs are immutable (final classes with final fields).
- Request DTOs use Jakarta Bean Validation annotations.
- Response DTOs use records for immutability and conciseness.
- Mapping between entities and DTOs uses MapStruct.
- No business logic in DTOs.

---

## 2. Common DTOs

### 2.1 ApiResponse\<T\>

| Field | Type | Description |
|-------|------|-------------|
| success | boolean | Operation success flag |
| message | String | Human-readable message |
| data | T | Response payload |
| timestamp | Instant | Response timestamp |

### 2.2 PagedResponse\<T\>

| Field | Type | Description |
|-------|------|-------------|
| content | List\<T\> | Page content |
| totalElements | long | Total records |
| totalPages | int | Total pages |
| number | int | Current page (0-based) |
| size | int | Page size |

### 2.3 ErrorResponse

| Field | Type | Description |
|-------|------|-------------|
| success | boolean | Always false |
| message | String | Error description |
| errors | List\<FieldError\> | Field-level errors |
| timestamp | Instant | Error timestamp |

---

## 3. Authentication DTOs

### 3.1 LoginRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| username | String | @NotBlank, @Size(min=3, max=50) | Login username |
| password | String | @NotBlank, @Size(min=8, max=100) | Login password |

### 3.2 LoginResponse

| Field | Type | Description |
|-------|------|-------------|
| accessToken | String | JWT access token |
| refreshToken | String | Refresh token |
| expiresIn | long | Access token TTL in seconds |
| user | UserSummary | Current user info |

### 3.3 UserSummary

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | User ID |
| username | String | Username |
| fullName | String | Display name |
| role | String | User role |

### 3.4 RefreshTokenRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| refreshToken | String | @NotBlank | Refresh token |

### 3.5 RefreshTokenResponse

| Field | Type | Description |
|-------|------|-------------|
| accessToken | String | New access token |
| refreshToken | String | New refresh token |
| expiresIn | long | Access token TTL in seconds |

### 3.6 ChangePasswordRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| currentPassword | String | @NotBlank | Current password |
| newPassword | String | @NotBlank, @Size(min=8), @Pattern(complexity regex) | New password |

---

## 4. Patient DTOs

### 4.1 PatientRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| fullName | String | @NotBlank, @Size(max=100) | Patient name |
| dateOfBirth | LocalDate | @NotNull, @Past | Date of birth |
| gender | String | @NotBlank, @Pattern(MALE\|FEMALE\|OTHER) | Gender |
| phoneNumber | String | @Size(max=20), @Pattern(phone regex) | Contact phone |
| address | String | @Size(max=500) | Address |
| nextOfKinName | String | @Size(max=100) | Next-of-kin name |
| nextOfKinPhone | String | @Size(max=20) | Next-of-kin phone |

### 4.2 PatientResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Patient ID |
| patientNumber | String | Human-readable ID |
| fullName | String | Patient name |
| dateOfBirth | LocalDate | Date of birth |
| gender | String | Gender |
| phoneNumber | String | Contact phone |
| address | String | Address |
| nextOfKinName | String | Next-of-kin name |
| nextOfKinPhone | String | Next-of-kin phone |
| isActive | boolean | Active status |
| createdAt | Instant | Registration timestamp |
| recentAssessments | List\<ClinicalAssessmentResponse\> | Last 5 assessments |

### 4.3 PatientSummaryResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Patient ID |
| patientNumber | String | Human-readable ID |
| fullName | String | Patient name |
| gender | String | Gender |
| phoneNumber | String | Contact phone |
| isActive | boolean | Active status |
| createdAt | Instant | Registration timestamp |

### 4.4 PatientSearchRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| search | String | @Size(max=100) | Name, number, or phone |
| page | Integer | @Min(0), default 0 | Page number |
| size | Integer | @Min(1), @Max(100), default 20 | Page size |
| sort | String | default "createdAt,desc" | Sort field and direction |

---

## 5. Clinical Assessment DTOs

### 5.1 ClinicalAssessmentRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| patientId | UUID | @NotNull | Patient ID |
| admissionId | UUID | nullable | Admission ID (optional) |
| severityLevel | String | @NotBlank, @Pattern(severity enum) | Severity classification |
| triageClassification | String | @NotBlank, @Pattern(triage enum) | Triage level |
| infectionStatus | String | @NotBlank, @Pattern(infection enum) | Infection status |
| clinicalNotes | String | @Size(max=2000) | Clinical notes |
| isReassessment | boolean | default false | Reassessment flag |

### 5.2 ClinicalAssessmentResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Assessment ID |
| patientId | UUID | Patient ID |
| admissionId | UUID | Admission ID (nullable) |
| assessedBy | UserSummary | Assessing clinician |
| severityLevel | String | Severity classification |
| triageClassification | String | Triage level |
| infectionStatus | String | Infection status |
| clinicalNotes | String | Clinical notes |
| isReassessment | boolean | Reassessment flag |
| assessmentTimestamp | Instant | Assessment time |
| createdAt | Instant | Record creation time |

---

## 6. Admission DTOs

### 6.1 AdmissionRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| patientId | UUID | @NotNull | Patient ID |
| wardId | UUID | @NotNull | Ward ID |
| admissionNotes | String | @Size(max=2000) | Admission notes |

### 6.2 AdmissionResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Admission ID |
| admissionNumber | String | Human-readable ID |
| patient | PatientSummary | Patient info |
| ward | WardSummary | Ward info |
| bed | BedSummary | Bed info (nullable) |
| status | String | Admission status |
| admissionNotes | String | Admission notes |
| dischargeOutcome | String | Discharge outcome (nullable) |
| dischargeNotes | String | Discharge notes (nullable) |
| admittedAt | Instant | Admission timestamp |
| dischargedAt | Instant | Discharge timestamp (nullable) |
| isActive | boolean | Active status |
| lengthOfStay | Long | Days since admission |
| latestAssessment | ClinicalAssessmentResponse | Most recent assessment |
| assignedStaff | List\<StaffSummary\> | Assigned staff |
| assignedEquipment | List\<EquipmentSummary\> | Assigned equipment |

### 6.3 AdmissionSummaryResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Admission ID |
| admissionNumber | String | Human-readable ID |
| patientName | String | Patient name |
| wardName | String | Ward name |
| bedNumber | String | Bed number (nullable) |
| status | String | Admission status |
| admittedAt | Instant | Admission timestamp |
| lengthOfStay | Long | Days since admission |

### 6.4 DischargeRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| dischargeOutcome | String | @NotBlank, @Pattern(outcome enum) | Discharge outcome |
| dischargeNotes | String | @Size(max=2000) | Discharge notes |

### 6.5 TransferRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| targetWardId | UUID | @NotNull | Target ward ID |
| targetBedId | UUID | nullable | Target bed ID (nullable) |
| transferReason | String | @NotBlank, @Size(max=500) | Transfer reason |

---

## 7. Bed DTOs

### 7.1 BedRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| bedNumber | String | @NotBlank, @Size(max=20) | Bed number |
| wardId | UUID | @NotNull | Ward ID |
| bedType | String | @NotBlank, @Pattern(bedType enum) | Bed type |
| isIsolationCapable | boolean | default false | Isolation capability |

### 7.2 BedResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Bed ID |
| bedNumber | String | Bed number |
| wardId | UUID | Ward ID |
| wardName | String | Ward name |
| bedType | String | Bed type |
| isIsolationCapable | boolean | Isolation capability |
| status | String | Bed status |
| currentAdmissionId | UUID | Current admission (nullable) |
| lastMaintenanceAt | Instant | Last maintenance (nullable) |
| createdAt | Instant | Creation timestamp |

### 7.3 BedAvailabilityResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Bed ID |
| bedNumber | String | Bed number |
| wardId | UUID | Ward ID |
| wardName | String | Ward name |
| bedType | String | Bed type |
| isIsolationCapable | boolean | Isolation capability |
| lastCleanedAt | Instant | Last cleaning completion |

---

## 8. Bed Cleaning DTOs

### 8.1 CleaningTaskResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Task ID |
| bedId | UUID | Bed ID |
| bedNumber | String | Bed number |
| wardName | String | Ward name |
| admissionId | UUID | Admission ID |
| status | String | Task status |
| assignedTo | StaffSummary | Assigned cleaner (nullable) |
| assignedAt | Instant | Assignment time (nullable) |
| startedAt | Instant | Start time (nullable) |
| completedAt | Instant | Completion time (nullable) |
| verifiedBy | UserSummary | Verifier (nullable) |
| verifiedAt | Instant | Verification time (nullable) |
| cleaningNotes | String | Notes (nullable) |
| createdAt | Instant | Creation timestamp |

### 8.2 CleaningAssignmentRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| staffId | UUID | @NotNull | Staff member ID |

### 8.3 CleaningCompletionRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| cleaningNotes | String | @Size(max=500) | Cleaning notes |

---

## 9. Ward DTOs

### 9.1 WardRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| name | String | @NotBlank, @Size(max=50) | Ward name |
| wardType | String | @NotBlank, @Pattern(wardType enum) | Ward type |
| maxBedCapacity | Integer | @NotNull, @Min(1) | Maximum beds |
| isolationLevel | String | @NotBlank, @Pattern(isolation enum) | Isolation level |
| equipmentZone | String | @Size(max=50) | Equipment zone |

### 9.2 WardResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Ward ID |
| name | String | Ward name |
| wardType | String | Ward type |
| maxBedCapacity | Integer | Maximum beds |
| currentBedCount | Integer | Current bed count |
| isolationLevel | String | Isolation level |
| equipmentZone | String | Equipment zone |
| status | String | Ward status |
| occupancyRate | Double | Occupancy percentage |
| createdAt | Instant | Creation timestamp |

### 9.3 WardStatusResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Ward ID |
| name | String | Ward name |
| totalBeds | Integer | Total beds |
| availableBeds | Integer | Available beds |
| occupiedBeds | Integer | Occupied beds |
| cleaningBeds | Integer | Beds being cleaned |
| maintenanceBeds | Integer | Beds under maintenance |
| occupancyRate | Double | Occupancy percentage |

---

## 10. Staff DTOs

### 10.1 StaffRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| fullName | String | @NotBlank, @Size(max=100) | Staff name |
| role | String | @NotBlank, @Pattern(staffRole enum) | Staff role |
| specialization | String | @Size(max=50) | Specialization |
| wardId | UUID | nullable | Assigned ward |
| certificationStatus | String | @Pattern(certStatus enum) | Certification status |
| certificationExpiry | LocalDate | nullable | Certification expiry |
| maxWorkloadThreshold | BigDecimal | @Min(0) | Max workload |

### 10.2 StaffResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Staff ID |
| staffNumber | String | Human-readable ID |
| fullName | String | Staff name |
| role | String | Staff role |
| specialization | String | Specialization |
| certificationStatus | String | Certification status |
| certificationExpiry | LocalDate | Certification expiry |
| wardId | UUID | Assigned ward (nullable) |
| wardName | String | Ward name (nullable) |
| availabilityStatus | String | Availability status |
| workloadScore | BigDecimal | Current workload score |
| maxWorkloadThreshold | BigDecimal | Max workload threshold |
| createdAt | Instant | Creation timestamp |

### 10.3 StaffSummary

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Staff ID |
| staffNumber | String | Human-readable ID |
| fullName | String | Staff name |
| role | String | Staff role |
| specialization | String | Specialization |

### 10.4 StaffWorkloadResponse

| Field | Type | Description |
|-------|------|-------------|
| staffId | UUID | Staff ID |
| staffNumber | String | Human-readable ID |
| fullName | String | Staff name |
| role | String | Staff role |
| workloadScore | BigDecimal | Current workload |
| maxThreshold | BigDecimal | Maximum threshold |
| utilizationPercentage | Double | Workload as percentage |
| assignedPatients | Integer | Number of assigned patients |
| isOverThreshold | boolean | Exceeds threshold |

---

## 11. Shift DTOs

### 11.1 ShiftRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| shiftName | String | @NotBlank, @Size(max=50) | Shift name |
| shiftDate | LocalDate | @NotNull, @FutureOrPresent | Shift date |
| startTime | LocalTime | @NotNull | Start time |
| endTime | LocalTime | @NotNull | End time |
| wardId | UUID | @NotNull | Ward ID |
| minRequiredStaff | Integer | @NotNull, @Min(1) | Minimum staff |
| maxStaff | Integer | @NotNull, @Min(1) | Maximum staff |

### 11.2 ShiftResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Shift ID |
| shiftName | String | Shift name |
| shiftDate | LocalDate | Shift date |
| startTime | LocalTime | Start time |
| endTime | LocalTime | End time |
| wardId | UUID | Ward ID |
| wardName | String | Ward name |
| minRequiredStaff | Integer | Minimum staff |
| maxStaff | Integer | Maximum staff |
| assignedCount | Integer | Currently assigned |
| status | String | Shift status |
| createdAt | Instant | Creation timestamp |

### 11.3 ShiftAssignmentRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| staffId | UUID | @NotNull | Staff member ID |

### 11.4 ShiftAssignmentResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Assignment ID |
| staffId | UUID | Staff ID |
| staffNumber | String | Staff number |
| staffName | String | Staff name |
| shiftId | UUID | Shift ID |
| status | String | Assignment status |
| assignedBy | UserSummary | Assigning user |
| createdAt | Instant | Assignment timestamp |

---

## 12. Equipment DTOs

### 12.1 EquipmentRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| name | String | @NotBlank, @Size(max=100) | Equipment name |
| equipmentType | String | @NotBlank, @Pattern(type enum) | Equipment type |
| serialNumber | String | @NotBlank, @Size(max=50) | Serial number |
| location | String | @Size(max=100) | Location |

### 12.2 EquipmentResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Equipment ID |
| name | String | Equipment name |
| equipmentType | String | Equipment type |
| serialNumber | String | Serial number |
| location | String | Location |
| status | String | Equipment status |
| assignedAdmissionId | UUID | Assigned admission (nullable) |
| assignedWardId | UUID | Assigned ward (nullable) |
| createdAt | Instant | Creation timestamp |

### 12.3 EquipmentSummary

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Equipment ID |
| name | String | Equipment name |
| equipmentType | String | Equipment type |
| serialNumber | String | Serial number |
| status | String | Equipment status |

### 12.4 MaintenanceRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| maintenanceType | String | @NotBlank, @Pattern(type enum) | Maintenance type |
| scheduledDate | LocalDate | @NotNull, @FutureOrPresent | Scheduled date |
| performedBy | String | @Size(max=100) | Performed by |

### 12.5 MaintenanceResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Maintenance ID |
| equipmentId | UUID | Equipment ID |
| equipmentName | String | Equipment name |
| maintenanceType | String | Maintenance type |
| status | String | Maintenance status |
| scheduledDate | LocalDate | Scheduled date |
| completedDate | LocalDate | Completed date (nullable) |
| performedBy | String | Performed by (nullable) |
| maintenanceNotes | String | Notes (nullable) |
| cost | BigDecimal | Cost (nullable) |
| nextMaintenanceDate | LocalDate | Next maintenance (nullable) |
| createdAt | Instant | Creation timestamp |

---

## 13. Resource DTOs

### 13.1 ResourceRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| name | String | @NotBlank, @Size(max=100) | Resource name |
| category | String | @NotBlank, @Pattern(category enum) | Resource category |
| unitOfMeasure | String | @NotBlank, @Size(max=20) | Unit of measure |
| minimumThreshold | Integer | @Min(0) | Minimum threshold |
| reorderPoint | Integer | @Min(0) | Reorder point |
| criticalityLevel | String | @NotBlank, @Pattern(criticality enum) | Criticality level |

### 13.2 ResourceResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Resource ID |
| name | String | Resource name |
| category | String | Resource category |
| unitOfMeasure | String | Unit of measure |
| minimumThreshold | Integer | Minimum threshold |
| reorderPoint | Integer | Reorder point |
| criticalityLevel | String | Criticality level |
| defaultSupplierId | UUID | Default supplier (nullable) |
| createdAt | Instant | Creation timestamp |

### 13.3 InventoryTransactionRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| resourceInventoryId | UUID | @NotNull | Inventory record ID |
| transactionType | String | @NotBlank, @Pattern(type enum) | Transaction type |
| quantity | Integer | @NotNull | Quantity (+ for inbound, - for outbound) |
| admissionId | UUID | nullable | Linked admission |
| referenceDocument | String | @Size(max=100) | Reference document |
| notes | String | @Size(max=500) | Notes |

### 13.4 InventoryTransactionResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Transaction ID |
| resourceInventoryId | UUID | Inventory record ID |
| resourceName | String | Resource name |
| location | String | Location |
| transactionType | String | Transaction type |
| quantity | Integer | Quantity |
| admissionId | UUID | Admission (nullable) |
| referenceDocument | String | Reference document (nullable) |
| notes | String | Notes (nullable) |
| performedBy | UserSummary | Performer |
| transactionTimestamp | Instant | Transaction time |
| createdAt | Instant | Record creation |

### 13.5 InventoryStockResponse

| Field | Type | Description |
|-------|------|-------------|
| resourceId | UUID | Resource ID |
| resourceName | String | Resource name |
| location | String | Location |
| currentStock | Integer | Current stock |
| expirationDate | LocalDate | Expiration (nullable) |
| batchNumber | String | Batch number (nullable) |
| unitOfMeasure | String | Unit of measure |
| isBelowThreshold | boolean | Below minimum threshold |

---

## 14. Supplier DTOs

### 14.1 SupplierRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| name | String | @NotBlank, @Size(max=100) | Supplier name |
| contactPerson | String | @Size(max=100) | Contact person |
| phoneNumber | String | @Size(max=20) | Phone number |
| email | String | @Email, @Size(max=100) | Email address |
| address | String | @Size(max=500) | Address |
| leadTimeDays | Integer | @Min(0) | Lead time in days |

### 14.2 SupplierResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Supplier ID |
| name | String | Supplier name |
| contactPerson | String | Contact person (nullable) |
| phoneNumber | String | Phone number (nullable) |
| email | String | Email (nullable) |
| address | String | Address (nullable) |
| leadTimeDays | Integer | Lead time (nullable) |
| isActive | boolean | Active status |
| createdAt | Instant | Creation timestamp |

---

## 15. Recommendation DTOs

### 15.1 RecommendationResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Recommendation ID |
| admissionId | UUID | Admission ID |
| batchType | String | Batch type |
| status | String | Recommendation status |
| items | List\<RecommendationItemResponse\> | Recommendation items |
| generatedAt | Instant | Generation time |
| expiresAt | Instant | Expiration time |

### 15.2 RecommendationItemResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Item ID |
| itemType | String | Item type (Bed, Staff, Equipment, Resource) |
| recommendedEntityType | String | Entity type |
| recommendedEntityId | UUID | Entity ID |
| rank | Integer | Recommendation rank |
| confidenceScore | BigDecimal | Confidence score |
| scoringBreakdown | Map\<String, Object\> | Factor scores (nullable) |
| rationale | String | Human-readable explanation |
| status | String | Item status |
| decision | RecommendationDecisionResponse | User decision (nullable) |

### 15.3 RecommendationDecisionRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| overriddenEntityType | String | nullable | Alternative entity type |
| overriddenEntityId | UUID | nullable | Alternative entity ID |
| overrideJustification | String | @NotBlank (if overriding) | Override reason |

### 15.4 RecommendationDecisionResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Decision ID |
| decisionType | String | Decision type |
| overrideJustification | String | Override reason (nullable) |
| decidedBy | UserSummary | Decision maker |
| decidedAt | Instant | Decision time |

---

## 16. Forecast DTOs

### 16.1 ForecastRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| forecastType | String | @NotBlank, @Pattern(type enum) | Forecast type |
| forecastHorizon | String | @NotBlank, @Pattern(horizon enum) | Horizon (7-day, 14-day, 30-day) |

### 16.2 ForecastResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Forecast ID |
| forecastType | String | Forecast type |
| forecastHorizon | String | Horizon |
| targetPeriodStart | LocalDate | Period start |
| targetPeriodEnd | LocalDate | Period end |
| predictedValues | Map\<String, Object\> | Predicted data |
| modelUsed | String | Model algorithm |
| accuracyScore | BigDecimal | Accuracy (nullable) |
| generatedAt | Instant | Generation time |

---

## 17. Notification DTOs

### 17.1 NotificationResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Notification ID |
| title | String | Title |
| message | String | Message body |
| notificationType | String | Notification type |
| sourceModule | String | Source module |
| sourceEntityType | String | Source entity type (nullable) |
| sourceEntityId | UUID | Source entity (nullable) |
| isRead | boolean | Read status |
| readAt | Instant | Read time (nullable) |
| createdAt | Instant | Creation timestamp |

---

## 18. Report DTOs

### 18.1 ReportRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| fromDate | LocalDate | @NotNull | Report start date |
| toDate | LocalDate | @NotNull, @After(fromDate) | Report end date |
| wardId | UUID | nullable | Filter by ward |
| format | String | @Pattern(PDF\|CSV), default JSON | Output format |

### 18.2 OccupancyReportResponse

| Field | Type | Description |
|-------|------|-------------|
| reportPeriod | String | Date range |
| overallOccupancyRate | Double | Overall occupancy |
| wardBreakdown | List\<WardOccupancyDetail\> | Per-ward breakdown |
| averageLengthOfStay | Double | Average LOS in days |
| bedTurnoverRate | Double | Turnover rate |

### 18.3 ResourceReportResponse

| Field | Type | Description |
|-------|------|-------------|
| reportPeriod | String | Date range |
| categoryBreakdown | List\<ResourceCategoryDetail\> | Per-category breakdown |
| lowStockItems | List\<InventoryStockResponse\> | Items below threshold |
| totalConsumption | Map\<String, Integer\> | Consumption by resource |

### 18.4 CdsPerformanceReportResponse

| Field | Type | Description |
|-------|------|-------------|
| reportPeriod | String | Date range |
| totalRecommendations | Integer | Total generated |
| acceptanceRate | Double | Acceptance percentage |
| overrideRate | Double | Override percentage |
| averageConfidence | BigDecimal | Average confidence score |
| breakdownByType | Map\<String, Object\> | Per-type metrics |

---

## 19. Admin DTOs

### 19.1 UserManagementRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| username | String | @NotBlank, @Size(min=3, max=50) | Username |
| email | String | @NotBlank, @Email | Email |
| password | String | @NotBlank, @Size(min=8) | Password |
| fullName | String | @NotBlank, @Size(max=100) | Full name |
| role | String | @NotBlank, @Pattern(role enum) | User role |

### 19.2 UserManagementResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | User ID |
| username | String | Username |
| email | String | Email |
| fullName | String | Full name |
| role | String | User role |
| status | String | Account status |
| lastLoginAt | Instant | Last login (nullable) |
| createdAt | Instant | Creation timestamp |

### 19.3 SystemConfigRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| configValue | String | @NotBlank | Configuration value |

### 19.4 SystemConfigResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Config ID |
| configKey | String | Configuration key |
| configValue | String | Configuration value |
| valueType | String | Value type |
| description | String | Description (nullable) |
| category | String | Category |
| defaultValue | String | Default value (nullable) |
| requiresRestart | boolean | Restart required |

---

## 20. Audit DTOs

### 20.1 AuditLogResponse

| Field | Type | Description |
|-------|------|-------------|
| id | UUID | Log ID |
| timestamp | Instant | Event timestamp |
| userId | UUID | User ID (nullable) |
| username | String | Username (nullable) |
| actionType | String | Action type |
| entityType | String | Entity type |
| entityId | UUID | Entity ID |
| beforeValue | Map\<String, Object\> | Before state (nullable) |
| afterValue | Map\<String, Object\> | After state (nullable) |
| ipAddress | String | IP address (nullable) |

### 20.2 AuditSearchRequest

| Field | Type | Validation | Description |
|-------|------|------------|-------------|
| entityType | String | nullable | Filter by entity type |
| entityId | UUID | nullable | Filter by entity ID |
| userId | UUID | nullable | Filter by user |
| actionType | String | nullable | Filter by action type |
| fromDate | LocalDate | nullable | Start date |
| toDate | LocalDate | nullable | End date |
| page | Integer | @Min(0), default 0 | Page number |
| size | Integer | @Min(1), @Max(100), default 20 | Page size |

---

## 21. Document References

| Document | Reference |
|----------|-----------|
| API Specification | `docs/design/03-api-specification.md` |
| Entity Design | `docs/design/06-entity-design.md` |
| Validation Rules | `docs/design/13-validation-rules.md` |
