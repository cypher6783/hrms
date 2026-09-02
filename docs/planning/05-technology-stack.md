# 05 — Technology Stack

## 1. Technology Selection Summary

| Layer | Technology | Version |
|-------|-----------|---------|
| Backend Language | Java | 21 LTS |
| Backend Framework | Spring Boot | 3.2.x |
| Security | Spring Security | 6.2.x |
| ORM | Hibernate (via Spring Data JPA) | 6.4.x |
| Database | PostgreSQL | 16.x |
| Build Tool | Maven | 3.9.x |
| Frontend Framework | React | 18.x |
| CSS Framework | TailwindCSS | 3.4.x |
| Frontend Build | Vite | 5.x |
| API Documentation | SpringDoc OpenAPI | 2.3.x |
| Testing | JUnit 5, Mockito, Testcontainers | Latest |
| Code Quality | SonarQube (via Maven plugin) | Latest |
| Version Control | Git | 2.x |
| Containerization | Docker | 24.x (later phase) |
| Orchestration | Docker Compose | 2.x (later phase) |

---

## 2. Backend

### 2.1 Java 21

**Reasons**:
- Long-Term Support (LTS) release with guaranteed updates.
- Virtual threads improve concurrency handling for I/O-bound operations.
- Pattern matching and record classes improve code expressiveness.
- Sealed classes enable better domain modeling.
- Project-specific requirement to use Java as the implementation language.

**Alternatives Considered**:
- Java 17: LTS release, but lacks virtual threads and recent language improvements.
- Java 11: Outdated LTS; missing modern language features and performance improvements.

**Advantages**:
- Mature ecosystem with extensive library support.
- Strong typing and compile-time error detection.
- Excellent tooling (IDE support, debugging, profiling).
- Wide industry adoption in healthcare enterprise systems.

**Disadvantages**:
- More verbose than some modern languages.
- Slower development iteration compared to dynamically typed languages.
- Requires JVM, which increases deployment artifact size.

### 2.2 Spring Boot 3.2.x

**Reasons**:
- Industry-standard framework for Java enterprise applications.
- Convention-over-configuration reduces boilerplate.
- Embedded server support (Tomcat) simplifies deployment.
- Extensive auto-configuration for database, security, and monitoring.
- Active development with regular releases and security patches.

**Alternatives Considered**:
- Jakarta EE (Payara, WildFly): More complex configuration; less developer-friendly for rapid development.
- Micronaut: Lighter weight, but smaller ecosystem and community.
- Quarkus: Optimized for GraalVM/native images, but adds deployment complexity.

**Advantages**:
- Rapid application startup with Spring Boot 3.x optimizations.
- Native image support via GraalVM for future optimization.
- Comprehensive actuator endpoints for health checks and monitoring.
- Spring Boot DevTools for developer productivity.

**Disadvantages**:
- Large framework footprint; startup time can be slower than lightweight alternatives.
- Auto-configuration can be opaque when debugging configuration issues.
- Frequent version updates may require dependency management attention.

### 2.3 Spring Security 6.2.x

**Reasons**:
- De facto standard for Java application security.
- Comprehensive support for JWT, OAuth2, and session-based authentication.
- Role-based access control with method-level security.
- Integration with Spring Boot auto-configuration.
- Extensible filter chain for custom security logic.

**Alternatives Considered**:
- Apache Shiro: Simpler but less feature-rich for enterprise requirements.
- JAAS: Low-level; requires significant custom implementation.
- Custom security implementation: High maintenance burden and security risk.

**Advantages**:
- Battle-tested in production healthcare systems.
- Regular security updates and vulnerability patches.
- Extensive documentation and community support.
- Method-level security annotations (`@PreAuthorize`, `@Secured`).

**Disadvantages**:
- Steep learning curve for complex configurations.
- Configuration can become verbose for custom requirements.
- Overhead for simple applications.

### 2.4 Spring Data JPA + Hibernate 6.4.x

**Reasons**:
- Repository pattern simplifies data access layer.
- Auto-generated queries from method names reduce boilerplate.
- Hibernate provides full JPA 3.1 compliance with advanced features.
- First-level cache and dirty checking reduce database round trips.
- Integration with Spring transaction management.

**Alternatives Considered**:
- JDBC Template: More control but more boilerplate; no object-relational mapping.
- jOOQ: Type-safe SQL, but requires code generation and adds learning curve.
- MyBatis: SQL-centric approach; less suitable for complex domain models.

**Advantages**:
- Database-agnostic repository interfaces (easier PostgreSQL-specific optimization).
- Built-in pagination and sorting support.
- Audit field auto-population via `@CreatedDate`, `@LastModifiedDate`.
- Hibernate schema validation ensures JPA entities match database schema.

**Disadvantages**:
- N+1 query problem requires careful query design.
- Lazy loading can cause unexpected database queries.
- Complex queries may require native SQL, bypassing JPA abstraction.

---

## 3. Database

### 3.1 PostgreSQL 16.x

**Reasons**:
- ACID compliance essential for healthcare data integrity.
- Native JSON/JSONB support for flexible data storage.
- Full-text search for patient lookup and audit filtering.
- Advanced indexing (B-tree, GIN, GiST) for query optimization.
- Mature replication and backup capabilities.
- Open-source with no licensing costs.

