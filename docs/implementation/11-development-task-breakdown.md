# 11 — Development Task Breakdown

## 1. Authentication Module

### Objectives
- Implement JWT-based authentication
- Implement refresh token rotation
- Implement password policy and history
- Implement account lockout

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| AUTH-01 | Create User entity with JPA annotations | Low | 2 | None | [ ] |
| AUTH-02 | Create RefreshToken entity | Low | 1 | None | [ ] |
| AUTH-03 | Create PasswordHistory entity | Low | 1 | None | [ ] |
| AUTH-04 | Create LoginAuditLog entity | Low | 1 | None | [ ] |
| AUTH-05 | Create UserRepository with custom queries | Medium | 3 | AUTH-01 | [ ] |
| AUTH-06 | Create RefreshTokenRepository | Low | 2 | AUTH-02 | [ ] |
| AUTH-07 | Create PasswordHistoryRepository | Low | 2 | AUTH-03 | [ ] |
| AUTH-08 | Create LoginAuditLogRepository | Low | 2 | AUTH-04 | [ ] |
| AUTH-09 | Implement JwtTokenProvider | High | 4 | None | [ ] |
| AUTH-10 | Implement JwtAuthenticationFilter | High | 4 | AUTH-09 | [ ] |
| AUTH-11 | Configure Spring Security | High | 4 | AUTH-10 | [ ] |
| AUTH-12 | Implement AuthService (login, logout, refresh) | High | 6 | AUTH-05, AUTH-09 | [ ] |
| AUTH-13 | Implement TokenService | Medium | 3 | AUTH-09 | [ ] |
| AUTH-14 | Implement PasswordService | Medium | 3 | AUTH-07 | [ ] |
| AUTH-15 | Create AuthController | Medium | 3 | AUTH-12 | [ ] |
| AUTH-16 | Create Auth DTOs | Low | 2 | None | [ ] |
| AUTH-17 | Implement password complexity validation | Low | 2 | None | [ ] |
| AUTH-18 | Implement account lockout logic | Medium | 3 | AUTH-05 | [ ] |
| AUTH-19 | Implement refresh token rotation | Medium | 3 | AUTH-06 | [ ] |
| AUTH-20 | Implement password history check | Medium | 2 | AUTH-07 | [ ] |

### Acceptance Criteria
- User can login with valid credentials
- JWT access token issued with 15-minute expiry
- Refresh token issued with 7-day expiry
- Refresh token rotated on each use
- Password complexity enforced
- Account locked after 5 failed attempts
- Password history prevents reuse of last 5
- All authentication events logged

---

## 2. Patient Module

### Objectives
- Implement patient registration and management
- Implement patient search with pagination
- Implement soft delete

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| PAT-01 | Create Patient entity with JPA annotations | Low | 2 | None | [ ] |
| PAT-02 | Create PatientRepository with search queries | Medium | 3 | PAT-01 | [ ] |
| PAT-03 | Implement PatientApplicationService | Medium | 4 | PAT-02 | [ ] |
| PAT-04 | Create PatientController | Medium | 3 | PAT-03 | [ ] |
| PAT-05 | Create Patient DTOs (Request, Response, Summary) | Low | 2 | None | [ ] |
| PAT-06 | Implement patient number auto-generation | Low | 2 | PAT-01 | [ ] |
| PAT-07 | Implement patient search with filters | Medium | 3 | PAT-02 | [ ] |
| PAT-08 | Implement soft delete (deactivation) | Low | 2 | PAT-03 | [ ] |
| PAT-09 | Implement patient edit with audit trail | Medium | 2 | PAT-03 | [ ] |

### Acceptance Criteria
- Patient registered with auto-generated number
- Patient searchable by name, number, phone
- Patient demographics editable
- Patient soft-deleted (not physically removed)
- All changes audited

---

## 3. Clinical Assessment Module

