package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.ResourceSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceSupplierRepository extends JpaRepository<ResourceSupplier, UUID>, JpaSpecificationExecutor<ResourceSupplier> {
    List<ResourceSupplier> findByIsActiveTrue();
    Optional<ResourceSupplier> findByName(String name);
    boolean existsByName(String name);
}
