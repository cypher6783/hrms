package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.ResourceInventory;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class InventorySpecification {

    public static Specification<ResourceInventory> hasResourceId(UUID resourceId) {
        return (root, query, cb) -> resourceId == null ? null : cb.equal(root.get("resourceId"), resourceId);
    }

    public static Specification<ResourceInventory> hasLocation(String location) {
        return (root, query, cb) -> location == null ? null : cb.equal(root.get("location"), location);
    }

    public static Specification<ResourceInventory> isInStock() {
        return (root, query, cb) -> cb.greaterThan(root.get("currentStock"), 0);
    }
}
