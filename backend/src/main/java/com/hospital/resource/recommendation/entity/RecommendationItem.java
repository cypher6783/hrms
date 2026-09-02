package com.hospital.resource.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recommendation_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationItem {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "recommendation_id", nullable = false)
    private UUID recommendationId;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    @Column(name = "recommended_entity_type", nullable = false, length = 50)
    private String recommendedEntityType;

    @Column(name = "recommended_entity_id", nullable = false)
    private UUID recommendedEntityId;

    @Column(nullable = false)
    @Builder.Default
    private Integer rank = 1;

    @Column(name = "confidence_score", nullable = false, precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "scoring_breakdown", columnDefinition = "jsonb")
    private String scoringBreakdown;

    @Column(columnDefinition = "TEXT")
    private String rationale;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
