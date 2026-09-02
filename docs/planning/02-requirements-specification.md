# 02 — Requirements Specification

## 1. Functional Requirements

### 1.1 Authentication Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-AUTH-01 | The system shall support user registration with username, email, password, and role assignment. | High |
| FR-AUTH-02 | The system shall authenticate users using username/password credentials. | High |
| FR-AUTH-03 | The system shall enforce password complexity rules (minimum 8 characters, at least one uppercase, one lowercase, one digit, one special character). | High |
| FR-AUTH-04 | The system shall lock accounts after 5 consecutive failed login attempts for 15 minutes. | High |
| FR-AUTH-05 | The system shall support JWT-based session management with access tokens (15-minute expiry) and refresh tokens (7-day expiry). | High |
| FR-AUTH-06 | The system shall support password reset via email-based token flow. | Medium |
| FR-AUTH-07 | The system shall enforce password history (last 5 passwords cannot be reused). | High |
| FR-AUTH-08 | The system shall log all authentication events to a dedicated login audit log (login success, login failure, logout, password changes, account lock/unlock). | High |
| FR-AUTH-09 | The system shall support refresh token rotation (new refresh token issued on each use). | High |
| FR-AUTH-10 | The system shall support account unlock by administrator after lockout period. | Medium |

### 1.2 Patient Management Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-PM-01 | The system shall allow registration of new patients with demographics (name, date of birth, gender, phone, address, next-of-kin). | High |
| FR-PM-02 | The system shall assign a unique patient identifier upon registration. | High |
| FR-PM-03 | The system shall record and display the complete patient history within the unit. | High |
| FR-PM-04 | The system shall support patient search by name, ID, phone number, or date of registration. | High |
| FR-PM-05 | The system shall support editing of patient demographic information with audit trail. | Medium |
| FR-PM-06 | The system shall support soft deletion of patient records (deactivation, not permanent deletion). | Medium |

### 1.3 Clinical Assessment Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-CA-01 | The system shall allow clinicians to record clinical assessments with severity level, triage classification, and infection status. | High |
| FR-CA-02 | Each clinical assessment shall be linked to a patient and optionally to an admission. | High |
| FR-CA-03 | The system shall record the assessing clinician and assessment timestamp. | High |
| FR-CA-04 | The system shall support reassessment with an "is reassessment" flag. | High |
| FR-CA-05 | The system shall enforce severity reassessment within 24 hours of admission. | High |
| FR-CA-06 | The system shall treat clinical assessments as append-only (no updates to historical assessments). | High |
| FR-CA-07 | The system shall display the most recent assessment as the patient's current clinical state. | High |
| FR-CA-08 | The system shall maintain a complete assessment timeline per patient and per admission. | High |

### 1.3 Admission Management Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-ADM-01 | The system shall support creation of an admission record linked to a registered patient. | High |
| FR-ADM-02 | The system shall track admission status (pending, admitted, transferred, discharged). | High |
| FR-ADM-03 | The system shall support ward assignment during admission with recommendation from the engine. | High |
| FR-ADM-04 | The system shall support patient transfer between wards with bed release and re-allocation. | High |
| FR-ADM-05 | The system shall support discharge processing with discharge summary fields (outcome, date, notes). | High |
| FR-ADM-06 | The system shall record admission and discharge timestamps. | High |
| FR-ADM-07 | The system shall flag patients exceeding expected length-of-stay thresholds. | Medium |
| FR-ADM-08 | The system shall maintain a complete admission history per patient. | High |

### 1.4 Bed Management Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-BED-01 | The system shall maintain a registry of all beds with attributes (bed number, ward, bed type, isolation capability, status). | High |
| FR-BED-02 | The system shall track bed status in real time (available, occupied, reserved, under maintenance). | High |
| FR-BED-03 | The system shall calculate and display bed occupancy rates per ward and overall. | High |
| FR-BED-04 | The system shall recommend available beds based on patient requirements (type, isolation, ward preference). | High |
| FR-BED-05 | The system shall support bed reservation with timeout (auto-release after configurable period). | Medium |
| FR-BED-06 | The system shall support bed type classification (General, Isolation-Positive-Pressure, Isolation-Negative-Pressure, ICU). | High |
| FR-BED-07 | The system shall notify relevant staff when beds reach critical occupancy (≥ 90%). | Medium |

