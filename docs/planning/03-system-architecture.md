# 03 — System Architecture

## 1. Architecture Style

The system adopts a **layered monolithic architecture** with clear separation of concerns across presentation, business logic, persistence, and infrastructure layers. This style is chosen for the following reasons:

- **Appropriate scale**: The system serves a single hospital unit (BSUTH Lassa Fever Unit) with a bounded user population, making a monolith the most cost-effective and maintainable approach.
- **Team capacity**: A small development team (2–5 developers) benefits from a single deployable artifact rather than the operational complexity of microservices.
- **Rapid iteration**: A monolith allows faster feature development and deployment during the initial release cycle.
- **Future extensibility**: Well-defined module boundaries within the monolith enable future extraction into services if scale demands it.

The architecture is modular within the monolith, with each system module encapsulated in its own package namespace. Inter-module communication occurs through defined service interfaces, preventing tight coupling between modules.

## 2. High-Level Architecture

```
┌─────────────────────────────────────────────────────┐
│                   CLIENT LAYER                       │
│              React + TailwindCSS SPA                  │
│    ┌──────────┬──────────┬──────────┬──────────┐    │
│    │Dashboard │ Patient  │  Bed     │ Resource  │    │
│    │  Views   │  Views   │  Views   │  Views    │    │
│    └──────────┴──────────┴──────────┴──────────┘    │
└──────────────────────┬──────────────────────────────┘
                       │ HTTPS / REST API
                       ▼
┌─────────────────────────────────────────────────────┐
│                 PRESENTATION LAYER                    │
│           Spring Boot REST Controllers               │
│    ┌──────────┬──────────┬──────────┬──────────┐    │
│    │   Auth   │ Patient  │   Bed    │ Resource  │    │
│    │Controller│Controller│Controller│Controller │    │
│    └──────────┴──────────┴──────────┴──────────┘    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                  SECURITY LAYER                       │
│         Spring Security + JWT Filters                │
│    ┌──────────┬──────────┬──────────┬──────────┐    │
│    │  Auth    │  Role    │  Audit   │  Session  │    │
│    │ Filter   │  Check   │  Filter  │  Mgmt     │    │
│    └──────────┴──────────┴──────────┴──────────┘    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│              APPLICATION SERVICE LAYER                │
│         Orchestration and Workflow Coordination        │
│    ┌──────────┬──────────┬──────────┬──────────┐    │
│    │ Patient  │   Bed    │ Resource │Recommend. │    │
│    │   App    │   App    │   App    │   App     │    │
│    │ Service  │ Service  │ Service  │ Service   │    │
│    ├──────────┼──────────┼──────────┼──────────┤    │
│    │Admission │  Ward    │Equipment │Forecasting│    │
│    │   App    │   App    │   App    │   App     │    │
│    │ Service  │ Service  │ Service  │ Service   │    │
│    ├──────────┼──────────┼──────────┼──────────┤    │
│    │  Staff   │ Notif.   │ Report   │  Audit   │    │
│    │   App    │   App    │   App    │   App     │    │
│    │ Service  │ Service  │ Service  │ Service   │    │
│    └──────────┴──────────┴──────────┴──────────┘    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                DOMAIN SERVICE LAYER                   │
│          Business Rules and Domain Logic              │
│    ┌──────────┬──────────┬──────────┬──────────┐    │
│    │ Patient  │   Bed    │ Resource │Rule-Based │    │
│    │ Domain   │ Domain   │ Domain   │CDS Engine │    │
│    │ Service  │ Service  │ Service  │           │    │
│    ├──────────┼──────────┼──────────┼──────────┤    │
│    │Admission │  Ward    │Equipment │Forecasting│    │
│    │ Domain   │ Domain   │ Domain   │  Domain   │    │
│    │ Service  │ Service  │ Service  │ Service   │    │
│    ├──────────┼──────────┼──────────┼──────────┤    │
│    │  Staff   │Inventory │Workload  │           │    │
│    │ Domain   │ Domain   │ Calculator│           │    │
│    │ Service  │ Service  │          │           │    │
│    └──────────┴──────────┴──────────┴──────────┘    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│               DATA ACCESS LAYER                       │
│           Spring Data JPA Repositories                │
│    ┌──────────┬──────────┬──────────┬──────────┐    │
│    │ Patient  │   Bed    │ Resource │ Equipment │    │
│    │   Repo   │   Repo   │   Repo   │   Repo    │    │
│    ├──────────┼──────────┼──────────┼──────────┤    │
│    │Admission │  Ward    │  Staff   │  Audit    │    │
│    │   Repo   │   Repo   │   Repo   │   Repo    │    │
│    └──────────┴──────────┴──────────┴──────────┘    │
└──────────────────────┬──────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                DATABASE LAYER                         │
│                  PostgreSQL                            │
│    ┌──────────────────────────────────────────┐     │
│    │  hospital_resource_db (primary database)  │     │
│    └──────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────┘
```

