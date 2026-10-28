# 01 — Implementation Roadmap

## 1. Implementation Order

The system is implemented in sequential phases, each building on the previous phase. Within each phase, modules are implemented in dependency order.

### 1.1 Phase Sequence

| Phase | Name | Duration | Modules |
|-------|------|----------|---------|
| 1 | Foundation | Weeks 1–2 | Project setup, common infrastructure, authentication |
| 2 | Core Domain | Weeks 3–5 | Patient, ClinicalAssessment, Ward, Bed |
| 3 | Operations | Weeks 6–8 | Admission, BedCleaning, Staff, Shift |
| 4 | Resources | Weeks 9–11 | Resource, Inventory, Equipment, Maintenance |
| 5 | Intelligence | Weeks 12–14 | CDS Engine, Recommendation, Forecast |
| 6 | Support | Weeks 15–16 | Notification, Report, Audit, Admin |
| 7 | Frontend | Weeks 17–20 | React UI for all modules |
| 8 | Integration | Weeks 21–22 | End-to-end testing, performance tuning |
| 9 | Deployment | Weeks 23–24 | Staging, UAT, production deployment |

---

## 2. Module Dependencies

### 2.1 Dependency Graph

```
Authentication ─────────────────────────────────────────────────────────┐
    │                                                                   │
    ▼                                                                   │
Patient ──► ClinicalAssessment ──► Admission ──► BedCleaning            │
    │              │                   │              │                  │
    │              │                   ▼              ▼                  │
    │              │               Bed ◄──────────── Ward                │
    │              │                                  │                  │
    │              │                                  ▼                  │
    │              │                              Staff ◄────────────────┤
    │              │                                  │                  │
    │              │                                  ▼                  │
    │              │                           Shift/Assignment          │
    │              │                                                   │
    │              ▼                                                   │
    │          CDS Engine ──► Recommendation                            │
    │              │                                                   │
    │              ├──► Resource ──► Inventory                          │
    │              │                                                   │
    │              ├──► Equipment ──► Maintenance                       │
    │              │                                                   │
    │              └──► Staff (workload)                                │
    │                                                                  │
    ├──► Notification                                                   │
    ├──► Report                                                         │
    ├──► Audit                                                          │
    └──► Admin                                                          │
```

### 2.2 Implementation Dependencies

| Module | Depends On | Blocks |
|--------|-----------|--------|
| Authentication | None | All modules |
| Patient | Authentication | ClinicalAssessment, Admission |
| ClinicalAssessment | Patient | Admission, CDS Engine |
| Ward | Authentication | Bed, Staff, Admission |
| Bed | Ward | Admission, BedCleaning |
| Admission | Patient, Ward, Bed | BedCleaning, CDS Engine, Recommendation |
| BedCleaning | Bed, Admission | Bed availability |
| Staff | Ward | Shift, CDS Engine |
| Shift | Staff | ShiftAssignment |
| Resource | Authentication | Inventory, CDS Engine |
| Inventory | Resource | CDS Engine |
| Equipment | Authentication | Maintenance, CDS Engine |
| Maintenance | Equipment | Equipment availability |
| CDS Engine | All resource modules | Recommendation |
| Recommendation | CDS Engine | Notification |
| Forecast | Admission, Resource | Report |
| Notification | Recommendation | None |
| Report | All modules | None |
| Audit | All modules | None |
| Admin | Authentication | None |

---

## 3. Milestone Breakdown

### Milestone 1: Foundation Complete (Week 2)

**Deliverables**:
- Maven project structure
- Spring Boot application starts
- PostgreSQL connection
- Flyway migrations (V001–V016)
- JWT authentication working
- User login/logout
- Refresh token rotation
- Password policy enforced
- Audit logging infrastructure

**Acceptance Criteria**:
- User can login with credentials
- JWT tokens are issued and refreshed
- Password complexity enforced
- Account lockout after 5 failures
- Audit logs created for authentication events

### Milestone 2: Core Domain Complete (Week 5)

**Deliverables**:
- Patient CRUD
- ClinicalAssessment recording
- Ward management
- Bed management
- Basic search and pagination

**Acceptance Criteria**:
- Patient registration with auto-generated number
- Clinical assessments recorded with timestamps
- Ward configuration with capacity
- Bed status tracking
- Soft deletes for patients

### Milestone 3: Operations Complete (Week 8)

**Deliverables**:
- Full admission lifecycle
- Bed cleaning workflow
- Staff management
- Shift management
- Ward occupancy calculation

**Acceptance Criteria**:
- Admission create → assign bed → transfer → discharge
- Cleaning tasks created on discharge
- Staff workload calculated dynamically
- Shift assignments with overlap prevention
- Ward occupancy rates accurate

### Milestone 4: Resources Complete (Week 11)