### 1.5 Ward Management Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-WARD-01 | The system shall maintain a registry of all wards with attributes (name, type, capacity, isolation level, equipment zone). | High |
| FR-WARD-02 | The system shall track ward-level bed counts and occupancy status. | High |
| FR-WARD-03 | The system shall support ward configuration for isolation requirements (contact, droplet, airborne). | High |
| FR-WARD-04 | The system shall display ward status overview on the dashboard. | High |
| FR-WARD-05 | The system shall support adding, editing, and deactivating wards. | Medium |

### 1.6 Resource Management Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-RES-01 | The system shall maintain an inventory of consumable resources (PPE, medications, IV fluids, laboratory supplies, sanitization materials). | High |
| FR-RES-02 | The system shall track stock levels with current quantity, minimum threshold, and reorder point. | High |
| FR-RES-03 | The system shall generate low-stock alerts when inventory falls below minimum threshold. | High |
| FR-RES-04 | The system shall support recording of resource consumption linked to patient admissions. | Medium |
| FR-RES-05 | The system shall recommend resource allocation based on patient severity and admission requirements. | High |
| FR-RES-06 | The system shall maintain a resource allocation history with timestamps and responsible user. | High |
| FR-RES-07 | The system shall support categorization of resources by type, criticality, and expiration tracking. | Medium |

### 1.7 Equipment Management Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-EQ-01 | The system shall maintain a registry of medical equipment with attributes (name, type, location, status, serial number, maintenance schedule). | High |
| FR-EQ-02 | The system shall track equipment status (available, in-use, under-maintenance, out-of-service). | High |
| FR-EQ-03 | The system shall assign equipment to patients or wards with tracking. | High |
| FR-EQ-04 | The system shall generate maintenance alerts based on scheduled maintenance intervals. | Medium |
| FR-EQ-05 | The system shall recommend equipment allocation based on patient severity and required equipment profile. | High |
| FR-EQ-06 | The system shall maintain equipment utilization history. | Medium |

### 1.8 Staff Management Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-STF-01 | The system shall maintain staff profiles with attributes (name, role, specialization, certification, maximum workload threshold). | High |
| FR-STF-02 | The system shall calculate staff workload dynamically using the defined formula (patient count × severity weights × time factors). | High |
| FR-STF-03 | The system shall support staff assignment to wards and patients. | High |
| FR-STF-04 | The system shall recommend staff allocation based on workload balance, specialization, and availability. | High |
| FR-STF-05 | The system shall display staff availability and current workload scores. | Medium |
| FR-STF-06 | The system shall support shift definitions and staff-to-shift assignments. | Medium |
| FR-STF-07 | The system shall prevent staff assignment to overlapping shifts. | Medium |

### 1.9 Bed Cleaning Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-BC-01 | The system shall create a cleaning task when a patient is discharged from a bed. | High |
| FR-BC-02 | The system shall track cleaning task status (Pending, Assigned, In Progress, Completed, Verified). | High |
| FR-BC-03 | The system shall support assignment of cleaning tasks to staff members. | High |
| FR-BC-04 | The system shall require supervisor verification before a bed becomes available. | High |
| FR-BC-05 | The system shall update bed status through the cleaning workflow lifecycle. | High |
| FR-BC-06 | The system shall enforce 2-hour cleaning completion target for isolation beds. | Medium |

### 1.10 Inventory Transaction Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-INV-01 | The system shall record all stock movements as inventory transactions (Purchase, Issue, Return, Adjustment, Transfer, Disposal). | High |
| FR-INV-02 | Inventory transactions shall be append-only (no updates or deletes). | High |
| FR-INV-03 | The system shall calculate current stock as the sum of all transaction quantities. | High |
| FR-INV-04 | The system shall link resource issues to patient admissions for consumption tracking. | Medium |
| FR-INV-05 | The system shall support multiple inventory locations (e.g., Central Store, Ward Store). | Medium |

