# 10 — Security Design

## 1. Authentication Flow

### 1.1 Login Sequence

```
1. Client sends POST /api/v1/auth/login with {username, password}
2. AuthController receives request
3. AuthService validates credentials:
   a. Check if account is locked (failed_login_attempts >= 5 AND locked_until > now)
   b. Load User by username
   c. Verify password using BCrypt
   d. If success: reset failed_login_attempts, update last_login_at
   e. If failure: increment failed_login_attempts, lock if threshold reached
4. If successful:
   a. Generate JWT access token (15-minute expiry)
   b. Generate refresh token (7-day expiry)
   c. Store refresh token hash in refresh_tokens table
   d. Log LOGIN_SUCCESS to login_audit_logs
   e. Return {accessToken, refreshToken, expiresIn, user}
5. If failed:
   a. Log LOGIN_FAILURE to login_audit_logs
   b. Return 401 Unauthorized
```

### 1.2 Token Refresh Sequence

```
1. Client sends POST /api/v1/auth/refresh with {refreshToken}
2. AuthService validates refresh token:
   a. Hash the provided token (SHA-256)
   b. Look up in refresh_tokens by token_hash
   c. Check: not revoked AND expires_at > now
   d. If valid: revoke old token (set revoked = true)
   e. Generate new access token + new refresh token
   f. Store new refresh token hash
   g. Return new tokens
3. If invalid: return 401 Unauthorized
```

### 1.3 Logout Sequence

```
1. Client sends POST /api/v1/auth/logout with {refreshToken}
2. AuthService hashes and revokes the refresh token
3. Log LOGOUT to login_audit_logs
4. Return success
```

---

## 2. Authorization Flow

### 2.1 JWT Filter Processing

```
1. Request arrives at JwtAuthenticationFilter
2. Extract Authorization header
3. If header starts with "Bearer ":
   a. Extract token
   b. Validate using JwtTokenProvider
   c. Extract user ID, roles, expiry
   d. If valid: create Authentication object, set in SecurityContext
   e. If expired: return 401
4. If no header: continue (public endpoint may be accessible)
5. Filter chain continues to AuthorizationManager
```

### 2.2 Role-Based Authorization

Each endpoint is annotated with required roles using `@PreAuthorize`:

```java
@PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('WARD_MANAGER')")
```

The role check is enforced at both controller and service layers.

---

## 3. JWT Lifecycle

### 3.1 Access Token

| Property | Value |
|----------|-------|
| Algorithm | RS256 (asymmetric) |
| Expiry | 15 minutes |
| Claims | sub (user ID), role, iat, exp, jti |
| Storage | Client memory (not localStorage) |
| Transmission | Authorization: Bearer {token} |

### 3.2 Refresh Token

| Property | Value |
|----------|-------|
| Algorithm | SHA-256 (stored as hash) |
| Expiry | 7 days |
| Storage | HttpOnly, Secure, SameSite=Strict cookie |
| Rotation | New token issued on each use |
| Revocation | Old token revoked immediately |

### 3.3 Token Validation Rules

- Access token validated on every protected request
- Refresh token validated only on /refresh endpoint
- Expired tokens rejected immediately
- Revoked refresh tokens rejected
- Invalid signatures rejected

---

## 4. Refresh Token Lifecycle

| State | Description | Next States |
|-------|-------------|-------------|
| Created | Token issued on login/refresh | Active |
| Active | Valid, usable for refresh | Used, Revoked, Expired |
| Used | Consumed during refresh (old token) | Revoked (immediate) |
| Revoked | Manually or automatically revoked | Terminal |
| Expired | Past expires_at timestamp | Terminal |

---

## 5. Password Policy

### 5.1 Complexity Requirements