## 3. Layered Architecture

### 3.1 Presentation Layer

**Responsibility**: Handles HTTP request/response cycle, input validation, and DTO transformation.

**Components**:
- REST Controllers: Define API endpoints, validate request payloads, delegate to application service layer.
- Request/Response DTOs: Isolate API contracts from internal domain models.
- Exception Handlers: Centralize error response formatting.
- Swagger/OpenAPI: Auto-generate API documentation.

**Conventions**:
- Controllers do not contain business logic.
- All controllers return standardized response envelopes (`ApiResponse<T>`).
- Input validation uses Jakarta Bean Validation annotations on DTOs.

### 3.2 Security Layer

**Responsibility**: Enforces authentication, authorization, and audit logging at the HTTP level.

**Components**:
- JWT Authentication Filter: Validates JWT tokens on protected endpoints.
- Role-Based Authorization: Checks user roles against endpoint permissions.
- Audit Interceptor: Logs incoming requests with user context.
- CORS Configuration: Restricts cross-origin access to authorized frontend origins.

**Conventions**:
- Security configuration is centralized in a dedicated configuration class.
- Public endpoints (login, health check) are explicitly whitelisted.
- All other endpoints require valid JWT authentication.

### 3.3 Application Service Layer

**Responsibility**: Orchestrates business workflows, coordinates domain services, and manages transaction boundaries. This layer contains no business rules itself—it delegates to domain services.

**Components**:
- Application Service Classes: One per module, responsible for workflow orchestration.
- Transaction Management: `@Transactional` boundaries defined at this layer.
- DTO Mapping: Converts between API DTOs and domain entities.
- Event Publishing: Publishes domain events for cross-module communication.

**Conventions**:
- Application services are stateless and injected via constructor-based dependency injection.
- Transaction boundaries are defined at the service method level using `@Transactional`.
- Application services call domain services for business logic.
- Application services do NOT contain business rules—they orchestrate domain services.

### 3.4 Domain Service Layer

**Responsibility**: Contains all domain-specific business rules, validation logic, scoring algorithms, and computation. This is the core business logic layer.

**Components**:
- Domain Service Classes: Business rule implementation per domain concept.
- Rule-Based Clinical Decision Support Engine: Multi-factor scoring and allocation recommendation logic.
- Workload Calculator: Staff workload score computation using the defined formula.
- Inventory Service: Stock level calculations and transaction validation.
- Forecasting Domain Service: Statistical model implementation (Moving Average, Linear Regression).
- Validation Rules: Business rule enforcement beyond input validation.

**Conventions**:
- Domain services contain pure business logic with no infrastructure dependencies.
- Domain services are injected via constructor-based dependency injection.
- Domain services communicate with other domain services through interfaces.
- Domain services do NOT manage transactions—they are called within transactional application services.

### 3.5 Data Access Layer

**Responsibility**: Abstracts database interactions through the repository pattern.

**Components**:
- Spring Data JPA Repositories: Interface-based data access with auto-generated query implementations.
- Custom Query Methods: Derived queries and JPQL for complex data retrieval.
- Entity Mappers: Convert between JPA entities and domain/DTO objects.

**Conventions**:
- Repositories expose only necessary data operations (no business logic in repositories).
- Complex queries are defined using `@Query` annotations or query derivation.
- Pagination is supported for all list operations.
- Soft deletes are implemented via a `deleted` flag rather than physical deletion.

### 3.5 Database Layer

**Responsibility**: Persistent storage of all system data.

**Components**:
- PostgreSQL database with schema-per-concern organization.
- Automated migrations using Flyway.
- Connection pooling via HikariCP (Spring Boot default).

## 4. Module Communication

### 4.1 Intra-Module Communication

Modules communicate through well-defined service interfaces. The Recommendation Engine, as the most cross-cutting module, depends on services from multiple modules:

