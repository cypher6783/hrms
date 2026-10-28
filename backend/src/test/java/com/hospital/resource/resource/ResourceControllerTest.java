package com.hospital.resource.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.resource.controller.ResourceController;
import com.hospital.resource.resource.dto.*;
import com.hospital.resource.resource.service.InventoryApplicationService;
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
    private InventoryApplicationService inventoryService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private ResourceResponse testResponse;
    private UUID resourceId;

    @BeforeEach
    void setUp() {
        resourceId = UUID.randomUUID();

        testResponse = new ResourceResponse(
                resourceId,
                "Ribavirin 200mg",
                "MEDICATION",
                "Capsule",
                20,
                50,
                "HIGH",
                null,
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void getResource_Success() throws Exception {
        when(resourceService.getResource(resourceId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/resources/" + resourceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Ribavirin 200mg"))
                .andExpect(jsonPath("$.data.category").value("MEDICATION"));
    }

    @Test
    void getAllResources_Success() throws Exception {
        when(resourceService.getAllResources()).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Ribavirin 200mg"));
    }

    @Test
    @WithMockUser
    void createResource_Success() throws Exception {
        ResourceRequest request = new ResourceRequest(
                "Ribavirin 200mg",
                "MEDICATION",
                "Capsule",
                20,
                50,
                "HIGH",
                null
        );

        when(resourceService.createResource(any(ResourceRequest.class), any())).thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Ribavirin 200mg"));
    }
}
