# 11 — UI Navigation

## 1. Navigation Structure

### 1.1 Top-Level Navigation

```
┌─────────────────────────────────────────────────────────────┐
│  Logo    Dashboard    Patients    Admissions    Resources    │
│          Beds         Wards       Staff         Equipment   │
│          Shifts       Reports     Notifications  Admin      │
│                                                              │
│                           [User Menu] [Logout]               │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Role-Based Menu Visibility

| Menu Item | Administrator | Ward Manager | Nursing Officer | Resource Manager | Equipment Officer | Medical Doctor | Dashboard Viewer |
|-----------|---------------|--------------|-----------------|------------------|-------------------|----------------|------------------|
| Dashboard | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Patients | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Admissions | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Beds | ✓ | ✓ | ✓ | - | - | ✓ | ✓ |
| Wards | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Staff | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Shifts | ✓ | ✓ | - | - | - | - | ✓ |
| Resources | ✓ | ✓ | - | ✓ | - | - | ✓ |
| Inventory | ✓ | ✓ | - | ✓ | - | - | ✓ |
| Suppliers | ✓ | - | - | ✓ | - | - | - |
| Equipment | ✓ | ✓ | ✓ | - | ✓ | ✓ | ✓ |
| Maintenance | ✓ | - | - | - | ✓ | - | ✓ |
| Recommendations | ✓ | ✓ | ✓ | - | - | ✓ | ✓ |
| Forecasts | ✓ | ✓ | - | - | - | - | ✓ |
| Reports | ✓ | ✓ | - | ✓ | - | - | ✓ |
| Notifications | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Admin | ✓ | - | - | - | - | - | - |
| Audit Logs | ✓ | - | - | - | - | - | - |

---

## 2. Screen Inventory

### 2.1 Authentication Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Login | /login | Username/password form |
| Forgot Password | /forgot-password | Email-based reset (future) |

### 2.2 Dashboard Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Main Dashboard | / | Overview widgets: bed occupancy, pending admissions, alerts |
| Ward Overview | /dashboard/wards | Ward-by-ward status cards |
| Resource Overview | /dashboard/resources | Stock levels, low-stock alerts |

### 2.3 Patient Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Patient List | /patients | Searchable, paginated table |
| Patient Registration | /patients/new | Registration form |
| Patient Detail | /patients/:id | Demographics, admissions, assessments |
| Patient Edit | /patients/:id/edit | Demographics edit form |

### 2.4 Clinical Assessment Screens

| Screen | URL | Description |
|--------|-----|-------------|
| New Assessment | /assessments/new | Assessment form (linked to patient) |
| Assessment Timeline | /patients/:id/assessments | Chronological assessment list |
| Reassessment | /assessments/new?patient=:id&admission=:id | Pre-filled reassessment form |

### 2.5 Admission Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Admission List | /admissions | Filterable by status, ward |
| New Admission | /admissions/new | Admission form (patient + ward selection) |
| Admission Detail | /admissions/:id | Full admission view with recommendations |
| Bed Assignment | /admissions/:id/assign-bed | Bed selection with recommendations |
| Transfer | /admissions/:id/transfer | Transfer form (target ward, bed) |
| Discharge | /admissions/:id/discharge | Discharge form (outcome, notes) |

### 2.6 Bed Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Bed List | /beds | Filterable by ward, status, type |
| Bed Detail | /beds/:id | Bed info, current admission, cleaning status |
| Bed Registration | /beds/new | New bed form (admin only) |

### 2.7 Bed Cleaning Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Cleaning Tasks | /cleaning | Pending/active tasks list |
| Task Detail | /cleaning/:id | Task status, assignment, completion |

### 2.8 Ward Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Ward List | /wards | All wards with occupancy summary |
| Ward Detail | /wards/:id | Ward info, bed list, staff list |
| Ward Registration | /wards/new | New ward form (admin only) |
| Ward Edit | /wards/:id/edit | Ward configuration edit |

### 2.9 Staff Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Staff List | /staff | Filterable by ward, role, availability |
| Staff Detail | /staff/:id | Profile, workload, assignments |
| Staff Registration | /staff/new | New staff form (admin only) |
| Staff Edit | /staff/:id/edit | Profile edit |

### 2.10 Shift Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Shift Calendar | /shifts | Calendar view of shifts |
| Shift Detail | /shifts/:id | Shift info, assigned staff |
| New Shift | /shifts/new | Shift creation form |
| Assign Staff | /shifts/:id/assign | Staff assignment form |

### 2.11 Equipment Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Equipment List | /equipment | Filterable by type, status, ward |
| Equipment Detail | /equipment/:id | Info, assignment, maintenance history |
| Equipment Registration | /equipment/new | New equipment form |
| Equipment Edit | /equipment/:id/edit | Equipment update form |
| Assign Equipment | /equipment/:id/assign | Assignment form |

### 2.12 Maintenance Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Maintenance List | /maintenance | Scheduled/in-progress maintenance |
| Schedule Maintenance | /maintenance/new | Maintenance scheduling form |
| Maintenance Detail | /maintenance/:id | Maintenance record detail |

### 2.13 Resource Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Resource List | /resources | All resource types |
| Resource Detail | /resources/:id | Resource info, stock levels |
| New Resource | /resources/new | Resource definition form |
| Edit Resource | /resources/:id/edit | Resource update form |

### 2.14 Inventory Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Stock Overview | /inventory/stock | Current stock levels across locations |
| Transaction History | /inventory/transactions | Transaction log |
| Record Transaction | /inventory/transactions/new | Transaction form (purchase, issue, etc.) |

### 2.15 Supplier Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Supplier List | /suppliers | All suppliers |
| New Supplier | /suppliers/new | Supplier registration form |
| Supplier Detail | /suppliers/:id | Supplier info |

### 2.16 Recommendation Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Pending Recommendations | /recommendations | Active recommendations for current user |
| Recommendation Detail | /recommendations/:id | Full recommendation with items |
| Recommendation History | /recommendations/history | Past recommendations |

### 2.17 Forecast Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Forecast Dashboard | /forecasts | Current forecasts, generate new |
| Forecast Detail | /forecasts/:id | Forecast data visualization |

### 2.18 Report Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Report Menu | /reports | Report type selection |
| Occupancy Report | /reports/occupancy | Bed occupancy analytics |
| Resource Report | /reports/resources | Resource utilization analytics |
| Staff Report | /reports/staff | Staff workload analytics |
| CDS Performance Report | /reports/cds | Recommendation engine metrics |
| Audit Report | /reports/audit | Audit trail (admin only) |

### 2.19 Notification Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Notification List | /notifications | User notifications |
| Notification Bell | (header dropdown) | Quick notification view |

### 2.20 Admin Screens

| Screen | URL | Description |
|--------|-----|-------------|
| User Management | /admin/users | User list, create, edit |
| User Detail | /admin/users/:id | User info, status |
| System Configuration | /admin/config | Configuration parameters |

### 2.21 Audit Screens

| Screen | URL | Description |
|--------|-----|-------------|
| Audit Log List | /audit-logs | Searchable audit trail |
| Audit Log Detail | /audit-logs/:id | Full audit entry |

---

## 3. Navigation Flow

### 3.1 Primary Workflow

```
Login → Dashboard → [Patient Registration → Admission → Bed Assignment → Recommendations → Accept/Override]
                  → [Clinical Assessment → Reassessment]
                  → [Discharge → Cleaning Task → Bed Available]
