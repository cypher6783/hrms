package com.hospital.resource.equipment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "equipment_type", nullable = false, length = 50)
    private String equipmentType;

    @Column(name = "serial_number", nullable = false, unique = true, length = 50)
    private String serialNumber;

    @Column(length = 100)
    private String location;

    @Column(nullable = false, length = 30)
    private String status = "AVAILABLE";

    @Column(name = "assigned_admission_id")
    private UUID assignedAdmissionId;

    @Column(name = "assigned_ward_id")
    private UUID assignedWardId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    public Equipment() {}

    public Equipment(UUID id, String name, String equipmentType, String serialNumber, String location, String status, UUID assignedAdmissionId, UUID assignedWardId, Instant createdAt, Instant updatedAt, UUID createdBy, UUID updatedBy) {
        this.id = id;
        this.name = name;
        this.equipmentType = equipmentType;
        this.serialNumber = serialNumber;
        this.location = location;
        this.status = status != null ? status : "AVAILABLE";
        this.assignedAdmissionId = assignedAdmissionId;
        this.assignedWardId = assignedWardId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
    }

    public static EquipmentBuilder builder() {
        return new EquipmentBuilder();
    }

    public static class EquipmentBuilder {
        private UUID id;
        private String name;
        private String equipmentType;
        private String serialNumber;
        private String location;
        private String status = "AVAILABLE";
        private UUID assignedAdmissionId;
        private UUID assignedWardId;
        private Instant createdAt;
        private Instant updatedAt;
        private UUID createdBy;
        private UUID updatedBy;

        public EquipmentBuilder id(UUID id) { this.id = id; return this; }
        public EquipmentBuilder name(String name) { this.name = name; return this; }
        public EquipmentBuilder equipmentType(String equipmentType) { this.equipmentType = equipmentType; return this; }
        public EquipmentBuilder serialNumber(String serialNumber) { this.serialNumber = serialNumber; return this; }
        public EquipmentBuilder location(String location) { this.location = location; return this; }
        public EquipmentBuilder status(String status) { this.status = status; return this; }
        public EquipmentBuilder assignedAdmissionId(UUID assignedAdmissionId) { this.assignedAdmissionId = assignedAdmissionId; return this; }
        public EquipmentBuilder assignedWardId(UUID assignedWardId) { this.assignedWardId = assignedWardId; return this; }
        public EquipmentBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public EquipmentBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public EquipmentBuilder createdBy(UUID createdBy) { this.createdBy = createdBy; return this; }
        public EquipmentBuilder updatedBy(UUID updatedBy) { this.updatedBy = updatedBy; return this; }

        public Equipment build() {
            return new Equipment(id, name, equipmentType, serialNumber, location, status, assignedAdmissionId, assignedWardId, createdAt, updatedAt, createdBy, updatedBy);
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

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEquipmentType() { return equipmentType; }
    public void setEquipmentType(String equipmentType) { this.equipmentType = equipmentType; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getAssignedAdmissionId() { return assignedAdmissionId; }
    public void setAssignedAdmissionId(UUID assignedAdmissionId) { this.assignedAdmissionId = assignedAdmissionId; }

    public UUID getAssignedWardId() { return assignedWardId; }
    public void setAssignedWardId(UUID assignedWardId) { this.assignedWardId = assignedWardId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }

    public UUID getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(UUID updatedBy) { this.updatedBy = updatedBy; }
}
