# 04 — Module Breakdown

## 1. Authentication Module

### 1.1 Responsibilities

- User login and logout processing.
- JWT access token generation and validation.
- Refresh token generation, rotation, and revocation.
- Password hashing and verification.
- Account lockout after failed attempts.
- Password reset workflow.
- Password history enforcement (prevent reuse of last 5 passwords).
- Login history recording for security monitoring.

### 1.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Administration Module | User accounts and roles are managed by administrators. |
| Login Audit Log Module | All authentication events are logged. |
| Notification Module | Password reset emails and security alerts. |

### 1.3 Inputs

| Input | Source |
|-------|--------|
| Login credentials (username, password) | User via login form |
| Refresh token | Client application |
| Password reset request | User via reset form |
| New password (after reset) | User via reset form |
| User account data | Administrator via user management |

### 1.4 Outputs

| Output | Destination |
|--------|-------------|
| JWT access token (15-minute expiry) | Client application |
| Refresh token (7-day expiry) | Client application (HttpOnly cookie) |
| Authentication status (success/failure) | Client application |
| Login audit record | Login Audit Log |
| Password reset token | Email service |

### 1.5 Interactions

- Validates user credentials against stored hashed passwords.
- Generates JWT tokens containing user ID, roles, and expiry.
- Issues refresh tokens for seamless session renewal.
- Rotates refresh tokens on each use (old token revoked).
- Enforces account lockout policy after 5 consecutive failures.
- Checks password history before allowing password changes.
- Delegates user management operations to the Administration Module.
- Logs all authentication events to the Login Audit Log.

### 1.6 Future Expansion

- Multi-factor authentication (TOTP) integration.
- OAuth2 integration for single sign-on (SSO) with hospital-wide identity provider.
- LDAP/Active Directory integration if hospital adopts centralized directory services.

---

## 2. Patient Management Module

### 2.1 Responsibilities

- Patient registration with demographic data capture.
- Unique patient identifier generation.
- Patient record retrieval and search.
- Patient history tracking across admissions.
- Patient record soft deletion (deactivation).

### 2.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Clinical Assessment Module | Patient clinical state is accessed through assessments. |
| Admission Management Module | Patient records are linked to admission records. |
| Audit Logging Module | All patient data changes are logged. |

### 2.3 Inputs

| Input | Source |
|-------|--------|
| Patient demographics (name, DOB, gender, contact, next-of-kin) | Nursing Officer |
| Patient search criteria | Any authorized user |

### 2.4 Outputs

| Output | Destination |
|--------|-------------|
| Registered patient record | System storage |
| Patient profile with full history | User interface |
| Patient search results | User interface |

### 2.5 Interactions

- Receives patient registration data from Nursing Officers.
- Provides patient data to the Admission Module for admission processing.
- Maintains patient history across multiple admissions.
- Does NOT store clinical state (severity, triage, infection status) — these reside in ClinicalAssessment.

### 2.6 Future Expansion

- Integration with external patient identification systems (e.g., national health ID).
- Support for patient photo capture and storage.
- Patient outcome tracking beyond discharge (follow-up, readmission correlation).

---

## 3. Clinical Assessment Module

### 3.1 Responsibilities

- Recording encounter-specific clinical assessments (severity, triage, infection status).
- Linking assessments to patients and optionally to admissions.
- Tracking reassessments with an "is reassessment" flag.
- Enforcing reassessment schedules (within 24 hours of admission).
- Maintaining assessment history (append-only).
- Providing the most recent assessment as the patient's current clinical state.

### 3.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Patient Management Module | Assessments are linked to patient records. |
| Admission Management Module | Assessments are linked to admissions. |
| CDS Engine | Clinical assessment data feeds into recommendations. |
| Audit Logging Module | All assessment events are logged. |

### 3.3 Inputs

| Input | Source |
|-------|--------|
| Severity level (Mild, Moderate, Severe, Critical) | Medical Doctor |
| Triage classification (Emergency, Urgent, Semi-Urgent, Non-Urgent) | Medical Doctor / Nursing Officer |
| Infection status (Suspected, Confirmed, Ruled-Out) | Medical Doctor |
| Clinical notes | Medical Doctor |
| Patient ID | System (from patient record) |
| Admission ID | System (from admission record, if applicable) |

### 3.4 Outputs

| Output | Destination |
|--------|-------------|
| Clinical assessment record | System storage |
| Most recent assessment per admission | CDS Engine, User Interface |
| Assessment timeline | User Interface |
| Reassessment due alerts | Notification Module |

### 3.5 Interactions