```

### 3.2 Resource Workflow

```
Dashboard → Resource List → Stock Overview → Record Transaction → Low Stock Alert → Notification
```

### 3.3 Equipment Workflow

```
Dashboard → Equipment List → Assign Equipment → Maintenance Schedule → Maintenance Complete
```

### 3.4 Staff Workflow

```
Dashboard → Staff List → Workload View → Shift Calendar → Assign Staff → Shift Complete
```

---

## 4. Dashboard Layout

### 4.1 Main Dashboard

```
┌─────────────────────────────────────────────────────────────┐
│  Bed Occupancy Widget        Pending Admissions Widget      │
│  ┌──────────────────┐        ┌──────────────────┐          │
│  │ Total: 50        │        │ Pending: 3       │          │
│  │ Occupied: 35     │        │ Today: 5         │          │
│  │ Available: 12    │        │ This Week: 22    │          │
│  │ Cleaning: 3      │        │                  │          │
│  │ Occupancy: 70%   │        │                  │          │
│  └──────────────────┘        └──────────────────┘          │
│                                                              │
│  Low Stock Alerts Widget       Pending Recommendations      │
│  ┌──────────────────┐        ┌──────────────────┐          │
│  │ Critical: 2      │        │ Bed: 3 pending   │          │
│  │ Warning: 5       │        │ Staff: 2 pending │          │
│  │ Expiring: 3      │        │ Equipment: 1     │          │
│  └──────────────────┘        └──────────────────┘          │
│                                                              │
│  Ward Status Overview (cards per ward)                      │
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐              │
│  │Ward A  │ │Ward B  │ │Ward C  │ │Ward D  │              │
│  │80%     │ │65%     │ │90%     │ │45%     │              │
│  │20/25   │ │13/20   │ │18/20   │ │9/20    │              │
│  └────────┘ └────────┘ └────────┘ └────────┘              │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Ward Overview Dashboard