| Rule | Requirement |
|------|-------------|
| Minimum length | 8 characters |
| Maximum length | 128 characters |
| Uppercase letters | At least 1 |
| Lowercase letters | At least 1 |
| Digits | At least 1 |
| Special characters | At least 1 (!@#$%^&*()_+-=[]{}|;:,.<>?) |

### 5.2 Password History

- Last 5 password hashes retained in password_history table
- New password checked against all 5 using BCrypt comparison
- Oldest entry deleted when history exceeds 5

### 5.3 Password Storage

- BCrypt with strength factor 12
- Hash stored in users.password_hash
- Previous hashes stored in password_history.password_hash

---

## 6. Session Handling

### 6.1 Stateful Aspects

| Aspect | Implementation |
|--------|---------------|
| Access token | Stateless (validated from JWT) |
| Refresh token | Stateful (stored in database) |
| Account lockout | Stateful (tracked in users table) |
| Failed login attempts | Stateful (tracked in users table) |

### 6.2 Session Termination

- Logout revokes refresh token
- Access token expires naturally (15 min)
- Admin can deactivate account (revokes all tokens on next attempt)
- Password change revokes all refresh tokens

---

## 7. RBAC Matrix

### 7.1 Role Definitions

| Role | Description | Key Permissions |
|------|-------------|-----------------|
| ADMINISTRATOR | System administrator | Full system access, user management, configuration |
| WARD_MANAGER | Ward operations manager | Ward management, recommendation review, override authority |
| NURSING_OFFICER | Nursing staff | Patient registration, triage, admission processing |
| RESOURCE_MANAGER | Inventory manager | Resource management, stock monitoring |
| EQUIPMENT_OFFICER | Equipment custodian | Equipment management, maintenance tracking |
| MEDICAL_DOCTOR | Physician | Clinical assessment, severity classification |
| DASHBOARD_VIEWER | Read-only viewer | Dashboard and report viewing only |

### 7.2 Permission Matrix

| Resource | ADMIN | WARD_MGR | NURSE | RES_MGR | EQ_OFF | DOCTOR | VIEWER |
|----------|-------|----------|-------|---------|--------|--------|--------|
| Users (CRUD) | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ |
| Patients (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Patients (Write) | ✓ | ✓ | ✓ | ✗ | ✗ | ✓ | ✗ |
| Assessments (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Assessments (Write) | ✓ | ✗ | ✓ | ✗ | ✗ | ✓ | ✗ |
| Admissions (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Admissions (Write) | ✓ | ✓ | ✓ | ✗ | ✗ | ✓ | ✗ |
| Beds (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Beds (Write) | ✓ | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ |
| Cleaning (Read) | ✓ | ✓ | ✓ | ✗ | ✗ | ✗ | ✓ |
| Cleaning (Write) | ✓ | ✓ | ✓ | ✗ | ✗ | ✗ | ✗ |
| Wards (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Wards (Write) | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ |
| Staff (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Staff (Write) | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ |
| Shifts (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Shifts (Write) | ✓ | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ |
| Equipment (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Equipment (Write) | ✓ | ✓ | ✗ | ✗ | ✓ | ✗ | ✗ |
| Resources (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Resources (Write) | ✓ | ✗ | ✗ | ✓ | ✗ | ✗ | ✗ |
| Inventory (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Inventory (Write) | ✓ | ✗ | ✗ | ✓ | ✗ | ✗ | ✗ |
| Recommendations (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Recommendations (Override) | ✓ | ✓ | ✗ | ✗ | ✗ | ✓ | ✗ |
| Forecasts (Read) | ✓ | ✓ | ✗ | ✗ | ✗ | ✗ | ✓ |
| Forecasts (Generate) | ✓ | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ |
| Notifications (Read) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| Reports (Read) | ✓ | ✓ | ✗ | ✓ | ✗ | ✗ | ✓ |
| Audit Logs (Read) | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ |
| System Config (Read) | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ |
| System Config (Write) | ✓ | ✗ | ✗ | ✗ | ✗ | ✗ | ✗ |

---

## 8. Audit Strategy

### 8.1 Authentication Auditing

All authentication events logged to login_audit_logs:
- LOGIN_SUCCESS
- LOGIN_FAILURE
- LOGOUT
- PASSWORD_CHANGE
- PASSWORD_RESET
- ACCOUNT_LOCKED
- ACCOUNT_UNLOCKED

### 8.2 Data Modification Auditing

All data modifications logged to audit_logs via:
- JPA EntityListeners (@PrePersist, @PreUpdate)
- Database triggers as backup
- Each record includes integrity hash (SHA-256 of chained fields)

### 8.3 Audit Access Control

- Only ADMINISTRATOR role can query audit logs
- Audit logs are append-only (database triggers prevent UPDATE/DELETE)
- Audit records include cryptographic integrity hash

---

## 9. Security Sequence — Full Request Flow

```
1. Client → nginx (TLS termination)
2. nginx → Spring Boot
3. JwtAuthenticationFilter:
   a. Extract Bearer token
   b. Validate signature and expiry
   c. Set SecurityContext
4. AuthorizationManager:
   a. Check role against endpoint requirements
   b. Deny if insufficient permissions
5. Controller:
   a. Validate request DTO (Jakarta Bean Validation)
   b. Delegate to ApplicationService
6. ApplicationService:
   a. Execute business logic
   b. Persist via Repository
   c. Trigger audit logging
7. Response:
   a. Serialize to DTO
   b. Return standardized ApiResponse envelope
```

---

## 10. Threat Model

| Threat | Mitigation |
|--------|-----------|
| Brute-force login | Account lockout after 5 failures, 15-minute cooldown |
| Token theft | HttpOnly cookies for refresh tokens, short-lived access tokens (15 min) |
| Token replay | JWT expiry, refresh token rotation, revocation on logout |
| SQL injection | JPA parameterized queries only |
| XSS | Input validation, Content-Security-Policy headers |
| CSRF | Spring Security CSRF filter, SameSite cookies |
| Privilege escalation | RBAC enforced at service layer, not just controller |
| Data exposure | No PHI in logs, TLS 1.2+ for all traffic |
| Session fixation | New session on login, old session invalidated |
| Password guessing | Password complexity requirements, account lockout |
| Audit tampering | Append-only audit tables, integrity hash chain |
| Configuration exposure | Environment variables for secrets, no hardcoded credentials |

---

## 11. Security Headers

| Header | Value |
|--------|-------|
| Content-Security-Policy | default-src 'self'; script-src 'self' |
| X-Content-Type-Options | nosniff |
| X-Frame-Options | DENY |
| Strict-Transport-Security | max-age=31536000; includeSubDomains |
| X-XSS-Protection | 1; mode=block |
| Referrer-Policy | strict-origin-when-cross-origin |

---

## 12. Document References

| Document | Reference |
|----------|-----------|
| System Architecture | `docs/planning/03-system-architecture.md` |
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Domain Model | `docs/planning/06-domain-model.md` |
| Service Design | `docs/design/09-service-design.md` |
