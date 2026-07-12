package com.hospital.resource.resource.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransaction {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "resource_inventory_id", nullable = false)
    private UUID resourceInventoryId;

    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "admission_id")
    private UUID admissionId;

    @Column(name = "reference_document", length = 100)
    private String referenceDocument;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "performed_by", nullable = false)
    private UUID performedBy;

    @Column(name = "transaction_timestamp", nullable = false)
    private Instant transactionTimestamp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