### 1.11 CDS Engine

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-REC-01 | The system shall generate bed allocation recommendations when a patient is admitted or transferred. | High |
| FR-REC-02 | The system shall generate staff assignment recommendations based on workload and availability. | High |
| FR-REC-03 | The system shall generate equipment allocation recommendations based on patient needs and availability. | High |
| FR-REC-04 | The system shall generate resource (consumable) allocation recommendations based on severity and stock. | High |
| FR-REC-05 | The system shall present recommendations with confidence scores and rationale. | High |
| FR-REC-06 | The system shall allow authorized users to override recommendations with recorded justification. | High |
| FR-REC-07 | The system shall consider the latest ClinicalAssessment (severity, triage, infection status) for the admission in recommendations. | High |
| FR-REC-08 | The system shall provide fallback recommendations when optimal options are unavailable. | Medium |
| FR-REC-09 | The system shall track recommendation acceptance and override rates for engine tuning. | Medium |
| FR-REC-10 | The system shall store scoring breakdown (factor scores and weights) for each recommendation item. | Medium |

### 1.12 Forecasting Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-FCT-01 | The system shall analyze historical admission data to identify trends. | Medium |
| FR-FCT-02 | The system shall generate demand forecasts using Simple Moving Average (7-day horizon) and Weighted Moving Average (14-day, 30-day horizons). | Medium |
| FR-FCT-03 | The system shall display forecast results with visualization (charts, trend lines). | Medium |
| FR-FCT-04 | The system shall flag anticipated shortages based on forecasts. | Medium |
| FR-FCT-05 | The system shall store forecast snapshots for historical accuracy analysis. | Medium |
| FR-FCT-06 | The system shall calculate forecast accuracy using Mean Absolute Percentage Error (MAPE). | Low |

### 1.11 Notification Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-NOT-01 | The system shall send in-app notifications for critical events (new admissions, bed recommendations, low stock). | High |
| FR-NOT-02 | The system shall support email notifications for important alerts. | Medium |
| FR-NOT-03 | The system shall maintain a notification history per user. | Medium |
| FR-NOT-04 | The system shall support configurable notification preferences per user role. | Low |
| FR-NOT-05 | The system shall generate escalation notifications when recommendations are not acted upon within configured timeframes. | Medium |

### 1.12 Reporting Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-RPT-01 | The system shall generate bed occupancy reports (daily, weekly, monthly). | High |
| FR-RPT-02 | The system shall generate resource utilization reports. | High |
| FR-RPT-03 | The system shall generate staff workload reports. | Medium |
| FR-RPT-04 | The system shall generate patient outcome summary reports. | Medium |
| FR-RPT-05 | The system shall generate recommendation performance reports (acceptance rate, override rate). | Medium |
| FR-RPT-06 | The system shall support report export in PDF and CSV formats. | Medium |
| FR-RPT-07 | The system shall generate audit trail reports for compliance review. | High |

### 1.13 Audit Logging Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-AUD-01 | The system shall log all user actions with timestamp, user ID, action type, and affected entity. | High |
| FR-AUD-02 | The system shall log all data changes with before/after values. | High |
| FR-AUD-03 | The system shall log all recommendation engine decisions and overrides. | High |
| FR-AUD-04 | The system shall make audit logs read-only and immutable. | High |
| FR-AUD-05 | The system shall support audit log search and filtering by user, date range, action type, and entity. | Medium |

### 1.14 Administration Module

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-ADMN-01 | The system shall support user account creation, editing, and deactivation by administrators. | High |
| FR-ADMN-02 | The system shall support role management with defined permissions per role. | High |
| FR-ADMN-03 | The system shall support system configuration (session timeout, notification settings, thresholds). | Medium |
| FR-ADMN-04 | The system shall provide a user management dashboard showing active users and roles. | Medium |
| FR-ADMN-05 | The system shall support configuration of recommendation engine parameters and weights. | Medium |

## 2. Non-Functional Requirements

