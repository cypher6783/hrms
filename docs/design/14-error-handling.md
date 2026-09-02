# 14 — Error Handling

## 1. Global Exception Strategy

All exceptions are caught by `GlobalExceptionHandler` (a `@RestControllerAdvice` class) and converted to standardized error responses.

### 1.1 Exception Hierarchy

```
RuntimeException
├── BusinessException
│   ├── ResourceNotFoundException
│   ├── ValidationException
│   ├── ConflictException
│   ├── UnauthorizedException
│   └── ForbiddenException
├── DataAccessException (Spring)
├── MethodArgumentNotValidException (Jakarta Validation)
└── HttpRequestMethodNotSupportedException
```

---

## 2. Business Exceptions

### 2.1 ResourceNotFoundException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 404 Not Found |
| When Thrown | Entity not found by ID or query |
| Example | Patient with ID xyz not found |
| Usage | Entity lookup by ID, required relationship missing |

### 2.2 ValidationException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 400 Bad Request |
| When Thrown | Business rule violation |
| Examples | Cannot assign bed: ward at full capacity; Cannot create admission: patient has active admission |
| Usage | Business logic validation in services |

### 2.3 ConflictException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 409 Conflict |
| When Thrown | Duplicate resource or constraint violation |
| Examples | Username already exists; Bed number already in use; Staff has overlapping shift |
| Usage | Uniqueness checks, state conflicts |

### 2.4 UnauthorizedException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 401 Unauthorized |
| When Thrown | Authentication failure |
| Examples | Invalid credentials; Account locked; Token expired |
| Usage | Login, token validation |

### 2.5 ForbiddenException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 403 Forbidden |
| When Thrown | Authorization failure |
| Example | User does not have required role |
| Usage | Role-based access control |

---

## 3. Validation Exceptions

### 3.1 MethodArgumentNotValidException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 400 Bad Request |
| When Thrown | Jakarta Bean Validation fails on DTO |
| Format | List of field errors with messages |

### 3.2 ConstraintViolationException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 400 Bad Request |
| When Thrown | Method-level validation fails |

---

## 4. Security Exceptions

### 4.1 AuthenticationException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 401 Unauthorized |
| When Thrown | Spring Security authentication failure |
| Usage | Invalid JWT, missing token |

### 4.2 AccessDeniedException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 403 Forbidden |
| When Thrown | Spring Security authorization failure |
| Usage | Insufficient permissions |

---

## 5. Database Exceptions

### 5.1 DataIntegrityViolationException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 409 Conflict |
| When Thrown | FK violation, unique constraint violation |
| Handling | Convert to ConflictException with meaningful message |

### 5.2 CannotAcquireLockException

| Attribute | Value |
|-----------|-------|
| HTTP Status | 409 Conflict |
| When Thrown | Optimistic locking failure |
| Handling | Retry or return conflict error |

---

## 6. Logging Policy

### 6.1 Log Levels

| Level | Usage |
|-------|-------|
| ERROR | Unexpected exceptions, system failures |
| WARN | Business rule violations, validation failures |
| INFO | Successful operations, state changes |
| DEBUG | Detailed flow information (development only) |

### 6.2 Log Format

```
[TIMESTAMP] [LEVEL] [CLASS] [METHOD] - [MESSAGE] [EXCEPTION]
```

### 6.3 Sensitive Data

- Passwords never logged.
- JWT tokens logged as masked (first 8 chars + "...").
- PHI (patient data) logged only with entity ID, not full data.
- Stack traces logged at ERROR level only.

### 6.4 Structured Logging

All logs use structured format (JSON) for log aggregation:

```json
{
  "timestamp": "2026-06-28T10:30:00Z",
  "level": "ERROR",
  "service": "AdmissionApplicationService",
  "method": "createAdmission",
  "message": "Patient has active admission",
  "patientId": "abc-123",
  "userId": "user-456",
  "exception": "ValidationException"
}
```

---

## 7. Standard API Error Response

### 7.1 Success Response

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { },
  "timestamp": "2026-06-28T10:30:00Z"
}
```

### 7.2 Error Response

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "fullName",
      "message": "Full name is required"
    }
  ],
  "timestamp": "2026-06-28T10:30:00Z"
}
```

### 7.3 Business Error Response

```json
{
  "success": false,
  "message": "Cannot create admission: patient has an active admission",
  "errors": [],
  "timestamp": "2026-06-28T10:30:00Z"
}
```

### 7.4 Authentication Error Response

```json
{
  "success": false,
  "message": "Invalid credentials",
  "errors": [],
  "timestamp": "2026-06-28T10:30:00Z"
}
```

### 7.5 Authorization Error Response

```json
{
  "success": false,
  "message": "Access denied: insufficient permissions",
  "errors": [],
  "timestamp": "2026-06-28T10:30:00Z"
}
```

---

## 8. Exception to HTTP Status Mapping

| Exception | HTTP Status | Response |
|-----------|-------------|----------|
| ResourceNotFoundException | 404 | Error with message |
| ValidationException | 400 | Error with message |
| ConflictException | 409 | Error with message |
| UnauthorizedException | 401 | Error with message |
| ForbiddenException | 403 | Error with message |
| MethodArgumentNotValidException | 400 | Error with field errors |
| DataIntegrityViolationException | 409 | Converted to ConflictException |
| AuthenticationException | 401 | Error with message |
| AccessDeniedException | 403 | Error with message |
| HttpRequestMethodNotSupportedException | 405 | Error with message |
| Exception (catch-all) | 500 | Generic error message |

---

## 9. Retry Strategy

| Scenario | Retry | Backoff |
|----------|-------|---------|
| Database connection failure | 3 attempts | Exponential (1s, 2s, 4s) |
| Optimistic lock failure | 1 retry | Immediate |
| External service timeout | 2 attempts | Linear (1s) |

---

## 10. Document References

| Document | Reference |
|----------|-----------|
| System Architecture | `docs/planning/03-system-architecture.md` |
| Validation Rules | `docs/design/13-validation-rules.md` |
| Error Handling | `docs/design/14-error-handling.md` |