```
Recommendation Engine
    ├── reads → Patient Service (severity, triage)
    ├── reads → Bed Service (availability, type, isolation)
    ├── reads → Ward Service (occupancy, configuration)
    ├── reads → Equipment Service (availability, status)
    ├── reads → Staff Service (workload, availability, specialization)
    ├── reads → Resource Service (stock levels, allocation history)
    ├── reads → Audit Service (historical utilization patterns)
    └── writes → Notification Service (alerts, recommendations)
```

### 4.2 Event-Driven Interactions

Certain cross-module interactions use an internal event system (application-level event bus, not external message broker):

| Event | Producer | Consumers |
|-------|----------|-----------|
| PatientAdmitted | Admission Service | Recommendation Engine, Notification Service, Audit Service |
| BedStatusChanged | Bed Service | Recommendation Engine, Dashboard Service, Audit Service |
| ResourceBelowThreshold | Resource Service | Notification Service, Recommendation Engine |
| EquipmentMaintenanceDue | Equipment Service | Notification Service, Audit Service |
| StaffAssigned | Staff Service | Audit Service, Notification Service |
| RecommendationGenerated | Recommendation Engine | Notification Service, Audit Service |
| RecommendationOverridden | Recommendation Engine | Audit Service, Notification Service |

### 4.3 Data Flow for Core Workflows

**Admission Workflow**:
```
Nursing Officer → Patient Controller → Patient Service (register/validate)
    → Admission Controller → Admission Service (create admission)
        → Recommendation Engine (generate bed recommendation)
            → Bed Service (query available beds)
            → Ward Service (query ward occupancy)
            → Equipment Service (query equipment availability)
            → Staff Service (query staff workload)
            → Resource Service (query resource stock)
        → Notification Service (alert ward manager)
        → Audit Service (log recommendation)
    → Ward Manager reviews → accepts/overrides recommendation
        → Bed Service (assign bed)
        → Audit Service (log decision)
```

## 5. Technology Justification

### 5.1 Java 21 + Spring Boot

| Factor | Justification |
|--------|---------------|
| Project Requirement | Mandated Java as the implementation language for OOP compliance. |
| Enterprise Adoption | Spring Boot is the de facto standard for enterprise Java applications in healthcare. |
| Ecosystem | Mature ecosystem with extensive libraries for security, data access, and API development. |
| Spring Security | Industry-leading security framework with hospital-grade authentication and authorization support. |
| Spring Data JPA | Simplifies database access with repository abstraction while retaining full JPA/Hibernate power. |
| Community Support | Large community ensures rapid issue resolution and abundant learning resources. |
| Java 21 Features | Virtual threads, pattern matching, and record classes improve code expressiveness and performance. |

### 5.2 PostgreSQL

| Factor | Justification |
|--------|---------------|
| ACID Compliance | Essential for healthcare data integrity where partial writes are unacceptable. |
| JSON Support | Native JSON/JSONB for flexible data storage (recommendation parameters, audit metadata). |
| Full-Text Search | Built-in search capabilities for patient lookup and audit log filtering. |
| Performance | Handles complex joins and aggregations required by reporting and forecasting modules. |
| Cost | Open-source with no licensing fees, appropriate for budget-constrained deployment. |
| Reliability | Proven track record in healthcare and institutional deployments. |

### 5.3 React + TailwindCSS

| Factor | Justification |
|--------|---------------|
| Component Reusability | React's component model maps well to dashboard widgets and form components. |
| TailwindCSS | Utility-first CSS enables rapid UI development with consistent design. |
| State Management | React's built-in state management suffices for the application's complexity. |
| Developer Availability | Large talent pool and extensive documentation. |

### 5.4 Maven

| Factor | Justification |
|--------|---------------|
| Convention over Configuration | Standardized project structure reduces onboarding time. |
| Dependency Management | Centralized dependency management with conflict resolution. |
| Plugin Ecosystem | Comprehensive plugins for testing, code quality, and deployment. |
| Industry Standard | Most widely used build tool in the Java ecosystem. |

## 6. Scalability Considerations

### 6.1 Current Scale Requirements

| Metric | Requirement |
|--------|-------------|
| Concurrent Users | ≤ 50 |
| Patient Records | ≤ 10,000 |
| Audit Log Entries | ≤ 500,000 |
| Daily Transactions | ≤ 5,000 |

### 6.2 Scalability Strategy

The monolithic architecture is sufficient for the current scale. The following measures ensure the system can grow within its deployment model:

