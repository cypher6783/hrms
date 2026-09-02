package com.hospital.resource.equipment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.equipment.controller.EquipmentMaintenanceController;
import com.hospital.resource.equipment.dto.MaintenanceRequest;
import com.hospital.resource.equipment.dto.MaintenanceResponse;
import com.hospital.resource.equipment.service.EquipmentMaintenanceApplicationService;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipmentMaintenanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class EquipmentMaintenanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EquipmentMaintenanceApplicationService maintenanceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID equipmentId;
    private UUID maintenanceId;

    @BeforeEach
    void setUp() {
        equipmentId = UUID.randomUUID();
        maintenanceId = UUID.randomUUID();
    }

    @Test
    @WithMockUser
    void scheduleMaintenance_Success() throws Exception {
        MaintenanceRequest request = new MaintenanceRequest("PREVENTIVE", LocalDate.now(), "Tech C", "Calibrate sensors", BigDecimal.valueOf(200), LocalDate.now().plusMonths(12));
        MaintenanceResponse response = new MaintenanceResponse(maintenanceId, equipmentId, "PREVENTIVE", "SCHEDULED", LocalDate.now(), null, "Tech C", "Calibrate sensors", BigDecimal.valueOf(200), LocalDate.now().plusMonths(12), Instant.now(), Instant.now());
        when(maintenanceService.scheduleMaintenance(any(), any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/equipment/{equipmentId}/maintenance", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.maintenanceType").value("PREVENTIVE"));
    }

    @Test
    @WithMockUser
    void verifyAndReturnToService_Success() throws Exception {
        MaintenanceResponse response = new MaintenanceResponse(maintenanceId, equipmentId, "PREVENTIVE", "VERIFIED", LocalDate.now(), LocalDate.now(), "Tech C", "Calibrate sensors", BigDecimal.valueOf(200), LocalDate.now().plusMonths(12), Instant.now(), Instant.now());
        when(maintenanceService.verifyAndReturnToService(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/equipment/maintenance/{maintenanceId}/verify", maintenanceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("VERIFIED"));
    }
}
