package com.hospital.resource.resource.domain;

import com.hospital.resource.common.exception.ConflictException;
import com.hospital.resource.common.exception.ValidationException;
import com.hospital.resource.resource.entity.Resource;
import com.hospital.resource.resource.entity.ResourceAllocation;
import com.hospital.resource.resource.entity.ResourceReservation;

import java.time.Instant;

public class ResourceDomainService {

    public void validateResourceUniqueness(boolean exists) {
        if (exists) {
            throw new ConflictException("A resource with the same name already exists in this category");
        }
    }

    public void validateReservationAvailability(Integer currentStock, Integer activeAllocations, Integer activeReservations, Integer requestedQuantity) {
        int availableUnreserved = currentStock - activeAllocations - activeReservations;
        if (requestedQuantity > availableUnreserved) {
            throw new ValidationException(String.format("Insufficient unreserved stock for reservation. Current stock: %d, Allocated: %d, Reserved: %d, Available: %d, Requested: %d",
                    currentStock, activeAllocations, activeReservations, availableUnreserved, requestedQuantity));
        }
    }

    public void validateAllocationAvailability(Integer currentStock, Integer activeAllocations, Integer requestedQuantity) {
        int available = currentStock - activeAllocations;
        if (requestedQuantity > available) {
            throw new ValidationException(String.format("Insufficient stock for allocation. Current stock: %d, Active allocations: %d, Available: %d, Requested: %d",
                    currentStock, activeAllocations, available, requestedQuantity));
        }
    }

    public void validateReservationActive(ResourceReservation reservation) {
        if (!"RESERVED".equals(reservation.getStatus())) {
            throw new ValidationException("Reservation is no longer active. Current status: " + reservation.getStatus());
        }
        if (reservation.getExpiresAt() != null && reservation.getExpiresAt().isBefore(Instant.now())) {
            reservation.setStatus("EXPIRED");
            throw new ValidationException("Reservation has expired");
        }
    }

    public void validateAllocationActive(ResourceAllocation allocation) {
        if (allocation.getReleasedAt() != null) {
            throw new ValidationException("Resource allocation has already been released");
        }
    }
}
