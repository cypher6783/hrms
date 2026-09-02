package com.hospital.resource.recommendation.service;

import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.recommendation.dto.*;
import com.hospital.resource.recommendation.entity.*;
import com.hospital.resource.recommendation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationApplicationService {

    private final AllocationRecommendationRepository recommendationRepository;
    private final RecommendationItemRepository itemRepository;
    private final RecommendationDecisionRepository decisionRepository;

    @Transactional(readOnly = true)
    public RecommendationResponse getRecommendation(UUID id) {
        AllocationRecommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", id.toString()));
        List<RecommendationItem> items = itemRepository.findByRecommendationId(id);
        return toResponse(recommendation, items);
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponse> getRecommendationsByAdmission(UUID admissionId) {
        List<AllocationRecommendation> recommendations = recommendationRepository.findByAdmissionIdAndStatus(admissionId, "PENDING");
        return recommendations.stream()
                .map(r -> {
                    List<RecommendationItem> items = itemRepository.findByRecommendationId(r.getId());
                    return toResponse(r, items);
                })
                .toList();
    }

    @Transactional
    public RecommendationDecisionResponse makeDecision(UUID itemId, RecommendationDecisionRequest request, UUID userId) {
        RecommendationItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation item", itemId.toString()));

        if (!"PENDING".equals(item.getStatus())) {
            throw new ValidationException("Item is not in PENDING status");
        }

        item.setStatus("ACCEPTED".equals(request.decisionType()) ? "ACCEPTED" : "OVERRIDDEN");
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
        return new DecisionResponse(decision.getId(), decision.getRecommendationItemId(),
                decision.getDecisionType(), decision.getDecidedBy(), decision.getDecidedAt());
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

    private record DecisionResponse(UUID id, UUID recommendationItemId, String decisionType, UUID decidedBy, Instant decidedAt) implements RecommendationDecisionResponse {}
}
