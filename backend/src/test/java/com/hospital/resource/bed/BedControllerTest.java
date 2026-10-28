package com.hospital.resource.bed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.bed.controller.BedController;
import com.hospital.resource.bed.dto.BedAvailabilityResponse;
import com.hospital.resource.bed.dto.BedRequest;
import com.hospital.resource.bed.dto.BedResponse;
import com.hospital.resource.bed.service.BedApplicationService;
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

@WebMvcTest(BedController.class)
@AutoConfigureMockMvc(addFilters = false)
class BedControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BedApplicationService bedService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private BedResponse testResponse;
    private UUID bedId;
    private UUID wardId;

    @BeforeEach
    void setUp() {
        bedId = UUID.randomUUID();
        wardId = UUID.randomUUID();
        testResponse = new BedResponse(
                bedId, "B-001", wardId, "STANDARD",
                false, "AVAILABLE", null, null, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createBed_Success() throws Exception {
        BedRequest request = new BedRequest("B-001", wardId, "STANDARD", false);
        when(bedService.createBed(any(BedRequest.class), any(UUID.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/beds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bedNumber").value("B-001"));
    }

    @Test
    @WithMockUser
    void getBed_Success() throws Exception {
        when(bedService.getBed(bedId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/beds/{id}", bedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(bedId.toString()));
    }

    @Test
    @WithMockUser
    void getBedsByWard_Success() throws Exception {
        when(bedService.getBedsByWard(wardId)).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/beds/ward/{wardId}", wardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void getAvailableIsolationBeds_Success() throws Exception {
        when(bedService.getAvailableIsolationBeds()).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/beds/available/isolation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void getBedAvailability_Success() throws Exception {
        BedAvailabilityResponse availability = new BedAvailabilityResponse(
                wardId, "Cardiology", 20, 15, 2, 5
        );
        when(bedService.getBedAvailability(wardId)).thenReturn(availability);

        mockMvc.perform(get("/api/v1/beds/availability/{wardId}", wardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalBeds").value(20));
    }

    @Test
    @WithMockUser
    void updateBed_Success() throws Exception {
        BedRequest request = new BedRequest("B-001", wardId, "STANDARD", false);
        when(bedService.updateBed(eq(bedId), any(BedRequest.class), any(UUID.class))).thenReturn(testResponse);

        mockMvc.perform(put("/api/v1/beds/{id}", bedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void updateBedStatus_Success() throws Exception {
        when(bedService.updateBedStatus(bedId, "OCCUPIED")).thenReturn(testResponse);

        mockMvc.perform(put("/api/v1/beds/{id}/status", bedId)
                        .param("status", "OCCUPIED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void filterBeds_Success() throws Exception {
        when(bedService.filterBeds(any(), any(), any(), any())).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/beds/filter")
                        .param("wardId", wardId.toString())
                        .param("bedType", "STANDARD")
                        .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
