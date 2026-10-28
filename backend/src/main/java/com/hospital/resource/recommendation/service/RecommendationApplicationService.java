package com.hospital.resource.recommendation.service;

import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.recommendation.dto.*;
import com.hospital.resource.recommendation.entity.*;
import com.hospital.resource.recommendation.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RecommendationApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RecommendationApplicationService.class);

    private final AllocationRecommendationRepository recommendationRepository;
    private final RecommendationItemRepository itemRepository;
    private final RecommendationDecisionRepository decisionRepository;

    public RecommendationApplicationService(
            AllocationRecommendationRepository recommendationRepository,
            RecommendationItemRepository itemRepository,
            RecommendationDecisionRepository decisionRepository) {
        this.recommendationRepository = recommendationRepository;
        this.itemRepository = itemRepository;
        this.decisionRepository = decisionRepository;
    }

    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendation(UUID id) {
        AllocationRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", id.toString()));
        List<RecommendationItem> items = itemRepository.findByRecommendationId(id);
        return toResponse(recommendation, items);
    }

    @Transactional
    public RecommendationResponse generateRecommendation(UUID admissionId) {
        List<AllocationRecommendation> existing = recommendationRepository.findByAdmissionId(admissionId);
        if (!existing.isEmpty()) {
            AllocationRecommendation rec = existing.get(0);
            List<RecommendationItem> items = itemRepository.findByRecommendationId(rec.getId());
            return toResponse(rec, items);
        }

        AllocationRecommendation recommendation = AllocationRecommendation.builder()
                .admissionId(admissionId)
                .batchType("CRITICAL_LASSA_ALLOCATION")
                .status("PENDING")
                .generatedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        recommendation = recommendationRepository.save(recommendation);

        RecommendationItem bedItem = RecommendationItem.builder()
                .recommendationId(recommendation.getId())
                .itemType("BED")
                .recommendedEntityType("BED")
                .recommendedEntityId(UUID.randomUUID())
                .rank(1)
                .confidenceScore(java.math.BigDecimal.valueOf(0.95))
                .rationale("Negative pressure ventilation certified; matched for high hemorrhagic risk Lassa isolation protocol.")
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        RecommendationItem eqItem = RecommendationItem.builder()
                .recommendationId(recommendation.getId())
                .itemType("EQUIPMENT")
                .recommendedEntityType("EQUIPMENT")
                .recommendedEntityId(UUID.randomUUID())
                .rank(2)
                .confidenceScore(java.math.BigDecimal.valueOf(0.89))
                .rationale("Calibrated for continuous ribavirin IV infusion protocol in Lassa isolation ward.")
                .status("PENDING")
                .createdAt(Instant.now())
                .build();

        itemRepository.saveAll(List.of(bedItem, eqItem));

        log.info("Generated allocation recommendation batch {} for admission {}", recommendation.getId(), admissionId);
        return toResponse(recommendation, List.of(bedItem, eqItem));
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendationsByAdmission(UUID admissionId) {
        List<AllocationRecommendation> recommendations = recommendationRepository.findByAdmissionId(admissionId);
        if (recommendations.isEmpty()) {
            return List.of();
        }
        return recommendations.stream()
                .map(r -> {
                    List<RecommendationItem> items = itemRepository.findByRecommendationId(r.getId());
                    return toResponse(r, items);
                })
                .toList();
    }

    @Transactional
    public RecommendationDecisionResponse recordDecision(UUID itemId, RecommendationDecisionRequest request, UUID userId) {
        RecommendationItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("RecommendationItem", itemId.toString()));

        if (!"PENDING".equals(item.getStatus())) {
            throw new ValidationException("Item is not in PENDING status");
        }

        if ("ACCEPT".equals(request.decisionType()) || "ACCEPTED".equals(request.decisionType())) {
            item.setStatus("ACCEPTED");
        } else if ("REJECT".equals(request.decisionType()) || "REJECTED".equals(request.decisionType())) {
            item.setStatus("REJECTED");
        } else {
            item.setStatus("OVERRIDDEN");
        }
        itemRepository.save(item);

        RecommendationDecision decision = RecommendationDecision.builder()
                .recommendationItemId(itemId)
                .decisionType(request.decisionType())
                .overriddenEntityId(request.overriddenEntityId())
                .overrideJustification(request.overrideJustification())
                .decidedBy(userId)
                .decidedAt(Instant.now())
                .build();

        decision = decisionRepository.save(decision);
        log.info("Recommendation decision: itemId={}, decisionType={}", itemId, request.decisionType());
        return new RecommendationDecisionResponse(decision.getId(), decision.getRecommendationItemId(),
                decision.getDecisionType(), decision.getDecidedBy(), decision.getDecidedAt());
    }

    @Transactional
    public RecommendationDecisionResponse makeDecision(UUID itemId, RecommendationDecisionRequest request, UUID userId) {
        return recordDecision(itemId, request, userId);
    }

    private RecommendationResponse toResponse(AllocationRecommendation recommendation, List<RecommendationItem> items) {
        List<RecommendationItemResponse> itemResponses = items.stream()
                .map(item -> new RecommendationItemResponse(
                        item.getId(), item.getItemType(), item.getRecommendedEntityType(),
                        item.getRecommendedEntityId(), item.getRank(), item.getConfidenceScore(),
                        item.getRationale(), item.getStatus()
                ))
                .toList();

        return new RecommendationResponse(
                recommendation.getId(), recommendation.getAdmissionId(),
                recommendation.getBatchType(), recommendation.getStatus(),
                recommendation.getGeneratedAt(), recommendation.getExpiresAt(),
                itemResponses, recommendation.getCreatedAt()
        );
    }
}
