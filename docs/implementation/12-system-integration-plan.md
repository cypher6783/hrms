# 12 — System Integration Plan

## 1. Backend Integration Order

### 1.1 Phase 1: Infrastructure

| Step | Module | Integration Point | Status |
|------|--------|-------------------|--------|
| 1 | Common | Exception handling, DTOs, utilities | [ ] |
| 2 | Authentication | JWT, Spring Security, User entity | [ ] |
| 3 | Database | Flyway migrations, repository base | [ ] |

### 1.2 Phase 2: Core Domain

| Step | Module | Integration Point | Status |
|------|--------|-------------------|--------|
| 4 | Patient | Depends on Auth (audit fields) | [ ] |
| 5 | ClinicalAssessment | Depends on Patient, Admission | [ ] |
| 6 | Ward | Depends on Auth (audit fields) | [ ] |
| 7 | Bed | Depends on Ward | [ ] |

### 1.3 Phase 3: Operations

| Step | Module | Integration Point | Status |
|------|--------|-------------------|--------|
| 8 | Admission | Depends on Patient, Ward, Bed | [ ] |
| 9 | BedCleaning | Depends on Bed, Admission | [ ] |
| 10 | Staff | Depends on Ward | [ ] |
| 11 | Shift | Depends on Staff | [ ] |

### 1.4 Phase 4: Resources

| Step | Module | Integration Point | Status |
|------|--------|-------------------|--------|
| 12 | Resource | Depends on Auth | [ ] |
| 13 | Inventory | Depends on Resource | [ ] |
| 14 | Equipment | Depends on Auth | [ ] |
| 15 | Maintenance | Depends on Equipment | [ ] |

### 1.5 Phase 5: Intelligence

| Step | Module | Integration Point | Status |
|------|--------|-------------------|--------|
| 16 | CDS Engine | Depends on all resource modules | [ ] |
| 17 | Recommendation | Depends on CDS Engine | [ ] |
| 18 | Forecast | Depends on Admission, Resource | [ ] |

### 1.6 Phase 6: Support

| Step | Module | Integration Point | Status |
|------|--------|-------------------|--------|
| 19 | Notification | Depends on Recommendation | [ ] |
| 20 | Report | Depends on all modules | [ ] |
| 21 | Audit | Depends on all modules | [ ] |
| 22 | Admin | Depends on Auth | [ ] |

---

## 2. Frontend Integration Order

### 2.1 Phase 1: Foundation

| Step | Component | Integration Point | Status |
|------|-----------|-------------------|--------|
| 1 | Project Setup | React, Vite, TailwindCSS | [ ] |
| 2 | Routing | React Router | [ ] |
| 3 | Layout | Sidebar, Header, Content | [ ] |
| 4 | Auth Context | JWT storage, refresh | [ ] |
| 5 | API Client | Axios configuration | [ ] |

### 2.2 Phase 2: Authentication

| Step | Component | Integration Point | Status |
|------|-----------|-------------------|--------|
| 6 | Login Page | POST /auth/login | [ ] |
| 7 | Protected Routes | JWT validation | [ ] |

### 2.3 Phase 3: Core Pages

| Step | Component | Integration Point | Status |
|------|-----------|-------------------|--------|
| 8 | Dashboard | GET /wards/status, GET /admissions | [ ] |
| 9 | Patient Pages | CRUD /patients | [ ] |
| 10 | Assessment Pages | CRUD /assessments | [ ] |
| 11 | Admission Pages | CRUD /admissions | [ ] |
| 12 | Bed Pages | CRUD /beds | [ ] |
| 13 | Cleaning Pages | CRUD /cleaning | [ ] |
| 14 | Ward Pages | CRUD /wards | [ ] |

### 2.4 Phase 4: Operations Pages

| Step | Component | Integration Point | Status |
|------|-----------|-------------------|--------|
| 15 | Staff Pages | CRUD /staff | [ ] |
| 16 | Shift Pages | CRUD /shifts | [ ] |
| 17 | Equipment Pages | CRUD /equipment | [ ] |
| 18 | Maintenance Pages | CRUD /maintenance | [ ] |

### 2.5 Phase 5: Resource Pages

| Step | Component | Integration Point | Status |
|------|-----------|-------------------|--------|
| 19 | Resource Pages | CRUD /resources | [ ] |
| 20 | Inventory Pages | CRUD /inventory | [ ] |
| 21 | Supplier Pages | CRUD /suppliers | [ ] |

### 2.6 Phase 6: Intelligence Pages

| Step | Component | Integration Point | Status |
|------|-----------|-------------------|--------|
| 22 | Recommendation Pages | /recommendations | [ ] |
| 23 | Forecast Pages | /forecasts | [ ] |
| 24 | Report Pages | /reports | [ ] |
| 25 | Notification Pages | /notifications | [ ] |
| 26 | Admin Pages | /admin | [ ] |

---

## 3. Database Integration

### 3.1 Migration Integration

