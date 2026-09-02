package com.hospital.resource.resource;

import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.repository.ResourceRepository;
import com.hospital.resource.resource.repository.ResourceSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ResourceRepositoryTest {

    @Autowired
    private ResourceRepository resourceRepository;

    private Resource testResource;

    @BeforeEach
    void setUp() {
        testResource = Resource.builder()
                .name("Paracetamol 500mg")
                .category("MEDICATION")
                .unitOfMeasure("TABLET")
                .minimumThreshold(100)
                .reorderPoint(200)
                .criticalityLevel("HIGH")
                .createdBy(UUID.randomUUID())
                .build();
        resourceRepository.save(testResource);
    }

    @Test
    void findByCategory_Success() {
        List<Resource> result = resourceRepository.findByCategory("MEDICATION");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Paracetamol 500mg");
    }

    @Test
    void existsByNameAndCategory_Success() {
        boolean exists = resourceRepository.existsByNameAndCategory("Paracetamol 500mg", "MEDICATION");
        assertThat(exists).isTrue();
    }

    @Test
    void specificationSearch_ByCategoryAndName() {
        var spec = ResourceSpecification.hasCategory("MEDICATION")
                .and(ResourceSpecification.nameContains("Paracetamol"));

        Page<Resource> page = resourceRepository.findAll(spec, PageRequest.of(0, 10));
        assertThat(page.getContent()).hasSize(1);
    }
}
