package com.hospital.resource.equipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.equipment.controller.EquipmentController;
import com.hospital.resource.equipment.dto.*;
import com.hospital.resource.equipment.service.EquipmentApplicationService;
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

@WebMvcTest(EquipmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class EquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EquipmentApplicationService equipmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID equipmentId;
    private EquipmentResponse testResponse;

    @BeforeEach
    void setUp() {
        equipmentId = UUID.randomUUID();
        testResponse = new EquipmentResponse(
                equipmentId, "Infusion Pump X", "INFUSION_PUMP", "SN-INF-111",
                "ICU Ward", "AVAILABLE", null, null, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createEquipment_Success() throws Exception {
        EquipmentRequest request = new EquipmentRequest("Infusion Pump X", "INFUSION_PUMP", "SN-INF-111", "ICU Ward", null);
        when(equipmentService.createEquipment(any(), any())).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.serialNumber").value("SN-INF-111"));
    }

    @Test
    @WithMockUser
    void assignEquipment_Success() throws Exception {
        UUID admissionId = UUID.randomUUID();
        EquipmentAllocationResponse allocationResponse = new EquipmentAllocationResponse(
                UUID.randomUUID(), equipmentId, admissionId, Instant.now(), null, UUID.randomUUID()
        );
        when(equipmentService.assignEquipment(any(), any(), any())).thenReturn(allocationResponse);

        mockMvc.perform(post("/api/v1/equipment/{id}/assign", equipmentId)
                        .param("admissionId", admissionId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
