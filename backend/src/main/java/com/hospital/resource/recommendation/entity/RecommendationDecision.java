package com.hospital.resource.recommendation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recommendation_decisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationDecision {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "recommendation_item_id", nullable = false)
    private UUID recommendationItemId;

    @Column(name = "decision_type", nullable = false, length = 20)
    private String decisionType;

    @Column(name = "overridden_entity_type", length = 50)
    private String overriddenEntityType;

    @Column(name = "overridden_entity_id")
    private UUID overriddenEntityId;

    @Column(name = "override_justification", columnDefinition = "TEXT")
    private String overrideJustification;

    @Column(name = "decided_by", nullable = false)
    private UUID decidedBy;

    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