### Objectives
- Implement clinical assessment recording
- Implement assessment timeline
- Implement reassessment tracking

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| CAS-01 | Create ClinicalAssessment entity | Low | 2 | None | [ ] |
| CAS-02 | Create ClinicalAssessmentRepository | Medium | 3 | CAS-01 | [ ] |
| CAS-03 | Implement ClinicalAssessmentApplicationService | Medium | 4 | CAS-02 | [ ] |
| CAS-04 | Create ClinicalAssessmentController | Medium | 3 | CAS-03 | [ ] |
| CAS-05 | Create Assessment DTOs | Low | 2 | None | [ ] |
| CAS-06 | Implement append-only behavior | Low | 2 | CAS-03 | [ ] |
| CAS-07 | Implement reassessment tracking | Medium | 2 | CAS-03 | [ ] |
| CAS-08 | Implement 24-hour reassessment check | Medium | 2 | CAS-03 | [ ] |

### Acceptance Criteria
- Assessment recorded with timestamp and assessor
- Assessment linked to patient and admission
- Assessment timeline viewable
- Reassessment flag tracked
- Append-only (no updates to historical assessments)

---

## 4. Ward Module

### Objectives
- Implement ward configuration management
- Implement ward status display
- Implement occupancy calculation

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| WAR-01 | Create Ward entity | Low | 2 | None | [ ] |
| WAR-02 | Create WardRepository | Low | 2 | WAR-01 | [ ] |
| WAR-03 | Implement WardApplicationService | Medium | 3 | WAR-02 | [ ] |
| WAR-04 | Create WardController | Medium | 3 | WAR-03 | [ ] |
| WAR-05 | Create Ward DTOs | Low | 2 | None | [ ] |
| WAR-06 | Implement occupancy calculation | Medium | 3 | WAR-03 | [ ] |
| WAR-07 | Implement ward capacity validation | Low | 2 | WAR-03 | [ ] |

### Acceptance Criteria
- Ward created with name, type, capacity, isolation level
- Ward status display with occupancy
- Ward capacity enforced
- Inactive wards cannot receive admissions

---

## 5. Bed Module

### Objectives
- Implement bed registry and management
- Implement bed status tracking
- Implement bed availability queries

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| BED-01 | Create Bed entity | Low | 2 | None | [ ] |
| BED-02 | Create BedRepository with availability queries | Medium | 3 | BED-01 | [ ] |
| BED-03 | Implement BedApplicationService | Medium | 4 | BED-02 | [ ] |
| BED-04 | Implement BedDomainService | Medium | 3 | BED-03 | [ ] |
| BED-05 | Create BedController | Medium | 3 | BED-03 | [ ] |
| BED-06 | Create Bed DTOs | Low | 2 | None | [ ] |
| BED-07 | Implement bed status transitions | Medium | 3 | BED-04 | [ ] |
| BED-08 | Implement bed availability query | Medium | 2 | BED-02 | [ ] |

### Acceptance Criteria
- Bed registered with number, type, ward, isolation capability
- Bed status tracked in real time
- Bed availability query works correctly
- One occupant per bed enforced
- Isolation beds mandatory for confirmed patients

---

## 6. Admission Module

### Objectives
- Implement full admission lifecycle
- Implement bed assignment workflow
- Implement transfer and discharge

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| ADM-01 | Create Admission entity | Low | 2 | None | [ ] |
| ADM-02 | Create AdmissionRepository | Medium | 3 | ADM-01 | [ ] |
| ADM-03 | Implement AdmissionApplicationService | High | 8 | ADM-02, BED-03, WAR-03 | [ ] |
| ADM-04 | Create AdmissionController | Medium | 4 | ADM-03 | [ ] |
| ADM-05 | Create Admission DTOs | Low | 3 | None | [ ] |
| ADM-06 | Implement admission creation | Medium | 3 | ADM-03 | [ ] |
| ADM-07 | Implement bed assignment | Medium | 3 | ADM-03, BED-03 | [ ] |
| ADM-08 | Implement patient transfer | High | 4 | ADM-03 | [ ] |
| ADM-09 | Implement discharge processing | High | 4 | ADM-03, BED-03 | [ ] |
| ADM-10 | Implement active admission check | Low | 2 | ADM-02 | [ ] |

### Acceptance Criteria
- Admission created with patient and ward
- Bed assigned during admission
- Transfer creates new admission record
- Discharge releases bed for cleaning
- One active admission per patient enforced
- Length of stay calculated

---

## 7. Bed Cleaning Module