- Records clinical assessments with timestamps and assessor identity.
- Provides the latest clinical state to the CDS Engine for recommendation generation.
- Triggers reassessment reminders when 24-hour threshold is approached.
- Maintains append-only assessment history for clinical audit.

### 3.6 Future Expansion

- Integration with laboratory systems for automated infection status updates.
- Clinical decision support for severity scoring (e.g., WHO Lassa fever severity criteria).
- Assessment templates for standardized clinical documentation.

---

## 4. Admission Management Module

### 4.1 Responsibilities

- Admission record creation linked to patient records.
- Admission status tracking (pending, admitted, transferred, discharged).
- Ward assignment processing with CDS Engine integration.
- Patient transfer between wards.
- Discharge processing with outcome recording.
- Discharge triggers bed cleaning workflow.
- Admission and discharge timestamp management.
- Length-of-stay monitoring and flagging.
- Complete admission history maintenance per patient.

### 4.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Patient Management Module | Admissions are linked to patient records. |
| Clinical Assessment Module | Assessments are linked to admissions. |
| Bed Management Module | Bed assignment and release during admission lifecycle. |
| Bed Cleaning Module | Discharge triggers cleaning workflow. |
| Ward Management Module | Ward assignment and capacity checks. |
| CDS Engine | Generates bed, staff, equipment, and resource recommendations. |
| Staff Management Module | Staff assignments during admission. |
| Audit Logging Module | All admission events are logged. |
| Notification Module | Admission and discharge notifications. |

### 4.3 Inputs

| Input | Source |
|-------|--------|
| Admission request (patient ID, admission notes) | Nursing Officer |
| Transfer request (target ward, reason) | Ward Manager |
| Discharge record (outcome, notes, date) | Medical Doctor |
| Recommendation from CDS Engine | CDS Engine |

### 4.4 Outputs

| Output | Destination |
|--------|-------------|
| Admission record with status | System storage |
| Bed assignment request | Bed Management Module |
| Cleaning task creation | Bed Cleaning Module |
| Staff assignment request | Staff Management Module |
| Admission/discharge notifications | Notification Module |
| Admission history | User interface |

### 4.5 Interactions

- Creates admission records and triggers the CDS Engine workflow.
- Coordinates with Bed Management for bed assignment and release.
- Handles patient transfers by releasing current bed and requesting new allocation.
- Processes discharges and triggers bed cleaning workflow via Bed Cleaning Module.
- Provides admission data for reporting and forecasting.

### 4.6 Future Expansion

- Pre-admission screening and reservation workflow.
- Inter-hospital transfer coordination.
- Integration with emergency department triage systems.

---

## 5. Bed Management Module

### 5.1 Responsibilities

- Bed registry maintenance (bed number, ward, type, isolation capability, status).
- Real-time bed status tracking (available, occupied, reserved, maintenance, cleaning required).
- Bed occupancy rate calculation per ward and overall.
- Bed recommendation for incoming patients.
- Bed reservation with timeout management.
- Bed type classification and filtering.
- Critical occupancy alerting.

### 5.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Ward Management Module | Beds are organized within wards. |
| CDS Engine | Provides recommendation parameters for bed allocation. |
| Bed Cleaning Module | Manages cleaning workflow for bed status transitions. |
| Admission Management Module | Bed assignment and release events. |
| Audit Logging Module | All bed status changes are logged. |
| Notification Module | Occupancy alerts and reservation timeouts. |

### 5.3 Inputs

| Input | Source |
|-------|--------|
| Bed configuration data | Administrator |
| Bed status change requests | Ward Manager / System / Cleaning Workflow |
| Patient requirements (type, isolation) | CDS Engine |
| Occupancy threshold settings | Administrator |

### 5.4 Outputs

| Output | Destination |
|--------|-------------|
| Bed status information | Dashboard, CDS Engine |
| Bed occupancy reports | Reporting Module |
| Available bed recommendations | Admission Module |
| Occupancy alerts | Notification Module |

### 5.5 Interactions

- Maintains the authoritative bed inventory for the Lassa Fever Unit.
- Provides real-time bed availability data to the CDS Engine.
- Processes bed assignment requests from the Admission Module.
- Triggers occupancy alerts when thresholds are breached.
- Coordinates with Ward Management for capacity enforcement.

### 5.6 Future Expansion

- Bed sensor integration for automated occupancy detection.
- Dynamic bed configuration based on outbreak severity.
- Multi-unit bed management across hospital departments.

---

## 6. Bed Cleaning Module

### 6.1 Responsibilities