- **Database Indexing**: Proper indexing strategy (see `07-database-plan.md`) ensures query performance as data volume grows.
- **Connection Pooling**: HikariCP connection pooling manages database connections efficiently under load.
- **Caching**: In-memory caching (Spring Cache with Caffeine) for frequently accessed reference data (ward configurations, bed inventories, staff profiles).
- **Pagination**: All list endpoints return paginated results to prevent memory exhaustion.
- **Async Processing**: Long-running operations (report generation, forecasting) execute asynchronously with status polling.

### 6.3 Future Scaling Path

If the system expands beyond a single unit or hospital:

1. **Phase 1**: Extract the Recommendation Engine into a separate service.
2. **Phase 2**: Introduce an API Gateway for multi-service routing.
3. **Phase 3**: Migrate to a microservices architecture with independent service deployment.

## 7. Security Considerations

### 7.1 Authentication and Authorization

- JWT-based stateless authentication with short-lived access tokens (15-minute expiry).
- Role-based access control (RBAC) with five defined roles: Administrator, Ward Manager, Nursing Officer, Resource Manager, Equipment Officer.
- Permission matrix enforced at the service layer, not just the controller layer.

### 7.2 Data Protection

- All patient health information (PHI) is encrypted at rest using PostgreSQL column-level encryption for sensitive fields.
- TLS 1.2+ for all data in transit.
- No PHI in application logs; structured logging with PHI scrubbing.
- Session tokens stored in HttpOnly, Secure, SameSite cookies.

### 7.3 Application Security

- Input validation on all API inputs using Jakarta Bean Validation.
- SQL injection prevention via JPA parameterized queries.
- CSRF protection using Spring Security's CSRF filter.
- Rate limiting on authentication endpoints (5 attempts per minute per IP).
- Security headers (Content-Security-Policy, X-Content-Type-Options, X-Frame-Options).

### 7.4 Audit and Compliance

- Comprehensive audit logging of all data access and modifications.
- Immutable audit records with cryptographic integrity verification.
- Role-based audit log access (only Administrators and authorized reviewers).

### 7.5 Infrastructure Security

- Database accessible only from the application server (network-level restriction).
- Application server behind a reverse proxy (nginx) for TLS termination.
- Regular dependency vulnerability scanning via Maven plugins.

## 8. Deployment Overview

### 8.1 Initial Deployment (Phase 1)

```
┌──────────────────────────────────────────────┐
│              Application Server               │
│  ┌──────────────┐    ┌──────────────────┐   │
│  │  Spring Boot  │    │   PostgreSQL      │   │
│  │  Application  │───▶│   Database        │   │
│  │  (JAR)       │    │   (Local/VM)      │   │
│  └──────────────┘    └──────────────────┘   │
│                                              │
│  ┌──────────────┐                            │
│  │    nginx      │  (TLS termination,        │
│  │  (reverse     │   static file serving)    │
│  │   proxy)      │                            │
│  └──────────────┘                            │
└──────────────────────────────────────────────┘
```

- Spring Boot application deployed as an executable JAR.
- PostgreSQL installed on the same server or a dedicated database server.
- nginx as reverse proxy for TLS termination and static file serving.
- React frontend built and served as static files by nginx or Spring Boot.

### 8.2 Docker Deployment (Phase 2)

```
┌──────────────────────────────────────────────────┐
│                Docker Compose                     │
│  ┌──────────────┐  ┌──────────────┐             │
│  │  app          │  │  postgres    │             │
│  │  (Spring Boot)│  │  (Database)  │             │
│  └──────────────┘  └──────────────┘             │
│  ┌──────────────┐  ┌──────────────┐             │
│  │  nginx        │  │  redis       │             │
│  │  (Reverse     │  │  (Cache -    │             │
│  │   Proxy)      │  │   optional)  │             │
│  └──────────────┘  └──────────────┘             │
└──────────────────────────────────────────────────┘
```

- Docker Compose orchestration for all services.
- Named volumes for database persistence.
- Health checks for service readiness.
- Environment-based configuration via `.env` files.

### 8.3 CI/CD Pipeline

```
Code Push → Build (Maven) → Test (JUnit) → Code Quality (SonarQube)
    → Package (Docker) → Deploy to Staging → Manual Approval → Deploy to Production
```

## 9. Document References

| Document | Reference |
|----------|-----------|
| Project Scope | `docs/planning/01-project-scope.md` |
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Technology Stack | `docs/planning/05-technology-stack.md` |
| Database Plan | `docs/planning/07-database-plan.md` |
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