### Objectives
- Implement cleaning workflow
- Implement task assignment and verification
- Implement bed status transitions

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| CLN-01 | Create BedCleaning entity | Low | 2 | None | [ ] |
| CLN-02 | Create BedCleaningRepository | Low | 2 | CLN-01 | [ ] |
| CLN-03 | Implement BedCleaningApplicationService | Medium | 4 | CLN-02, BED-03 | [ ] |
| CLN-04 | Implement BedCleaningDomainService | Medium | 3 | CLN-03 | [ ] |
| CLN-05 | Create BedCleaningController | Medium | 3 | CLN-03 | [ ] |
| CLN-06 | Create Cleaning DTOs | Low | 2 | None | [ ] |
| CLN-07 | Implement workflow status transitions | Medium | 3 | CLN-04 | [ ] |
| CLN-08 | Implement 2-hour isolation cleaning check | Low | 2 | CLN-04 | [ ] |

### Acceptance Criteria
- Cleaning task created on discharge
- Task assigned to cleaner
- Task status tracked through workflow
- Verification required before bed available
- Isolation bed 2-hour target enforced

---

## 8. Staff Module

### Objectives
- Implement staff management
- Implement workload calculation
- Implement shift management

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| STF-01 | Create Staff entity | Low | 2 | None | [ ] |
| STF-02 | Create StaffRepository | Low | 2 | STF-01 | [ ] |
| STF-03 | Implement StaffApplicationService | Medium | 4 | STF-02 | [ ] |
| STF-04 | Implement WorkloadCalculator | High | 6 | STF-03 | [ ] |
| STF-05 | Create StaffController | Medium | 3 | STF-03 | [ ] |
| STF-06 | Create Staff DTOs | Low | 2 | None | [ ] |
| STF-07 | Create StaffShift entity | Low | 1 | None | [ ] |
| STF-08 | Create ShiftAssignment entity | Low | 1 | None | [ ] |
| STF-09 | Create StaffShiftRepository | Low | 2 | STF-07 | [ ] |
| STF-10 | Create ShiftAssignmentRepository | Low | 2 | STF-08 | [ ] |
| STF-11 | Implement ShiftApplicationService | Medium | 4 | STF-09, STF-10 | [ ] |
| STF-12 | Implement ShiftDomainService | Medium | 3 | STF-11 | [ ] |
| STF-13 | Create ShiftController | Medium | 3 | STF-11 | [ ] |
| STF-14 | Create Shift DTOs | Low | 2 | None | [ ] |
| STF-15 | Implement overlap prevention | Medium | 3 | STF-12 | [ ] |

### Acceptance Criteria
- Staff registered with role, specialization, certification
- Workload calculated dynamically
- Shifts created and staff assigned
- Overlapping shifts prevented
- Certification expiry checked for critical assignments

---

## 9. Resource Module

### Objectives
- Implement resource definition
- Implement inventory management
- Implement supplier management

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| RES-01 | Create Resource entity | Low | 2 | None | [ ] |
| RES-02 | Create ResourceInventory entity | Low | 2 | None | [ ] |
| RES-03 | Create InventoryTransaction entity | Low | 2 | None | [ ] |
| RES-04 | Create ResourceSupplier entity | Low | 1 | None | [ ] |
| RES-05 | Create ResourceRepository | Low | 2 | RES-01 | [ ] |
| RES-06 | Create ResourceInventoryRepository | Low | 2 | RES-02 | [ ] |
| RES-07 | Create InventoryTransactionRepository | Medium | 3 | RES-03 | [ ] |
| RES-08 | Create ResourceSupplierRepository | Low | 2 | RES-04 | [ ] |
| RES-09 | Implement ResourceApplicationService | Medium | 4 | RES-05 | [ ] |
| RES-10 | Implement InventoryApplicationService | High | 6 | RES-06, RES-07 | [ ] |
| RES-11 | Implement InventoryDomainService | Medium | 4 | RES-10 | [ ] |
| RES-12 | Create ResourceController | Medium | 3 | RES-09 | [ ] |
| RES-13 | Create InventoryController | Medium | 3 | RES-10 | [ ] |
| RES-14 | Create SupplierController | Medium | 3 | RES-09 | [ ] |
| RES-15 | Create Resource/Inventory/Supplier DTOs | Low | 3 | None | [ ] |
| RES-16 | Implement low-stock alerts | Medium | 3 | RES-10 | [ ] |

