package com.hospital.resource.bed;

import com.hospital.resource.bed.entity.Bed;
import com.hospital.resource.bed.repository.BedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class BedRepositoryTest {

    @Autowired
    private BedRepository bedRepository;

    private Bed testBed;
    private UUID wardId;

    @BeforeEach
    void setUp() {
        wardId = UUID.randomUUID();

        testBed = Bed.builder()
                .bedNumber("B-001")
                .wardId(wardId)
                .bedType("STANDARD")
                .isIsolationCapable(false)
                .status("AVAILABLE")
                .createdBy(UUID.randomUUID())
                .build();
        bedRepository.save(testBed);
    }

    @Test
    void countByWardIdAndStatus_Available() {
        long count = bedRepository.countByWardIdAndStatus(wardId, "AVAILABLE");
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countByWardIdAndStatus_Occupied() {
        long count = bedRepository.countByWardIdAndStatus(wardId, "OCCUPIED");
        assertThat(count).isEqualTo(0);
    }

    @Test
    void countAvailableByWardId_Success() {
        long count = bedRepository.countAvailableByWardId(wardId);
        assertThat(count).isEqualTo(1);
    }

    @Test
    void countOccupiedByWardId_Success() {
        long count = bedRepository.countOccupiedByWardId(wardId);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void findByWardId_Success() {
        List<Bed> result = bedRepository.findByWardId(wardId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBedNumber()).isEqualTo("B-001");
    }

    @Test
    void findByWardId_Empty() {
        List<Bed> result = bedRepository.findByWardId(UUID.randomUUID());
        assertThat(result).isEmpty();
    }

    @Test
    void findByStatusAndIsIsolationCapable_AvailableNonIsolation() {
        List<Bed> result = bedRepository.findByStatusAndIsIsolationCapable("AVAILABLE", false);
        assertThat(result).hasSize(1);
    }

    @Test
    void findByStatusAndIsIsolationCapable_AvailableIsolation() {
        List<Bed> result = bedRepository.findByStatusAndIsIsolationCapable("AVAILABLE", true);
        assertThat(result).isEmpty();
    }

    @Test
    void findBedsWithFilters_ByWardId() {
        List<Bed> result = bedRepository.findBedsWithFilters(wardId, null, null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findBedsWithFilters_ByStatus() {
        List<Bed> result = bedRepository.findBedsWithFilters(null, null, "AVAILABLE", null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findBedsWithFilters_ByBedType() {
        List<Bed> result = bedRepository.findBedsWithFilters(null, "STANDARD", null, null);
        assertThat(result).hasSize(1);
    }

    @Test
    void findBedsWithFilters_ByIsolation() {
        List<Bed> result = bedRepository.findBedsWithFilters(null, null, null, false);
        assertThat(result).hasSize(1);
    }

    @Test
    void findBedsWithFilters_AllParams() {
        List<Bed> result = bedRepository.findBedsWithFilters(wardId, "STANDARD", "AVAILABLE", false);
        assertThat(result).hasSize(1);
    }

    @Test
    void findBedsWithFilters_NoMatch() {
        List<Bed> result = bedRepository.findBedsWithFilters(wardId, "STANDARD", "OCCUPIED", null);
        assertThat(result).isEmpty();
    }

    @Test
    void saveBed_SetsId() {
        Bed newBed = Bed.builder()
                .bedNumber("B-002")
                .wardId(wardId)
                .bedType("STANDARD")
                .isIsolationCapable(false)
                .status("AVAILABLE")
                .build();
        Bed saved = bedRepository.save(newBed);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void saveBed_DefaultStatus() {
        Bed newBed = Bed.builder()
                .bedNumber("B-003")
                .wardId(wardId)
                .bedType("ICU")
                .isIsolationCapable(true)
                .build();
        Bed saved = bedRepository.save(newBed);
        assertThat(saved.getStatus()).isEqualTo("AVAILABLE");
    }
}
