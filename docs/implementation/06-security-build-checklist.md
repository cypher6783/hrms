# 06 — Security Implementation Checklist

## 1. JWT Implementation

### 1.1 JwtTokenProvider

- [ ] Generate access token with claims: sub (userId), role, iat, exp
- [ ] Access token expiry: 15 minutes
- [ ] HS256 algorithm for signing
- [ ] Secret key from environment variable (JWT_SECRET)
- [ ] Validate token: signature, expiry, notBefore
- [ ] Extract claims from token
- [ ] Generate refresh token (UUID v4)
- [ ] SHA-256 hash for database storage

**Completion Criteria**: Tokens generated, validated, and expired correctly.

### 1.2 JwtAuthenticationFilter

- [ ] Extract token from Authorization header (Bearer scheme)
- [ ] Validate token using JwtTokenProvider
- [ ] Extract userId and role from token
- [ ] Create UsernamePasswordAuthenticationToken
- [ ] Set SecurityContextHolder
- [ ] Skip filter for public endpoints (/auth/login, /auth/refresh)
- [ ] Return 401 for invalid/expired tokens

**Completion Criteria**: Filter intercepts requests, validates tokens, sets security context.

---

## 2. Refresh Token Lifecycle

### 2.1 Token Creation

- [ ] Generate UUID v4 as refresh token
- [ ] Hash token with SHA-256
- [ ] Store hash in refresh_tokens table
- [ ] Set expiry (7 days from creation)
- [ ] Send plaintext token to client in HttpOnly cookie

### 2.2 Token Rotation

- [ ] Validate refresh token (exists, not revoked, not expired)
- [ ] Revoke all user's existing refresh tokens
- [ ] Generate new refresh token
- [ ] Store new token hash
- [ ] Return new access token + new refresh token

### 2.3 Token Revocation

- [ ] On logout: revoke all user's refresh tokens
- [ ] On password change: revoke all user's refresh tokens
- [ ] On account deactivation: revoke all user's refresh tokens
- [ ] Revoked tokens cannot be used for renewal

### 2.4 Token Cleanup

- [ ] Scheduled task to delete expired refresh tokens
- [ ] Runs daily at 2:00 AM

**Completion Criteria**: Refresh tokens created, rotated, revoked, and cleaned up.

---

## 3. Authentication Flow

### 3.1 Login

- [ ] Validate username exists
- [ ] Check account not locked
- [ ] Verify password with bcrypt
- [ ] On success: reset failed attempts, update last login, generate tokens
- [ ] On failure: increment failed attempts, record audit
- [ ] Lock account after 5 consecutive failures
- [ ] Record login audit log entry

### 3.2 Logout

- [ ] Revoke refresh token
- [ ] Record logout audit log entry
- [ ] Client clears tokens

### 3.3 Token Refresh

- [ ] Validate refresh token
- [ ] Check token not revoked
- [ ] Check token not expired
- [ ] Rotate tokens (revoke old, create new)
- [ ] Return new tokens

**Completion Criteria**: Authentication flow complete with all security checks.

---

## 4. Authorization Flow

### 4.1 Role-Based Access Control

| Role | Permissions |
|------|-------------|
| ADMINISTRATOR | Full system access |
| WARD_MANAGER | Ward management, recommendation review, override |
| NURSING_OFFICER | Patient registration, triage, admission |
| RESOURCE_MANAGER | Resource management, stock monitoring |
| EQUIPMENT_OFFICER | Equipment management, maintenance |
| MEDICAL_DOCTOR | Clinical assessment, severity classification |
| DASHBOARD_VIEWER | Dashboard and report viewing only |

### 4.2 Endpoint Authorization

- [ ] @PreAuthorize annotations on all protected endpoints
- [ ] Role checks at controller level
- [ ] Permission checks at service level for complex rules
- [ ] 403 returned for unauthorized access

**Completion Criteria**: All endpoints enforce correct role-based access.

---

## 5. Password Policy

### 5.1 Complexity Requirements

- [ ] Minimum 8 characters
- [ ] At least 1 uppercase letter
- [ ] At least 1 lowercase letter
- [ ] At least 1 digit
- [ ] At least 1 special character
- [ ] Maximum 100 characters

### 5.2 Password Storage

- [ ] bcrypt hashing with work factor 12
- [ ] No plaintext password storage
- [ ] Password hash never logged

### 5.3 Password History

