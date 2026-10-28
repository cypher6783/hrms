package com.hospital.resource.admin.controller;

import com.hospital.resource.admin.dto.UserManagementRequest;
import com.hospital.resource.admin.dto.UserManagementResponse;
import com.hospital.resource.admin.service.AdminUserService;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @PostMapping
    public ApiResponse<UserManagementResponse> createUser(@Valid @RequestBody UserManagementRequest request) {
        UUID adminUserId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(adminUserService.createUser(request, adminUserId));
    }

    @GetMapping
    public ApiResponse<List<UserManagementResponse>> getAllUsers() {
        return ApiResponse.success(adminUserService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ApiResponse<UserManagementResponse> getUser(@PathVariable UUID id) {
        return ApiResponse.success(adminUserService.getUser(id));
    }

    @PostMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateUser(@PathVariable UUID id) {
        UUID adminUserId = SecurityUtils.getCurrentUserId();
        adminUserService.deactivateUser(id, adminUserId);
        return ApiResponse.success("User deactivated", null);
    }

    @PostMapping("/{id}/unlock")
    public ApiResponse<Void> unlockUser(@PathVariable UUID id) {
        UUID adminUserId = SecurityUtils.getCurrentUserId();
        adminUserService.unlockUser(id, adminUserId);
        return ApiResponse.success("User unlocked", null);
    }
}
