# 05 — REST API Implementation Checklist

## 1. Endpoint Implementation Order

### Phase 1: Authentication (Priority: Critical)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 1 | /api/v1/auth/login | POST | AuthController | [ ] |
| 2 | /api/v1/auth/refresh | POST | AuthController | [ ] |
| 3 | /api/v1/auth/logout | POST | AuthController | [ ] |
| 4 | /api/v1/auth/change-password | POST | AuthController | [ ] |

### Phase 2: Patient (Priority: High)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 5 | /api/v1/patients | POST | PatientController | [ ] |
| 6 | /api/v1/patients | GET | PatientController | [ ] |
| 7 | /api/v1/patients/{id} | GET | PatientController | [ ] |
| 8 | /api/v1/patients/{id} | PUT | PatientController | [ ] |
| 9 | /api/v1/patients/{id} | DELETE | PatientController | [ ] |

### Phase 3: Clinical Assessment (Priority: High)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 10 | /api/v1/assessments | POST | ClinicalAssessmentController | [ ] |
| 11 | /api/v1/assessments/patient/{patientId} | GET | ClinicalAssessmentController | [ ] |
| 12 | /api/v1/assessments/admission/{admissionId}/latest | GET | ClinicalAssessmentController | [ ] |

### Phase 4: Ward (Priority: High)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 13 | /api/v1/wards | POST | WardController | [ ] |
| 14 | /api/v1/wards | GET | WardController | [ ] |
| 15 | /api/v1/wards/{id} | GET | WardController | [ ] |
| 16 | /api/v1/wards/{id} | PUT | WardController | [ ] |

### Phase 5: Bed (Priority: High)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 17 | /api/v1/beds | POST | BedController | [ ] |
| 18 | /api/v1/beds | GET | BedController | [ ] |
| 19 | /api/v1/beds/{id} | GET | BedController | [ ] |
| 20 | /api/v1/beds/{id} | PUT | BedController | [ ] |
| 21 | /api/v1/beds/available | GET | BedController | [ ] |

### Phase 6: Admission (Priority: High)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 22 | /api/v1/admissions | POST | AdmissionController | [ ] |
| 23 | /api/v1/admissions | GET | AdmissionController | [ ] |
| 24 | /api/v1/admissions/{id} | GET | AdmissionController | [ ] |
| 25 | /api/v1/admissions/{id}/assign-bed | PUT | AdmissionController | [ ] |
| 26 | /api/v1/admissions/{id}/transfer | POST | AdmissionController | [ ] |
| 27 | /api/v1/admissions/{id}/discharge | POST | AdmissionController | [ ] |

### Phase 7: Bed Cleaning (Priority: High)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 28 | /api/v1/cleaning/tasks | GET | BedCleaningController | [ ] |
| 29 | /api/v1/cleaning/tasks/{id}/assign | PUT | BedCleaningController | [ ] |
| 30 | /api/v1/cleaning/tasks/{id}/start | PUT | BedCleaningController | [ ] |
| 31 | /api/v1/cleaning/tasks/{id}/complete | PUT | BedCleaningController | [ ] |
| 32 | /api/v1/cleaning/tasks/{id}/verify | PUT | BedCleaningController | [ ] |

### Phase 8: Staff (Priority: High)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 33 | /api/v1/staff | POST | StaffController | [ ] |
| 34 | /api/v1/staff | GET | StaffController | [ ] |
| 35 | /api/v1/staff/{id} | GET | StaffController | [ ] |
| 36 | /api/v1/staff/{id} | PUT | StaffController | [ ] |

### Phase 9: Shift (Priority: Medium)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 37 | /api/v1/shifts | POST | ShiftController | [ ] |
| 38 | /api/v1/shifts | GET | ShiftController | [ ] |
| 39 | /api/v1/shifts/{id}/assign | POST | ShiftController | [ ] |

### Phase 10: Equipment (Priority: Medium)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 40 | /api/v1/equipment | POST | EquipmentController | [ ] |
| 41 | /api/v1/equipment | GET | EquipmentController | [ ] |
| 42 | /api/v1/equipment/{id} | GET | EquipmentController | [ ] |
| 43 | /api/v1/equipment/{id}/assign | PUT | EquipmentController | [ ] |
| 44 | /api/v1/equipment/{id}/maintenance | POST | EquipmentMaintenanceController | [ ] |

### Phase 11: Resource (Priority: Medium)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 45 | /api/v1/resources | POST | ResourceController | [ ] |
| 46 | /api/v1/resources | GET | ResourceController | [ ] |
| 47 | /api/v1/inventory/transactions | POST | InventoryController | [ ] |
| 48 | /api/v1/inventory/transactions | GET | InventoryController | [ ] |
| 49 | /api/v1/inventory/stock | GET | InventoryController | [ ] |
| 50 | /api/v1/suppliers | POST | SupplierController | [ ] |
| 51 | /api/v1/suppliers | GET | SupplierController | [ ] |

