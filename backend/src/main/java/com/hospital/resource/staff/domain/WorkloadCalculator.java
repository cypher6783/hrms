package com.hospital.resource.staff.domain;

import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.staff.dto.StaffWorkloadResponse;
import com.hospital.resource.staff.entity.Staff;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadCalculator {

    private final AdmissionRepository admissionRepository;

    private static final Map<String, BigDecimal> SEVERITY_WEIGHTS = Map.of(
            "CRITICAL", new BigDecimal("1.5"),
            "HIGH", new BigDecimal("1.2"),
            "MODERATE", new BigDecimal("1.0"),
            "LOW", new BigDecimal("0.8")
    );

    private static final BigDecimal ISOLATION_WEIGHT = new BigDecimal("0.3");

    @Transactional(readOnly = true)
    public StaffWorkloadResponse calculateWorkload(Staff staff) {
        long activeAdmissions = admissionRepository.countActiveAdmissionsByWard(staff.getWardId());

        BigDecimal baseWorkload = BigDecimal.valueOf(activeAdmissions);

        BigDecimal severityFactor = calculateSeverityFactor(staff.getWardId());
        BigDecimal workload = baseWorkload.multiply(severityFactor);

        BigDecimal threshold = staff.getMaxWorkloadThreshold();
        BigDecimal percentage = BigDecimal.ZERO;
        if (threshold.compareTo(BigDecimal.ZERO) > 0) {
            percentage = workload.divide(threshold, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        boolean isOverloaded = workload.compareTo(threshold) > 0;

        return new StaffWorkloadResponse(
                staff.getId(),
                staff.getStaffNumber(),
                workload,
                threshold,
                percentage,
                activeAdmissions,
                isOverloaded
        );
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateWorkloadValue(UUID wardId, BigDecimal threshold) {
        long activeAdmissions = admissionRepository.countActiveAdmissionsByWard(wardId);
        BigDecimal baseWorkload = BigDecimal.valueOf(activeAdmissions);
        BigDecimal severityFactor = calculateSeverityFactor(wardId);
        return baseWorkload.multiply(severityFactor);
    }

    @Transactional(readOnly = true)
    public boolean isOverloaded(Staff staff) {
        BigDecimal workload = calculateWorkloadValue(staff.getWardId(), staff.getMaxWorkloadThreshold());
        return workload.compareTo(staff.getMaxWorkloadThreshold()) > 0;
    }

    private BigDecimal calculateSeverityFactor(UUID wardId) {
        var stats = admissionRepository.countByStatusForWard(wardId);
        BigDecimal factor = BigDecimal.ONE;

        for (Object[] row : stats) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            BigDecimal weight = SEVERITY_WEIGHTS.getOrDefault(status, BigDecimal.ONE);
            factor = factor.add(weight.multiply(BigDecimal.valueOf(count)));
        }

        return factor;
    }
}
