package com.hospital.resource.resource.mapper;

import com.hospital.resource.resource.dto.InventoryTransactionResponse;
import com.hospital.resource.resource.entity.InventoryTransaction;

import java.util.List;

public interface InventoryMapper {

    InventoryTransactionResponse toTransactionResponse(InventoryTransaction transaction);

    List<InventoryTransactionResponse> toTransactionResponseList(List<InventoryTransaction> transactions);
}