- [ ] Store last 5 password hashes
- [ ] Check new password against history
- [ ] Reject if match found
- [ ] Remove oldest entry when history exceeds 5

### 5.4 Password Change

- [ ] Verify current password
- [ ] Check password history
- [ ] Hash new password
- [ ] Store in password_history
- [ ] Update users.password_hash
- [ ] Revoke all refresh tokens
- [ ] Record audit log

**Completion Criteria**: Password policy fully enforced.

---

## 6. Account Lockout

### 6.1 Lockout Mechanism

- [ ] Track failed_login_attempts in users table
- [ ] Increment on each failed login
- [ ] Lock account when counter reaches 5
- [ ] Set locked_until timestamp (15 minutes from now)
- [ ] Reject login attempts while locked
- [ ] Reset counter on successful login

### 6.2 Manual Unlock

- [ ] Admin endpoint: PUT /admin/users/{id}/unlock
- [ ] Reset failed_login_attempts to 0
- [ ] Set locked_until to NULL
- [ ] Record audit log

**Completion Criteria**: Account lockout and unlock working correctly.

---

## 7. Audit Logging

### 7.1 Authentication Audit

| Event | Audit Table | Fields |
|-------|-------------|--------|
| Login success | login_audit_logs | username, userId, ip, userAgent, timestamp |
| Login failure | login_audit_logs | username, ip, userAgent, failureReason, timestamp |
| Logout | login_audit_logs | username, userId, ip, timestamp |
| Password change | login_audit_logs | userId, timestamp |
| Account lock | login_audit_logs | userId, timestamp |
| Account unlock | login_audit_logs | userId, timestamp |

### 7.2 Data Audit

| Event | Audit Table | Fields |
|-------|-------------|--------|
| Create | audit_logs | entityType, entityId, afterValue, userId, timestamp |
| Update | audit_logs | entityType, entityId, beforeValue, afterValue, userId, timestamp |
| Delete | audit_logs | entityType, entityId, beforeValue, userId, timestamp |

### 7.3 Audit Integrity

- [ ] SHA-256 hash chain on audit_logs
- [ ] PostgreSQL triggers prevent UPDATE/DELETE
- [ ] Append-only enforcement

### 7.4 Audit Retention

- [ ] login_audit_logs: 90 days retention
- [ ] audit_logs: 2 years retention
- [ ] Scheduled cleanup task

**Completion Criteria**: All audit events captured with integrity.

---

## 8. Input Validation

- [ ] Jakarta Bean Validation on all request DTOs
- [ ] @NotBlank, @NotNull, @Size, @Pattern annotations
- [ ] @Email, @Min, @Max for numeric fields
- [ ] Custom validators for complex rules
- [ ] GlobalExceptionHandler catches validation errors
- [ ] 400 response with field errors

**Completion Criteria**: All inputs validated before processing.

---

## 9. Security Headers

- [ ] Content-Security-Policy: default-src 'self'
- [ ] X-Content-Type-Options: nosniff
- [ ] X-Frame-Options: DENY
- [ ] X-XSS-Protection: 1; mode=block
- [ ] Strict-Transport-Security: max-age=31536000
- [ ] Referrer-Policy: strict-origin-when-cross-origin

**Completion Criteria**: Security headers configured in application.

---

## 10. CORS Configuration

- [ ] Allowed origins from environment variable
- [ ] Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- [ ] Allowed headers: Authorization, Content-Type
- [ ] Allow credentials: true
- [ ] Max age: 3600 seconds

**Completion Criteria**: CORS configured for frontend origin.

---

## 11. Security Verification

| Check | Status |
|-------|--------|
| JWT tokens expire correctly | [ ] |
| Refresh tokens rotate correctly | [ ] |
| Refresh tokens revoke correctly | [ ] |
| Password complexity enforced | [ ] |
| Password history enforced | [ ] |
| Account lockout after 5 failures | [ ] |
| Account unlock by admin works | [ ] |
| Role-based access enforced | [ ] |
| Audit logs created for all events | [ ] |
| Audit logs are append-only | [ ] |
| Input validation on all endpoints | [ ] |
| Security headers configured | [ ] |
| CORS configured correctly | [ ] |
| No plaintext passwords stored | [ ] |
| No sensitive data in logs | [ ] |

---

## 12. Document References

| Document | Reference |
|----------|-----------|
| Security Design | `docs/design/10-security-design.md` |
| Validation Rules | `docs/design/13-validation-rules.md` |
| Error Handling | `docs/design/14-error-handling.md` |
