package com.hospital.resource.resource.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.service.InventoryApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryApplicationService inventoryService;

    @PostMapping("/transaction")
    public ApiResponse<InventoryTransactionResponse> recordTransaction(@Valid @RequestBody InventoryTransactionRequest request) {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(inventoryService.recordTransaction(request, userId));
    }

    @GetMapping("/stock/{resourceId}")
    public ApiResponse<InventoryStockResponse> getStock(@PathVariable UUID resourceId) {
        return ApiResponse.success(inventoryService.getStock(resourceId));
    }

    @GetMapping("/transactions/{inventoryId}")
    public ApiResponse<List<InventoryTransactionResponse>> getTransactionHistory(@PathVariable UUID inventoryId) {
        return ApiResponse.success(inventoryService.getTransactionHistory(inventoryId));
    }
}
