package com.hospital.resource.bed.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "beds", uniqueConstraints = @UniqueConstraint(columnNames = {"bed_number", "ward_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bed {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "bed_number", nullable = false, length = 20)
    private String bedNumber;

    @Column(name = "ward_id", nullable = false)
    private UUID wardId;

    @Column(name = "bed_type", nullable = false, length = 40)
    private String bedType;

    @Column(name = "is_isolation_capable", nullable = false)
    @Builder.Default
    private Boolean isIsolationCapable = false;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "AVAILABLE";

    @Column(name = "current_admission_id")
    private UUID currentAdmissionId;

    @Column(name = "last_maintenance_at")
    private Instant lastMaintenanceAt;

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
    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }
    public UUID getWardId() { return wardId; }
    public void setWardId(UUID wardId) { this.wardId = wardId; }
    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }
    public Boolean getIsIsolationCapable() { return isIsolationCapable; }
    public Boolean getIsIsolation() { return isIsolationCapable; }
    public void setIsIsolationCapable(Boolean isIsolationCapable) { this.isIsolationCapable = isIsolationCapable; }
    public void setIsIsolation(Boolean isIsolation) { this.isIsolationCapable = isIsolation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getCurrentAdmissionId() { return currentAdmissionId; }
    public void setCurrentAdmissionId(UUID currentAdmissionId) { this.currentAdmissionId = currentAdmissionId; }
    public Instant getLastMaintenanceAt() { return lastMaintenanceAt; }
    public void setLastMaintenanceAt(Instant lastMaintenanceAt) { this.lastMaintenanceAt = lastMaintenanceAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }
}