- Creating cleaning tasks when patients are discharged.
- Tracking cleaning workflow status (Pending, Assigned, In Progress, Completed, Verified).
- Assigning cleaning tasks to staff members.
- Requiring supervisor verification before bed becomes available.
- Managing bed status transitions through the cleaning lifecycle.
- Enforcing cleaning completion targets (2 hours for isolation beds).

### 6.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Bed Management Module | Bed status is updated through cleaning workflow. |
| Admission Management Module | Discharge triggers cleaning task creation. |
| Staff Management Module | Cleaners are assigned from staff pool. |
| Notification Module | Cleaning assignment and completion notifications. |
| Audit Logging Module | All cleaning events are logged. |

### 6.3 Inputs

| Input | Source |
|-------|--------|
| Discharge event | Admission Management Module |
| Cleaner assignment | Ward Manager |
| Cleaning completion | Cleaner staff |
| Verification approval | Supervisor |

### 6.4 Outputs

| Output | Destination |
|--------|-------------|
| Cleaning task record | System storage |
| Bed status updates | Bed Management Module |
| Cleaning assignment notifications | Notification Module |
| Cleaning metrics | Reporting Module |

### 6.5 Interactions

- Receives discharge events from Admission Module and creates cleaning tasks.
- Assigns cleaners and tracks cleaning progress.
- Coordinates with Bed Management for status transitions.
- Provides cleaning metrics for operational reporting.

### 6.6 Future Expansion

- Automated cleaning task assignment based on staff availability.
- Cleaning quality checklists and photo documentation.
- Integration with hospital cleaning management systems.

---

## 7. Ward Management Module

### 7.1 Responsibilities

- Ward registry maintenance (name, type, capacity, isolation level, equipment zone).
- Ward-level capacity and occupancy tracking.
- Isolation requirement configuration (contact, droplet, airborne).
- Ward status overview for dashboard display.
- Ward configuration management (add, edit, deactivate).

### 7.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Bed Management Module | Beds are organized within wards; ward capacity is the sum of bed counts. |
| CDS Engine | Ward configuration informs isolation and capacity recommendations. |
| Staff Management Module | Staff assignments are ward-scoped. |
| Audit Logging Module | Ward configuration changes are logged. |

### 7.3 Inputs

| Input | Source |
|-------|--------|
| Ward configuration data | Administrator |
| Ward capacity settings | Administrator |
| Isolation level designation | Administrator / Infection Control Policy |

### 7.4 Outputs

| Output | Destination |
|--------|-------------|
| Ward status overview | Dashboard |
| Ward configuration data | Bed Management, CDS Engine |
| Ward capacity data | Reporting Module |

### 7.5 Interactions

- Defines the structural units within which beds and staff are organized.
- Provides isolation and capacity configuration to the CDS Engine.
- Supplies ward status data for the real-time dashboard.
- Coordinates with Staff Management for ward-level staffing.

### 7.6 Future Expansion

- Dynamic ward reconfiguration during outbreak surges.
- Ward-specific protocol enforcement (e.g., different PPE requirements per ward).
- Integration with hospital facility management systems.

---

## 8. Resource Management Module

### 8.1 Responsibilities

- Consumable resource definition and metadata management.
- Resource categorization by type, criticality, and supplier.
- Low-stock threshold and reorder point configuration.
- Resource allocation recommendations based on patient severity.

### 8.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Clinical Assessment Module | Resource allocation is driven by patient severity from assessments. |
| Admission Management Module | Resource consumption is linked to admissions. |
| Inventory Transaction Module | Stock levels are calculated from transactions. |
| CDS Engine | Provides resource allocation recommendations. |
| Notification Module | Low-stock alerts and allocation notifications. |

### 8.3 Inputs

| Input | Source |
|-------|--------|
| Resource definition data | Resource Manager |
| Threshold settings | Administrator |
| Patient severity data | Clinical Assessment Module |

### 8.4 Outputs

| Output | Destination |
|--------|-------------|
| Resource definitions | Inventory Transaction Module, CDS Engine |
| Low-stock alerts | Notification Module |
| Resource allocation recommendations | Admission Module, Ward Manager |

### 8.5 Interactions

- Defines resource types and metadata.
- Provides resource data to the CDS Engine for allocation decisions.
- Coordinates with Inventory Transaction Module for stock level queries.

### 8.6 Future Expansion

- Automated reorder triggering with supplier integration.
- Budget tracking and cost-per-patient analysis.

---

## 9. Inventory Transaction Module

### 9.1 Responsibilities

