package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, UUID>, JpaSpecificationExecutor<Resource> {
    List<Resource> findByCategory(String category);
    Optional<Resource> findByNameAndCategory(String name, String category);
    boolean existsByNameAndCategory(String name, String category);
}
