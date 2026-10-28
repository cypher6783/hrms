package com.hospital.resource.resource.repository;

import com.hospital.resource.resource.entity.Resource;
import org.springframework.data.jpa.domain.Specification;

public class ResourceSpecification {

    public static Specification<Resource> hasCategory(String category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Resource> hasCriticalityLevel(String criticalityLevel) {
        return (root, query, cb) -> criticalityLevel == null ? null : cb.equal(root.get("criticalityLevel"), criticalityLevel);
    }

    public static Specification<Resource> nameContains(String name) {
        return (root, query, cb) -> name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
