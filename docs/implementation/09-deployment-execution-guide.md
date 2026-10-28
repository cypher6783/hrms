# 09 — Deployment Execution Guide

## 1. Local Development Deployment

### 1.1 Prerequisites

| Tool | Version | Purpose |
|------|---------|---------|
| JDK | 21 LTS | Java runtime |
| Maven | 3.9.x | Build tool |
| PostgreSQL | 16.x | Database |
| Node.js | 20 LTS | Frontend build |
| Git | 2.x | Version control |

### 1.2 Setup Steps

```bash
# 1. Clone repository
git clone https://github.com/hospital-resource/management.git
cd management

# 2. Create database
psql -U postgres -c "CREATE DATABASE hospital_resource;"

# 3. Build backend
mvn clean install

# 4. Run backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 5. Install frontend dependencies
cd frontend
npm install

# 6. Run frontend
npm run dev
```

### 1.3 Environment Variables (Development)

```bash
SPRING_PROFILES_ACTIVE=dev
DATABASE_URL=jdbc:postgresql://localhost:5432/hospital_resource
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=devpassword
JWT_SECRET=dev-secret-key-change-in-production
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### 1.4 Verification

| Check | Expected |
|-------|----------|
| Backend starts on port 8080 | http://localhost:8080 |
| Frontend starts on port 3000 | http://localhost:3000 |
| Database migrations run | All V001–V030 applied |
| Seed data loaded | Admin user, wards, resources |
| Login works | Admin/admin123 |

---

## 2. Docker Compose Deployment (Development)

### 2.1 docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: hospital_resource
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: devpassword
    volumes:
      - pgdata:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: docker
      DATABASE_URL: jdbc:postgresql://postgres:5432/hospital_resource
      DATABASE_USERNAME: postgres
      DATABASE_PASSWORD: devpassword
      JWT_SECRET: docker-secret-key

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "3000:80"
    depends_on:
      - app

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    depends_on:
      - app
      - frontend
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf

volumes:
  pgdata:
```

### 2.2 Deployment Steps

```bash
# 1. Build and start all services
docker-compose up -d --build

# 2. Check service status
docker-compose ps

# 3. View logs
docker-compose logs -f app

# 4. Access application
# Frontend: http://localhost
# API: http://localhost/api/v1
```

### 2.3 Verification

| Check | Command | Expected |
|-------|---------|----------|
| All containers running | docker-compose ps | All "Up" |
| Database accessible | docker-compose exec postgres psql -U postgres -d hospital_resource -c "\dt" | Tables listed |
| Backend healthy | curl http://localhost:8080/actuator/health | status: UP |
| Frontend accessible | curl http://localhost | HTML response |

---

## 3. Staging Deployment

### 3.1 Staging Environment

| Component | Specification |
|-----------|---------------|
| Server | Linux VM (Ubuntu 22.04) |
| CPU | 4 cores |
| RAM | 8 GB |
| Storage | 100 GB SSD |
| Database | PostgreSQL 16 (dedicated) |
| SSL | Let's Encrypt certificate |

### 3.2 Deployment Steps

```bash
# 1. Pull latest code
git pull origin develop

# 2. Build production JAR
mvn clean package -DskipTests

# 3. Run database migrations
flyway migrate -url=jdbc:postgresql://db-host:5432/hospital_resource_staging

# 4. Deploy application
sudo systemctl stop hospital-resource
cp target/hospital-resource.jar /opt/hospital-resource/
sudo systemctl start hospital-resource

# 5. Deploy frontend
cd frontend
npm run build
cp -r dist/* /var/www/html/

# 6. Reload nginx
sudo nginx -t && sudo systemctl reload nginx
```

### 3.3 Environment Variables (Staging)

```bash
SPRING_PROFILES_ACTIVE=staging
DATABASE_URL=jdbc:postgresql://db-host:5432/hospital_resource_staging
DATABASE_USERNAME=hospital_app
DATABASE_PASSWORD=<secure-password>
JWT_SECRET=<secure-secret>
CORS_ALLOWED_ORIGINS=https://staging.bsuth.edu.ng
```

### 3.4 Verification

| Check | Method | Expected |
|-------|--------|----------|
| Application health | GET /actuator/health | status: UP |
| Database connectivity | GET /actuator/health/db | status: UP |
| Login works | POST /api/v1/auth/login | Tokens returned |
| API responses | POST /api/v1/patients | 201 Created |
| Frontend loads | Browser navigation | Dashboard visible |
| SSL certificate | Browser check | Valid certificate |

