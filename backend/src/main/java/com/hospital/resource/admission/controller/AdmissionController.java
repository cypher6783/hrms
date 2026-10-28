package com.hospital.resource.admission.controller;

import com.hospital.resource.admission.dto.*;
import com.hospital.resource.admission.service.AdmissionApplicationService;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admissions")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionApplicationService admissionService;

    @PostMapping
    public ApiResponse<AdmissionResponse> createAdmission(@Valid @RequestBody AdmissionRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(admissionService.createAdmission(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<AdmissionResponse> getAdmission(@PathVariable UUID id) {
        return ApiResponse.success(admissionService.getAdmission(id));
    }

    @GetMapping("/{id}/summary")
    public ApiResponse<AdmissionSummaryResponse> getAdmissionSummary(@PathVariable UUID id) {
        return ApiResponse.success(admissionService.getAdmissionSummary(id));
    }

    @GetMapping
    public ApiResponse<PagedResponse<AdmissionResponse>> searchAdmissions(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID wardId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        AdmissionSearchRequest request = new AdmissionSearchRequest(
                patientId, wardId, status, null, null, page, size);
        return ApiResponse.success(admissionService.searchAdmissions(request));
    }

    @GetMapping("/patient/{patientId}/active")
    public ApiResponse<AdmissionResponse> getActiveAdmission(@PathVariable UUID patientId) {
        return ApiResponse.success(admissionService.getActiveAdmissionByPatient(patientId));
    }

    @GetMapping("/ward/{wardId}")
    public ApiResponse<List<AdmissionSummaryResponse>> getAdmissionsByWard(@PathVariable UUID wardId) {
        return ApiResponse.success(admissionService.getAdmissionsByWard(wardId));
    }

    @GetMapping("/stats")
    public ApiResponse<AdmissionApplicationService.AdmissionStatsResponse> getAdmissionStats() {
        return ApiResponse.success(admissionService.getAdmissionStats());
    }

    @PutMapping("/{id}/transfer")
    public ApiResponse<AdmissionResponse> transferAdmission(
            @PathVariable UUID id,
            @Valid @RequestBody TransferRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(admissionService.transferAdmission(id, request, userId));
    }

    @PutMapping("/{id}/discharge")
    public ApiResponse<AdmissionResponse> dischargeAdmission(
            @PathVariable UUID id,
            @Valid @RequestBody DischargeRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(admissionService.dischargeAdmission(id, request, userId));
    }
}
