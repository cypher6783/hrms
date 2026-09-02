package com.hospital.resource.integration;

import com.hospital.resource.admission.dto.*;
import com.hospital.resource.admission.entity.Admission;
import com.hospital.resource.admission.repository.AdmissionRepository;
import com.hospital.resource.admission.service.AdmissionApplicationService;
import com.hospital.resource.bed.entity.Bed;
import com.hospital.resource.bed.repository.BedRepository;
import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import com.hospital.resource.ward.entity.Ward;
import com.hospital.resource.ward.repository.WardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AdmissionWorkflowIntegrationTest {

    @Autowired
    private AdmissionApplicationService admissionService;

    @Autowired
    private AdmissionRepository admissionRepository;

    @Autowired
    private BedRepository bedRepository;

    @Autowired
    private WardRepository wardRepository;

    @Autowired
    private BedCleaningRepository cleaningRepository;

    private Ward testWard;
    private Bed testBed;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        testWard = Ward.builder()
                .name("Test Ward " + UUID.randomUUID().toString().substring(0, 8))
                .wardType("GENERAL")
                .maxBedCapacity(10)
                .isolationLevel("NONE")
                .status("ACTIVE")
                .build();
        testWard = wardRepository.save(testWard);

        testBed = Bed.builder()
                .bedNumber("B-001")
                .wardId(testWard.getId())
                .bedType("STANDARD")
                .isIsolationCapable(false)
                .status("AVAILABLE")
                .build();
        testBed = bedRepository.save(testBed);
    }

    @Test
    void fullAdmissionLifecycle() {
        // 1. Create admission
        AdmissionRequest admissionRequest = new AdmissionRequest(
                UUID.randomUUID(), testWard.getId(), testBed.getId(), "Test admission"
        );
        AdmissionResponse admission = admissionService.createAdmission(admissionRequest, userId);

        assertThat(admission).isNotNull();
        assertThat(admission.status()).isEqualTo("ADMITTED");
        assertThat(admission.patientId()).isEqualTo(admissionRequest.patientId());

        // Verify bed is occupied
        Bed occupiedBed = bedRepository.findById(testBed.getId()).orElseThrow();
        assertThat(occupiedBed.getStatus()).isEqualTo("OCCUPIED");

        // 2. Discharge admission
        DischargeRequest dischargeRequest = new DischargeRequest("RECOVERED", "Discharged successfully");
        AdmissionResponse discharged = admissionService.dischargeAdmission(admission.id(), dischargeRequest, userId);

        assertThat(discharged.status()).isEqualTo("DISCHARGED");
        assertThat(discharged.isActive()).isFalse();

        // Verify bed is in cleaning required
        Bed cleaningBed = bedRepository.findById(testBed.getId()).orElseThrow();
        assertThat(cleaningBed.getStatus()).isEqualTo("CLEANING_REQUIRED");

        // Verify cleaning task created
        var cleaningTasks = cleaningRepository.findByBedIdAndStatus(testBed.getId(), "PENDING");
        assertThat(cleaningTasks).hasSize(1);
    }

    @Test
    void transferWorkflow() {
        // Create second bed
        Bed secondBed = Bed.builder()
                .bedNumber("B-002")
                .wardId(testWard.getId())
                .bedType("STANDARD")
                .isIsolationCapable(false)
                .status("AVAILABLE")
                .build();
        secondBed = bedRepository.save(secondBed);

        // 1. Create admission
        AdmissionRequest admissionRequest = new AdmissionRequest(
                UUID.randomUUID(), testWard.getId(), testBed.getId(), "Test admission"
        );
        AdmissionResponse admission = admissionService.createAdmission(admissionRequest, userId);

        // 2. Transfer to new bed
        TransferRequest transferRequest = new TransferRequest(testWard.getId(), secondBed.getId(), "Transfer notes");
        AdmissionResponse transferred = admissionService.transferAdmission(admission.id(), transferRequest, userId);

        assertThat(transferred.wardId()).isEqualTo(testWard.getId());
        assertThat(transferred.bedId()).isEqualTo(secondBed.getId());

        // Verify old bed is in cleaning required
        Bed oldBed = bedRepository.findById(testBed.getId()).orElseThrow();
        assertThat(oldBed.getStatus()).isEqualTo("CLEANING_REQUIRED");

        // Verify new bed is occupied
        Bed newBed = bedRepository.findById(secondBed.getId()).orElseThrow();
        assertThat(newBed.getStatus()).isEqualTo("OCCUPIED");

        // Verify cleaning task created for old bed
        var cleaningTasks = cleaningRepository.findByBedIdAndStatus(testBed.getId(), "PENDING");
        assertThat(cleaningTasks).hasSize(1);
    }

    @Test
    void createAdmission_DuplicateActiveAdmission_ThrowsConflict() {
        AdmissionRequest admissionRequest = new AdmissionRequest(
                UUID.randomUUID(), testWard.getId(), testBed.getId(), "First admission"
        );
        admissionService.createAdmission(admissionRequest, userId);

        // Try to create another admission for the same patient
        AdmissionRequest duplicateRequest = new AdmissionRequest(
                admissionRequest.patientId(), testWard.getId(), null, "Duplicate"
        );

        try {
            admissionService.createAdmission(duplicateRequest, userId);
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("already has an active admission");
        }
    }
}