---

## 4. Production Deployment

### 4.1 Production Environment

| Component | Specification |
|-----------|---------------|
| Server | Linux VM (Ubuntu 22.04) |
| CPU | 8 cores |
| RAM | 16 GB |
| Storage | 500 GB SSD |
| Database | PostgreSQL 16 (dedicated server) |
| SSL | Let's Encrypt certificate |
| Backup | Daily automated backups |
| Monitoring | Actuator + Prometheus + Grafana |

### 4.2 Deployment Steps

```bash
# 1. Create release branch
git checkout -b release/v1.0.0 develop

# 2. Update version
mvn versions:set -DnewVersion=1.0.0

# 3. Build production JAR
mvn clean package -DskipTests

# 4. Run full test suite
mvn verify

# 5. Tag release
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0

# 6. Deploy to production
ssh production-server
cd /opt/hospital-resource

# 7. Backup current version
cp hospital-resource.jar hospital-resource.jar.bak

# 8. Deploy new version
sudo systemctl stop hospital-resource
cp /path/to/hospital-resource.jar .
sudo systemctl start hospital-resource

# 9. Verify
curl http://localhost:8080/actuator/health
```

### 4.3 Environment Variables (Production)

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://prod-db-host:5432/hospital_resource
DATABASE_USERNAME=hospital_app
DATABASE_PASSWORD=<production-password>
JWT_SECRET=<production-secret>
CORS_ALLOWED_ORIGINS=https://app.bsuth.edu.ng
LOG_LEVEL=WARN
```

### 4.4 Post-Deployment Verification

| Check | Method | Expected | Critical |
|-------|--------|----------|----------|
| Application health | GET /actuator/health | status: UP | Yes |
| Database connectivity | GET /actuator/health/db | status: UP | Yes |
| Authentication | POST /api/v1/auth/login | 200 OK | Yes |
| Patient creation | POST /api/v1/patients | 201 Created | Yes |
| Admission workflow | Full workflow test | Success | Yes |
| Recommendation generation | POST /api/v1/recommendations/generate | 201 Created | Yes |
| Report generation | GET /api/v1/reports/occupancy | 200 OK | No |
| Frontend | Browser navigation | Dashboard loads | Yes |
| SSL certificate | Browser check | Valid | Yes |
| Backup verification | Check backup timestamp | Recent | Yes |

---

## 5. Rollback Procedure

### 5.1 Application Rollback

```bash
# 1. Stop current application
sudo systemctl stop hospital-resource

# 2. Restore previous version
cp /opt/hospital-resource/hospital-resource.jar.bak /opt/hospital-resource/hospital-resource.jar

# 3. Start previous version
sudo systemctl start hospital-resource

# 4. Verify
curl http://localhost:8080/actuator/health
```

### 5.2 Database Rollback

```bash
# 1. Stop application
sudo systemctl stop hospital-resource

# 2. Restore database from backup
pg_restore -d hospital_resource /path/to/backup.dump

# 3. Start application
sudo systemctl start hospital-resource

# 4. Verify
curl http://localhost:8080/actuator/health
```

### 5.3 Rollback Triggers

| Condition | Action |
|-----------|--------|
| Application fails to start | Rollback immediately |
| Health check fails for > 5 minutes | Rollback immediately |
| Critical functionality broken | Rollback within 15 minutes |
| Non-critical issue | Hotfix instead of rollback |

---

## 6. Release Validation

### 6.1 Pre-Release Checklist

- [ ] All unit tests pass
- [ ] All integration tests pass
- [ ] All API tests pass
- [ ] All security tests pass
- [ ] Code coverage ≥ 75%
- [ ] No critical bugs open
- [ ] Database migrations tested
- [ ] Seed data verified
- [ ] Performance tests pass
- [ ] Security scan clean
- [ ] Documentation updated
- [ ] Release notes prepared

### 6.2 Post-Release Checklist

- [ ] Application health check passes
- [ ] Authentication works
- [ ] Core workflows functional
- [ ] Frontend loads correctly
- [ ] SSL certificate valid
- [ ] Backup verified
- [ ] Monitoring active
- [ ] Rollback tested
- [ ] Stakeholders notified

---

## 7. Document References

| Document | Reference |
|----------|-----------|
| Deployment Design | `docs/design/16-deployment-design.md` |
| Technology Stack | `docs/planning/05-technology-stack.md` |
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
