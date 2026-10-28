# 03 — Frontend Build Checklist

## 1. Project Setup

- [ ] React project initialized with Vite
- [ ] TailwindCSS configured
- [ ] React Router configured
- [ ] Axios configured for API calls
- [ ] Project structure created:
  ```
  src/
  ├── components/
  │   ├── common/
  │   ├── layout/
  │   └── ui/
  ├── pages/
  ├── services/
  ├── hooks/
  ├── context/
  ├── utils/
  └── types/
  ```
- [ ] Environment variables configured (API URL)
- [ ] Auth context implemented
- [ ] Protected route wrapper implemented

**Completion Criteria**: Project starts, routing works, API connection configured.

---

## 2. Routing Configuration

| Route | Component | Auth Required | Roles |
|-------|-----------|---------------|-------|
| /login | LoginPage | No | Public |
| / | DashboardPage | Yes | All |
| /patients | PatientListPage | Yes | All |
| /patients/new | PatientFormPage | Yes | Nurse, Admin |
| /patients/:id | PatientDetailPage | Yes | All |
| /patients/:id/edit | PatientFormPage | Yes | Nurse, Admin |
| /assessments/new | AssessmentFormPage | Yes | Doctor, Nurse |
| /patients/:id/assessments | AssessmentTimelinePage | Yes | All |
| /admissions | AdmissionListPage | Yes | All |
| /admissions/new | AdmissionFormPage | Yes | Nurse, Doctor |
| /admissions/:id | AdmissionDetailPage | Yes | All |
| /admissions/:id/assign-bed | BedAssignmentPage | Yes | Ward Manager |
| /admissions/:id/transfer | TransferFormPage | Yes | Ward Manager, Doctor |
| /admissions/:id/discharge | DischargeFormPage | Yes | Doctor, Ward Manager |
| /beds | BedListPage | Yes | All |
| /beds/new | BedFormPage | Yes | Admin |
| /beds/:id | BedDetailPage | Yes | All |
| /cleaning | CleaningTaskListPage | Yes | Ward Manager |
| /cleaning/:id | CleaningTaskDetailPage | Yes | Ward Manager |
| /wards | WardListPage | Yes | All |
| /wards/new | WardFormPage | Yes | Admin |
| /wards/:id | WardDetailPage | Yes | All |
| /wards/:id/edit | WardFormPage | Yes | Admin |
| /staff | StaffListPage | Yes | All |
| /staff/new | StaffFormPage | Yes | Admin |
| /staff/:id | StaffDetailPage | Yes | All |
| /shifts | ShiftCalendarPage | Yes | Ward Manager |
| /shifts/new | ShiftFormPage | Yes | Ward Manager |
| /shifts/:id | ShiftDetailPage | Yes | Ward Manager |
| /equipment | EquipmentListPage | Yes | All |
| /equipment/new | EquipmentFormPage | Yes | Equipment Officer |
| /equipment/:id | EquipmentDetailPage | Yes | All |
| /maintenance | MaintenanceListPage | Yes | Equipment Officer |
| /maintenance/new | MaintenanceFormPage | Yes | Equipment Officer |
| /resources | ResourceListPage | Yes | All |
| /resources/new | ResourceFormPage | Yes | Resource Manager |
| /resources/:id | ResourceDetailPage | Yes | All |
| /inventory/stock | StockOverviewPage | Yes | Resource Manager |
| /inventory/transactions | TransactionListPage | Yes | Resource Manager |
| /inventory/transactions/new | TransactionFormPage | Yes | Resource Manager |
| /suppliers | SupplierListPage | Yes | Resource Manager |
| /suppliers/new | SupplierFormPage | Yes | Resource Manager |
| /recommendations | RecommendationListPage | Yes | Ward Manager, Doctor |
| /recommendations/:id | RecommendationDetailPage | Yes | Ward Manager, Doctor |
| /forecasts | ForecastDashboardPage | Yes | Ward Manager, Admin |
| /reports | ReportMenuPage | Yes | Ward Manager, Admin, Viewer |
| /reports/occupancy | OccupancyReportPage | Yes | Ward Manager, Admin |
| /reports/resources | ResourceReportPage | Yes | Resource Manager |
| /reports/staff | StaffReportPage | Yes | Admin |
| /reports/cds | CdsPerformanceReportPage | Yes | Admin |
| /notifications | NotificationListPage | Yes | All |
| /admin/users | UserManagementPage | Yes | Admin |
| /admin/users/:id | UserDetailPage | Yes | Admin |
| /admin/config | SystemConfigPage | Yes | Admin |
| /audit-logs | AuditLogListPage | Yes | Admin |

---

## 3. API Integration Mapping

### 3.1 API Service Files

| Service File | API Base | Endpoints |
|-------------|----------|-----------|
| authService.ts | /api/v1/auth | login, refresh, logout, change-password |
| patientService.ts | /api/v1/patients | CRUD, search |
| assessmentService.ts | /api/v1/assessments | create, timeline, latest |
| admissionService.ts | /api/v1/admissions | CRUD, assign-bed, transfer, discharge |
| bedService.ts | /api/v1/beds | CRUD, available |
| cleaningService.ts | /api/v1/cleaning | tasks, assign, start, complete, verify |
| wardService.ts | /api/v1/wards | CRUD, status |
| staffService.ts | /api/v1/staff | CRUD, workload |
| shiftService.ts | /api/v1/shifts | CRUD, assign |
| equipmentService.ts | /api/v1/equipment | CRUD, assign, maintenance |
| resourceService.ts | /api/v1/resources | CRUD |
| inventoryService.ts | /api/v1/inventory | transactions, stock |
| supplierService.ts | /api/v1/suppliers | CRUD |
| recommendationService.ts | /api/v1/recommendations | generate, pending, accept, override |
| forecastService.ts | /api/v1/forecasts | generate, list |
| notificationService.ts | /api/v1/notifications | list, mark-read |
| reportService.ts | /api/v1/reports | occupancy, resources, cds, audit |
| adminService.ts | /api/v1/admin | users, config |
| auditService.ts | /api/v1/audit-logs | search |

