package com.hospital.resource.ward.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.ward.dto.WardRequest;
import com.hospital.resource.ward.dto.WardResponse;
import com.hospital.resource.ward.dto.WardStatusResponse;
import com.hospital.resource.ward.service.WardApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wards")
@RequiredArgsConstructor
public class WardController {

    private final WardApplicationService wardService;

    @PostMapping
    public ApiResponse<WardResponse> createWard(@Valid @RequestBody WardRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(wardService.createWard(request, userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<WardResponse> getWard(@PathVariable UUID id) {
        return ApiResponse.success(wardService.getWard(id));
    }

    @GetMapping
    public ApiResponse<List<WardResponse>> getAllActiveWards() {
        return ApiResponse.success(wardService.getAllActiveWards());
    }

    @GetMapping("/{id}/status")
    public ApiResponse<WardStatusResponse> getWardStatus(@PathVariable UUID id) {
        return ApiResponse.success(wardService.getWardStatus(id));
    }

    @GetMapping("/search")
    public ApiResponse<PagedResponse<WardResponse>> searchWards(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(wardService.searchWards(search, page, size));
    }

    @PutMapping("/{id}")
    public ApiResponse<WardResponse> updateWard(@PathVariable UUID id, @Valid @RequestBody WardRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(wardService.updateWard(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deactivateWard(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        wardService.deactivateWard(id, userId);
        return ApiResponse.success("Ward deactivated", null);
    }
}
