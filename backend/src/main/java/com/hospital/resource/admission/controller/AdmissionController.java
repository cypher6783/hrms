package com.hospital.resource.admission.controller;

import com.hospital.resource.admission.dto.*;
import com.hospital.resource.admission.service.AdmissionApplicationService;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/patient/{patientId}/active")
    public ApiResponse<AdmissionResponse> getActiveAdmission(@PathVariable UUID patientId) {
        return ApiResponse.success(admissionService.getActiveAdmissionByPatient(patientId));
    }

    @PostMapping("/{id}/transfer")
    public ApiResponse<AdmissionResponse> transferAdmission(
            @PathVariable UUID id,
            @RequestParam UUID newWardId,
            @RequestParam(required = false) UUID newBedId) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(admissionService.transferAdmission(id, newWardId, newBedId, userId));
    }

    @PostMapping("/{id}/discharge")
    public ApiResponse<AdmissionResponse> dischargeAdmission(
            @PathVariable UUID id,
            @Valid @RequestBody DischargeRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(admissionService.dischargeAdmission(id, request, userId));
    }
}
