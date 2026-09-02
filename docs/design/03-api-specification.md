# 03 — API Specification

## 1. API Conventions

### 1.1 Base URL

```
/api/v1
```

### 1.2 Standard Response Envelope

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { },
  "timestamp": "2026-06-28T10:30:00Z"
}
```

### 1.3 Error Response Format

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    { "field": "fullName", "message": "Full name is required" }
  ],
  "timestamp": "2026-06-28T10:30:00Z"
}
```

### 1.4 Pagination

Query parameters: `page` (default 0), `size` (default 20, max 100), `sort` (field,direction).

Response includes `content`, `totalElements`, `totalPages`, `number`, `size`.

---

## 2. Authentication Endpoints

### POST /api/v1/auth/login

| Attribute | Value |
|-----------|-------|
| Purpose | Authenticate user and issue tokens |
| Auth | Public |
| Request | `{ "username": "string", "password": "string" }` |
| Response | `{ "accessToken": "string", "refreshToken": "string", "expiresIn": 900, "user": { "id", "username", "role", "fullName" } }` |
| Status | 200 OK, 401 Unauthorized, 423 Locked |

### POST /api/v1/auth/refresh

| Attribute | Value |
|-----------|-------|
| Purpose | Refresh access token using refresh token |
| Auth | Public (valid refresh token required) |
| Request | `{ "refreshToken": "string" }` |
| Response | `{ "accessToken": "string", "refreshToken": "string", "expiresIn": 900 }` |
| Status | 200 OK, 401 Unauthorized |

### POST /api/v1/auth/logout

| Attribute | Value |
|-----------|-------|
| Purpose | Revoke refresh token and invalidate session |
| Auth | Bearer JWT |
| Request | `{ "refreshToken": "string" }` |
| Response | `{ "message": "Logged out successfully" }` |
| Status | 200 OK, 401 Unauthorized |

### POST /api/v1/auth/change-password

| Attribute | Value |
|-----------|-------|
| Purpose | Change current user's password |
| Auth | Bearer JWT |
| Request | `{ "currentPassword": "string", "newPassword": "string" }` |
| Response | `{ "message": "Password changed successfully" }` |
| Status | 200 OK, 400 Bad Request, 401 Unauthorized |

---

## 3. Patient Endpoints

### POST /api/v1/patients

| Attribute | Value |
|-----------|-------|
| Purpose | Register a new patient |
| Auth | Bearer JWT |
| Roles | NURSING_OFFICER, ADMINISTRATOR |
| Request | `{ "fullName", "dateOfBirth", "gender", "phoneNumber", "address", "nextOfKinName", "nextOfKinPhone" }` |
| Response | PatientResponse with generated patientNumber |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/patients

| Attribute | Value |
|-----------|-------|
| Purpose | List patients with search and pagination |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `search`, `page`, `size`, `sort` |
| Response | PagedResponse\<PatientSummaryResponse\> |
| Status | 200 OK |

### GET /api/v1/patients/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Get patient details |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | PatientResponse with recent assessments |
| Status | 200 OK, 404 Not Found |

### PUT /api/v1/patients/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Update patient demographics |
| Auth | Bearer JWT |
| Roles | NURSING_OFFICER, ADMINISTRATOR |
| Request | `{ "fullName", "phoneNumber", "address", "nextOfKinName", "nextOfKinPhone" }` |
| Response | PatientResponse |
| Status | 200 OK, 404 Not Found, 400 Validation Error |

### DELETE /api/v1/patients/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Soft delete (deactivate) patient |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Response | `{ "message": "Patient deactivated" }` |
| Status | 200 OK, 404 Not Found |

---

## 4. Clinical Assessment Endpoints

### POST /api/v1/assessments

| Attribute | Value |
|-----------|-------|
| Purpose | Record a clinical assessment |
| Auth | Bearer JWT |
| Roles | MEDICAL_DOCTOR, NURSING_OFFICER |
| Request | `{ "patientId", "admissionId", "severityLevel", "triageClassification", "infectionStatus", "clinicalNotes", "isReassessment" }` |
| Response | ClinicalAssessmentResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/assessments/patient/{patientId}

| Attribute | Value |
|-----------|-------|
| Purpose | Get assessment timeline for a patient |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `page`, `size` |
| Response | PagedResponse\<ClinicalAssessmentResponse\> |
| Status | 200 OK, 404 Not Found |

### GET /api/v1/assessments/admission/{admissionId}/latest