### Acceptance Criteria
- Resources defined with category, threshold, criticality
- Inventory transactions recorded (append-only)
- Stock calculated from transactions
- Low-stock alerts triggered
- Supplier management functional

---

## 10. Equipment Module

### Objectives
- Implement equipment registry
- Implement maintenance scheduling
- Implement equipment allocation

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| EQP-01 | Create Equipment entity | Low | 2 | None | [ ] |
| EQP-02 | Create EquipmentMaintenance entity | Low | 2 | None | [ ] |
| EQP-03 | Create EquipmentRepository | Low | 2 | EQP-01 | [ ] |
| EQP-04 | Create EquipmentMaintenanceRepository | Low | 2 | EQP-02 | [ ] |
| EQP-05 | Implement EquipmentApplicationService | Medium | 4 | EQP-03 | [ ] |
| EQP-06 | Implement EquipmentMaintenanceApplicationService | Medium | 4 | EQP-04 | [ ] |
| EQP-07 | Create EquipmentController | Medium | 3 | EQP-05 | [ ] |
| EQP-08 | Create EquipmentMaintenanceController | Medium | 3 | EQP-06 | [ ] |
| EQP-09 | Create Equipment/Maintenance DTOs | Low | 2 | None | [ ] |
| EQP-10 | Implement maintenance overdue check | Medium | 2 | EQP-06 | [ ] |

### Acceptance Criteria
- Equipment registered with serial number, type, status
- Maintenance scheduled and tracked
- Equipment allocation linked to admissions
- Maintenance overdue alerts generated
- Out-of-service equipment cannot be assigned

---

## 11. CDS Engine

### Objectives
- Implement recommendation scoring
- Implement multi-factor analysis
- Implement fallback logic

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| CDS-01 | Implement CdsEngineService | High | 8 | All resource modules | [ ] |
| CDS-02 | Implement BedScoringService | High | 6 | BED-03, WAR-03 | [ ] |
| CDS-03 | Implement StaffScoringService | High | 6 | STF-03, STF-04 | [ ] |
| CDS-04 | Implement EquipmentScoringService | High | 6 | EQP-03 | [ ] |
| CDS-05 | Implement ResourceScoringService | High | 6 | RES-06, RES-07 | [ ] |
| CDS-06 | Implement ScoringFactors configuration | Medium | 3 | CDS-01 | [ ] |
| CDS-07 | Implement fallback logic | Medium | 4 | CDS-01 | [ ] |
| CDS-08 | Implement rationale generation | Medium | 3 | CDS-01 | [ ] |
| CDS-09 | Test scoring accuracy | High | 6 | CDS-01–CDS-08 | [ ] |

### Acceptance Criteria
- Bed recommendations generated correctly
- Staff recommendations generated correctly
- Equipment recommendations generated correctly
- Resource recommendations generated correctly
- Confidence scores calculated
- Rationale generated for each recommendation
- Fallback triggered when no options available

---

## 12. Recommendation Module

### Objectives
- Implement recommendation lifecycle
- Implement accept/override workflow
- Implement recommendation tracking

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| REC-01 | Create AllocationRecommendation entity | Low | 1 | None | [ ] |
| REC-02 | Create RecommendationItem entity | Low | 1 | None | [ ] |
| REC-03 | Create RecommendationDecision entity | Low | 1 | None | [ ] |
| REC-04 | Create AllocationRecommendationRepository | Low | 2 | REC-01 | [ ] |
| REC-05 | Create RecommendationItemRepository | Low | 2 | REC-02 | [ ] |
| REC-06 | Create RecommendationDecisionRepository | Low | 2 | REC-03 | [ ] |
| REC-07 | Implement RecommendationApplicationService | High | 6 | REC-04, REC-05, REC-06, CDS-01 | [ ] |
| REC-08 | Create RecommendationController | Medium | 4 | REC-07 | [ ] |
| REC-09 | Create Recommendation DTOs | Low | 3 | None | [ ] |
| REC-10 | Implement accept workflow | Medium | 3 | REC-07 | [ ] |
| REC-11 | Implement override workflow | Medium | 3 | REC-07 | [ ] |
| REC-12 | Implement expiry logic | Medium | 2 | REC-07 | [ ] |