**Deliverables**:
- Resource management
- Inventory transactions
- Equipment management
- Maintenance scheduling
- Low-stock alerts

**Acceptance Criteria**:
- Inventory transactions append-only
- Stock levels calculated from transactions
- Equipment status tracking
- Maintenance overdue notifications
- Resource allocations linked to admissions

### Milestone 5: Intelligence Complete (Week 14)

**Deliverables**:
- CDS engine with scoring
- Recommendation generation
- Recommendation accept/override
- Forecast generation
- Confidence scores and rationale

**Acceptance Criteria**:
- Bed/staff/equipment/resource recommendations generated
- Scoring breakdown stored
- Override requires justification
- Recommendations expire after timeout
- Forecasts generated using Moving Average

### Milestone 6: Support Complete (Week 16)

**Deliverables**:
- Notification system
- Report generation
- Audit log querying
- Admin user management
- System configuration

**Acceptance Criteria**:
- Notifications delivered for all event types
- Reports generate in PDF/CSV
- Audit logs searchable
- User management CRUD
- System config editable

### Milestone 7: Frontend Complete (Week 20)

**Deliverables**:
- All screens implemented
- All workflows functional
- Dashboard with real-time data
- Responsive design

**Acceptance Criteria**:
- All user stories pass
- Navigation works correctly
- Forms validate properly
- Tables display data correctly
- Role-based menu visibility

### Milestone 8: Integration Complete (Week 22)

**Deliverables**:
- End-to-end testing
- Performance testing
- Security testing
- Bug fixes

**Acceptance Criteria**:
- All critical paths tested
- Response time targets met
- No critical security vulnerabilities
- All bugs resolved

### Milestone 9: Production Ready (Week 24)

**Deliverables**:
- Production deployment
- UAT completed
- Documentation complete
- Training delivered

**Acceptance Criteria**:
- System live in production
- Stakeholder sign-off
- User training complete
- Support process established

---

## 4. Sprint Mapping

### Sprint 1 (Week 1–2)

| Task | Module | Est. Hours |
|------|--------|-----------|
| Project setup (Maven, Spring Boot) | Foundation | 8 |
| Database configuration | Foundation | 4 |
| Flyway migrations (V001–V016) | Foundation | 8 |
| Common infrastructure (DTOs, exceptions) | Foundation | 8 |
| User entity and repository | Auth | 8 |
| JWT token provider | Auth | 8 |
| JWT authentication filter | Auth | 8 |
| Auth controller and service | Auth | 12 |
| Refresh token management | Auth | 8 |
| Password history enforcement | Auth | 6 |
| Account lockout logic | Auth | 6 |
| Login audit logging | Auth | 6 |
| **Sprint Total** | | **90** |

### Sprint 2 (Week 3–4)

| Task | Module | Est. Hours |
|------|--------|-----------|
| Patient entity and repository | Patient | 8 |
| Patient service and controller | Patient | 12 |
| Patient search with pagination | Patient | 8 |
| ClinicalAssessment entity | Assessment | 6 |
| Assessment service and controller | Assessment | 10 |
| Ward entity and repository | Ward | 6 |
| Ward service and controller | Ward | 8 |
| Bed entity and repository | Bed | 8 |
| Bed service and controller | Bed | 10 |
| Bed availability queries | Bed | 6 |
| **Sprint Total** | | **82** |

### Sprint 3 (Week 5–6)

| Task | Module | Est. Hours |
|------|--------|-----------|
| Admission entity and repository | Admission | 8 |
| Admission lifecycle service | Admission | 16 |
| Admission controller | Admission | 8 |
| BedCleaning entity and repository | Cleaning | 6 |
| Cleaning workflow service | Cleaning | 12 |
| Cleaning controller | Cleaning | 6 |
| Staff entity and repository | Staff | 8 |
| Staff service and controller | Staff | 10 |
| WorkloadCalculator domain service | Staff | 12 |
| **Sprint Total** | | **86** |

### Sprint 4 (Week 7–8)

| Task | Module | Est. Hours |
|------|--------|-----------|
| StaffShift entity | Shift | 6 |
| ShiftAssignment entity | Shift | 4 |
| Shift service and controller | Shift | 10 |
| ShiftDomainService | Shift | 8 |
| Staff admission join service | Staff | 6 |
| Equipment entity and repository | Equipment | 8 |
| Equipment service and controller | Equipment | 10 |
| EquipmentMaintenance entity | Equipment | 6 |
| Maintenance service and controller | Equipment | 10 |
| **Sprint Total** | | **68** |

### Sprint 5 (Week 9–10)