### 3.2 Axios Configuration

- [ ] Base URL from environment variable
- [ ] JWT token attached via Authorization header
- [ ] 401 response interceptor (redirect to login)
- [ ] 403 response interceptor (show access denied)
- [ ] Request/response logging (dev only)
- [ ] Timeout configured (30 seconds)

---

## 4. Component Checklist

### 4.1 Common Components

- [ ] Button (primary, secondary, danger, disabled states)
- [ ] Input (text, number, date, password)
- [ ] Select (dropdown)
- [ ] Textarea
- [ ] Checkbox
- [ ] Radio
- [ ] Modal/Dialog
- [ ] Table (sortable, paginated)
- [ ] Pagination
- [ ] SearchInput
- [ ] Badge (status indicators)
- [ ] Alert (success, error, warning, info)
- [ ] LoadingSpinner
- [ ] EmptyState
- [ ] Card
- [ ] Tabs

### 4.2 Layout Components

- [ ] AppLayout (sidebar + header + content)
- [ ] Sidebar (navigation menu)
- [ ] Header (user menu, notifications)
- [ ] Breadcrumb
- [ ] PageHeader

### 4.3 Page Components

- [ ] LoginPage
- [ ] DashboardPage
- [ ] PatientListPage, PatientFormPage, PatientDetailPage
- [ ] AssessmentFormPage, AssessmentTimelinePage
- [ ] AdmissionListPage, AdmissionFormPage, AdmissionDetailPage
- [ ] BedAssignmentPage, TransferFormPage, DischargeFormPage
- [ ] BedListPage, BedFormPage, BedDetailPage
- [ ] CleaningTaskListPage, CleaningTaskDetailPage
- [ ] WardListPage, WardFormPage, WardDetailPage
- [ ] StaffListPage, StaffFormPage, StaffDetailPage
- [ ] ShiftCalendarPage, ShiftFormPage, ShiftDetailPage
- [ ] EquipmentListPage, EquipmentFormPage, EquipmentDetailPage
- [ ] MaintenanceListPage, MaintenanceFormPage
- [ ] ResourceListPage, ResourceFormPage, ResourceDetailPage
- [ ] StockOverviewPage, TransactionListPage, TransactionFormPage
- [ ] SupplierListPage, SupplierFormPage
- [ ] RecommendationListPage, RecommendationDetailPage
- [ ] ForecastDashboardPage
- [ ] ReportMenuPage, OccupancyReportPage, ResourceReportPage, StaffReportPage, CdsPerformanceReportPage
- [ ] NotificationListPage
- [ ] UserManagementPage, UserDetailPage
- [ ] SystemConfigPage
- [ ] AuditLogListPage

---

## 5. Dashboard Widgets

- [ ] BedOccupancyWidget (overall and per-ward)
- [ ] PendingAdmissionsWidget
- [ ] LowStockAlertsWidget
- [ ] PendingRecommendationsWidget
- [ ] WardStatusCards
- [ ] RecentNotificationsWidget

---

## 6. Form Validation

| Form | Required Fields | Validation Rules |
|------|-----------------|------------------|
| Patient Registration | fullName, dateOfBirth, gender | DOB not future, phone format |
| Clinical Assessment | severityLevel, triageClassification, infectionStatus | Enum values only |
| Admission | patientId, wardId | Active admission check |
| Bed Assignment | bedId | Bed must be available |
| Discharge | dischargeOutcome | Enum values only |
| Transfer | targetWardId | Ward must be active |
| Bed Registration | bedNumber, wardId, bedType | Unique within ward |
| Ward Registration | name, wardType, maxBedCapacity | Name unique, capacity > 0 |
| Staff Registration | fullName, role | Staff number auto-generated |
| Shift | shiftName, shiftDate, startTime, endTime, wardId | End > Start |
| Equipment Registration | name, equipmentType, serialNumber | Serial unique |
| Resource | name, category, unitOfMeasure | Category enum |
| Transaction | resourceInventoryId, transactionType, quantity | Non-zero, non-negative stock |
| Supplier | name | Name required |

---

## 7. UI Completion Criteria

| Criterion | Target |
|-----------|--------|
| All pages render without errors | 100% |
| All forms validate correctly | 100% |
| All API calls handle loading state | 100% |
| All API calls handle error state | 100% |
| All tables support pagination | 100% |
| All tables support sorting | 100% |
| All forms support edit mode | 100% |
| Role-based menu visibility | 100% |
| Responsive design (mobile, tablet, desktop) | 100% |
| Loading spinners on async operations | 100% |
| Toast notifications for success/error | 100% |

---

## 8. Document References

| Document | Reference |
|----------|-----------|
| UI Navigation | `docs/design/11-ui-navigation.md` |
| API Specification | `docs/design/03-api-specification.md` |
| DTO Design | `docs/design/07-dto-design.md` |