| Attribute | Value |
|-----------|-------|
| Purpose | Get most recent assessment for an admission |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | ClinicalAssessmentResponse |
| Status | 200 OK, 404 Not Found |

---

## 5. Admission Endpoints

### POST /api/v1/admissions

| Attribute | Value |
|-----------|-------|
| Purpose | Create a new admission |
| Auth | Bearer JWT |
| Roles | NURSING_OFFICER, MEDICAL_DOCTOR |
| Request | `{ "patientId", "wardId", "admissionNotes" }` |
| Response | AdmissionResponse |
| Status | 201 Created, 400 Validation Error, 409 Conflict (active admission exists) |

### GET /api/v1/admissions

| Attribute | Value |
|-----------|-------|
| Purpose | List admissions with filtering |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `status`, `wardId`, `patientId`, `page`, `size`, `sort` |
| Response | PagedResponse\<AdmissionSummaryResponse\> |
| Status | 200 OK |

### GET /api/v1/admissions/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Get admission details |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | AdmissionResponse with assessments, staff, equipment, resources |
| Status | 200 OK, 404 Not Found |

### PUT /api/v1/admissions/{id}/assign-bed

| Attribute | Value |
|-----------|-------|
| Purpose | Assign a bed to an admission |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, ADMINISTRATOR |
| Request | `{ "bedId" }` |
| Response | AdmissionResponse |
| Status | 200 OK, 400 Validation Error |

### POST /api/v1/admissions/{id}/transfer

| Attribute | Value |
|-----------|-------|
| Purpose | Transfer patient to another ward/bed |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, MEDICAL_DOCTOR |
| Request | `{ "targetWardId", "targetBedId", "transferReason" }` |
| Response | AdmissionResponse (new admission record) |
| Status | 201 Created, 400 Validation Error |

### POST /api/v1/admissions/{id}/discharge

| Attribute | Value |
|-----------|-------|
| Purpose | Discharge a patient |
| Auth | Bearer JWT |
| Roles | MEDICAL_DOCTOR, WARD_MANAGER |
| Request | `{ "dischargeOutcome", "dischargeNotes" }` |
| Response | AdmissionResponse |
| Status | 200 OK, 400 Validation Error |

---

## 6. Bed Endpoints

### POST /api/v1/beds

| Attribute | Value |
|-----------|-------|
| Purpose | Register a new bed |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "bedNumber", "wardId", "bedType", "isIsolationCapable" }` |
| Response | BedResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/beds

| Attribute | Value |
|-----------|-------|
| Purpose | List beds with filtering |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `wardId`, `status`, `bedType`, `page`, `size` |
| Response | PagedResponse\<BedResponse\> |
| Status | 200 OK |

### GET /api/v1/beds/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Get bed details |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | BedResponse |
| Status | 200 OK, 404 Not Found |

### PUT /api/v1/beds/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Update bed details |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "bedType", "isIsolationCapable" }` |
| Response | BedResponse |
| Status | 200 OK, 404 Not Found |

### GET /api/v1/beds/available

| Attribute | Value |
|-----------|-------|
| Purpose | Get available beds for recommendation |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, NURSING_OFFICER, MEDICAL_DOCTOR |
| Query | `wardId`, `bedType`, `isolationCapable` |
| Response | List\<BedAvailabilityResponse\> |
| Status | 200 OK |

---

## 7. Bed Cleaning Endpoints

### GET /api/v1/cleaning/tasks

| Attribute | Value |
|-----------|-------|
| Purpose | List pending cleaning tasks |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, ADMINISTRATOR |
| Query | `status`, `wardId`, `page`, `size` |
| Response | PagedResponse\<CleaningTaskResponse\> |
| Status | 200 OK |

### PUT /api/v1/cleaning/tasks/{id}/assign

| Attribute | Value |
|-----------|-------|
| Purpose | Assign a cleaner to a task |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER |
| Request | `{ "staffId" }` |
| Response | CleaningTaskResponse |
| Status | 200 OK, 400 Validation Error |

### PUT /api/v1/cleaning/tasks/{id}/start

| Attribute | Value |
|-----------|-------|
| Purpose | Mark cleaning as started |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, NURSING_OFFICER |
| Response | CleaningTaskResponse |
| Status | 200 OK |

### PUT /api/v1/cleaning/tasks/{id}/complete

| Attribute | Value |
|-----------|-------|
| Purpose | Mark cleaning as completed |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, NURSING_OFFICER |
| Request | `{ "cleaningNotes" }` |
| Response | CleaningTaskResponse |
| Status | 200 OK |