- Recording all stock movements as inventory transactions (Purchase, Issue, Return, Adjustment, Transfer, Disposal).
- Maintaining an append-only transaction ledger.
- Calculating current stock from transaction history.
- Linking resource issues to patient admissions.
- Supporting multiple inventory locations.
- Generating low-stock alerts based on calculated stock levels.

### 9.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Resource Management Module | Transactions are linked to resource definitions. |
| Admission Management Module | Issues are linked to admissions. |
| Notification Module | Low-stock alerts when calculated stock falls below threshold. |
| Audit Logging Module | All inventory transactions are logged. |

### 9.3 Inputs

| Input | Source |
|-------|--------|
| Stock movement data (type, quantity, reference) | Resource Manager |
| Admission ID (for patient issues) | Nursing Officer |
| Location | Resource Manager |

### 9.4 Outputs

| Output | Destination |
|--------|-------------|
| Inventory transaction record | System storage |
| Calculated stock levels | Dashboard, CDS Engine |
| Low-stock alerts | Notification Module |
| Consumption reports | Reporting Module |

### 9.5 Interactions

- Records every stock movement in the transaction ledger.
- Calculates current stock levels from transaction history.
- Provides stock data to CDS Engine for recommendation generation.
- Generates low-stock alerts when calculated stock falls below threshold.

### 9.6 Future Expansion

- Barcode/QR code scanning for transaction recording.
- Integration with hospital procurement systems.
- Automated reorder generation based on reorder points.

---

## 10. Equipment Management Module

### 10.1 Responsibilities

- Medical equipment registry maintenance (name, type, location, status, serial number).
- Equipment status tracking (available, in-use, under-maintenance, out-of-service).
- Equipment assignment to patients or wards.
- Equipment allocation recommendations based on patient needs.
- Equipment utilization history tracking.

### 10.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Clinical Assessment Module | Equipment allocation is driven by patient needs from assessments. |
| Admission Management Module | Equipment assignment is linked to admissions. |
| Ward Management Module | Equipment is located within wards. |
| CDS Engine | Provides equipment allocation recommendations. |
| Equipment Maintenance Module | Maintenance scheduling and alerts. |
| Audit Logging Module | All equipment transactions are logged. |

### 10.3 Inputs

| Input | Source |
|-------|--------|
| Equipment registry data | Equipment Officer |
| Equipment status updates | Equipment Officer |
| Patient equipment requirements | CDS Engine |

### 10.4 Outputs

| Output | Destination |
|--------|-------------|
| Equipment status information | Dashboard, CDS Engine |
| Equipment allocation recommendations | Admission Module, Ward Manager |
| Equipment status changes | Audit Logging Module |

### 10.5 Interactions

- Maintains the authoritative equipment inventory for the Lassa Fever Unit.
- Provides equipment availability data to the CDS Engine.
- Coordinates with Equipment Maintenance Module for maintenance scheduling.
- Tracks equipment assignments to patients and wards for infection control compliance.

### 10.6 Future Expansion

- IoT-based equipment location tracking.
- Predictive maintenance using usage pattern analysis.
- Integration with hospital biomedical engineering department systems.

---

## 11. Equipment Maintenance Module

### 11.1 Responsibilities

- Recording maintenance history for equipment (scheduled, unscheduled, repair, calibration).
- Tracking maintenance status (Scheduled, In Progress, Completed, Overdue).
- Generating maintenance alerts for overdue maintenance.
- Preventing equipment assignment when maintenance is overdue.

### 11.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Equipment Management Module | Maintenance is linked to equipment records. |
| Notification Module | Maintenance alerts and overdue warnings. |
| Audit Logging Module | All maintenance events are logged. |

### 11.3 Inputs

| Input | Source |
|-------|--------|
| Maintenance records | Equipment Officer |
| Maintenance completion | Equipment Officer / Vendor |
| Equipment ID | System |

### 11.4 Outputs

| Output | Destination |
|--------|-------------|
| Maintenance record | System storage |
| Maintenance alerts | Notification Module |
| Equipment status update | Equipment Management Module |

### 11.5 Interactions

- Records maintenance history for each piece of equipment.
- Generates alerts for scheduled and overdue maintenance.
- Updates equipment status when maintenance is completed.
- Provides maintenance data for equipment utilization reporting.

### 11.6 Future Expansion

- Predictive maintenance using usage data.
- Integration with vendor maintenance scheduling systems.
- Maintenance cost tracking and budgeting.

---

## 12. Staff Management Module

### 12.1 Responsibilities

