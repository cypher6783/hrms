package com.hospital.resource.recommendation.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.recommendation.dto.*;
import com.hospital.resource.recommendation.service.RecommendationApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationApplicationService recommendationService;

    @GetMapping("/{id}")
    public ApiResponse<RecommendationResponse> getRecommendation(@PathVariable UUID id) {
        return ApiResponse.success(recommendationService.getRecommendation(id));
    }

    @GetMapping("/admission/{admissionId}")
    public ApiResponse<List<RecommendationResponse>> getRecommendationsByAdmission(@PathVariable UUID admissionId) {
        return ApiResponse.success(recommendationService.getRecommendationsByAdmission(admissionId));
    }

    @PostMapping("/admission/{admissionId}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ADMIN', 'WARD_MANAGER', 'MEDICAL_DOCTOR', 'DOCTOR', 'NURSING_OFFICER', 'NURSE')")
    public ApiResponse<RecommendationResponse> generateRecommendation(@PathVariable UUID admissionId) {
        return ApiResponse.success(recommendationService.generateRecommendation(admissionId));
    }

    @PostMapping("/items/{itemId}/decide")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'ADMIN', 'WARD_MANAGER', 'MEDICAL_DOCTOR', 'DOCTOR')")
    public ApiResponse<RecommendationDecisionResponse> makeDecision(
            @PathVariable UUID itemId,
            @Valid @RequestBody RecommendationDecisionRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(recommendationService.recordDecision(itemId, request, userId));
    }
}
