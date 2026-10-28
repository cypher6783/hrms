package com.hospital.resource.report.controller;

import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.report.service.ReportApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportApplicationService reportService;

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboardSummary() {
        return ApiResponse.success(reportService.generateDashboardSummary());
    }
}