| Task | Module | Est. Hours |
|------|--------|-----------|
| Resource entity and repository | Resource | 6 |
| Resource service and controller | Resource | 8 |
| ResourceInventory entity | Inventory | 6 |
| InventoryTransaction entity | Inventory | 4 |
| Inventory service and controller | Inventory | 12 |
| InventoryDomainService | Inventory | 8 |
| ResourceSupplier entity | Supplier | 4 |
| Supplier service and controller | Supplier | 6 |
| Resource allocation service | Resource | 8 |
| Equipment allocation service | Equipment | 6 |
| **Sprint Total** | | **68** |

### Sprint 6 (Week 11–12)

| Task | Module | Est. Hours |
|------|--------|-----------|
| CDS Engine core | CDS | 20 |
| BedScoringService | CDS | 12 |
| StaffScoringService | CDS | 10 |
| EquipmentScoringService | CDS | 10 |
| ResourceScoringService | CDS | 10 |
| AllocationRecommendation entity | Rec | 4 |
| RecommendationItem entity | Rec | 4 |
| RecommendationDecision entity | Rec | 4 |
| Recommendation service and controller | Rec | 12 |
| **Sprint Total** | | **86** |

### Sprint 7 (Week 13–14)

| Task | Module | Est. Hours |
|------|--------|-----------|
| ForecastCalculator domain service | Forecast | 12 |
| MovingAverageModel | Forecast | 6 |
| WeightedMovingAverageModel | Forecast | 6 |
| ForecastSnapshot entity | Forecast | 4 |
| Forecast service and controller | Forecast | 10 |
| Notification entity | Notification | 4 |
| Notification service and controller | Notification | 10 |
| Report service and controller | Report | 16 |
| Audit log querying service | Audit | 8 |
| **Sprint Total** | | **76** |

### Sprint 8 (Week 15–16)

| Task | Module | Est. Hours |
|------|--------|-----------|
| SystemConfiguration entity | Admin | 4 |
| Admin user management | Admin | 10 |
| System config service | Admin | 6 |
| Audit log search | Audit | 8 |
| Report PDF generation | Report | 12 |
| Report CSV export | Report | 8 |
| Scheduled tasks (cleanup, alerts) | Support | 10 |
| Backend integration testing | Testing | 16 |
| **Sprint Total** | | **74** |

### Sprint 9–12 (Week 17–20) — Frontend

| Task | Module | Est. Hours |
|------|--------|-----------|
| React project setup | Frontend | 8 |
| Routing and layout | Frontend | 12 |
| Login page | Frontend | 8 |
| Dashboard | Frontend | 16 |
| Patient pages | Frontend | 16 |
| Assessment pages | Frontend | 12 |
| Admission pages | Frontend | 20 |
| Bed pages | Frontend | 12 |
| Cleaning pages | Frontend | 12 |
| Ward pages | Frontend | 10 |
| Staff pages | Frontend | 14 |
| Shift pages | Frontend | 12 |
| Equipment pages | Frontend | 14 |
| Resource pages | Frontend | 14 |
| Inventory pages | Frontend | 12 |
| Supplier pages | Frontend | 8 |
| Recommendation pages | Frontend | 16 |
| Forecast pages | Frontend | 10 |
| Report pages | Frontend | 14 |
| Notification pages | Frontend | 8 |
| Admin pages | Frontend | 12 |
| **Sprint Total** | | **254** |

---

## 5. Critical Path

```
Authentication → Patient → Admission → CDS Engine → Recommendation → Frontend → Integration → Deployment
```

Any delay on the critical path directly impacts the project timeline.

---

## 6. Development Sequence Summary

| Order | Module | Dependencies | Est. Days |
|-------|--------|-------------|-----------|
| 1 | Project Setup | None | 2 |
| 2 | Authentication | None | 5 |
| 3 | Patient | Auth | 3 |
| 4 | ClinicalAssessment | Patient | 2 |
| 5 | Ward | Auth | 2 |
| 6 | Bed | Ward | 3 |
| 7 | Admission | Patient, Ward, Bed | 5 |
| 8 | BedCleaning | Bed, Admission | 3 |
| 9 | Staff | Ward | 3 |
| 10 | Shift | Staff | 3 |
| 11 | Resource | Auth | 2 |
| 12 | Inventory | Resource | 3 |
| 13 | Equipment | Auth | 2 |
| 14 | Maintenance | Equipment | 2 |
| 15 | CDS Engine | All resources | 5 |
| 16 | Recommendation | CDS Engine | 4 |
| 17 | Forecast | Admission, Resource | 3 |
| 18 | Notification | Recommendation | 2 |
| 19 | Report | All modules | 4 |
| 20 | Audit | All modules | 2 |
| 21 | Admin | Auth | 2 |
| 22 | Frontend | All backend | 20 |
| 23 | Integration | All modules | 5 |
| 24 | Deployment | All | 3 |

---

## 7. Document References

| Document | Reference |
|----------|-----------|
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Service Design | `docs/design/09-service-design.md` |
| Package Structure | `docs/design/04-package-structure.md` |