- Staff profile maintenance (name, role, specialization, certification, maximum workload threshold).
- Staff assignment to wards and patients.
- Staff allocation recommendations based on workload balance and availability.
- Staff availability display.

### 12.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Admission Management Module | Staff are assigned during admission processing. |
| Ward Management Module | Staff are organized by ward. |
| CDS Engine | Provides staff allocation recommendations. |
| Workload Calculator | Calculates dynamic workload scores. |
| Shift Management Module | Staff are assigned to shifts. |
| Audit Logging Module | All staff assignments are logged. |
| Notification Module | Assignment notifications. |

### 12.3 Inputs

| Input | Source |
|-------|--------|
| Staff profile data | Administrator |
| Staff availability status | Staff Member / Administrator |

### 12.4 Outputs

| Output | Destination |
|--------|-------------|
| Staff availability status | Dashboard, CDS Engine |
| Staff allocation recommendations | Admission Module, Ward Manager |
| Workload reports | Reporting Module |

### 12.5 Interactions

- Maintains the authoritative staff roster for the Lassa Fever Unit.
- Provides availability data to the CDS Engine.
- Coordinates with Workload Calculator for dynamic workload scoring.
- Tracks staff certifications for compliance with assignment rules.

### 12.6 Future Expansion

- Automated shift scheduling optimization.
- Fatigue risk assessment based on consecutive shift patterns.
- Integration with hospital HR and payroll systems.

---

## 13. Workload Calculator

### 13.1 Responsibilities

- Calculating staff workload scores using the defined formula.
- Recalculating workload on assignment changes.
- Providing workload data to CDS Engine and reporting.

### 13.2 Workload Formula

```
Workload Score = Σ (Patient Factor × Severity Weight × Time Factor)
```

Patient factors:
- Mild: 1.0 | Moderate: 1.5 | Severe: 2.5 | Critical: 4.0
- Isolation: +1.0 | ICU: +1.5

### 13.3 Maximum Thresholds

| Role | Max Score | Alert At |
|------|-----------|----------|
| Nurse | 12.0 | 10.0 (80%) |
| Doctor | 15.0 | 12.0 (80%) |
| Lab Technician | 10.0 | 8.0 (80%) |
| Support Staff | 8.0 | 6.0 (80%) |

### 13.4 Recalculation Triggers

- New patient assignment
- Patient discharge or transfer
- Patient severity change
- Shift assignment change
- Shift start or end

---

## 14. Shift Management Module

### 14.1 Responsibilities

- Defining shift periods (name, date, start/end time, ward).
- Managing staff-to-shift assignments.
- Preventing overlapping shift assignments.
- Tracking shift status (Scheduled, In Progress, Completed).

### 14.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Staff Management Module | Shifts are assigned to staff. |
| Ward Management Module | Shifts are ward-specific. |
| Notification Module | Shift assignment and absence notifications. |
| Audit Logging Module | All shift events are logged. |

### 14.3 Inputs

| Input | Source |
|-------|--------|
| Shift definitions | Ward Manager |
| Staff-to-shift assignments | Ward Manager |

### 14.4 Outputs

| Output | Destination |
|--------|-------------|
| Shift schedule | Dashboard |
| Staff availability based on shifts | CDS Engine, Workload Calculator |

### 14.5 Interactions

- Defines shift schedules for each ward.
- Assigns staff to shifts and tracks attendance.
- Provides shift-based availability data to CDS Engine.
- Coordinates with Workload Calculator for time-based workload factors.

### 14.6 Future Expansion

- Automated shift scheduling based on demand forecasts.
- Swap request workflow.
- Integration with hospital HR systems.

---

## 15. CDS Engine (Rule-Based Clinical Decision Support)

### 15.1 Responsibilities

- Generating bed allocation recommendations based on multi-factor analysis.
- Generating staff assignment recommendations based on workload and availability.
- Generating equipment allocation recommendations based on patient needs.
- Generating resource (consumable) allocation recommendations based on severity and stock.
- Presenting recommendations with confidence scores and rationale.
- Supporting recommendation override with justification recording.
- Tracking recommendation acceptance and override rates.
- Providing fallback recommendations when optimal options are unavailable.

### 15.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Clinical Assessment Module | Patient severity, triage, and infection status data. |
| Bed Management Module | Bed availability, type, and isolation data. |
| Ward Management Module | Ward occupancy and configuration data. |
| Equipment Management Module | Equipment availability and status data. |
| Staff Management Module | Staff availability and specialization data. |
| Workload Calculator | Staff workload scores. |
| Inventory Transaction Module | Resource stock levels. |
| Audit Logging Module | Historical utilization data and recommendation tracking. |
| Notification Module | Recommendation alerts and escalation messages. |

