-- V019: Comprehensive Demo Clinical Seed Data for BSUTH Lassa Fever Treatment Center
-- Makurdi, Benue State, Nigeria

-- 1. Additional Authorized Staff Users (Password: Admin@12345)
INSERT INTO users (id, username, email, password_hash, full_name, role, status, failed_login_attempts, created_at, updated_at)
VALUES
    ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'doctor', 'doctor.gbande@bsuth.ng', '$2a$12$6nyJ0Hd9CyiGoiBw9e.1BeB8rf1G4OODuZuG.hyt7sPX/nMsvFN6W', 'Dr. Terver Gbande', 'MEDICAL_DOCTOR', 'ACTIVE', 0, NOW(), NOW()),
    ('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'wardmanager', 'wardmanager.kaan@bsuth.ng', '$2a$12$6nyJ0Hd9CyiGoiBw9e.1BeB8rf1G4OODuZuG.hyt7sPX/nMsvFN6W', 'Iorwuese Kaan', 'WARD_MANAGER', 'ACTIVE', 0, NOW(), NOW()),
    ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'cleaner1', 'hygiene.tyokyaa@bsuth.ng', '$2a$12$6nyJ0Hd9CyiGoiBw9e.1BeB8rf1G4OODuZuG.hyt7sPX/nMsvFN6W', 'Moses Tyokyaa', 'CLEANER', 'ACTIVE', 0, NOW(), NOW()),
    ('d1eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'resourcemanager', 'inventory.chia@bsuth.ng', '$2a$12$6nyJ0Hd9CyiGoiBw9e.1BeB8rf1G4OODuZuG.hyt7sPX/nMsvFN6W', 'Victoria Chia', 'RESOURCE_MANAGER', 'ACTIVE', 0, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- 2. Clinical Isolation & Treatment Wards
INSERT INTO wards (id, name, ward_type, max_bed_capacity, isolation_level, equipment_zone, status, created_at, updated_at, created_by)
VALUES
    ('c1679d90-2798-4a3b-8cbe-579b9a559901', 'Lassa ICU Isolation Ward B', 'ICU', 8, 'HIGH_ISOLATION', 'ZONE_B', 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('c2679d90-2798-4a3b-8cbe-579b9a559902', 'Suspected Viral Hemorrhagic Ward C', 'ISOLATION', 10, 'MODERATE', 'ZONE_C', 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('c3679d90-2798-4a3b-8cbe-579b9a559903', 'Convalescent Step-Down Ward E', 'STEP_DOWN', 12, 'STANDARD', 'ZONE_E', 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (name) DO NOTHING;

-- 3. Realistic Hospital Beds across Treatment Wards
-- Ward A Beds (High Isolation)
INSERT INTO beds (id, bed_number, ward_id, bed_type, is_isolation_capable, status, created_at, updated_at, created_by)
VALUES
    ('33f80923-667b-4c4b-a71c-716bfd863622', 'BED-A02', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 'ISOLATION_NEGATIVE_PRESSURE', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('33f80923-667b-4c4b-a71c-716bfd863623', 'BED-A03', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 'ISOLATION_NEGATIVE_PRESSURE', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('33f80923-667b-4c4b-a71c-716bfd863624', 'BED-A04', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 'ISOLATION_NEGATIVE_PRESSURE', TRUE, 'CLEANING_REQUIRED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('33f80923-667b-4c4b-a71c-716bfd863625', 'BED-A05', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 'ISOLATION_NEGATIVE_PRESSURE', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (bed_number, ward_id) DO NOTHING;

-- Ward B Beds (ICU Isolation)
INSERT INTO beds (id, bed_number, ward_id, bed_type, is_isolation_capable, status, created_at, updated_at, created_by)
VALUES
    ('44f80923-667b-4c4b-a71c-716bfd863631', 'ICU-B01', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 'ICU', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('44f80923-667b-4c4b-a71c-716bfd863632', 'ICU-B02', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 'ICU', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('44f80923-667b-4c4b-a71c-716bfd863633', 'ICU-B03', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 'ICU', TRUE, 'OCCUPIED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('44f80923-667b-4c4b-a71c-716bfd863634', 'ICU-B04', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 'ICU', TRUE, 'CLEANING_REQUIRED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('44f80923-667b-4c4b-a71c-716bfd863635', 'ICU-B05', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 'ICU', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (bed_number, ward_id) DO NOTHING;

-- Ward C Beds (Suspected Lassa)
INSERT INTO beds (id, bed_number, ward_id, bed_type, is_isolation_capable, status, created_at, updated_at, created_by)
VALUES
    ('55f80923-667b-4c4b-a71c-716bfd863641', 'SUSP-C01', 'c2679d90-2798-4a3b-8cbe-579b9a559902', 'STANDARD', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('55f80923-667b-4c4b-a71c-716bfd863642', 'SUSP-C02', 'c2679d90-2798-4a3b-8cbe-579b9a559902', 'STANDARD', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('55f80923-667b-4c4b-a71c-716bfd863643', 'SUSP-C03', 'c2679d90-2798-4a3b-8cbe-579b9a559902', 'STANDARD', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('55f80923-667b-4c4b-a71c-716bfd863644', 'SUSP-C04', 'c2679d90-2798-4a3b-8cbe-579b9a559902', 'STANDARD', TRUE, 'OCCUPIED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (bed_number, ward_id) DO NOTHING;

-- Ward D Beds (Emergency Isolation)
INSERT INTO beds (id, bed_number, ward_id, bed_type, is_isolation_capable, status, created_at, updated_at, created_by)
VALUES
    ('66f80923-667b-4c4b-a71c-716bfd863652', 'ISO-D-02', 'eecac66d-a5c8-4a29-b881-553d3ca28576', 'ISOLATION_NEGATIVE_PRESSURE', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('66f80923-667b-4c4b-a71c-716bfd863653', 'ISO-D-03', 'eecac66d-a5c8-4a29-b881-553d3ca28576', 'ISOLATION_NEGATIVE_PRESSURE', TRUE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('66f80923-667b-4c4b-a71c-716bfd863654', 'ISO-D-04', 'eecac66d-a5c8-4a29-b881-553d3ca28576', 'ISOLATION_NEGATIVE_PRESSURE', TRUE, 'CLEANING_REQUIRED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (bed_number, ward_id) DO NOTHING;

-- Ward E Beds (Convalescent Step-Down)
INSERT INTO beds (id, bed_number, ward_id, bed_type, is_isolation_capable, status, created_at, updated_at, created_by)
VALUES
    ('77f80923-667b-4c4b-a71c-716bfd863661', 'STEP-E01', 'c3679d90-2798-4a3b-8cbe-579b9a559903', 'STANDARD', FALSE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('77f80923-667b-4c4b-a71c-716bfd863662', 'STEP-E02', 'c3679d90-2798-4a3b-8cbe-579b9a559903', 'STANDARD', FALSE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('77f80923-667b-4c4b-a71c-716bfd863663', 'STEP-E03', 'c3679d90-2798-4a3b-8cbe-579b9a559903', 'STANDARD', FALSE, 'AVAILABLE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (bed_number, ward_id) DO NOTHING;

-- 4. Realistic Benue State Lassa Fever Patients
INSERT INTO patients (id, patient_number, full_name, date_of_birth, gender, phone_number, address, next_of_kin_name, next_of_kin_phone, is_active, created_at, updated_at, created_by)
VALUES
    ('e1c607d8-673f-4599-95ab-a55addde9f11', 'PT-20261029-0001', 'Dooshima Kave', '1992-05-14', 'FEMALE', '+2348034567801', 'High Level Area, Makurdi, Benue State', 'Terkura Kave', '+2348034567899', TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('e1c607d8-673f-4599-95ab-a55addde9f12', 'PT-20261029-0002', 'Aondover Ikyer', '1985-11-20', 'MALE', '+2348021112233', 'Wurukum Market Road, Makurdi, Benue State', 'Ngizan Ikyer', '+2348021112234', TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('e1c607d8-673f-4599-95ab-a55addde9f13', 'PT-20261029-0003', 'Comfort Ochanya', '1998-03-08', 'FEMALE', '+2348057778899', 'Otukpo Main Town, Benue State', 'Sunday Ochanya', '+2348057778800', TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('e1c607d8-673f-4599-95ab-a55addde9f14', 'PT-20261029-0004', 'Terna Shior', '1976-08-30', 'MALE', '+2348163334455', 'Gboko South, Benue State', 'Mlumun Shior', '+2348163334466', TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('e1c607d8-673f-4599-95ab-a55addde9f15', 'PT-20261029-0005', 'Hembafan Agbo', '2001-12-05', 'FEMALE', '+2348092223344', 'North Bank, Makurdi, Benue State', 'Barnabas Agbo', '+2348092223355', TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('e1c607d8-673f-4599-95ab-a55addde9f16', 'PT-20261029-0006', 'Kwashie Terhemba', '1968-04-18', 'MALE', '+2348145556677', 'Katsina-Ala Central, Benue State', 'Sember Terhemba', '+2348145556688', TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (patient_number) DO NOTHING;

-- 5. Clinical Assessments for Patients
INSERT INTO clinical_assessments (id, patient_id, assessed_by, severity_level, triage_classification, infection_status, clinical_notes, is_reassessment, assessment_timestamp, created_at)
VALUES
    ('ca1607d8-673f-4599-95ab-a55addde9f01', 'e1c607d8-673f-4599-95ab-a55addde9f11', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CRITICAL', 'RED', 'CONFIRMED', 'High viral load RT-PCR positive. Acute hemorrhagic fever, temp 40.2C, thrombocytopenia. Requires negative pressure isolation.', FALSE, NOW(), NOW()),
    ('ca1607d8-673f-4599-95ab-a55addde9f02', 'e1c607d8-673f-4599-95ab-a55addde9f12', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'SEVERE', 'YELLOW', 'CONFIRMED', 'Confirmed Lassa fever, persistent vomiting, retrosternal pain, dehydration. Requires isolation bed and ribavirin IV protocol.', FALSE, NOW(), NOW()),
    ('ca1607d8-673f-4599-95ab-a55addde9f03', 'e1c607d8-673f-4599-95ab-a55addde9f13', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'SEVERE', 'YELLOW', 'SUSPECTED', 'Close family contact of confirmed case. Fever for 4 days, pharyngitis, conjunctival injection. Triage to isolation.', FALSE, NOW(), NOW()),
    ('ca1607d8-673f-4599-95ab-a55addde9f04', 'e1c607d8-673f-4599-95ab-a55addde9f14', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'MODERATE', 'YELLOW', 'SUSPECTED', 'Agricultural worker with rodent exposure. Fever, muscle aches, sore throat. Awaiting PCR result.', FALSE, NOW(), NOW()),
    ('ca1607d8-673f-4599-95ab-a55addde9f05', 'e1c607d8-673f-4599-95ab-a55addde9f15', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'MILD', 'GREEN', 'RULED_OUT', 'Two consecutive negative RT-PCR tests for Lassa virus. Non-infectious febrile illness.', FALSE, NOW(), NOW()),
    ('ca1607d8-673f-4599-95ab-a55addde9f06', 'e1c607d8-673f-4599-95ab-a55addde9f16', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CRITICAL', 'RED', 'CONFIRMED', 'Elderly patient with acute encephalopathy, pulmonary edema, hematuria. Requires ICU isolation bed and ventilator support.', FALSE, NOW(), NOW()),
    ('ca1607d8-673f-4599-95ab-a55addde9f07', '8d1b4a45-4a09-4481-a1eb-7b120188e5f7', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'SEVERE', 'YELLOW', 'CONFIRMED', 'Confirmed Lassa case with high fever and prostration. Admitted for immediate Ribavirin protocol.', FALSE, NOW(), NOW()),
    ('ca1607d8-673f-4599-95ab-a55addde9f08', 'b1c607d8-673f-4599-95ab-a55addde9f03', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'CRITICAL', 'RED', 'CONFIRMED', 'Emergency Lassa case presented with bleeding diathesis and hypotension.', FALSE, NOW(), NOW())
ON CONFLICT (id) DO NOTHING;

-- 6. Active Admissions Ready for AI Recommendation or in Treatment
INSERT INTO admissions (id, admission_number, patient_id, ward_id, bed_id, status, admission_notes, admitted_at, is_active, created_at, updated_at, created_by)
VALUES
    ('a1c607d8-673f-4599-95ab-a55addde9f21', 'ADM-20261029-1001', 'e1c607d8-673f-4599-95ab-a55addde9f11', 'c1679d90-2798-4a3b-8cbe-579b9a559901', NULL, 'ADMITTED', 'Critical confirmed Lassa patient awaiting AI bed recommendation', NOW() - INTERVAL '2 hours', TRUE, NOW() - INTERVAL '2 hours', NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('a1c607d8-673f-4599-95ab-a55addde9f22', 'ADM-20261029-1002', 'e1c607d8-673f-4599-95ab-a55addde9f12', 'b9679d90-2798-4a3b-8cbe-579b9a559900', NULL, 'ADMITTED', 'Severe confirmed case ready for negative pressure bed assignment', NOW() - INTERVAL '1 hour', TRUE, NOW() - INTERVAL '1 hour', NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('a1c607d8-673f-4599-95ab-a55addde9f23', 'ADM-20261029-1003', 'e1c607d8-673f-4599-95ab-a55addde9f13', 'c2679d90-2798-4a3b-8cbe-579b9a559902', NULL, 'ADMITTED', 'Suspected Lassa case with high contact risk awaiting bed allocation', NOW() - INTERVAL '45 minutes', TRUE, NOW() - INTERVAL '45 minutes', NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('a1c607d8-673f-4599-95ab-a55addde9f24', 'ADM-20261029-1004', 'e1c607d8-673f-4599-95ab-a55addde9f16', 'c1679d90-2798-4a3b-8cbe-579b9a559901', '44f80923-667b-4c4b-a71c-716bfd863633', 'ADMITTED', 'Critical ICU patient currently receiving treatment on bed ICU-B03', NOW() - INTERVAL '6 hours', TRUE, NOW() - INTERVAL '6 hours', NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (admission_number) DO NOTHING;

-- Link occupied bed ICU-B03 to its current admission
UPDATE beds
SET current_admission_id = 'a1c607d8-673f-4599-95ab-a55addde9f24'
WHERE id = '44f80923-667b-4c4b-a71c-716bfd863633';

-- 7. Bed Decontamination & Cleaning Tasks across Stages
INSERT INTO bed_cleaning (id, bed_id, admission_id, status, assigned_to, assigned_at, started_at, completed_at, verified_by, verified_at, cleaning_notes, created_at, updated_at)
VALUES
    ('bc1607d8-673f-4599-95ab-a55addde9f01', '33f80923-667b-4c4b-a71c-716bfd863624', '51d1f15d-b481-45c2-87d9-95f0fd38e21d', 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, 'Post-discharge terminal disinfection required: 0.5% chlorine solution and UV fumigation.', NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours'),
    ('bc1607d8-673f-4599-95ab-a55addde9f02', '44f80923-667b-4c4b-a71c-716bfd863634', 'b8a2d8ee-7479-4eb1-acd5-bd28ec5f4e83', 'ASSIGNED', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', NOW() - INTERVAL '2 hours', NULL, NULL, NULL, NULL, 'ICU bed assigned to decontamination specialist. PPE Level 4 required.', NOW() - INTERVAL '2 hours', NOW() - INTERVAL '1 hour'),
    ('bc1607d8-673f-4599-95ab-a55addde9f03', '66f80923-667b-4c4b-a71c-716bfd863654', 'f3b87cd4-7f1e-45ff-985c-8168b032b7f3', 'IN_PROGRESS', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', NOW() - INTERVAL '1 hour', NOW() - INTERVAL '30 minutes', NULL, NULL, NULL, 'Terminal misting and mattress encasement cleaning currently ongoing.', NOW() - INTERVAL '1 hour', NOW() - INTERVAL '30 minutes')
ON CONFLICT (id) DO NOTHING;

-- 8. Strategic Resource Suppliers (NCDC, WHO, Federal Depot)
INSERT INTO resource_suppliers (id, name, contact_person, phone_number, email, address, lead_time_days, is_active, created_at, updated_at, created_by)
VALUES
    ('ba1607d8-673f-4599-95ab-a55addde9f31', 'Nigeria Centre for Disease Control (NCDC) Strategic Stockpile', 'Dr. Chikwe Ihekweazu', '+23480097000010', 'stockpile@ncdc.gov.ng', 'Plot 801, Ebitu Ukiwe Street, Jabi, Abuja', 2, TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ba1607d8-673f-4599-95ab-a55addde9f32', 'WHO Emergency Medical Logistics Hub', 'Mrs. Zainab Aliyu', '+2348031002000', 'logistics.ngr@who.int', 'UN House, Diplomatic Zone, Abuja', 4, TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ba1607d8-673f-4599-95ab-a55addde9f33', 'Federal Central Medical Depot & Gas Makurdi', 'Mr. Emmanuel Agada', '+2348169998877', 'medsupplies@makurdistores.ng', 'Hospital Road, Makurdi, Benue State', 1, TRUE, NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (id) DO NOTHING;

-- 9. Clinical Resources & Essential Lassa Therapeutics
INSERT INTO resources (id, name, category, unit_of_measure, minimum_threshold, reorder_point, criticality_level, default_supplier_id, created_at, updated_at, created_by)
VALUES
    ('bb1607d8-673f-4599-95ab-a55addde9f41', 'Ribavirin 200mg/2ml Injection (Lassa Antiviral)', 'MEDICATION', 'VIAL', 100, 250, 'CRITICAL', 'ba1607d8-673f-4599-95ab-a55addde9f31', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('bb1607d8-673f-4599-95ab-a55addde9f42', 'Level 4 Viral Hemorrhagic PPE Hazmat Kits', 'PPE', 'KIT', 80, 200, 'CRITICAL', 'ba1607d8-673f-4599-95ab-a55addde9f31', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('bb1607d8-673f-4599-95ab-a55addde9f43', 'Lassa Virus RT-PCR Rapid Detection Kits', 'DIAGNOSTIC', 'KIT', 50, 150, 'CRITICAL', 'ba1607d8-673f-4599-95ab-a55addde9f32', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('bb1607d8-673f-4599-95ab-a55addde9f44', 'Medical Grade Liquid Oxygen Cylinders (Size G)', 'GAS', 'CYLINDER', 20, 40, 'HIGH', 'ba1607d8-673f-4599-95ab-a55addde9f33', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('bb1607d8-673f-4599-95ab-a55addde9f45', 'Sterile Dialysis Consumables & Bloodlines', 'CONSUMABLE', 'PACK', 30, 60, 'HIGH', 'ba1607d8-673f-4599-95ab-a55addde9f32', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('bb1607d8-673f-4599-95ab-a55addde9f46', 'Heavy-Duty Nitrile Examination Gloves (Box of 100)', 'PPE', 'BOX', 150, 300, 'NORMAL', 'ba1607d8-673f-4599-95ab-a55addde9f33', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('bb1607d8-673f-4599-95ab-a55addde9f47', 'Fluid-Shield N95 Respirator Masks (Box of 50)', 'PPE', 'BOX', 100, 200, 'HIGH', 'ba1607d8-673f-4599-95ab-a55addde9f31', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('bb1607d8-673f-4599-95ab-a55addde9f48', 'Intravenous Normal Saline 0.9% 500ml', 'MEDICATION', 'BOTTLE', 200, 500, 'NORMAL', 'ba1607d8-673f-4599-95ab-a55addde9f33', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (id) DO NOTHING;

-- 10. Resource Inventory Across Hospital Storage Nodes
INSERT INTO resource_inventory (id, resource_id, location, current_stock, expiration_date, batch_number, created_at, updated_at)
VALUES
    ('bc1607d8-673f-4599-95ab-a55addde9f71', 'bb1607d8-673f-4599-95ab-a55addde9f41', 'Central Pharmacy Depot', 320, '2028-12-31', 'BATCH-RIB-2026-A', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f72', 'bb1607d8-673f-4599-95ab-a55addde9f41', 'Ward A Satellite Pharmacy', 80, '2028-12-31', 'BATCH-RIB-2026-B', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f73', 'bb1607d8-673f-4599-95ab-a55addde9f42', 'Central Infection Control Store', 160, '2029-06-30', 'BATCH-PPE-2026-01', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f74', 'bb1607d8-673f-4599-95ab-a55addde9f42', 'Lassa Ward A Antechamber', 45, '2029-06-30', 'BATCH-PPE-2026-02', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f75', 'bb1607d8-673f-4599-95ab-a55addde9f43', 'Molecular Diagnostic Laboratory', 110, '2027-09-30', 'BATCH-PCR-2026-X', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f76', 'bb1607d8-673f-4599-95ab-a55addde9f44', 'Medical Gas Manifold Room', 28, '2030-01-01', 'BATCH-OXY-2026', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f77', 'bb1607d8-673f-4599-95ab-a55addde9f45', 'Lassa ICU Emergency Stock', 42, '2028-04-30', 'BATCH-DIA-2026', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f78', 'bb1607d8-673f-4599-95ab-a55addde9f46', 'Central Medical Stores', 450, '2029-12-31', 'BATCH-GLV-2026', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f79', 'bb1607d8-673f-4599-95ab-a55addde9f47', 'Central Medical Stores', 220, '2029-08-31', 'BATCH-N95-2026', NOW(), NOW()),
    ('bc1607d8-673f-4599-95ab-a55addde9f7a', 'bb1607d8-673f-4599-95ab-a55addde9f48', 'Central Pharmacy Depot', 600, '2028-11-30', 'BATCH-SAL-2026', NOW(), NOW())
ON CONFLICT (resource_id, location, batch_number) DO NOTHING;

-- 11. Advanced Biomedical & ICU Equipment
INSERT INTO equipment (id, name, equipment_type, serial_number, location, status, assigned_admission_id, assigned_ward_id, created_at, updated_at, created_by)
VALUES
    ('ed1607d8-673f-4599-95ab-a55addde9f51', 'Hamilton-C6 Intensive Care Mechanical Ventilator', 'VENTILATOR', 'SN-VENT-BSUTH-001', 'Lassa ICU Isolation Ward B', 'AVAILABLE', NULL, 'c1679d90-2798-4a3b-8cbe-579b9a559901', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ed1607d8-673f-4599-95ab-a55addde9f52', 'Mindray BeneVision N17 Advanced Multiparameter Monitor', 'PATIENT_MONITOR', 'SN-MON-BSUTH-001', 'Lassa ICU Isolation Ward B', 'IN_USE', 'a1c607d8-673f-4599-95ab-a55addde9f24', 'c1679d90-2798-4a3b-8cbe-579b9a559901', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ed1607d8-673f-4599-95ab-a55addde9f53', 'Fresenius Kabi Volumetric Precision Infusion Pump', 'INFUSION_PUMP', 'SN-INF-BSUTH-001', 'Lassa Fever Isolation Ward A', 'AVAILABLE', NULL, 'b9679d90-2798-4a3b-8cbe-579b9a559900', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ed1607d8-673f-4599-95ab-a55addde9f54', 'DeVilbiss 10L High-Flow Oxygen Concentrator', 'OXYGEN_CONCENTRATOR', 'SN-OXY-BSUTH-001', 'Lassa Fever Isolation Ward A', 'AVAILABLE', NULL, 'b9679d90-2798-4a3b-8cbe-579b9a559900', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ed1607d8-673f-4599-95ab-a55addde9f55', 'Zoll R Series ALS Clinical Defibrillator', 'DEFIBRILLATOR', 'SN-DEF-BSUTH-001', 'Lassa Emergency Isolation Ward D', 'AVAILABLE', NULL, 'eecac66d-a5c8-4a29-b881-553d3ca28576', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ed1607d8-673f-4599-95ab-a55addde9f56', 'Philips Respironics V60 Non-Invasive Ventilator', 'VENTILATOR', 'SN-VENT-BSUTH-002', 'Lassa ICU Isolation Ward B', 'AVAILABLE', NULL, 'c1679d90-2798-4a3b-8cbe-579b9a559901', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ed1607d8-673f-4599-95ab-a55addde9f57', 'Fresenius 5008S Continuous Hemodialysis Machine', 'DIALYSIS_MACHINE', 'SN-DIAL-BSUTH-001', 'Lassa ICU Isolation Ward B', 'AVAILABLE', NULL, 'c1679d90-2798-4a3b-8cbe-579b9a559901', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ed1607d8-673f-4599-95ab-a55addde9f58', 'Mindray uMEC10 Vital Signs Monitor', 'PATIENT_MONITOR', 'SN-MON-BSUTH-002', 'Lassa Fever Isolation Ward A', 'UNDER_MAINTENANCE', NULL, 'b9679d90-2798-4a3b-8cbe-579b9a559900', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (serial_number) DO NOTHING;

-- 12. Equipment Maintenance Records
INSERT INTO equipment_maintenance (id, equipment_id, maintenance_type, status, scheduled_date, completed_date, performed_by, maintenance_notes, cost, next_maintenance_date, created_at, updated_at, created_by)
VALUES
    ('ee1607d8-673f-4599-95ab-a55addde9f01', 'ed1607d8-673f-4599-95ab-a55addde9f58', 'CORRECTIVE', 'IN_PROGRESS', CURRENT_DATE, NULL, 'Engr. Daniel Orbunde', 'Display screen flickering and NIBP cuff transducer calibration.', 35000.00, CURRENT_DATE + INTERVAL '90 days', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('ee1607d8-673f-4599-95ab-a55addde9f02', 'ed1607d8-673f-4599-95ab-a55addde9f51', 'PREVENTIVE', 'COMPLETED', CURRENT_DATE - INTERVAL '14 days', CURRENT_DATE - INTERVAL '14 days', 'Engr. Daniel Orbunde', 'Quarterly ventilator flow sensor calibration and HEPA bacterial filter replacement passed.', 50000.00, CURRENT_DATE + INTERVAL '76 days', NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (id) DO NOTHING;

-- 13. Clinical Staff Roster
INSERT INTO staff (id, staff_number, full_name, role, specialization, certification_status, certification_expiry, ward_id, max_workload_threshold, availability_status, created_at, updated_at, created_by)
VALUES
    ('fa1607d8-673f-4599-95ab-a55addde9f61', 'STF-BSUTH-001', 'Dr. Terver Gbande', 'MEDICAL_DOCTOR', 'Infectious Disease Specialist', 'CURRENT', '2028-12-31', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fa1607d8-673f-4599-95ab-a55addde9f62', 'STF-BSUTH-002', 'Dr. Grace Agbenu', 'MEDICAL_DOCTOR', 'Critical Care & Pulmonology', 'CURRENT', '2028-12-31', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fa1607d8-673f-4599-95ab-a55addde9f63', 'STF-BSUTH-003', 'Nurse Amina Lawal', 'NURSING_OFFICER', 'Viral Hemorrhagic Infection Control', 'CURRENT', '2027-10-31', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fa1607d8-673f-4599-95ab-a55addde9f64', 'STF-BSUTH-004', 'Nurse Moses Iorbee', 'NURSING_OFFICER', 'ICU Critical Care Nurse', 'CURRENT', '2027-10-31', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fa1607d8-673f-4599-95ab-a55addde9f65', 'STF-BSUTH-005', 'Iorwuese Kaan', 'WARD_MANAGER', 'Isolation Ward Administrator', 'CURRENT', '2029-06-30', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fa1607d8-673f-4599-95ab-a55addde9f66', 'STF-BSUTH-006', 'Moses Tyokyaa', 'CLEANER', 'Biosafety & Decontamination Lead', 'CURRENT', '2028-05-31', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fa1607d8-673f-4599-95ab-a55addde9f67', 'STF-BSUTH-007', 'Victoria Chia', 'RESOURCE_MANAGER', 'Medical Supply Chain Officer', 'CURRENT', '2028-08-31', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fa1607d8-673f-4599-95ab-a55addde9f68', 'STF-BSUTH-008', 'Engr. Daniel Orbunde', 'EQUIPMENT_OFFICER', 'Biomedical Engineering Specialist', 'CURRENT', '2029-01-31', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 1.00, 'ACTIVE', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (staff_number) DO NOTHING;

-- 14. Operational Shifts & Shift Scheduling
INSERT INTO staff_shifts (id, shift_name, shift_date, start_time, end_time, ward_id, min_required_staff, max_staff, status, created_at, updated_at, created_by)
VALUES
    ('fb1607d8-673f-4599-95ab-a55addde9f01', 'Morning Clinical Shift - Ward A', CURRENT_DATE, '07:00:00', '15:00:00', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 3, 6, 'SCHEDULED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fb1607d8-673f-4599-95ab-a55addde9f02', 'Afternoon/Evening Shift - Ward A', CURRENT_DATE, '15:00:00', '23:00:00', 'b9679d90-2798-4a3b-8cbe-579b9a559900', 3, 6, 'SCHEDULED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('fb1607d8-673f-4599-95ab-a55addde9f03', 'Night Emergency Shift - ICU Ward B', CURRENT_DATE, '23:00:00', '07:00:00', 'c1679d90-2798-4a3b-8cbe-579b9a559901', 2, 4, 'SCHEDULED', NOW(), NOW(), 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11')
ON CONFLICT (id) DO NOTHING;

INSERT INTO shift_assignments (id, staff_id, shift_id, status, assigned_by, created_at)
VALUES
    ('fc1607d8-673f-4599-95ab-a55addde9f01', 'fa1607d8-673f-4599-95ab-a55addde9f61', 'fb1607d8-673f-4599-95ab-a55addde9f01', 'CONFIRMED', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW()),
    ('fc1607d8-673f-4599-95ab-a55addde9f02', 'fa1607d8-673f-4599-95ab-a55addde9f63', 'fb1607d8-673f-4599-95ab-a55addde9f01', 'CONFIRMED', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW()),
    ('fc1607d8-673f-4599-95ab-a55addde9f03', 'fa1607d8-673f-4599-95ab-a55addde9f62', 'fb1607d8-673f-4599-95ab-a55addde9f03', 'CONFIRMED', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW()),
    ('fc1607d8-673f-4599-95ab-a55addde9f04', 'fa1607d8-673f-4599-95ab-a55addde9f64', 'fb1607d8-673f-4599-95ab-a55addde9f03', 'CONFIRMED', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', NOW())
ON CONFLICT (staff_id, shift_id) DO NOTHING;
