package com.hospital.resource.resource.mapper;

import com.hospital.resource.resource.dto.InventoryTransactionResponse;
import com.hospital.resource.resource.entity.InventoryTransaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryMapperImpl implements InventoryMapper {

    @Override
    public InventoryTransactionResponse toTransactionResponse(InventoryTransaction transaction) {
        if (transaction == null) return null;
        return new InventoryTransactionResponse(
                transaction.getId(),
                transaction.getResourceInventoryId(),
                transaction.getTransactionType(),
                transaction.getQuantity(),
                transaction.getAdmissionId(),
                transaction.getReferenceDocument(),
                transaction.getNotes(),
                transaction.getPerformedBy(),
                transaction.getTransactionTimestamp(),
                transaction.getCreatedAt()
        );
    }

    @Override
    public List<InventoryTransactionResponse> toTransactionResponseList(List<InventoryTransaction> transactions) {
        if (transactions == null) return List.of();
        return transactions.stream().map(this::toTransactionResponse).toList();
    }
}