| ID | Category | Requirement | Target |
|----|----------|-------------|--------|
| NFR-01 | Performance | API response time for standard operations | ≤ 2 seconds (95th percentile) |
| NFR-02 | Performance | Dashboard load time | ≤ 3 seconds |
| NFR-03 | Performance | Concurrent user support | ≥ 50 simultaneous users |
| NFR-04 | Availability | System uptime during operational hours | ≥ 99% |
| NFR-05 | Availability | Scheduled maintenance window | Outside 8:00 AM – 8:00 PM |
| NFR-06 | Scalability | Patient records capacity | ≥ 10,000 records with no degradation |
| NFR-07 | Scalability | Audit log retention | ≥ 2 years of operational data |
| NFR-08 | Security | Data encryption in transit | TLS 1.2+ |
| NFR-09 | Security | Password hashing | bcrypt with work factor ≥ 12 |
| NFR-10 | Security | Session management | Server-side sessions with secure cookie flags |
| NFR-11 | Security | API security | CSRF protection, input validation, SQL injection prevention |
| NFR-12 | Usability | Training requirement | ≤ 4 hours for basic proficiency |
| NFR-13 | Usability | Accessibility | WCAG 2.1 AA compliance for core workflows |
| NFR-14 | Maintainability | Code coverage | ≥ 70% for business logic |
| NFR-15 | Maintainability | Documentation | API documentation auto-generated from annotations |
| NFR-16 | Portability | Browser support | Chrome 90+, Firefox 90+, Edge 90+ |
| NFR-17 | Reliability | Data backup frequency | Daily automated backups |
| NFR-18 | Reliability | Recovery time objective (RTO) | ≤ 4 hours |
| NFR-19 | Reliability | Recovery point objective (RPO) | ≤ 1 hour |

## 3. Business Rules

| ID | Rule | Module |
|----|------|--------|
| BR-01 | A patient must be registered before an admission record can be created. | Patient, Admission |
| BR-02 | A bed can be assigned to only one patient at a time. | Bed |
| BR-03 | Patients with infection status "Confirmed" (from latest ClinicalAssessment) must be placed in isolation-designated beds. | Clinical Assessment, Bed, Ward |
| BR-04 | Staff workload score must not exceed the staff member's maximum workload threshold. | Staff |
| BR-05 | Equipment assigned to isolation patients must not be reassigned until decontamination is completed. | Equipment |
| BR-06 | CDS Engine overrides must include a mandatory justification note from the overriding user. | CDS Engine |
| BR-07 | Patients triaged as Emergency (from ClinicalAssessment) must receive a bed recommendation within 5 minutes of admission record creation. | Clinical Assessment, Admission, CDS Engine |
| BR-08 | A ward cannot exceed its defined maximum bed capacity. | Ward, Bed |
| BR-09 | Consumable resources below minimum threshold must trigger a notification to the resource manager. | Resource |
| BR-10 | All audit log entries and login audit log entries are immutable once written. | Audit |
| BR-11 | Only users with the Administrator role can manage user accounts and system configuration. | Administration |
| BR-12 | Clinical assessment severity must be reassessed within 24 hours of admission. | Clinical Assessment |
| BR-13 | Equipment must have completed maintenance before reassignment if maintenance is overdue. | Equipment, Maintenance |
| BR-14 | Staff members with expired certifications cannot be assigned to critical-care wards. | Staff |
| BR-15 | Discharged beds must complete the cleaning workflow (cleaning assigned, completed, and verified) before being reassigned to new patients. | Bed, Cleaning |
| BR-16 | Inventory transactions are append-only; no updates or deletes are permitted. | Inventory |
| BR-17 | Clinical assessments are append-only; historical assessments cannot be modified. | Clinical Assessment |
| BR-18 | New passwords cannot match any of the last 5 passwords in the user's password history. | Authentication |

## 4. Actors

| Actor | Description | Primary Modules |
|-------|-------------|-----------------|
| System Administrator | Manages user accounts, system configuration, roles, and permissions. | Administration, Authentication |
| Ward Manager | Oversees ward operations, reviews CDS recommendations, makes final allocation decisions. | Ward, Bed, CDS Engine, Reporting |
| Nursing Officer | Registers patients, executes daily care assignments, processes admissions. | Patient, Admission, Staff |
| Resource Manager | Manages consumable inventory, processes resource allocations, monitors stock levels. | Resource, Inventory, Reporting |
| Equipment Officer | Manages equipment registry, tracks maintenance, assigns equipment. | Equipment, Maintenance |
| Medical Doctor | Records clinical assessments, reviews patient status, overrides CDS recommendations. | Clinical Assessment, CDS Engine |
| Dashboard Viewer | Views operational dashboards and reports without modification rights. | Dashboard, Reporting |
| CDS Engine (System) | Automatically generates allocation recommendations based on defined rules and data. | CDS Engine, Forecasting |

## 5. User Stories

### 5.1 Authentication

