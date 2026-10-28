package com.hospital.resource.report.service;

import com.hospital.resource.admission.service.AdmissionApplicationService;
import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.patient.service.PatientApplicationService;
import com.hospital.resource.staff.service.StaffApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportApplicationService {

    private final PatientApplicationService patientService;
    private final AdmissionApplicationService admissionService;
    private final BedApplicationService bedService;
    private final StaffApplicationService staffService;

    @Transactional(readOnly = true)
    public Map<String, Object> generateDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("activePatients", patientService.getActivePatientCount());
        summary.put("activeAdmissions", admissionService.getActiveAdmissionCount());
        summary.put("activeStaff", staffService.getActiveStaffCount());
        return summary;
    }

    @Async("reportExecutor")
    public void generateOccupancyReport(UUID wardId) {
        log.info("Generating occupancy report for ward: {}", wardId);
        // Report generation logic will be implemented
    }
}
