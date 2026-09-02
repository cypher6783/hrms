# 16 — Deployment Design

## 1. Environment Strategy

### 1.1 Environment Matrix

| Environment | Purpose | Database | Backend | Frontend |
|------------|---------|----------|---------|----------|
| Development | Local development | Local PostgreSQL | localhost:8080 | localhost:3000 |
| Testing | Automated testing | Testcontainers | In-memory | N/A |
| Staging | Pre-production validation | Dedicated PostgreSQL | staging.bsuth.edu.ng | staging.bsuth.edu.ng |
| Production | Live system | Production PostgreSQL | app.bsuth.edu.ng | app.bsuth.edu.ng |

### 1.2 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_PROFILES_ACTIVE | Active profile | dev |
| DATABASE_URL | PostgreSQL connection URL | jdbc:postgresql://localhost:5432/hospital_resource |
| DATABASE_USERNAME | Database username | postgres |
| DATABASE_PASSWORD | Database password | (required) |
| JWT_SECRET | JWT signing secret | (required) |
| JWT_EXPIRY | Access token expiry (ms) | 900000 |
| REFRESH_TOKEN_EXPIRY | Refresh token expiry (ms) | 604800000 |
| EMAIL_HOST | SMTP host | (optional) |
| EMAIL_PORT | SMTP port | 587 |
| EMAIL_USERNAME | SMTP username | (optional) |
| EMAIL_PASSWORD | SMTP password | (optional) |
| CORS_ALLOWED_ORIGINS | Frontend origins | http://localhost:3000 |
| LOG_LEVEL | Application log level | INFO |

---

## 2. Development Deployment

### 2.1 Local Setup

```
1. Install JDK 21
2. Install PostgreSQL 16
3. Install Node.js 20 LTS
4. Clone repository
5. Run: mvn clean install
6. Run: mvn spring-boot:run
7. Frontend: npm install && npm run dev
```

### 2.2 Docker Compose (Development)

```yaml
services:
  postgres:
    image: postgres:16
    ports: ["5432:5432"]
    environment:
      POSTGRES_DB: hospital_resource
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: devpassword
    volumes: ["pgdata:/var/lib/postgresql/data"]

  app:
    build: .
    ports: ["8080:8080"]
    depends_on: [postgres]
    environment:
      DATABASE_URL: jdbc:postgresql://postgres:5432/hospital_resource
      JWT_SECRET: dev-secret-key

volumes:
  pgdata:
```

---

## 3. Production Deployment

### 3.1 Docker Architecture

```
┌──────────────────────────────────────────────────┐
│                Docker Compose                     │
│  ┌──────────────┐  ┌──────────────┐             │
│  │  nginx        │  │  app          │             │
│  │  (Reverse     │──▶│  (Spring Boot)│             │
│  │   Proxy)      │  │  :8080        │             │
│  └──────────────┘  └──────────────┘             │
│                        │                          │
│  ┌──────────────┐  ┌───▼──────────┐             │
│  │  frontend     │  │  postgres    │             │
│  │  (React)      │  │  (Database)  │             │
│  │  :3000        │  │  :5432       │             │
│  └──────────────┘  └──────────────┘             │
└──────────────────────────────────────────────────┘
```

### 3.2 Dockerfile (Backend)

```
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/hospital-resource.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 3.3 Dockerfile (Frontend)

```
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### 3.4 Nginx Configuration

```
server {
    listen 80;
    server_name app.bsuth.edu.ng;

    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://app:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # TLS configuration (Phase 2)
    # listen 443 ssl;
    # ssl_certificate /etc/ssl/certs/app.crt;
    # ssl_certificate_key /etc/ssl/private/app.key;
}
```

---

## 4. Configuration Profiles

### 4.1 application-dev.yml

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hospital_resource
    username: postgres
    password: devpassword
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: validate
  flyway:
    locations: classpath:db/migration

logging:
  level:
    com.hospital.resource: DEBUG
    org.hibernate.SQL: DEBUG
```

### 4.2 application-staging.yml

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
  flyway:
    locations: classpath:db/migration

logging:
  level:
    com.hospital.resource: INFO
```