### Acceptance Criteria
- Recommendations generated with items
- Items accepted with allocation executed
- Items overridden with justification
- Expired recommendations trigger re-evaluation
- All decisions logged in audit trail

---

## 13. Forecast Module

### Objectives
- Implement forecasting models
- Implement forecast generation
- Implement forecast storage

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| FCT-01 | Create ForecastSnapshot entity | Low | 1 | None | [ ] |
| FCT-02 | Create ForecastSnapshotRepository | Low | 2 | FCT-01 | [ ] |
| FCT-03 | Implement ForecastCalculator | High | 6 | None | [ ] |
| FCT-04 | Implement MovingAverageModel | Medium | 3 | FCT-03 | [ ] |
| FCT-05 | Implement WeightedMovingAverageModel | Medium | 3 | FCT-03 | [ ] |
| FCT-06 | Implement ForecastApplicationService | Medium | 4 | FCT-02, FCT-03 | [ ] |
| FCT-07 | Create ForecastController | Medium | 3 | FCT-06 | [ ] |
| FCT-08 | Create Forecast DTOs | Low | 2 | None | [ ] |

### Acceptance Criteria
- Forecasts generated using Moving Average
- Forecasts stored as snapshots
- Forecast accuracy calculated (MAPE)
- Historical forecasts viewable

---

## 14. Notification Module

### Objectives
- Implement notification delivery
- Implement notification history
- Implement mark-as-read

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| NOT-01 | Create Notification entity | Low | 1 | None | [ ] |
| NOT-02 | Create NotificationRepository | Low | 2 | NOT-01 | [ ] |
| NOT-03 | Implement NotificationApplicationService | Medium | 4 | NOT-02 | [ ] |
| NOT-04 | Create NotificationController | Medium | 3 | NOT-03 | [ ] |
| NOT-05 | Create Notification DTOs | Low | 1 | None | [ ] |
| NOT-06 | Implement mark-as-read | Low | 2 | NOT-03 | [ ] |
| NOT-07 | Implement mark-all-as-read | Low | 2 | NOT-03 | [ ] |

### Acceptance Criteria
- Notifications created for all event types
- Notifications viewable per user
- Notifications marked as read
- Unread count calculated

---

## 15. Report Module

### Objectives
- Implement report generation
- Implement PDF/CSV export
- Implement report queries

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| RPT-01 | Implement ReportApplicationService | High | 8 | All modules | [ ] |
| RPT-02 | Create ReportController | Medium | 4 | RPT-01 | [ ] |
| RPT-03 | Create Report DTOs | Low | 3 | None | [ ] |
| RPT-04 | Implement occupancy report | Medium | 4 | RPT-01 | [ ] |
| RPT-05 | Implement resource report | Medium | 4 | RPT-01 | [ ] |
| RPT-06 | Implement CDS performance report | Medium | 4 | RPT-01 | [ ] |
| RPT-07 | Implement audit report | Medium | 4 | RPT-01 | [ ] |
| RPT-08 | Implement PDF export | Medium | 4 | RPT-01 | [ ] |
| RPT-09 | Implement CSV export | Low | 3 | RPT-01 | [ ] |

### Acceptance Criteria
- All report types generated correctly
- PDF export functional
- CSV export functional
- Data matches source records

---

## 16. Audit Module

### Objectives
- Implement audit log querying
- Implement audit search

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| AUD-01 | Create AuditLogRepository | Low | 2 | None | [ ] |
| AUD-02 | Implement AuditApplicationService | Medium | 4 | AUD-01 | [ ] |
| AUD-03 | Create AuditController | Medium | 3 | AUD-02 | [ ] |
| AUD-04 | Create Audit DTOs | Low | 2 | None | [ ] |
| AUD-05 | Implement audit search with filters | Medium | 4 | AUD-02 | [ ] |

### Acceptance Criteria
- Audit logs searchable by entity, user, date range
- Audit logs immutable
- Audit log integrity verified

---

## 17. Admin Module

