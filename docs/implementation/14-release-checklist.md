# 14 — Release Checklist

## 1. Pre-Release Verification

### 1.1 Code Quality

| Check | Requirement | Status |
|-------|-------------|--------|
| All unit tests passing | 100% | [ ] |
| All integration tests passing | 100% | [ ] |
| All API tests passing | 100% | [ ] |
| All security tests passing | 100% | [ ] |
| Code coverage ≥ 75% | Target met | [ ] |
| No critical SonarQube issues | 0 critical | [ ] |
| No high security vulnerabilities | 0 high | [ ] |
| Code review completed | All PRs approved | [ ] |

### 1.2 Documentation

| Check | Requirement | Status |
|-------|-------------|--------|
| README updated | Current | [ ] |
| API documentation generated | OpenAPI complete | [ ] |
| Release notes prepared | Complete | [ ] |
| Changelog updated | Current version documented | [ ] |

---

## 2. Migration Verification

### 2.1 Database Migrations

| Check | Status |
|-------|--------|
| All migrations run on fresh database | [ ] |
| All migrations run on upgrade from previous version | [ ] |
| No migration errors | [ ] |
| All tables created correctly | [ ] |
| All indexes created correctly | [ ] |
| All constraints enforced | [ ] |
| All triggers created | [ ] |
| Seed data loaded correctly | [ ] |

### 2.2 Migration Rollback

| Check | Status |
|-------|--------|
| Rollback procedure documented | [ ] |
| Rollback tested on staging | [ ] |
| Rollback time acceptable | [ ] |

---

## 3. API Verification

### 3.1 Endpoint Verification

| Endpoint Category | Count | Verified | Status |
|-------------------|-------|----------|--------|
| Authentication | 4 | [ ] | [ ] |
| Patient | 5 | [ ] | [ ] |
| Clinical Assessment | 3 | [ ] | [ ] |
| Admission | 6 | [ ] | [ ] |
| Bed | 5 | [ ] | [ ] |
| Bed Cleaning | 5 | [ ] | [ ] |
| Ward | 4 | [ ] | [ ] |
| Staff | 4 | [ ] | [ ] |
| Shift | 3 | [ ] | [ ] |
| Equipment | 5 | [ ] | [ ] |
| Resource | 2 | [ ] | [ ] |
| Inventory | 3 | [ ] | [ ] |
| Supplier | 2 | [ ] | [ ] |
| Recommendation | 4 | [ ] | [ ] |
| Forecast | 2 | [ ] | [ ] |
| Notification | 3 | [ ] | [ ] |
| Report | 4 | [ ] | [ ] |
| Admin | 6 | [ ] | [ ] |
| Audit | 1 | [ ] | [ ] |
| **Total** | **71** | | |

### 3.2 API Response Verification

| Check | Status |
|-------|--------|
| All responses use ApiResponse<T> envelope | [ ] |
| Error responses match ErrorResponse format | [ ] |
| Pagination responses match PagedResponse format | [ ] |
| HTTP status codes correct | [ ] |
| Content-Type headers correct | [ ] |

---

## 4. Frontend Verification

### 4.1 Page Verification

| Page | Render | Form | Table | Status |
|------|--------|------|-------|--------|
| Login | [ ] | [ ] | - | [ ] |
| Dashboard | [ ] | - | [ ] | [ ] |
| Patient List | [ ] | - | [ ] | [ ] |
| Patient Form | [ ] | [ ] | - | [ ] |
| Patient Detail | [ ] | - | [ ] | [ ] |
| Assessment Form | [ ] | [ ] | - | [ ] |
| Assessment Timeline | [ ] | - | [ ] | [ ] |
| Admission List | [ ] | - | [ ] | [ ] |
| Admission Form | [ ] | [ ] | - | [ ] |
| Admission Detail | [ ] | - | [ ] | [ ] |
| Bed Assignment | [ ] | [ ] | - | [ ] |
| Transfer Form | [ ] | [ ] | - | [ ] |
| Discharge Form | [ ] | [ ] | - | [ ] |
| Bed List | [ ] | - | [ ] | [ ] |
| Cleaning Tasks | [ ] | - | [ ] | [ ] |
| Ward List | [ ] | - | [ ] | [ ] |
| Staff List | [ ] | - | [ ] | [ ] |
| Shift Calendar | [ ] | - | [ ] | [ ] |
| Equipment List | [ ] | - | [ ] | [ ] |
| Resource List | [ ] | - | [ ] | [ ] |
| Stock Overview | [ ] | - | [ ] | [ ] |
| Recommendations | [ ] | - | [ ] | [ ] |
| Forecasts | [ ] | - | [ ] | [ ] |
| Reports | [ ] | - | [ ] | [ ] |
| Notifications | [ ] | - | [ ] | [ ] |
| Admin Users | [ ] | - | [ ] | [ ] |
| System Config | [ ] | - | [ ] | [ ] |
| Audit Logs | [ ] | - | [ ] | [ ] |