### 4.3 application-prod.yml

```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate
  flyway:
    locations: classpath:db/migration

logging:
  level:
    com.hospital.resource: WARN
    org.hibernate.SQL: ERROR
```

---

## 5. Logging Configuration

### 5.1 Log Format

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
```

### 5.2 Log Levels

| Package | Development | Staging | Production |
|---------|-------------|---------|------------|
| com.hospital.resource | DEBUG | INFO | WARN |
| org.hibernate.SQL | DEBUG | INFO | ERROR |
| org.hibernate.type.descriptor.sql.BasicBinder | TRACE | INFO | ERROR |
| org.springframework.security | DEBUG | INFO | WARN |

### 5.3 Log Files

| File | Purpose | Rotation |
|------|---------|----------|
| app.log | Application logs | Daily, 30-day retention |
| audit.log | Audit events | Daily, 2-year retention |
| security.log | Security events | Daily, 90-day retention |
| error.log | Error logs | Daily, 90-day retention |

---

## 6. Monitoring

### 6.1 Health Checks

| Endpoint | Purpose | Interval |
|----------|---------|----------|
| /actuator/health | Application health | 30 seconds |
| /actuator/health/db | Database connectivity | 30 seconds |
| /actuator/info | Application info | On-demand |

### 6.2 Metrics

| Metric | Description | Alert Threshold |
|--------|-------------|-----------------|
| JVM memory usage | Heap and non-heap | > 80% |
| Database connection pool | Active/idle connections | > 80% pool used |
| HTTP request rate | Requests per second | Anomaly detection |
| HTTP error rate | 4xx/5xx responses | > 5% |
| Response time | 95th percentile | > 2 seconds |

### 6.3 Actuator Endpoints

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

---

## 7. Backup Strategy

### 7.1 Database Backups

| Type | Frequency | Retention | Method |
|------|-----------|-----------|--------|
| Full Backup | Daily (2:00 AM) | 30 days | pg_dump |
| WAL Archiving | Continuous | 7 days | pg_basebackup |
| Logical Backup | Weekly | 90 days | pg_dump --format=custom |

### 7.2 Application Backups

| Type | Frequency | Retention | Method |
|------|-----------|-----------|--------|
| Configuration | On change | 30 days | Git |
| Seed Data | On change | Indefinite | Git |
| User Uploads | Daily | 30 days | File copy |

### 7.3 Backup Verification

- Weekly restore test to verify backup integrity.
- Monthly full disaster recovery drill.

---

## 8. Disaster Recovery

### 8.1 RTO/RPO Targets

| Metric | Target |
|--------|--------|
| Recovery Time Objective (RTO) | ≤ 4 hours |
| Recovery Point Objective (RPO) | ≤ 1 hour |

### 8.2 Recovery Procedure

```
1. Assess failure scope (application, database, infrastructure)
2. If database failure:
   a. Restore from latest backup
   b. Apply WAL archiving for point-in-time recovery
   c. Verify data integrity
3. If application failure:
   a. Restart application containers
   b. Verify health checks pass
4. If infrastructure failure:
   a. Provision new infrastructure
   b. Deploy application
   c. Restore database
5. Verify system functionality
6. Notify stakeholders
```

---

## 9. CI/CD Pipeline

### 9.1 Pipeline Stages

```
Code Push → Build (Maven) → Unit Tests → Integration Tests
    → Code Quality (SonarQube) → Package (Docker)
    → Deploy to Staging → Smoke Tests
    → Manual Approval → Deploy to Production
    → Health Check → Notify
```

### 9.2 Quality Gates

| Gate | Requirement |
|------|-------------|
| Build | Must compile without errors |
| Unit Tests | ≥ 75% coverage |
| Integration Tests | All pass |
| SonarQube | No critical/blocker issues |
| Security Scan | No high/critical vulnerabilities |

---

## 10. Document References

| Document | Reference |
|----------|-----------|
| Technology Stack | `docs/planning/05-technology-stack.md` |
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
| System Architecture | `docs/planning/03-system-architecture.md` |
