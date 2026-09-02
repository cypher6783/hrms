package com.hospital.resource.assessment.controller;

import com.hospital.resource.assessment.dto.ClinicalAssessmentRequest;
import com.hospital.resource.assessment.dto.ClinicalAssessmentResponse;
import com.hospital.resource.assessment.service.ClinicalAssessmentApplicationService;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/assessments")
@RequiredArgsConstructor
public class ClinicalAssessmentController {

    private final ClinicalAssessmentApplicationService assessmentService;

    @PostMapping
    public ApiResponse<ClinicalAssessmentResponse> createAssessment(@Valid @RequestBody ClinicalAssessmentRequest request) {
        UUID assessedBy = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(assessmentService.createAssessment(request, assessedBy));
    }

    @GetMapping("/patient/{patientId}")
    public ApiResponse<List<ClinicalAssessmentResponse>> getPatientTimeline(@PathVariable UUID patientId) {
        return ApiResponse.success(assessmentService.getPatientTimeline(patientId));
    }

    @GetMapping("/admission/{admissionId}")
    public ApiResponse<List<ClinicalAssessmentResponse>> getAdmissionTimeline(@PathVariable UUID admissionId) {
        return ApiResponse.success(assessmentService.getAdmissionTimeline(admissionId));
    }

    @GetMapping("/admission/{admissionId}/latest")
    public ApiResponse<ClinicalAssessmentResponse> getLatestByAdmission(@PathVariable UUID admissionId) {
        return ApiResponse.success(assessmentService.getLatestByAdmission(admissionId));
    }
}
