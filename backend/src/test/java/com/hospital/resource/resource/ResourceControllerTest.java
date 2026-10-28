package com.hospital.resource.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.resource.controller.ResourceController;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.service.ResourceApplicationService;
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

@WebMvcTest(ResourceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceApplicationService resourceService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private ResourceResponse testResponse;
    private UUID resourceId;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();
        testResponse = new ResourceResponse(
                resourceId, "IV Cannula 18G", "CONSUMABLE", "PIECE",
                50, 100, "NORMAL", null, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createResource_Success() throws Exception {
        ResourceRequest request = new ResourceRequest("IV Cannula 18G", "CONSUMABLE", "PIECE", 50, 100, "NORMAL", null);
        when(resourceService.createResource(any(), any())).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("IV Cannula 18G"));
    }

    @Test
    @WithMockUser
    void getResource_Success() throws Exception {
        when(resourceService.getResource(resourceId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/resources/{id}", resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(resourceId.toString()));
    }

    @Test
    @WithMockUser
    void reserveResource_Success() throws Exception {
        ResourceReservationRequest request = new ResourceReservationRequest(resourceId, UUID.randomUUID(), 10, 30);
        ResourceReservationResponse reservationResponse = new ResourceReservationResponse(
                UUID.randomUUID(), resourceId, request.admissionId(), 10, "RESERVED", Instant.now(), Instant.now().plusSeconds(1800), UUID.randomUUID()
        );
        when(resourceService.reserveResource(any(), any())).thenReturn(reservationResponse);

        mockMvc.perform(post("/api/v1/resources/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quantity").value(10));
    }
}