### Phase 12: Recommendation (Priority: Medium)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 52 | /api/v1/recommendations/generate | POST | RecommendationController | [ ] |
| 53 | /api/v1/recommendations/admission/{id}/pending | GET | RecommendationController | [ ] |
| 54 | /api/v1/recommendations/{id}/items/{itemId}/accept | POST | RecommendationController | [ ] |
| 55 | /api/v1/recommendations/{id}/items/{itemId}/override | POST | RecommendationController | [ ] |

### Phase 13: Forecast (Priority: Low)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 56 | /api/v1/forecasts/generate | POST | ForecastController | [ ] |
| 57 | /api/v1/forecasts | GET | ForecastController | [ ] |

### Phase 14: Notification (Priority: Medium)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 58 | /api/v1/notifications | GET | NotificationController | [ ] |
| 59 | /api/v1/notifications/{id}/read | PUT | NotificationController | [ ] |
| 60 | /api/v1/notifications/read-all | PUT | NotificationController | [ ] |

### Phase 15: Report (Priority: Medium)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 61 | /api/v1/reports/occupancy | GET | ReportController | [ ] |
| 62 | /api/v1/reports/resource-utilization | GET | ReportController | [ ] |
| 63 | /api/v1/reports/cds-performance | GET | ReportController | [ ] |
| 64 | /api/v1/reports/audit | GET | ReportController | [ ] |

### Phase 16: Admin (Priority: Low)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 65 | /api/v1/admin/users | POST | AdminUserController | [ ] |
| 66 | /api/v1/admin/users | GET | AdminUserController | [ ] |
| 67 | /api/v1/admin/users/{id} | PUT | AdminUserController | [ ] |
| 68 | /api/v1/admin/users/{id}/unlock | PUT | AdminUserController | [ ] |
| 69 | /api/v1/admin/config | GET | SystemConfigController | [ ] |
| 70 | /api/v1/admin/config/{key} | PUT | SystemConfigController | [ ] |

### Phase 17: Audit (Priority: Low)

| # | Endpoint | Method | Controller | Status |
|---|----------|--------|------------|--------|
| 71 | /api/v1/audit-logs | GET | AuditController | [ ] |

---

## 2. Security Integration

| Endpoint | Authentication | Authorization | Status |
|----------|---------------|---------------|--------|
| POST /auth/login | Public | Public | [ ] |
| POST /auth/refresh | Public (valid refresh token) | Public | [ ] |
| All other endpoints | Bearer JWT | Role-based | [ ] |

---

## 3. Validation Implementation

| Endpoint | Request DTO | Validation Rules | Status |
|----------|-------------|------------------|--------|
| POST /patients | PatientRequest | @NotBlank, @NotNull, @Size, @Pattern | [ ] |
| POST /assessments | ClinicalAssessmentRequest | @NotBlank, @NotNull, @Pattern | [ ] |
| POST /admissions | AdmissionRequest | @NotNull | [ ] |
| POST /beds | BedRequest | @NotBlank, @NotNull | [ ] |
| POST /wards | WardRequest | @NotBlank, @NotNull, @Min | [ ] |
| POST /staff | StaffRequest | @NotBlank, @NotNull | [ ] |
| POST /shifts | ShiftRequest | @NotBlank, @NotNull | [ ] |
| POST /equipment | EquipmentRequest | @NotBlank, @NotNull | [ ] |
| POST /resources | ResourceRequest | @NotBlank, @NotNull, @Min | [ ] |
| POST /inventory/transactions | InventoryTransactionRequest | @NotNull, @NotZero | [ ] |
| POST /recommendations/override | RecommendationDecisionRequest | @NotBlank (if overriding) | [ ] |

---

## 4. Testing Requirements

| Test Type | Coverage Target | Status |
|-----------|----------------|--------|
| Unit tests for controllers | 100% of endpoints | [ ] |
| Request validation tests | All validation rules | [ ] |
| Authorization tests | All role combinations | [ ] |
| Error response tests | All error scenarios | [ ] |
| Pagination tests | All list endpoints | [ ] |

---

## 5. Document References

| Document | Reference |
|----------|-----------|
| API Specification | `docs/design/03-api-specification.md` |
| Validation Rules | `docs/design/13-validation-rules.md` |
| Error Handling | `docs/design/14-error-handling.md` |
| Security Design | `docs/design/10-security-design.md` |