**Alternatives Considered**:
- MySQL: Widely used but lacks advanced JSON support and某些 PostgreSQL-specific features.
- Oracle: Enterprise-grade but prohibitive licensing costs.
- Microsoft SQL Server: Strong ecosystem but Windows-centric licensing.
- H2 (embedded): Suitable for testing only; not production-grade.

**Advantages**:
- Extensible with custom types, functions, and operators.
- Strong community and comprehensive documentation.
- Horizontal scalability via read replicas (future phase).
- pg_dump and pg_basebackup for reliable backup and restore.

**Disadvantages**:
- Configuration tuning required for optimal performance.
- Memory management requires careful resource allocation.
- Less GUI tooling compared to MySQL or SQL Server.

---

## 4. Frontend

### 4.1 React 18.x

**Reasons**:
- Component-based architecture for reusable UI elements.
- Virtual DOM for efficient rendering.
- Large ecosystem of libraries and tools.
- Strong community support and extensive documentation.
- Server-side rendering capability for future enhancement.

**Alternatives Considered**:
- Angular: More opinionated framework; steeper learning curve for the team.
- Vue.js: Simpler learning curve but smaller enterprise adoption.
- Svelte: Compiler-based approach; smaller ecosystem.

**Advantages**:
- Declarative UI with JSX for expressive component definition.
- Hooks for state management and side effects.
- React DevTools for debugging and profiling.
- Concurrent rendering for improved responsiveness.

**Disadvantages**:
- Requires additional libraries for routing, state management, and form handling.
- JSX can be initially unfamiliar for developers used to template-based frameworks.
- Frequent ecosystem changes require ongoing learning.

### 4.2 TailwindCSS 3.4.x

**Reasons**:
- Utility-first approach enables rapid UI development.
- Consistent design system without custom CSS overhead.
- Responsive design utilities for dashboard layouts.
- Small production bundle size with purging of unused classes.
- Customizable theme system for hospital branding.

**Alternatives Considered**:
- Bootstrap: Component-based but opinionated design; larger bundle.
- Material-UI: React component library; adds dependency and design constraints.
- Custom CSS: High maintenance burden; inconsistent styling.

**Advantages**:
- No context switching between CSS files and components.
- Built-in responsive design breakpoints.
- Dark mode support for extended use scenarios.
- JIT (Just-In-Time) mode for on-demand CSS generation.

**Disadvantages**:
- HTML can become verbose with utility classes.
- Learning curve for developers accustomed to traditional CSS.
- Design consistency requires discipline without component abstraction.

### 4.3 Vite 5.x

**Reasons**:
- Fast development server with hot module replacement.
- Optimized production builds with Rollup.
- Native ES module support for modern browsers.
- TypeScript support out of the box.

**Alternantages Considered**:
- Webpack: More established but significantly slower development builds.
- Create React App: Deprecated; built on Webpack.

**Advantages**:
- Near-instantaneous development server startup.
- Efficient production builds with tree shaking.
- Plugin ecosystem for customization.

**Disadvantages**:
- Newer tool with smaller ecosystem compared to Webpack.
- Some legacy browser compatibility may require additional configuration.

---

## 5. Build Tool

### 5.1 Maven 3.9.x

**Reasons**:
- Standard build tool for Java projects.
- Convention-over-configuration reduces setup time.
- Comprehensive dependency management with conflict resolution.
- Extensive plugin ecosystem for testing, code quality, and deployment.
- Integration with CI/CD pipelines.

**Alternatives Considered**:
- Gradle: More flexible build scripting but steeper learning curve.
- Ant: Low-level; requires manual dependency management.

**Advantages**:
- Standardized project structure (src/main/java, src/test/java).
- Maven Central repository for dependency resolution.
- Maven Wrapper ensures consistent build environment.
- Support for multi-module projects.

**Disadvantages**:
- XML-based configuration can be verbose.
- Slower than Gradle for incremental builds.
- Plugin configuration can become complex.

---

## 6. Development Environment

### 6.1 Required Tools

| Tool | Purpose | Version |
|------|---------|---------|
| JDK | Java development kit | 21 LTS |
| Maven | Build tool | 3.9.x |
| IntelliJ IDEA / VS Code | IDE | Latest stable |
| Git | Version control | 2.x |
| Docker | Containerization (later phase) | 24.x |
| PostgreSQL | Database (local development) | 16.x |
| Node.js | Frontend build | 20 LTS |
| npm / pnpm | Frontend package manager | Latest |

### 6.2 Recommended IDE Configuration

- Spring Boot插件 (IntelliJ) or Spring Boot Extension Pack (VS Code).
- Java Extension Pack for code navigation and debugging.
- TailwindCSS IntelliSense for class autocompletion.
- SonarLint for real-time code quality feedback.

### 6.3 Database Setup

- Local PostgreSQL instance for development.
- Docker Compose for standardized development database.
- Flyway for schema migration management.
- Testcontainers for integration test database provisioning.

---

## 7. Testing Stack

| Tool | Purpose |
|------|---------|
| JUnit 5 | Unit and integration test framework |
| Mockito | Mocking framework for service layer tests |
| Testcontainers | PostgreSQL instance for integration tests |
| Spring Boot Test | Integration test support with application context |
| AssertJ | Fluent assertion library |
| JaCoCo | Code coverage reporting |
| SonarQube | Code quality and security analysis |
| Playwright (later) | End-to-end browser testing |

---

## 8. Document References

| Document | Reference |
|----------|-----------|
| System Architecture | `docs/planning/03-system-architecture.md` |
| Database Plan | `docs/planning/07-database-plan.md` |
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