### Objectives
- Implement user management
- Implement system configuration

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| ADMN-01 | Create SystemConfiguration entity | Low | 1 | None | [ ] |
| ADMN-02 | Create SystemConfigurationRepository | Low | 2 | ADMN-01 | [ ] |
| ADMN-03 | Implement AdminUserService | Medium | 4 | AUTH-05 | [ ] |
| ADMN-04 | Implement SystemConfigService | Medium | 3 | ADMN-02 | [ ] |
| ADMN-05 | Create AdminUserController | Medium | 3 | ADMN-03 | [ ] |
| ADMN-06 | Create SystemConfigController | Medium | 3 | ADMN-04 | [ ] |
| ADMN-07 | Create Admin DTOs | Low | 2 | None | [ ] |
| ADMN-08 | Implement account unlock | Low | 2 | ADMN-03 | [ ] |

### Acceptance Criteria
- User accounts created, updated, deactivated
- Account unlock functional
- System configuration editable
- Configuration changes audited

---

## 18. Frontend Implementation

### Objectives
- Implement all UI pages
- Implement API integration
- Implement responsive design

### Implementation Tasks

| Task ID | Task | Complexity | Est. Hours | Dependencies | Status |
|---------|------|-----------|------------|--------------|--------|
| UI-01 | React project setup | Low | 4 | None | [ ] |
| UI-02 | Routing and layout | Medium | 8 | UI-01 | [ ] |
| UI-03 | Login page | Medium | 4 | UI-02 | [ ] |
| UI-04 | Dashboard | High | 12 | UI-02 | [ ] |
| UI-05 | Patient pages (list, form, detail) | High | 12 | UI-02 | [ ] |
| UI-06 | Assessment pages | Medium | 8 | UI-02 | [ ] |
| UI-07 | Admission pages (list, form, detail) | High | 16 | UI-02 | [ ] |
| UI-08 | Bed pages | Medium | 8 | UI-02 | [ ] |
| UI-09 | Cleaning pages | Medium | 8 | UI-02 | [ ] |
| UI-10 | Ward pages | Medium | 8 | UI-02 | [ ] |
| UI-11 | Staff pages | Medium | 10 | UI-02 | [ ] |
| UI-12 | Shift pages | Medium | 8 | UI-02 | [ ] |
| UI-13 | Equipment pages | Medium | 10 | UI-02 | [ ] |
| UI-14 | Resource pages | Medium | 8 | UI-02 | [ ] |
| UI-15 | Inventory pages | Medium | 10 | UI-02 | [ ] |
| UI-16 | Supplier pages | Low | 6 | UI-02 | [ ] |
| UI-17 | Recommendation pages | High | 12 | UI-02 | [ ] |
| UI-18 | Forecast pages | Medium | 8 | UI-02 | [ ] |
| UI-19 | Report pages | Medium | 10 | UI-02 | [ ] |
| UI-20 | Notification pages | Low | 6 | UI-02 | [ ] |
| UI-21 | Admin pages | Medium | 8 | UI-02 | [ ] |

### Acceptance Criteria
- All pages render without errors
- All forms validate correctly
- All API calls handle loading/error states
- Responsive design works
- Role-based menu visibility enforced

---

## 19. Summary

| Module | Tasks | Total Hours | Complexity |
|--------|-------|-------------|------------|
| Authentication | 20 | 46 | High |
| Patient | 9 | 23 | Medium |
| Clinical Assessment | 8 | 20 | Medium |
| Ward | 7 | 19 | Medium |
| Bed | 8 | 24 | Medium |
| Admission | 10 | 36 | High |
| Bed Cleaning | 8 | 23 | Medium |
| Staff/Shift | 15 | 48 | High |
| Resource/Inventory | 16 | 44 | High |
| Equipment | 10 | 29 | Medium |
| CDS Engine | 9 | 48 | High |
| Recommendation | 12 | 29 | High |
| Forecast | 8 | 24 | Medium |
| Notification | 7 | 15 | Low |
| Report | 9 | 38 | High |
| Audit | 5 | 15 | Medium |
| Admin | 8 | 21 | Medium |
| Frontend | 21 | 190 | High |
| **Total** | **180** | **679** | |

---

## 20. Document References

| Document | Reference |
|----------|-----------|
| Implementation Roadmap | `docs/implementation/01-implementation-roadmap.md` |
| Service Design | `docs/design/09-service-design.md` |
| Entity Design | `docs/design/06-entity-design.md` |
| Package Structure | `docs/design/04-package-structure.md` |
