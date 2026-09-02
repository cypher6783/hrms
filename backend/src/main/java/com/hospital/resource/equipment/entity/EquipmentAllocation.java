package com.hospital.resource.equipment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "equipment_allocations")
public class EquipmentAllocation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "equipment_id", nullable = false)
    private UUID equipmentId;

    @Column(name = "admission_id", nullable = false)
    private UUID admissionId;

    @Column(name = "allocated_at", nullable = false, updatable = false)
    private Instant allocatedAt;

    @Column(name = "released_at")
    private Instant releasedAt;

    @Column(name = "allocated_by", nullable = false, updatable = false)
    private UUID allocatedBy;

    public EquipmentAllocation() {}

    public EquipmentAllocation(UUID id, UUID equipmentId, UUID admissionId, Instant allocatedAt, Instant releasedAt, UUID allocatedBy) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.admissionId = admissionId;
        this.allocatedAt = allocatedAt;
        this.releasedAt = releasedAt;
        this.allocatedBy = allocatedBy;
    }

    public static EquipmentAllocationBuilder builder() {
        return new EquipmentAllocationBuilder();
    }

    public static class EquipmentAllocationBuilder {
        private UUID id;
        private UUID equipmentId;
        private UUID admissionId;
        private Instant allocatedAt;
        private Instant releasedAt;
        private UUID allocatedBy;

        public EquipmentAllocationBuilder id(UUID id) { this.id = id; return this; }
        public EquipmentAllocationBuilder equipmentId(UUID equipmentId) { this.equipmentId = equipmentId; return this; }
        public EquipmentAllocationBuilder admissionId(UUID admissionId) { this.admissionId = admissionId; return this; }
        public EquipmentAllocationBuilder allocatedAt(Instant allocatedAt) { this.allocatedAt = allocatedAt; return this; }
        public EquipmentAllocationBuilder releasedAt(Instant releasedAt) { this.releasedAt = releasedAt; return this; }
        public EquipmentAllocationBuilder allocatedBy(UUID allocatedBy) { this.allocatedBy = allocatedBy; return this; }

        public EquipmentAllocation build() {
            return new EquipmentAllocation(id, equipmentId, admissionId, allocatedAt, releasedAt, allocatedBy);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (allocatedAt == null) {
            allocatedAt = Instant.now();
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getEquipmentId() { return equipmentId; }
    public void setEquipmentId(UUID equipmentId) { this.equipmentId = equipmentId; }

    public UUID getAdmissionId() { return admissionId; }
    public void setAdmissionId(UUID admissionId) { this.admissionId = admissionId; }

    public Instant getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(Instant allocatedAt) { this.allocatedAt = allocatedAt; }

    public Instant getReleasedAt() { return releasedAt; }
    public void setReleasedAt(Instant releasedAt) { this.releasedAt = releasedAt; }

    public UUID getAllocatedBy() { return allocatedBy; }
    public void setAllocatedBy(UUID allocatedBy) { this.allocatedBy = allocatedBy; }
}