```
┌─────────────────────────────────────────────────────────────┐
│  Ward: Isolation A                                          │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ Bed Status Grid:                                     │   │
│  │ [O][O][O][A][O][C][O][A][O][O]                      │   │
│  │ O=Occupied  A=Available  C=Cleaning  M=Maintenance  │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                              │
│  Staff on Duty: 8/10                                         │
│  Pending Tasks: 2 cleaning, 1 maintenance                   │
│  Active Recommendations: 3                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## 5. Form Designs

### 5.1 Patient Registration Form

| Field | Input Type | Required | Validation |
|-------|-----------|----------|------------|
| Full Name | Text | Yes | Max 100 chars |
| Date of Birth | Date picker | Yes | Not future |
| Gender | Select | Yes | Male/Female/Other |
| Phone Number | Tel | No | Max 20 chars |
| Address | Textarea | No | Max 500 chars |
| Next-of-Kin Name | Text | No | Max 100 chars |
| Next-of-Kin Phone | Tel | No | Max 20 chars |

### 5.2 Clinical Assessment Form

| Field | Input Type | Required | Validation |
|-------|-----------|----------|------------|
| Patient | Select (auto-filled) | Yes | From context |
| Admission | Select (auto-filled) | Yes | From context |
| Severity Level | Select | Yes | Mild/Moderate/Severe/Critical |
| Triage Classification | Select | Yes | Emergency/Urgent/Semi-Urgent/Non-Urgent |
| Infection Status | Select | Yes | Suspected/Confirmed/Ruled-Out |
| Clinical Notes | Textarea | No | Max 2000 chars |
| Is Reassessment | Checkbox | No | Default false |

### 5.3 Admission Form

| Field | Input Type | Required | Validation |
|-------|-----------|----------|------------|
| Patient | Select/search | Yes | Must exist |
| Ward | Select | Yes | Must be active |
| Admission Notes | Textarea | No | Max 2000 chars |

### 5.4 Bed Assignment Form

| Field | Input Type | Required | Validation |
|-------|-----------|----------|------------|
| Bed | Select (from recommendations) | Yes | Must be available |
| Recommendation | Display | - | Show recommendation details |

### 5.5 Discharge Form

| Field | Input Type | Required | Validation |
|-------|-----------|----------|------------|
| Discharge Outcome | Select | Yes | Recovered/Referred/Deceased/AMA |
| Discharge Notes | Textarea | No | Max 2000 chars |

---

## 6. Table Designs

### 6.1 Patient List Table

| Column | Sortable | Filterable | Width |
|--------|----------|------------|-------|
| Patient Number | Yes | Yes | 120px |
| Full Name | Yes | Yes | 200px |
| Gender | Yes | Yes | 80px |
| Phone | No | Yes | 150px |
| Registered | Yes | Yes | 120px |
| Actions | No | No | 100px |

### 6.2 Admission List Table

| Column | Sortable | Filterable | Width |
|--------|----------|------------|-------|
| Admission # | Yes | Yes | 120px |
| Patient | Yes | Yes | 200px |
| Ward | Yes | Yes | 150px |
| Bed | Yes | Yes | 100px |
| Status | Yes | Yes | 120px |
| Admitted | Yes | Yes | 120px |
| LOS | Yes | No | 80px |
| Actions | No | No | 150px |

### 6.3 Bed List Table

| Column | Sortable | Filterable | Width |
|--------|----------|------------|-------|
| Bed Number | Yes | Yes | 100px |
| Ward | Yes | Yes | 150px |
| Type | Yes | Yes | 150px |
| Isolation | Yes | Yes | 100px |
| Status | Yes | Yes | 120px |
| Admission | Yes | Yes | 120px |
| Actions | No | No | 100px |

---

## 7. Report Designs

### 7.1 Occupancy Report

- Summary cards: Total beds, Occupancy rate, Average LOS
- Bar chart: Occupancy by ward
- Line chart: Occupancy trend over period
- Table: Ward-by-ward breakdown

### 7.2 Resource Report

- Summary cards: Total resources, Low-stock count, Total consumption
- Pie chart: Consumption by category
- Bar chart: Top consumed resources
- Table: Resource stock levels

### 7.3 CDS Performance Report

- Summary cards: Total recommendations, Acceptance rate, Override rate
- Bar chart: Recommendations by type
- Line chart: Acceptance rate trend
- Table: Override reasons breakdown

---

## 8. Responsive Design

| Breakpoint | Layout |
|-----------|--------|
| ≥ 1280px | Full sidebar + content |
| ≥ 1024px | Collapsible sidebar + content |
| ≥ 768px | Hidden sidebar, hamburger menu |
| < 768px | Mobile-optimized, stacked layout |

---

## 9. Document References

| Document | Reference |
|----------|-----------|
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| API Specification | `docs/design/03-api-specification.md` |
