package com.hospital.resource.ward;

import com.hospital.resource.ward.entity.Ward;
import com.hospital.resource.ward.repository.WardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WardRepositoryTest {

    @Autowired
    private WardRepository wardRepository;

    private Ward testWard;

    @BeforeEach
    void setUp() {
        testWard = Ward.builder()
                .name("Cardiology Ward")
                .wardType("GENERAL")
                .maxBedCapacity(20)
                .isolationLevel("NONE")
                .equipmentZone("Zone-A")
                .status("ACTIVE")
                .createdBy(UUID.randomUUID())
                .build();
        wardRepository.save(testWard);
    }

    @Test
    void findByName_Success() {
        Optional<Ward> found = wardRepository.findByName("Cardiology Ward");
        assertThat(found).isPresent();
        assertThat(found.get().getWardType()).isEqualTo("GENERAL");
    }

    @Test
    void findByName_NotFound() {
        Optional<Ward> found = wardRepository.findByName("Nonexistent Ward");
        assertThat(found).isEmpty();
    }

    @Test
    void existsByName_True() {
        assertThat(wardRepository.existsByName("Cardiology Ward")).isTrue();
    }

    @Test
    void existsByName_False() {
        assertThat(wardRepository.existsByName("Nonexistent Ward")).isFalse();
    }

    @Test
    void findByStatus_Success() {
        List<Ward> result = wardRepository.findByStatus("ACTIVE");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void findByStatus_Empty() {
        List<Ward> result = wardRepository.findByStatus("INACTIVE");
        assertThat(result).isEmpty();
    }

    @Test
    void searchWards_ByName() {
        List<Ward> result = wardRepository.searchWards("Cardiology");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cardiology Ward");
    }

    @Test
    void searchWards_ByType() {
        List<Ward> result = wardRepository.searchWards("GENERAL");
        assertThat(result).hasSize(1);
    }

    @Test
    void searchWards_NoMatch() {
        List<Ward> result = wardRepository.searchWards("Nonexistent");
        assertThat(result).isEmpty();
    }

    @Test
    void searchWards_CaseInsensitive() {
        List<Ward> result = wardRepository.searchWards("cardiology");
        assertThat(result).hasSize(1);
    }

    @Test
    void saveWard_SetsId() {
        Ward newWard = Ward.builder()
                .name("Pediatrics Ward")
                .wardType("GENERAL")
                .maxBedCapacity(15)
                .isolationLevel("NONE")
                .status("ACTIVE")
                .build();
        Ward saved = wardRepository.save(newWard);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void saveWard_DefaultStatus() {
        Ward newWard = Ward.builder()
                .name("ICU")
                .wardType("INTENSIVE")
                .maxBedCapacity(10)
                .isolationLevel("HIGH")
                .build();
        Ward saved = wardRepository.save(newWard);
        assertThat(saved.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void saveWard_DefaultIsolationLevel() {
        Ward newWard = Ward.builder()
                .name("General Ward")
                .wardType("GENERAL")
                .maxBedCapacity(30)
                .status("ACTIVE")
                .build();
        Ward saved = wardRepository.save(newWard);
        assertThat(saved.getIsolationLevel()).isEqualTo("NONE");
    }
}
