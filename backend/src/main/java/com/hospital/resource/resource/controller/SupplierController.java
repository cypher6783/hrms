package com.hospital.resource.resource.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.resource.dto.SupplierRequest;
import com.hospital.resource.resource.dto.SupplierResponse;
import com.hospital.resource.resource.service.ResourceApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final ResourceApplicationService resourceService;

    @PostMapping
    public ApiResponse<SupplierResponse> createSupplier(@Valid @RequestBody SupplierRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(resourceService.createSupplier(request, userId));
    }

    @GetMapping
    public ApiResponse<List<SupplierResponse>> getAllSuppliers() {
        return ApiResponse.success(resourceService.getAllSuppliers());
    }
}