### 15.3 Inputs

| Input | Source |
|-------|--------|
| Latest ClinicalAssessment (severity, triage, infection status) | Clinical Assessment Module |
| Bed availability and configuration | Bed Management Module |
| Ward occupancy and isolation requirements | Ward Management Module |
| Equipment availability and status | Equipment Management Module |
| Staff availability, specialization, and workload | Staff Management Module, Workload Calculator |
| Resource inventory levels | Inventory Transaction Module |
| Historical utilization patterns | Audit Logging Module |
| Override justification | Ward Manager / Authorized User |
| Engine parameters | System Configuration |

### 15.4 Outputs

| Output | Destination |
|--------|-------------|
| Allocation Recommendation (with items for bed, staff, equipment, resource) | Admission Module, User Interface |
| Recommendation confidence scores and rationale | User Interface |
| Scoring breakdown (factor scores and weights) | User Interface, Audit |
| Override records | Audit Logging Module |
| Recommendation performance metrics | Reporting Module |

### 15.5 Interactions

- Aggregates data from all resource modules to generate holistic recommendations.
- Scores each available option based on weighted factors.
- Presents ranked recommendations with explanatory rationale.
- Accepts override decisions with mandatory justification.
- Tracks all recommendation events for performance analysis.

### 15.6 Future Expansion

- Machine learning-based recommendation optimization using historical override data.
- Real-time recommendation adjustment based on changing conditions.
- Multi-patient batch optimization during surge periods.

---

## 16. Forecasting Module

### 16.1 Responsibilities

- Analyzing historical admission data to identify trends.
- Generating demand forecasts using Simple Moving Average (7-day) and Weighted Moving Average (14-day, 30-day).
- Displaying forecast results with visualization.
- Storing forecast snapshots for accuracy analysis.
- Calculating forecast accuracy using MAPE.
- Flagging anticipated shortages based on forecasts.
- Supporting seasonal pattern recognition for Lassa fever admission cycles.

### 16.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Admission Management Module | Historical admission data for trend analysis. |
| Bed Management Module | Historical bed utilization data. |
| Inventory Transaction Module | Historical resource consumption data. |
| Staff Management Module | Historical staffing patterns. |
| Notification Module | Shortage anticipation alerts. |

### 16.3 Inputs

| Input | Source |
|-------|--------|
| Historical admission records | Admission Management Module |
| Historical bed occupancy data | Bed Management Module |
| Historical resource consumption data | Inventory Transaction Module |
| Forecast time horizon | User configuration |

### 16.4 Outputs

| Output | Destination |
|--------|-------------|
| Admission demand forecast | User Interface, Dashboard |
| Resource demand forecast | User Interface, Resource Manager |
| Bed demand forecast | User Interface, Ward Manager |
| Forecast snapshot | System Storage |
| Shortage anticipation alerts | Notification Module |

### 16.5 Interactions

- Queries historical data from multiple modules for trend analysis.
- Applies Simple Moving Average and Weighted Moving Average models.
- Stores forecast snapshots for accuracy analysis.
- Visualizes trends and forecasts on the dashboard.
- Generates proactive alerts when forecasts indicate anticipated shortages.

### 16.6 Future Expansion

- Integration with external epidemiological data sources (NCDC surveillance data).
- Machine learning models for improved prediction accuracy.
- Scenario modeling for outbreak surge planning.

---

## 17. Notification Module

### 17.1 Responsibilities

- Sending in-app notifications for critical events.
- Sending email notifications for important alerts.
- Maintaining notification history per user.
- Supporting configurable notification preferences per user role.
- Generating escalation notifications for unaddressed recommendations.

### 17.2 Dependencies

| Dependency | Nature |
|------------|--------|
| All Modules | Notifications are triggered by events across all modules. |
| Audit Logging Module | Notification events are logged. |

### 17.3 Inputs

| Input | Source |
|-------|--------|
| Notification events (admission, low stock, maintenance, CDS recommendation) | Various Modules |
| User notification preferences | User Configuration |
| Escalation timeframes | System Configuration |

### 17.4 Outputs

| Output | Destination |
|--------|-------------|
| In-app notifications | User Interface |
| Email notifications | User Email |
| Notification history | User Interface |

### 17.5 Interactions

- Receives notification events from all modules.
- Routes notifications based on event type and recipient role.
- Delivers notifications via in-app and email channels.
- Manages notification preferences and escalation rules.
- Maintains notification history for user reference.

