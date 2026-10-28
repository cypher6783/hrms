package com.hospital.resource.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.resource.controller.InventoryController;
import com.hospital.resource.resource.dto.InventoryStockResponse;
import com.hospital.resource.resource.dto.InventoryTransactionRequest;
import com.hospital.resource.resource.dto.InventoryTransactionResponse;
import com.hospital.resource.resource.service.InventoryApplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.hospital.resource.auth.security.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InventoryApplicationService inventoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID resourceId;
    private UUID inventoryId;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();
        inventoryId = UUID.randomUUID();
    }

    @Test
    @WithMockUser
    void getStock_Success() throws Exception {
        InventoryStockResponse response = new InventoryStockResponse(resourceId, "Saline Solution", 250, 50, false);
        when(inventoryService.getStock(resourceId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/inventory/stock/{resourceId}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalStock").value(250));
    }

    @Test
    @WithMockUser
    void recordTransaction_Success() throws Exception {
        InventoryTransactionRequest request = new InventoryTransactionRequest(inventoryId, "IN", 100, null, "REF-100", "Delivery");
        InventoryTransactionResponse response = new InventoryTransactionResponse(
                UUID.randomUUID(), inventoryId, "IN", 100, null, "REF-100", "Delivery", UUID.randomUUID(), Instant.now(), Instant.now()
        );
        when(inventoryService.recordTransaction(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/inventory/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantity").value(100));
    }
}