| Check | Status |
|-------|--------|
| All migrations run successfully | [ ] |
| Schema matches entity definitions | [ ] |
| Seed data loaded correctly | [ ] |
| Indexes created | [ ] |
| Constraints enforced | [ ] |
| Triggers working | [ ] |

### 3.2 Repository Integration

| Check | Status |
|-------|--------|
| All repositories compile | [ ] |
| All custom queries work | [ ] |
| Pagination working | [ ] |
| Soft delete filtering working | [ ] |
| Audit fields auto-populated | [ ] |

---

## 4. Security Integration

### 4.1 Authentication Integration

| Check | Status |
|-------|--------|
| Login flow complete | [ ] |
| JWT tokens generated correctly | [ ] |
| Refresh token rotation working | [ ] |
| Logout revokes tokens | [ ] |
| Password policy enforced | [ ] |
| Account lockout working | [ ] |

### 4.2 Authorization Integration

| Check | Status |
|-------|--------|
| All endpoints have @PreAuthorize | [ ] |
| Role checks working | [ ] |
| 403 returned for unauthorized | [ ] |
| Public endpoints accessible | [ ] |

---

## 5. CDS Engine Integration

### 5.1 Engine Integration

| Check | Status |
|-------|--------|
| Engine triggered on admission creation | [ ] |
| Engine triggered on severity change | [ ] |
| Engine triggered on bed release | [ ] |
| Recommendations generated correctly | [ ] |
| Confidence scores calculated | [ ] |
| Rationale generated | [ ] |

### 5.2 Recommendation Integration

| Check | Status |
|-------|--------|
| Recommendations stored in database | [ ] |
| Recommendations viewable in UI | [ ] |
| Accept workflow functional | [ ] |
| Override workflow functional | [ ] |
| Expiry logic working | [ ] |
| Audit logging captured | [ ] |

---

## 6. Notification Integration

| Check | Status |
|-------|--------|
| Notifications created for all events | [ ] |
| Notifications viewable in UI | [ ] |
| Mark-as-read working | [ ] |
| Unread count accurate | [ ] |

---

## 7. Report Integration

| Check | Status |
|-------|--------|
| All report types generate | [ ] |
| Data matches source records | [ ] |
| PDF export working | [ ] |
| CSV export working | [ ] |
| Date filters working | [ ] |

---

## 8. Testing Checkpoints

### Checkpoint 1: After Phase 1 (Infrastructure)

| Test | Status |
|------|--------|
| Application starts | [ ] |
| Database connects | [ ] |
| Authentication works | [ ] |
| User can login | [ ] |

### Checkpoint 2: After Phase 2 (Core Domain)

| Test | Status |
|------|--------|
| Patient CRUD works | [ ] |
| Assessment recording works | [ ] |
| Ward CRUD works | [ ] |
| Bed CRUD works | [ ] |

### Checkpoint 3: After Phase 3 (Operations)

| Test | Status |
|------|--------|
| Admission lifecycle works | [ ] |
| Bed cleaning workflow works | [ ] |
| Staff management works | [ ] |
| Shift management works | [ ] |

### Checkpoint 4: After Phase 4 (Resources)

| Test | Status |
|------|--------|
| Resource management works | [ ] |
| Inventory transactions work | [ ] |
| Equipment management works | [ ] |
| Maintenance scheduling works | [ ] |

### Checkpoint 5: After Phase 5 (Intelligence)

| Test | Status |
|------|--------|
| CDS engine generates recommendations | [ ] |
| Recommendations can be accepted | [ ] |
| Recommendations can be overridden | [ ] |
| Forecasts generate correctly | [ ] |

### Checkpoint 6: After Phase 6 (Support)

| Test | Status |
|------|--------|
| Notifications work | [ ] |
| Reports generate | [ ] |
| Audit logs searchable | [ ] |
| Admin functions work | [ ] |

### Checkpoint 7: After Phase 7 (Frontend)

| Test | Status |
|------|--------|
| All pages render | [ ] |
| All forms work | [ ] |
| All API calls succeed | [ ] |
| Responsive design works | [ ] |

### Checkpoint 8: After Phase 8 (Integration)

| Test | Status |
|------|--------|
| End-to-end workflows pass | [ ] |
| Performance targets met | [ ] |
| Security tests pass | [ ] |
| All bugs resolved | [ ] |

---

## 9. Integration Issues Log

| Issue | Module | Description | Resolution | Status |
|-------|--------|-------------|------------|--------|
| [ ] | [ ] | [ ] | [ ] | [ ] |

---

## 10. Document References

| Document | Reference |
|----------|-----------|
| Implementation Roadmap | `docs/implementation/01-implementation-roadmap.md` |
| Backend Build Checklist | `docs/implementation/02-backend-build-checklist.md` |
| Frontend Build Checklist | `docs/implementation/03-frontend-build-checklist.md` |
| Testing Execution Plan | `docs/implementation/08-testing-execution-plan.md` |