### PUT /api/v1/cleaning/tasks/{id}/verify

| Attribute | Value |
|-----------|-------|
| Purpose | Verify cleaning completion |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, ADMINISTRATOR |
| Response | CleaningTaskResponse |
| Status | 200 OK |

---

## 8. Ward Endpoints

### POST /api/v1/wards

| Attribute | Value |
|-----------|-------|
| Purpose | Create a new ward |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "name", "wardType", "maxBedCapacity", "isolationLevel", "equipmentZone" }` |
| Response | WardResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/wards

| Attribute | Value |
|-----------|-------|
| Purpose | List all wards |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `status`, `page`, `size` |
| Response | PagedResponse\<WardResponse\> |
| Status | 200 OK |

### GET /api/v1/wards/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Get ward details with bed count |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | WardResponse with occupancy data |
| Status | 200 OK, 404 Not Found |

### PUT /api/v1/wards/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Update ward configuration |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "wardType", "maxBedCapacity", "isolationLevel", "equipmentZone" }` |
| Response | WardResponse |
| Status | 200 OK, 404 Not Found |

---

## 9. Staff Endpoints

### POST /api/v1/staff

| Attribute | Value |
|-----------|-------|
| Purpose | Register a new staff member |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "fullName", "role", "specialization", "wardId", "certificationStatus", "certificationExpiry" }` |
| Response | StaffResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/staff

| Attribute | Value |
|-----------|-------|
| Purpose | List staff with filtering |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `wardId`, `role`, `availabilityStatus`, `page`, `size` |
| Response | PagedResponse\<StaffResponse\> |
| Status | 200 OK |

### GET /api/v1/staff/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Get staff details with workload |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | StaffResponse with workloadScore |
| Status | 200 OK, 404 Not Found |

### PUT /api/v1/staff/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Update staff profile |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "specialization", "wardId", "availabilityStatus" }` |
| Response | StaffResponse |
| Status | 200 OK, 404 Not Found |

---

## 10. Shift Endpoints

### POST /api/v1/shifts

| Attribute | Value |
|-----------|-------|
| Purpose | Create a new shift |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, ADMINISTRATOR |
| Request | `{ "shiftName", "shiftDate", "startTime", "endTime", "wardId", "minRequiredStaff", "maxStaff" }` |
| Response | ShiftResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/shifts

| Attribute | Value |
|-----------|-------|
| Purpose | List shifts with filtering |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `wardId`, `shiftDate`, `status`, `page`, `size` |
| Response | PagedResponse\<ShiftResponse\> |
| Status | 200 OK |

### POST /api/v1/shifts/{id}/assign

| Attribute | Value |
|-----------|-------|
| Purpose | Assign staff to a shift |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER |
| Request | `{ "staffId" }` |
| Response | ShiftAssignmentResponse |
| Status | 201 Created, 400 Validation Error, 409 Conflict (overlapping shift) |

---

## 11. Equipment Endpoints

### POST /api/v1/equipment

| Attribute | Value |
|-----------|-------|
| Purpose | Register new equipment |
| Auth | Bearer JWT |
| Roles | EQUIPMENT_OFFICER, ADMINISTRATOR |
| Request | `{ "name", "equipmentType", "serialNumber", "location" }` |
| Response | EquipmentResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/equipment

| Attribute | Value |
|-----------|-------|
| Purpose | List equipment with filtering |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `status`, `equipmentType`, `wardId`, `page`, `size` |
| Response | PagedResponse\<EquipmentResponse\> |
| Status | 200 OK |

### GET /api/v1/equipment/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Get equipment details |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | EquipmentResponse |
| Status | 200 OK, 404 Not Found |

### PUT /api/v1/equipment/{id}/assign

| Attribute | Value |
|-----------|-------|
| Purpose | Assign equipment to admission |
| Auth | Bearer JWT |
| Roles | EQUIPMENT_OFFICER, WARD_MANAGER |
| Request | `{ "admissionId" }` |
| Response | EquipmentResponse |
| Status | 200 OK, 400 Validation Error |

### POST /api/v1/equipment/{id}/maintenance

| Attribute | Value |
|-----------|-------|
| Purpose | Schedule maintenance |
| Auth | Bearer JWT |
| Roles | EQUIPMENT_OFFICER |
| Request | `{ "maintenanceType", "scheduledDate", "performedBy" }` |
| Response | MaintenanceResponse |
| Status | 201 Created |

---

## 12. Resource Endpoints

### POST /api/v1/resources

