# 17 — Coding Standards

## 1. Java Conventions

### 1.1 Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Package | lowercase, singular | `com.hospital.resource.patient` |
| Class | PascalCase, singular | `PatientApplicationService` |
| Interface | PascalCase, no prefix | `PatientRepository` (not IPatientRepository) |
| Method | camelCase | `createPatient()`, `findByPatientNumber()` |
| Field | camelCase | `patientNumber`, `fullName` |
| Constant | UPPER_SNAKE_CASE | `MAX_LOGIN_ATTEMPTS` |
| Enum | UPPER_SNAKE_CASE | `SEVERE`, `CONFIRMED` |
| Parameter | camelCase | `patientId`, `admissionRequest` |
| Local variable | camelCase | `currentStock`, `workloadScore` |

### 1.2 Class Organization

```
1. Package declaration
2. Imports (grouped: java, javax, org, com)
3. Class-level Javadoc
4. Class declaration
5. Static fields (final first)
6. Instance fields (final first)
7. Constructor
8. Public methods
9. Protected methods
10. Private methods
11. Inner classes/interfaces
```

### 1.3 Method Guidelines

- Maximum method length: 50 lines.
- Maximum parameters: 5 (use DTO for more).
- Maximum nesting depth: 3 levels.
- Return early for guard clauses.
- Single responsibility per method.

### 1.4 Record Classes (Java 21)

Use records for immutable DTOs and value objects:

```java
public record PatientResponse(
    UUID id,
    String patientNumber,
    String fullName,
    LocalDate dateOfBirth,
    String gender
) {}
```

### 1.5 Pattern Matching (Java 21)

Use pattern matching for instanceof:

```java
if (entity instanceof Patient patient) {
    // use patient directly
}
```

---

## 2. Spring Boot Conventions

### 2.1 Annotation Usage

| Annotation | Usage | Notes |
|-----------|-------|-------|
| @Service | Application and domain services | Constructor injection |
| @Repository | Data access interfaces | Extend JpaRepository |
| @RestController | REST controllers | Return ApiResponse<T> |
| @Configuration | Configuration classes | @Bean methods |
| @Transactional | Service methods | On public methods only |
| @Validated | Controller parameters | Enable validation |
| @PreAuthorize | Endpoint security | Role-based access |
| @Cacheable | Read-heavy queries | With evict on updates |
| @Async | Long-running operations | For report generation |

### 2.2 Dependency Injection

- Use constructor injection exclusively (no @Autowired on fields).
- Use Lombok @RequiredArgsConstructor for constructors.
- One constructor per class.

```java
@Service
@RequiredArgsConstructor
public class PatientApplicationService {
    private final PatientRepository patientRepository;
    private final AuditService auditService;
}
```

### 2.3 Transaction Management

- `@Transactional` on public service methods only.
- Use `@Transactional(readOnly = true)` for read operations.
- Default propagation: REQUIRED.
- Default isolation: DATABASE_DEFAULT.
- Rollback on: RuntimeException (unchecked exceptions).

### 2.4 Exception Handling

- Custom exceptions extend BusinessException.
- All exceptions caught by GlobalExceptionHandler.
- No try-catch in controllers (handle in service layer).
- Log exceptions at appropriate level before throwing.

---

## 3. Layering Rules

### 3.1 Layer Dependencies

```
Controller → Application Service → Domain Service → Repository
```

### 3.2 Forbidden Dependencies

| From | To | Reason |
|------|----|--------|
| Controller | Repository | Bypasses business logic |
| Domain Service | Application Service | Circular dependency |
| Repository | Service | Repository is data access only |
| DTO | Entity | DTOs are API contracts, not domain objects |
| Entity | DTO | Entities are domain objects, not API contracts |

### 3.3 DTO Mapping

- Use MapStruct for entity-DTO mapping.
- Mapping interfaces annotated with @Mapper.
- No manual mapping in service methods.

```java
@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientResponse toResponse(Patient patient);
    Patient toEntity(PatientRequest request);
}
```

---

## 4. Package Rules

### 4.1 Package Structure

- One package per module (patient, admission, bed, etc.).
- Sub-packages: controller, dto, service, domain, repository.
- Common package for shared utilities.

### 4.2 Package Visibility

- DTOs: public (API contracts).
- Controllers: public (Spring components).
- Services: public (injected by Spring).
- Domain classes: public (used by services).
- Repositories: public (extends Spring interfaces).
- Mappers: package-private (used only within package).

---

## 5. Logging Policy

### 5.1 Log Levels