| ID | Story | Acceptance Criteria |
|----|-------|---------------------|
| US-AUTH-01 | As a staff member, I want to log in with my credentials so that I can access the system securely. | User enters valid credentials and is redirected to the dashboard. Invalid credentials show an error message. |
| US-AUTH-02 | As a staff member, I want to be logged out automatically after inactivity so that my session is protected. | Session expires after 30 minutes of inactivity; user is redirected to login with a message. |
| US-AUTH-03 | As an administrator, I want to create user accounts so that new staff can access the system. | Admin fills user form; account is created with assigned role; user receives credentials. |

### 5.2 Patient Management

| ID | Story | Acceptance Criteria |
|----|-------|---------------------|
| US-PM-01 | As a nursing officer, I want to register a new patient quickly so that they can be triaged and admitted. | Patient form captures all required fields; unique ID is assigned; record is saved and searchable. |
| US-PM-02 | As a doctor, I want to record a clinical assessment with severity, triage, and infection status so that the CDS engine can recommend appropriate resources. | Assessment form captures severity, triage, infection status, and clinical notes; linked to patient and admission; saved with timestamp and assessor ID. |
| US-PM-03 | As a doctor, I want to view a patient's complete assessment history so that I can track clinical progression. | Patient profile shows all clinical assessments with timestamps, assessors, and values. |

### 5.3 Bed Management

| ID | Story | Acceptance Criteria |
|----|-------|---------------------|
| US-BED-01 | As a ward manager, I want to see real-time bed occupancy so that I can plan admissions. | Dashboard shows total beds, occupied, available, and cleaning-required per ward with percentage occupancy. |
| US-BED-02 | As a CDS engine, I want to recommend the best available bed for a new admission so that allocation is optimized. | Recommendation considers bed type, isolation needs (from ClinicalAssessment), ward proximity, and current occupancy. |
| US-BED-03 | As a ward manager, I want to override a bed recommendation when I have operational reasons so that the final decision remains with clinical staff. | Override is accepted with mandatory justification; audit log records the override. |

### 5.4 Bed Cleaning Workflow

| ID | Story | Acceptance Criteria |
|----|-------|---------------------|
| US-BC-01 | As a system, I want to automatically create a cleaning task when a patient is discharged so that beds are cleaned before reuse. | Cleaning task created with status "Pending"; bed status changes to "Cleaning Required". |
| US-BC-02 | As a ward manager, I want to assign cleaning tasks to staff so that cleaning is tracked. | Task status changes to "Assigned"; cleaner name and assignment time recorded. |
| US-BC-03 | As a cleaner, I want to mark cleaning tasks as completed so that verification can be initiated. | Task status changes to "Completed"; completion time recorded. |
| US-BC-04 | As a supervisor, I want to verify cleaning completion so that the bed becomes available for new patients. | Task status changes to "Verified"; bed status changes to "Available". |

### 5.5 Resource Management

| ID | Story | Acceptance Criteria |
|----|-------|---------------------|
| US-RES-01 | As a resource manager, I want to be alerted when stock falls below minimum so that replenishment can be arranged. | Alert is generated and notification sent when any resource inventory crosses below minimum threshold. |
| US-RES-02 | As a CDS engine, I want to recommend resource allocation based on patient severity so that critical patients receive priority. | Higher-severity patients (from ClinicalAssessment) receive resource allocation recommendations before lower-severity patients. |
| US-RES-03 | As a resource manager, I want to record all stock movements as transactions so that inventory is accurately tracked. | Every purchase, issue, return, or adjustment creates an inventory transaction record. |

### 5.6 Forecasting

| ID | Story | Acceptance Criteria |
|----|-------|---------------------|
| US-FCT-01 | As a ward manager, I want to see a 7-day demand forecast so that I can prepare staffing and supplies. | Forecast displays predicted admissions, bed demand, and resource requirements for the next 7 days using Simple Moving Average. |
| US-FCT-02 | As a ward manager, I want to see a 30-day forecast so that I can plan for anticipated surges. | Forecast uses Weighted Moving Average; historical visualization shows monthly admission patterns. |
| US-FCT-03 | As a ward manager, I want to see forecast accuracy metrics so that I can trust the predictions. | Dashboard shows MAPE score for each forecast type. |

### 5.7 Reporting

