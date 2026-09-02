package com.hospital.resource.staff.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "staff_number", nullable = false, unique = true, length = 20)
    private String staffNumber;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(length = 50)
    private String specialization;

    @Column(name = "certification_status", nullable = false, length = 20)
    @Builder.Default
    private String certificationStatus = "CURRENT";

    @Column(name = "certification_expiry")
    private LocalDate certificationExpiry;

    @Column(name = "ward_id")
    private UUID wardId;

    @Column(name = "max_workload_threshold", nullable = false)
    @Builder.Default
    private BigDecimal maxWorkloadThreshold = BigDecimal.ONE;

    @Column(name = "availability_status", nullable = false, length = 20)
    @Builder.Default
    private String availabilityStatus = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getStaffNumber() { return staffNumber; }
    public void setStaffNumber(String staffNumber) { this.staffNumber = staffNumber; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public String getCertificationStatus() { return certificationStatus; }
    public void setCertificationStatus(String certificationStatus) { this.certificationStatus = certificationStatus; }
    public LocalDate getCertificationExpiry() { return certificationExpiry; }
    public void setCertificationExpiry(LocalDate certificationExpiry) { this.certificationExpiry = certificationExpiry; }
    public UUID getWardId() { return wardId; }
    public void setWardId(UUID wardId) { this.wardId = wardId; }
    public BigDecimal getMaxWorkloadThreshold() { return maxWorkloadThreshold; }
    public void setMaxWorkloadThreshold(BigDecimal maxWorkloadThreshold) { this.maxWorkloadThreshold = maxWorkloadThreshold; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }

    public boolean isAvailable() {
        return "ACTIVE".equals(availabilityStatus);
    }

    public boolean isCertificationCurrent() {
        return certificationExpiry == null || !LocalDate.now().isAfter(certificationExpiry);
    }
}