| Level | When to Use |
|-------|-------------|
| ERROR | Unexpected exceptions, system failures |
| WARN | Business rule violations, recoverable errors |
| INFO | State changes, successful operations |
| DEBUG | Detailed flow information, variable values |
| TRACE | Fine-grained execution steps |

### 5.2 Log Messages

- Start with uppercase verb in present tense.
- Include relevant IDs (patientId, admissionId).
- Never log passwords, tokens, or PHI.
- Use structured logging (key-value pairs).

```java
log.info("Admission created: admissionId={}, patientId={}, wardId={}",
    admission.getId(), admission.getPatientId(), admission.getWardId());
```

### 5.3 Exception Logging

- Log at ERROR level with full stack trace.
- Include context (method, parameters, user).
- Use log.error("message", exception) for stack traces.

---

## 6. Testing Conventions

### 6.1 Test Class Structure

```java
@ExtendWith(MockitoExtension.class)
class PatientApplicationServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientApplicationService service;

    @Test
    void test_createPatient_validInput_returnsResponse() {
        // Given
        // When
        // Then
    }
}
```

### 6.2 Test Method Naming

```
test_{method}_{scenario}_{expectedResult}
```

### 6.3 Test Organization

- One test class per service/repository.
- Arrange-Act-Assert pattern.
- Each test tests one behavior.
- Use @DisplayName for readable test names.

### 6.4 Test Data

- Use builder pattern for test data.
- Define constants for common test values.
- Use @BeforeEach for shared setup.

---

## 7. Documentation Conventions

### 7.1 Javadoc

- All public classes must have Javadoc.
- All public methods must have Javadoc.
- Javadoc must include @param, @return, @throws.
- Use `{@link}` for cross-references.

### 7.2 Code Comments

- Explain WHY, not WHAT.
- No obvious comments (e.g., `// increment counter`).
- Use TODO comments with ticket reference: `// TODO: T123 - Implement caching`.
- Remove commented-out code.

### 7.3 API Documentation

- All endpoints documented via OpenAPI annotations.
- Request/response DTOs annotated with schema descriptions.
- Example values provided in annotations.

---

## 8. Git Workflow

### 8.1 Branch Strategy

```
main (production)
├── develop (integration)
│   ├── feature/T123-patient-registration
│   ├── feature/T124-admission-workflow
│   └── bugfix/T125-fix-login-error
└── release/v1.0.0
```

### 8.2 Commit Messages

```
Type(scope): Short description (max 72 chars)

Type: feat, fix, docs, style, refactor, test, chore
Scope: module name (patient, admission, bed, etc.)
Description: Imperative mood, no period

Examples:
feat(patient): implement patient registration endpoint
fix(admission): resolve active admission check logic
docs(api): update patient endpoint documentation
test(cds): add bed scoring unit tests
```

### 8.3 Pull Requests

- One feature per PR.
- PR description includes: what, why, how, testing.
- At least one code review required.
- All tests must pass before merge.
- No force-pushing to develop or main.

### 8.4 Code Review Checklist

- [ ] Follows coding standards
- [ ] Tests included and passing
- [ ] No security vulnerabilities
- [ ] No hardcoded values
- [ ] Exception handling appropriate
- [ ] Logging at correct levels
- [ ] Documentation updated
- [ ] No circular dependencies
- [ ] DTOs used for API boundaries

---

## 9. Security Conventions

### 9.1 Input Validation

- Validate all inputs at controller layer.
- Use Jakarta Bean Validation annotations.
- Validate at service layer for business rules.

### 9.2 Authentication

- Never store plaintext passwords.
- Use bcrypt for password hashing.
- Validate JWT on every protected endpoint.

### 9.3 Authorization

- Check roles at controller level (@PreAuthorize).
- Check permissions at service level for complex rules.

### 9.4 Data Protection

- No PHI in logs.
- Encrypt sensitive data at rest.
- Use parameterized queries (no string concatenation).

---

## 10. Performance Conventions

### 10.1 Database Queries

- Use pagination for all list operations.
- Index frequently queried columns.
- Avoid N+1 queries (use JOIN FETCH).
- Use @EntityGraph for optimized loading.

### 10.2 Caching

- Cache read-heavy, rarely-changing data.
- Evict cache on updates.
- Use cache key conventions: `{entity}:{id}`.

### 10.3 Async Operations

- Use @Async for long-running operations (reports, forecasts).
- Return immediately with status indicator.
- Notify user when complete.

---

## 11. Document References

| Document | Reference |
|----------|-----------|
| Technology Stack | `docs/planning/05-technology-stack.md` |
| System Architecture | `docs/planning/03-system-architecture.md` |
| Package Structure | `docs/design/04-package-structure.md` |