| Attribute | Value |
|-----------|-------|
| Purpose | Define a new resource type |
| Auth | Bearer JWT |
| Roles | RESOURCE_MANAGER, ADMINISTRATOR |
| Request | `{ "name", "category", "unitOfMeasure", "minimumThreshold", "reorderPoint", "criticalityLevel" }` |
| Response | ResourceResponse |
| Status | 201 Created |

### GET /api/v1/resources

| Attribute | Value |
|-----------|-------|
| Purpose | List resources |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `category`, `criticalityLevel`, `page`, `size` |
| Response | PagedResponse\<ResourceResponse\> |
| Status | 200 OK |

### POST /api/v1/inventory/transactions

| Attribute | Value |
|-----------|-------|
| Purpose | Record an inventory transaction |
| Auth | Bearer JWT |
| Roles | RESOURCE_MANAGER, ADMINISTRATOR |
| Request | `{ "resourceInventoryId", "transactionType", "quantity", "admissionId", "referenceDocument", "notes" }` |
| Response | InventoryTransactionResponse |
| Status | 201 Created, 400 Validation Error (negative stock) |

### GET /api/v1/inventory/transactions

| Attribute | Value |
|-----------|-------|
| Purpose | List inventory transactions |
| Auth | Bearer JWT |
| Roles | RESOURCE_MANAGER, ADMINISTRATOR |
| Query | `resourceId`, `transactionType`, `fromDate`, `toDate`, `page`, `size` |
| Response | PagedResponse\<InventoryTransactionResponse\> |
| Status | 200 OK |

### GET /api/v1/inventory/stock

| Attribute | Value |
|-----------|-------|
| Purpose | Get current stock levels |
| Auth | Bearer JWT |
| Roles | RESOURCE_MANAGER, ADMINISTRATOR, WARD_MANAGER |
| Query | `resourceId`, `location` |
| Response | List\<InventoryStockResponse\> |
| Status | 200 OK |

---

## 13. Supplier Endpoints

### POST /api/v1/suppliers

| Attribute | Value |
|-----------|-------|
| Purpose | Register a new supplier |
| Auth | Bearer JWT |
| Roles | RESOURCE_MANAGER, ADMINISTRATOR |
| Request | `{ "name", "contactPerson", "phoneNumber", "email", "address", "leadTimeDays" }` |
| Response | SupplierResponse |
| Status | 201 Created |

### GET /api/v1/suppliers

| Attribute | Value |
|-----------|-------|
| Purpose | List suppliers |
| Auth | Bearer JWT |
| Roles | RESOURCE_MANAGER, ADMINISTRATOR |
| Query | `isActive`, `page`, `size` |
| Response | PagedResponse\<SupplierResponse\> |
| Status | 200 OK |

---

## 14. Recommendation Endpoints

### POST /api/v1/recommendations/generate

| Attribute | Value |
|-----------|-------|
| Purpose | Generate recommendations for an admission |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, MEDICAL_DOCTOR, NURSING_OFFICER |
| Request | `{ "admissionId", "batchType" }` |
| Response | RecommendationResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/recommendations/admission/{admissionId}/pending

| Attribute | Value |
|-----------|-------|
| Purpose | Get pending recommendations for an admission |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, MEDICAL_DOCTOR, NURSING_OFFICER |
| Response | RecommendationResponse |
| Status | 200 OK, 404 Not Found |

### POST /api/v1/recommendations/{recommendationId}/items/{itemId}/accept

| Attribute | Value |
|-----------|-------|
| Purpose | Accept a recommendation item |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, MEDICAL_DOCTOR, ADMINISTRATOR |
| Response | RecommendationDecisionResponse |
| Status | 200 OK |

### POST /api/v1/recommendations/{recommendationId}/items/{itemId}/override

| Attribute | Value |
|-----------|-------|
| Purpose | Override a recommendation item |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, MEDICAL_DOCTOR, ADMINISTRATOR |
| Request | `{ "overriddenEntityType", "overriddenEntityId", "overrideJustification" }` |
| Response | RecommendationDecisionResponse |
| Status | 200 OK, 400 Validation Error (justification required) |

---

## 15. Forecast Endpoints

### POST /api/v1/forecasts/generate

| Attribute | Value |
|-----------|-------|
| Purpose | Generate a forecast |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, ADMINISTRATOR |
| Request | `{ "forecastType", "forecastHorizon" }` |
| Response | ForecastResponse |
| Status | 201 Created |

### GET /api/v1/forecasts

