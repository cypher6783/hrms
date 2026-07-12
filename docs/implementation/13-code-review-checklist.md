# 13 — Code Review Checklist

## 1. Architecture Compliance

- [ ] Code follows layered architecture (Controller → Service → Repository)
- [ ] No business logic in controllers
- [ ] No database access in controllers
- [ ] DTOs used for API boundaries
- [ ] Entities not exposed to API layer
- [ ] Domain services contain pure business logic
- [ ] No circular dependencies between modules
- [ ] Package structure follows defined conventions

---

## 2. Security Review

### 2.1 Authentication

- [ ] No hardcoded credentials
- [ ] Passwords hashed with bcrypt
- [ ] JWT tokens validated on every request
- [ ] Refresh tokens rotated correctly
- [ ] Logout revokes tokens
- [ ] Account lockout enforced

### 2.2 Authorization

- [ ] @PreAuthorize on all protected endpoints
- [ ] Role checks correct for each endpoint
- [ ] Service-level permission checks for complex rules
- [ ] No unauthorized data access

### 2.3 Input Validation

- [ ] All request DTOs have validation annotations
- [ ] All inputs validated before processing
- [ ] No SQL injection vulnerabilities (parameterized queries only)
- [ ] No XSS vulnerabilities (React escaping)
- [ ] No mass assignment vulnerabilities (DTO binding)

### 2.4 Data Protection

- [ ] No PHI in application logs
- [ ] No passwords in log output
- [ ] No tokens in URL parameters
- [ ] Sensitive data encrypted at rest
- [ ] TLS enforced for all communication

---

## 3. Performance Review

- [ ] Pagination implemented for all list operations
- [ ] No N+1 query problems (JOIN FETCH or @EntityGraph)
- [ ] Database indexes defined for query columns
- [ ] Caching implemented for read-heavy operations
- [ ] No unnecessary database queries
- [ ] Large collections not loaded into memory
- [ ] Async processing for long-running operations

---

## 4. Coding Standards

### 4.1 Naming Conventions

- [ ] Classes: PascalCase (PatientApplicationService)
- [ ] Methods: camelCase (createPatient)
- [ ] Fields: camelCase (patientNumber)
- [ ] Constants: UPPER_SNAKE_CASE (MAX_LOGIN_ATTEMPTS)
- [ ] Packages: lowercase, singular (com.hospital.resource.patient)
- [ ] Tables: snake_case, plural (patients)
- [ ] Columns: snake_case (patient_number)

### 4.2 Code Quality

- [ ] Method length ≤ 50 lines
- [ ] Class length ≤ 500 lines
- [ ] Nesting depth ≤ 3 levels
- [ ] Parameters ≤ 5 per method
- [ ] No magic numbers (use constants)
- [ ] No commented-out code
- [ ] No TODO without ticket reference

### 4.3 Java Specific

- [ ] Records used for immutable DTOs
- [ ] Pattern matching used for instanceof
- [ ] Constructor injection (no @Autowired on fields)
- [ ] @Transactional on public methods only
- [ ] Optional used for nullable returns
- [ ] Stream API used appropriately

---

## 5. Testing Requirements

- [ ] Unit tests for all business logic
- [ ] Integration tests for service workflows
- [ ] API tests for all endpoints
- [ ] Security tests for auth scenarios
- [ ] Test naming follows convention
- [ ] Tests are independent (no shared state)
- [ ] Tests clean up after themselves
- [ ] Code coverage ≥ 75% overall

---

## 6. Documentation Requirements

- [ ] Javadoc on all public classes
- [ Javadoc on all public methods
- [ ] @param, @return, @throws documented
- [ ] API documentation (OpenAPI) generated
- [ ] README updated with new features
- [ ] No obvious comments (explain WHY, not WHAT)

---

## 7. Error Handling

- [ ] All exceptions caught by GlobalExceptionHandler
- [ ] Business exceptions extend BusinessException
- [ ] Error responses use standard format (ApiResponse)
- [ ] Appropriate HTTP status codes returned
- [ ] Error messages user-friendly
- [ ] Sensitive details not exposed in errors
- [ ] Exceptions logged at correct level

---

## 8. Database

- [ ] Entity annotations correct
- [ ] Relationships properly mapped
- [ ] Cascade rules appropriate
- [ ] Indexes defined for queries
- [ ] Constraints enforced
- [ ] Soft deletes implemented correctly
- [ ] Audit fields populated
- [ ] No raw SQL (parameterized queries only)

---

## 9. API Design

- [ ] RESTful conventions followed
- [ ] HTTP methods used correctly
- [ ] Status codes appropriate
- [ ] Request/response DTOs defined
- [ ] Validation on all inputs
- [ ] Pagination on list endpoints
- [ ] Error responses consistent

---

## 10. Git

- [ ] Commit messages follow convention
- [ ] One feature per commit
- [ ] No large files committed
- [ ] No secrets committed
- [ ] .gitignore updated
- [ ] Branch naming follows convention

---

## 11. Review Summary

| Category | Pass | Fail | Notes |
|----------|------|------|-------|
| Architecture Compliance | [ ] | [ ] | |
| Security Review | [ ] | [ ] | |
| Performance Review | [ ] | [ ] | |
| Coding Standards | [ ] | [ ] | |
| Testing Requirements | [ ] | [ ] | |
| Documentation Requirements | [ ] | [ ] | |
| Error Handling | [ ] | [ ] | |
| Database | [ ] | [ ] | |
| API Design | [ ] | [ ] | |
| Git | [ ] | [ ] | |

**Overall Decision**: [ ] Approve | [ ] Approve with Comments | [ ] Request Changes

**Reviewer**: _________________ **Date**: _________________

---

## 12. Document References

| Document | Reference |
|----------|-----------|
| Coding Standards | `docs/design/17-coding-standards.md` |
| Security Design | `docs/design/10-security-design.md` |
| Error Handling | `docs/design/14-error-handling.md` |
| Validation Rules | `docs/design/13-validation-rules.md` |
