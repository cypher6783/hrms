package com.hospital.resource.audit.controller;

import com.hospital.resource.audit.dto.AuditLogResponse;
import com.hospital.resource.audit.service.AuditApplicationService;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditApplicationService auditService;

    @GetMapping("/entity/{entityType}/{entityId}")
    public ApiResponse<PagedResponse<AuditLogResponse>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable UUID entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(auditService.getAuditLogsByEntity(entityType, entityId, page, size));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<PagedResponse<AuditLogResponse>> getAuditLogsByUser(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(auditService.getAuditLogsByUser(userId, page, size));
    }
}
