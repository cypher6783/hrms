package com.hospital.resource.patient.controller;

import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.patient.dto.PatientRequest;
import com.hospital.resource.patient.dto.PatientResponse;
import com.hospital.resource.patient.service.PatientApplicationService;
import com.hospital.resource.auth.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientApplicationService patientService;

    @PostMapping
    public ApiResponse<PatientResponse> createPatient(@Valid @RequestBody PatientRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(patientService.createPatient(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<PatientResponse> getPatient(@PathVariable UUID id) {
        return ApiResponse.success(patientService.getPatient(id));
    }

    @GetMapping("/number/{patientNumber}")
    public ApiResponse<PatientResponse> getPatientByNumber(@PathVariable String patientNumber) {
        return ApiResponse.success(patientService.getPatientByNumber(patientNumber));
    }

    @GetMapping
    public ApiResponse<PagedResponse<PatientResponse>> searchPatients(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(patientService.searchPatients(search, page, size));
    }

    @PutMapping("/{id}")
    public ApiResponse<PatientResponse> updatePatient(@PathVariable UUID id, @Valid @RequestBody PatientRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(patientService.updatePatient(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivatePatient(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        patientService.deactivatePatient(id, userId);
        return ApiResponse.success("Patient deactivated", null);
    }
}