| Attribute | Value |
|-----------|-------|
| Purpose | List forecast snapshots |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, ADMINISTRATOR |
| Query | `forecastType`, `forecastHorizon`, `page`, `size` |
| Response | PagedResponse\<ForecastResponse\> |
| Status | 200 OK |

---

## 16. Notification Endpoints

### GET /api/v1/notifications

| Attribute | Value |
|-----------|-------|
| Purpose | Get current user's notifications |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Query | `isRead`, `type`, `page`, `size` |
| Response | PagedResponse\<NotificationResponse\> |
| Status | 200 OK |

### PUT /api/v1/notifications/{id}/read

| Attribute | Value |
|-----------|-------|
| Purpose | Mark notification as read |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | NotificationResponse |
| Status | 200 OK |

### PUT /api/v1/notifications/read-all

| Attribute | Value |
|-----------|-------|
| Purpose | Mark all notifications as read |
| Auth | Bearer JWT |
| Roles | All authenticated users |
| Response | `{ "message": "All notifications marked as read" }` |
| Status | 200 OK |

---

## 17. Report Endpoints

### GET /api/v1/reports/occupancy

| Attribute | Value |
|-----------|-------|
| Purpose | Generate bed occupancy report |
| Auth | Bearer JWT |
| Roles | WARD_MANAGER, ADMINISTRATOR, DASHBOARD_VIEWER |
| Query | `wardId`, `fromDate`, `toDate`, `format` (PDF, CSV) |
| Response | Report file or JSON |
| Status | 200 OK |

### GET /api/v1/reports/resource-utilization

| Attribute | Value |
|-----------|-------|
| Purpose | Generate resource utilization report |
| Auth | Bearer JWT |
| Roles | RESOURCE_MANAGER, ADMINISTRATOR, DASHBOARD_VIEWER |
| Query | `category`, `fromDate`, `toDate`, `format` |
| Response | Report file or JSON |
| Status | 200 OK |

### GET /api/v1/reports/cds-performance

| Attribute | Value |
|-----------|-------|
| Purpose | Generate CDS engine performance report |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR, WARD_MANAGER |
| Query | `fromDate`, `toDate`, `format` |
| Response | Report file or JSON |
| Status | 200 OK |

### GET /api/v1/reports/audit

| Attribute | Value |
|-----------|-------|
| Purpose | Generate audit trail report |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Query | `entityType`, `userId`, `fromDate`, `toDate`, `format` |
| Response | Report file or JSON |
| Status | 200 OK |

---

## 18. Admin Endpoints

### POST /api/v1/admin/users

| Attribute | Value |
|-----------|-------|
| Purpose | Create a new user account |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "username", "email", "password", "fullName", "role" }` |
| Response | UserManagementResponse |
| Status | 201 Created, 400 Validation Error |

### GET /api/v1/admin/users

| Attribute | Value |
|-----------|-------|
| Purpose | List user accounts |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Query | `role`, `status`, `page`, `size` |
| Response | PagedResponse\<UserManagementResponse\> |
| Status | 200 OK |

### PUT /api/v1/admin/users/{id}

| Attribute | Value |
|-----------|-------|
| Purpose | Update user account |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "fullName", "role", "status" }` |
| Response | UserManagementResponse |
| Status | 200 OK |

### PUT /api/v1/admin/users/{id}/unlock

| Attribute | Value |
|-----------|-------|
| Purpose | Unlock a locked account |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Response | `{ "message": "Account unlocked" }` |
| Status | 200 OK |

### GET /api/v1/admin/config

| Attribute | Value |
|-----------|-------|
| Purpose | List system configurations |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Query | `category` |
| Response | List\<SystemConfigResponse\> |
| Status | 200 OK |

### PUT /api/v1/admin/config/{key}

| Attribute | Value |
|-----------|-------|
| Purpose | Update a system configuration |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Request | `{ "configValue" }` |
| Response | SystemConfigResponse |
| Status | 200 OK, 400 Validation Error |

---

## 19. Audit Endpoints

### GET /api/v1/audit-logs

| Attribute | Value |
|-----------|-------|
| Purpose | Query audit logs |
| Auth | Bearer JWT |
| Roles | ADMINISTRATOR |
| Query | `entityType`, `entityId`, `userId`, `actionType`, `fromDate`, `toDate`, `page`, `size` |
| Response | PagedResponse\<AuditLogResponse\> |
| Status | 200 OK |

---

## 20. Document References

| Document | Reference |
|----------|-----------|
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Database Design | `docs/design/02-database-design.md` |
