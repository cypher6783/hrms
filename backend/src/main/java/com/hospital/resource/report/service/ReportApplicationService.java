package com.hospital.resource.report.service;

import com.hospital.resource.admission.service.AdmissionApplicationService;
import com.hospital.resource.bed.service.BedApplicationService;
import com.hospital.resource.patient.service.PatientApplicationService;
import com.hospital.resource.staff.service.StaffApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class ReportApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportApplicationService.class);

    private final PatientApplicationService patientService;
    private final AdmissionApplicationService admissionService;
    private final BedApplicationService bedService;
    private final StaffApplicationService staffService;

    public ReportApplicationService(
            PatientApplicationService patientService,
            AdmissionApplicationService admissionService,
            BedApplicationService bedService,
            StaffApplicationService staffService) {
        this.patientService = patientService;
        this.admissionService = admissionService;
        this.bedService = bedService;
        this.staffService = staffService;
    }

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
    }
}
