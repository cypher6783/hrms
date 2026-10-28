package com.hospital.resource.ward;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.ward.controller.WardController;
import com.hospital.resource.ward.dto.WardRequest;
import com.hospital.resource.ward.dto.WardResponse;
import com.hospital.resource.ward.dto.WardStatusResponse;
import com.hospital.resource.ward.service.WardApplicationService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WardController.class)
@AutoConfigureMockMvc(addFilters = false)
class WardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WardApplicationService wardService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private WardResponse testResponse;
    private UUID wardId;

    @BeforeEach
    void setUp() {
        wardId = UUID.randomUUID();
        testResponse = new WardResponse(
                wardId, "Cardiology Ward", "GENERAL", 20,
                "NONE", "Zone-A", "ACTIVE", Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createWard_Success() throws Exception {
        WardRequest request = new WardRequest("Cardiology Ward", "GENERAL", 20, "NONE", "Zone-A");
        when(wardService.createWard(any(WardRequest.class), any(UUID.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/wards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Cardiology Ward"));
    }

    @Test
    @WithMockUser
    void getWard_Success() throws Exception {
        when(wardService.getWard(wardId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/wards/{id}", wardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(wardId.toString()));
    }

    @Test
    @WithMockUser
    void updateWard_Success() throws Exception {
        WardRequest request = new WardRequest("Cardiology Ward Updated", "GENERAL", 25, "NONE", "Zone-A");
        when(wardService.updateWard(eq(wardId), any(WardRequest.class), any(UUID.class))).thenReturn(testResponse);

        mockMvc.perform(put("/api/v1/wards/{id}", wardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void deactivateWard_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/wards/{id}", wardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void getWardStatus_Success() throws Exception {
        WardStatusResponse statusResponse = new WardStatusResponse(
                wardId, "Cardiology Ward", 20, 15, 5, 0, 25.0
        );
        when(wardService.getWardStatus(wardId)).thenReturn(statusResponse);

        mockMvc.perform(get("/api/v1/wards/{id}/status", wardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalBeds").value(20));
    }
}