| ID | Story | Acceptance Criteria |
|----|-------|---------------------|
| US-RPT-01 | As an administrator, I want to generate a weekly occupancy report so that I can share it with hospital management. | Report includes bed turnover rate, average length of stay, and bed utilization by ward; exportable as PDF or CSV. |
| US-RPT-02 | As a ward manager, I want to see CDS engine performance metrics so that I can evaluate the engine's effectiveness. | Report shows acceptance rate, override rate, average confidence score, and recommendation response time. |
| US-RPT-03 | As a resource manager, I want to see resource consumption by severity so that I can optimize stock levels. | Report breaks down consumption by patient severity level and resource category. |
| US-RPT-04 | As an equipment officer, I want to see equipment downtime reports so that I can plan maintenance. | Report shows equipment availability percentage, maintenance frequency, and downtime duration. |

## 6. Acceptance Criteria Summary

| Module | Core Acceptance Criteria |
|--------|--------------------------|
| Authentication | Login, logout, JWT token refresh, failed attempt lockout, password reset, password history enforcement. |
| Patient Management | CRUD operations on patient records with audit trail; demographics only, no clinical state. |
| Clinical Assessment | Assessments recorded with severity, triage, infection status; append-only; linked to patient and admission. |
| Admission Management | Full admission lifecycle (create, admit, transfer, discharge) with timestamps and status tracking. |
| Bed Management | Real-time bed status; CDS engine provides valid bed suggestions; override with audit. |
| Bed Cleaning Workflow | Cleaning tasks created on discharge; workflow tracks assignment, completion, verification. |
| Ward Management | Ward configuration CRUD; occupancy calculations accurate; isolation designation enforced. |
| Resource Management | Resource definitions with inventory tracking; threshold alerts; severity-based recommendations. |
| Inventory Transactions | Transactional ledger for all stock movements; append-only; current stock calculated from transactions. |
| Equipment Management | Equipment registry with status tracking; assignment and maintenance scheduling functional. |
| Staff Management | Staff profiles with dynamic workload calculation; assignment based on availability and specialization. |
| Shift Management | Shift definitions, staff assignments, workload tracking. |
| CDS Engine | Generates multi-factor recommendations; displays rationale with scoring breakdown; supports override with justification. |
| Forecasting | Moving Average and Weighted Moving Average forecasts; visualizes historical and predicted data; accuracy tracking. |
| Notification | Delivers in-app and email notifications for defined events; maintains notification history. |
| Reporting | Generates accurate reports with export capability; includes turnover, utilization, and consumption metrics. |
| Audit Logging | Captures all user actions and data changes; logs are immutable and searchable. |
| Administration | User and role management; system configuration; permission enforcement verified. |

## 7. System Constraints

| Constraint | Detail |
|------------|--------|
| Language Requirement | Backend must be implemented in Java using Spring Boot framework. |
| Database | PostgreSQL is the required relational database. |
| Frontend | React with TailwindCSS for the user interface. |
| Deployment Target | Initially server-based deployment; Docker support in later phase. |
| Network | Internal hospital network; no public internet exposure for patient data. |
| Data Volume | System must handle up to 10,000 patient records and 500,000 audit log entries without performance degradation. |

## 8. Hospital Policies

| Policy | Implication for System |
|--------|------------------------|
| Infection Control Protocol | All confirmed Lassa fever patients must be assigned to isolation beds. Equipment used in isolation must be tracked and not shared without decontamination. |
| Patient Confidentiality | Patient data must be accessible only to authorized users based on their role and assignment. |
| Staff Duty of Care | Staff assignments must respect maximum workload thresholds and specialization requirements. |
| Equipment Maintenance | Equipment must undergo scheduled maintenance and cannot be assigned if overdue. |
| Resource Accountability | All resource consumption must be traced to patient admissions or ward activities. |
| Audit and Accountability | All system actions must be attributable to a specific user with timestamp for regulatory compliance. |

## 9. Document References

| Document | Reference |
|----------|-----------|
| Project Scope | `docs/planning/01-project-scope.md` |
| System Architecture | `docs/planning/03-system-architecture.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Domain Model | `docs/planning/06-domain-model.md` |
| Recommendation Engine Design | `docs/planning/08-recommendation-engine-design.md` |
| Open Questions | `docs/planning/12-open-questions.md` |