### 4.2 UI Verification

| Check | Status |
|-------|--------|
| All forms validate correctly | [ ] |
| All tables support pagination | [ ] |
| All tables support sorting | [ ] |
| Loading states display correctly | [ ] |
| Error messages display correctly | [ ] |
| Success messages display correctly | [ ] |
| Responsive design works | [ ] |
| Role-based menu visibility works | [ ] |

---

## 5. Security Verification

### 5.1 Authentication

| Check | Status |
|-------|--------|
| Login with valid credentials works | [ ] |
| Login with invalid credentials fails | [ ] |
| Account lockout after 5 failures works | [ ] |
| Token refresh works | [ ] |
| Logout revokes tokens | [ ] |
| Expired tokens rejected | [ ] |
| Password complexity enforced | [ ] |
| Password history enforced | [ ] |

### 5.2 Authorization

| Check | Status |
|-------|--------|
| Unauthorized access returns 401 | [ ] |
| Forbidden access returns 403 | [ ] |
| Role-based access enforced | [ ] |
| Admin-only endpoints restricted | [ ] |
| Public endpoints accessible without auth | [ ] |

### 5.3 Data Protection

| Check | Status |
|-------|--------|
| No PHI in logs | [ ] |
| No passwords in logs | [ ] |
| Audit logs captured | [ ] |
| Audit logs immutable | [ ] |
| Input validation working | [ ] |
| SQL injection prevented | [ ] |
| XSS prevented | [ ] |

---

## 6. Deployment Verification

### 6.1 Infrastructure

| Check | Status |
|-------|--------|
| Application starts successfully | [ ] |
| Database connection established | [ ] |
| Health check endpoint responds | [ ] |
| SSL certificate valid | [ ] |
| DNS configured correctly | [ ] |
| Firewall rules configured | [ ] |

### 6.2 Configuration

| Check | Status |
|-------|--------|
| Environment variables set | [ ] |
| Database credentials correct | [ ] |
| JWT secret configured | [ ] |
| CORS configured correctly | [ ] |
| Log levels appropriate | [ ] |

### 6.3 Monitoring

| Check | Status |
|-------|--------|
| Health check endpoint working | [ ] |
| Metrics endpoint working | [ ] |
| Application logs visible | [ ] |
| Error logs visible | [ ] |

---

## 7. Rollback Readiness

### 7.1 Rollback Procedure

| Check | Status |
|-------|--------|
| Rollback procedure documented | [ ] |
| Previous version JAR available | [ ] |
| Database backup available | [ ] |
| Rollback tested on staging | [ ] |
| Rollback time estimated | [ ] |

### 7.2 Rollback Triggers

| Condition | Action | Status |
|-----------|--------|--------|
| Application fails to start | Rollback immediately | [ ] |
| Health check fails > 5 min | Rollback immediately | [ ] |
| Critical functionality broken | Rollback within 15 min | [ ] |
| Non-critical issue | Hotfix instead | [ ] |

---

## 8. Post-Release Verification

### 8.1 Immediate (Within 1 hour)

| Check | Status |
|-------|--------|
| Application health check passes | [ ] |
| Authentication works | [ ] |
| Patient creation works | [ ] |
| Admission workflow works | [ ] |
| Frontend loads correctly | [ ] |

### 8.2 Short-term (Within 24 hours)

| Check | Status |
|-------|--------|
| No error spikes in logs | [ ] |
| Performance within targets | [ ] |
| No user-reported issues | [ ] |
| Backup verified | [ ] |

### 8.3 Medium-term (Within 1 week)

| Check | Status |
|-------|--------|
| No critical bugs reported | [ ] |
| User feedback collected | [ ] |
| Performance metrics reviewed | [ ] |
| Security scan clean | [ ] |

---

## 9. Release Sign-off

| Role | Name | Date | Signature |
|------|------|------|-----------|
| Technical Lead | [ ] | [ ] | [ ] |
| Security Reviewer | [ ] | [ ] | [ ] |
| QA Lead | [ ] | [ ] | [ ] |
| Project Manager | [ ] | [ ] | [ ] |
| Stakeholder Representative | [ ] | [ ] | [ ] |

---

## 10. Document References

| Document | Reference |
|----------|-----------|
| Deployment Execution Guide | `docs/implementation/09-deployment-execution-guide.md` |
| Definition of Done | `docs/implementation/10-definition-of-done.md` |
| Code Review Checklist | `docs/implementation/13-code-review-checklist.md` |
| Deployment Design | `docs/design/16-deployment-design.md` |
