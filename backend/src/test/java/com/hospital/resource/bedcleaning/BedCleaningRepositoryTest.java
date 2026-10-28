package com.hospital.resource.bedcleaning;

import com.hospital.resource.bedcleaning.entity.BedCleaning;
import com.hospital.resource.bedcleaning.repository.BedCleaningRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BedCleaningRepositoryTest {

    @Autowired
    private BedCleaningRepository cleaningRepository;

    private BedCleaning testCleaning;

    @BeforeEach
    void setUp() {
        testCleaning = BedCleaning.builder()
                .bedId(UUID.randomUUID())
                .admissionId(UUID.randomUUID())
                .status("PENDING")
                .createdAt(java.time.Instant.now())
                .updatedAt(java.time.Instant.now())
                .build();
        cleaningRepository.save(testCleaning);
    }

    @Test
    void findByStatus_Success() {
        List<BedCleaning> found = cleaningRepository.findByStatus("PENDING");
        assertThat(found).hasSize(1);
    }

    @Test
    void findByBedIdAndStatus_Success() {
        List<BedCleaning> found = cleaningRepository.findByBedIdAndStatus(testCleaning.getBedId(), "PENDING");
        assertThat(found).hasSize(1);
    }

    @Test
    void findByAdmissionIdAndStatus_Success() {
        Optional<BedCleaning> found = cleaningRepository.findByAdmissionIdAndStatus(
                testCleaning.getAdmissionId(), "PENDING");
        assertThat(found).isPresent();
    }

    @Test
    void searchCleaningTasks_WithFilters() {
        Page<BedCleaning> page = cleaningRepository.searchCleaningTasks(
                testCleaning.getBedId(), "PENDING", null, null, null,
                PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void countByStatus_Success() {
        long count = cleaningRepository.countByStatus("PENDING");
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByStatusGrouped_Success() {
        List<Object[]> counts = cleaningRepository.countByStatusGrouped();
        assertThat(counts).isNotEmpty();
    }
}
