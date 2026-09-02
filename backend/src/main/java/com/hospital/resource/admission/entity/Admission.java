package com.hospital.resource.admission.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admissions")
public class Admission {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "admission_number", nullable = false, unique = true, length = 20)
    private String admissionNumber;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "ward_id", nullable = false)
    private UUID wardId;

    @Column(name = "bed_id")
    private UUID bedId;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "admission_notes", columnDefinition = "TEXT")
    private String admissionNotes;

    @Column(name = "discharge_outcome", length = 30)
    private String dischargeOutcome;

    @Column(name = "discharge_notes", columnDefinition = "TEXT")
    private String dischargeNotes;

    @Column(name = "admitted_at", nullable = false)
    private Instant admittedAt;

    @Column(name = "discharged_at")
    private Instant dischargedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    public Admission() {}

    public Admission(UUID id, String admissionNumber, UUID patientId, UUID wardId, UUID bedId, String status, String admissionNotes, String dischargeOutcome, String dischargeNotes, Instant admittedAt, Instant dischargedAt, Boolean isActive, Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy) {
        this.id = id;
        this.admissionNumber = admissionNumber;
        this.patientId = patientId;
        this.wardId = wardId;
        this.bedId = bedId;
        this.status = status != null ? status : "PENDING";
        this.admissionNotes = admissionNotes;
        this.dischargeOutcome = dischargeOutcome;
        this.dischargeNotes = dischargeNotes;
        this.admittedAt = admittedAt;
        this.dischargedAt = dischargedAt;
        this.isActive = isActive != null ? isActive : true;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static AdmissionBuilder builder() {
        return new AdmissionBuilder();
    }

    public static class AdmissionBuilder {
        private UUID id;
        private String admissionNumber;
        private UUID patientId;
        private UUID wardId;
        private UUID bedId;
        private String status = "PENDING";
        private String admissionNotes;
        private String dischargeOutcome;
        private String dischargeNotes;
        private Instant admittedAt;
        private Instant dischargedAt;
        private Boolean isActive = true;
        private Instant createdAt;
        private Instant updatedAt;
        private UUID createdBy;
        private UUID updatedBy;

        public AdmissionBuilder id(UUID id) { this.id = id; return this; }
        public AdmissionBuilder admissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; return this; }
        public AdmissionBuilder patientId(UUID patientId) { this.patientId = patientId; return this; }
        public AdmissionBuilder wardId(UUID wardId) { this.wardId = wardId; return this; }
        public AdmissionBuilder bedId(UUID bedId) { this.bedId = bedId; return this; }
        public AdmissionBuilder status(String status) { this.status = status; return this; }
        public AdmissionBuilder admissionNotes(String admissionNotes) { this.admissionNotes = admissionNotes; return this; }
        public AdmissionBuilder dischargeOutcome(String dischargeOutcome) { this.dischargeOutcome = dischargeOutcome; return this; }
        public AdmissionBuilder dischargeNotes(String dischargeNotes) { this.dischargeNotes = dischargeNotes; return this; }
        public AdmissionBuilder admittedAt(Instant admittedAt) { this.admittedAt = admittedAt; return this; }
        public AdmissionBuilder dischargedAt(Instant dischargedAt) { this.dischargedAt = dischargedAt; return this; }
        public AdmissionBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public AdmissionBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public AdmissionBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public AdmissionBuilder createdBy(UUID createdBy) { this.createdBy = createdBy; return this; }
        public AdmissionBuilder updatedBy(UUID updatedBy) { this.updatedBy = updatedBy; return this; }

        public Admission build() {
            return new Admission(id, admissionNumber, patientId, wardId, bedId, status, admissionNotes, dischargeOutcome, dischargeNotes, admittedAt, dischargedAt, isActive, createdAt, updatedAt, createdBy, updatedBy);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public boolean isActive() {
        return "ACTIVE".equals(status) || "ADMITTED".equals(status);
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAdmissionNumber() { return admissionNumber; }
    public void setAdmissionNumber(String admissionNumber) { this.admissionNumber = admissionNumber; }

    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }

    public UUID getWardId() { return wardId; }
    public void setWardId(UUID wardId) { this.wardId = wardId; }

    public UUID getBedId() { return bedId; }
    public void setBedId(UUID bedId) { this.bedId = bedId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdmissionNotes() { return admissionNotes; }
    public void setAdmissionNotes(String admissionNotes) { this.admissionNotes = admissionNotes; }

    public String getDischargeOutcome() { return dischargeOutcome; }
    public void setDischargeOutcome(String dischargeOutcome) { this.dischargeOutcome = dischargeOutcome; }

    public String getDischargeNotes() { return dischargeNotes; }
    public void setDischargeNotes(String dischargeNotes) { this.dischargeNotes = dischargeNotes; }

    public Instant getAdmittedAt() { return admittedAt; }
    public void setAdmittedAt(Instant admittedAt) { this.admittedAt = admittedAt; }

    public Instant getDischargedAt() { return dischargedAt; }
    public void setDischargedAt(Instant dischargedAt) { this.dischargedAt = dischargedAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }
}