### 17.6 Future Expansion

- SMS notifications for critical alerts.
- Push notifications for mobile application (future phase).
- Integration with hospital paging systems.

---

## 18. Reporting Module

### 18.1 Responsibilities

- Generating bed occupancy reports (daily, weekly, monthly).
- Generating bed turnover rate and average length of stay reports.
- Generating bed utilization by ward reports.
- Generating resource consumption by severity reports.
- Generating staff utilization and workload reports.
- Generating equipment downtime reports.
- Generating CDS engine performance reports (acceptance rate, override rate, average confidence).
- Supporting report export in PDF and CSV formats.
- Generating audit trail reports for compliance review.

### 18.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Bed Management Module | Bed occupancy data for reports. |
| Inventory Transaction Module | Resource consumption data for reports. |
| Staff Management Module | Staff workload data for reports. |
| Equipment Management Module | Equipment utilization data for reports. |
| CDS Engine | Recommendation performance data for reports. |
| Audit Logging Module | Audit trail data for compliance reports. |

### 18.3 Inputs

| Input | Source |
|-------|--------|
| Report type and parameters | User |
| Date range and filters | User |
| Data from all operational modules | Various Modules |

### 18.4 Outputs

| Output | Destination |
|--------|-------------|
| Generated reports (PDF, CSV) | User Download |
| Report summaries | Dashboard |
| Scheduled report emails | Admin Email |

### 18.5 Interactions

- Aggregates data from all modules for report generation.
- Applies filters and date ranges as specified by users.
- Generates formatted reports in PDF and CSV formats.
- Supports scheduled report generation for recurring needs.
- Provides audit reports for compliance and regulatory review.

### 18.6 Future Expansion

- Interactive report builder with custom parameters.
- Automated report scheduling and distribution.
- Data export for external analysis tools.

---

## 19. Audit Logging Module

### 19.1 Responsibilities

- Logging all user actions with timestamp, user ID, action type, and affected entity.
- Logging all data changes with before/after values.
- Logging all CDS engine decisions and overrides.
- Ensuring audit logs are read-only and immutable.
- Supporting audit log search and filtering.

### 19.2 Dependencies

| Dependency | Nature |
|------------|--------|
| All Modules | Audit events are generated by all modules. |
| Authentication Module | User identity for audit trail. |

### 19.3 Inputs

| Input | Source |
|-------|--------|
| User actions and data changes | All Modules |
| Authentication events | Authentication Module |
| CDS Engine decisions | CDS Engine |

### 19.4 Outputs

| Output | Destination |
|--------|-------------|
| Immutable audit records | System Storage |
| Audit log search results | User Interface |
| Compliance reports | Reporting Module |

### 19.5 Interactions

- Captures audit events from all modules via AOP aspects or event listeners.
- Writes audit records to an append-only audit table.
- Provides search and filtering capabilities for audit review.
- Supplies audit data for compliance reporting.

### 19.6 Future Expansion

- Cryptographic integrity verification for audit records.
- Integration with hospital compliance management systems.
- Real-time audit anomaly detection.

---

## 20. Login Audit Log Module

### 20.1 Responsibilities

- Recording all authentication events (login success, login failure, logout, password changes, account lock/unlock).
- Maintaining a dedicated audit trail for security monitoring.
- Supporting brute-force detection and security analysis.

### 20.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Authentication Module | Authentication events are captured. |

### 20.3 Inputs

| Input | Source |
|-------|--------|
| Authentication events | Authentication Module |

### 20.4 Outputs

| Output | Destination |
|--------|-------------|
| Login audit records | System Storage |
| Security analysis results | Administrator Dashboard |

### 20.5 Interactions

- Records all authentication events with IP address and user agent.
- Provides data for brute-force detection and account lockout decisions.
- Retained for 90 days for security analysis.

### 20.6 Future Expansion

- Real-time brute-force detection and blocking.
- Integration with security information and event management (SIEM) systems.

---

## 14. Administration Module

### 14.1 Responsibilities

- User account management (create, edit, deactivate).
- Role management with defined permissions per role.
- System configuration management (session timeout, notification settings, thresholds).
- User management dashboard.
- CDS Engine parameter configuration.

### 21.2 Dependencies

| Dependency | Nature |
|------------|--------|
| Authentication Module | User accounts and authentication. |
| Audit Logging Module | Administrative actions are logged. |

### 21.3 Inputs

| Input | Source |
|-------|--------|
| User account data | Administrator |
| Role and permission configuration | Administrator |
| System configuration parameters | Administrator |
| CDS Engine parameters | Administrator |

### 21.4 Outputs

| Output | Destination |
|--------|-------------|
| Managed user accounts | Authentication Module |
| Configured roles and permissions | Security Layer |
| System configuration | All Modules |

### 21.5 Interactions

- Manages user lifecycle from creation to deactivation.
- Defines and enforces role-based access control.
- Configures system-wide parameters that affect all modules.
- Provides the administrative interface for system governance.

### 21.6 Future Expansion

- Multi-tenant configuration for hospital-wide deployment.
- API key management for external integrations.
- Self-service user profile management.

---

## 22. Module Interaction Matrix

| Module | Auth | Patient | ClinAssess | Admission | Bed | BedClean | Ward | Resource | Inventory | Equip | EquipMaint | Staff | Workload | Shift | CDS | Forecast | Notify | Report | Audit | LoginAudit | Admin |
|--------|------|---------|------------|-----------|-----|----------|------|----------|-----------|-------|------------|-------|----------|-------|-----|----------|--------|--------|-------|------------|-------|
| Auth | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | ✓ | — | — | ✓ | ✓ |
| Patient | ✓ | — | ✓ | ✓ | — | — | — | — | — | — | — | — | — | — | ✓ | ✓ | — | ✓ | ✓ | — | — |
| ClinAssess | ✓ | ✓ | — | ✓ | — | — | — | — | — | — | — | — | — | — | ✓ | — | ✓ | ✓ | ✓ | — | — |
| Admission | ✓ | ✓ | ✓ | — | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ | — | — | ✓ | ✓ | ✓ | ✓ | ✓ | — | — |
| Bed | — | — | — | ✓ | — | ✓ | ✓ | — | — | — | — | — | — | — | ✓ | ✓ | ✓ | ✓ | ✓ | — | — |
| BedClean | — | — | — | ✓ | ✓ | — | — | — | — | — | — | ✓ | — | — | — | — | ✓ | ✓ | ✓ | — | — |
| Ward | — | — | — | ✓ | ✓ | — | — | — | — | — | — | ✓ | — | ✓ | ✓ | — | — | ✓ | ✓ | — | ✓ |
| Resource | — | — | — | ✓ | — | — | — | — | ✓ | — | — | — | — | — | ✓ | ✓ | ✓ | ✓ | ✓ | — | — |
| Inventory | — | — | — | ✓ | — | — | — | ✓ | — | — | — | — | — | — | ✓ | ✓ | ✓ | ✓ | ✓ | — | — |
| Equip | — | — | — | ✓ | — | — | ✓ | — | — | — | ✓ | — | — | — | ✓ | — | ✓ | ✓ | ✓ | — | — |
| EquipMaint | — | — | — | — | — | — | — | — | — | ✓ | — | — | — | — | — | — | ✓ | ✓ | ✓ | — | — |
| Staff | — | — | — | ✓ | — | — | ✓ | — | — | — | — | — | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | — |
| Workload | — | — | — | ✓ | — | — | — | — | — | — | — | ✓ | — | ✓ | ✓ | — | ✓ | ✓ | ✓ | — | — |
| Shift | — | — | — | — | — | — | ✓ | — | — | — | — | ✓ | ✓ | — | — | — | ✓ | ✓ | ✓ | — | — |
| CDS | — | ✓ | ✓ | ✓ | ✓ | — | ✓ | ✓ | ✓ | ✓ | — | ✓ | ✓ | — | — | — | ✓ | ✓ | ✓ | — | — |
| Forecast | — | ✓ | ✓ | ✓ | ✓ | — | — | ✓ | ✓ | — | — | ✓ | — | — | — | — | ✓ | ✓ | ✓ | — | — |
| Notify | ✓ | — | — | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ | ✓ | ✓ | — | — | ✓ | — | — |
| Report | — | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ | ✓ | — | — | ✓ | — | — |
| Audit | ✓ | — | — | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ | — | — | — |
| LoginAudit | ✓ | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | — | ✓ | — | — | — |
| Admin | ✓ | — | — | — | — | — | ✓ | — | — | — | — | ✓ | — | — | ✓ | — | — | — | ✓ | — | — |

## 23. Document References

| Document | Reference |
|----------|-----------|
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| System Architecture | `docs/planning/03-system-architecture.md` |
| Domain Model | `docs/planning/06-domain-model.md` |
| CDS Engine Design | `docs/planning/08-recommendation-engine-design.md` |
| Database Plan | `docs/planning/07-database-plan.md` |
