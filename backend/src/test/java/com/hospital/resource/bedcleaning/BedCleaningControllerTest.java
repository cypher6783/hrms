package com.hospital.resource.bedcleaning;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.bedcleaning.controller.BedCleaningController;
import com.hospital.resource.bedcleaning.dto.*;
import com.hospital.resource.bedcleaning.service.BedCleaningApplicationService;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BedCleaningController.class)
@AutoConfigureMockMvc(addFilters = false)
class BedCleaningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BedCleaningApplicationService cleaningService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private CleaningTaskResponse testResponse;
    private UUID cleaningId;

    @BeforeEach
    void setUp() {
        cleaningId = UUID.randomUUID();
        testResponse = new CleaningTaskResponse(
                cleaningId, UUID.randomUUID(), UUID.randomUUID(),
                "PENDING", null, null, null, null, null, null, null, Instant.now()
        );
    }

    @Test
    @WithMockUser
    void getPendingTasks_Success() throws Exception {
        when(cleaningService.getPendingTasks()).thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/bed-cleaning/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void assignTask_Success() throws Exception {
        CleaningAssignmentRequest request = new CleaningAssignmentRequest(UUID.randomUUID());
        when(cleaningService.assignTask(any(UUID.class), any(CleaningAssignmentRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/bed-cleaning/{id}/assign", cleaningId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void startCleaning_Success() throws Exception {
        when(cleaningService.startCleaning(any(UUID.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/bed-cleaning/{id}/start", cleaningId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void completeCleaning_Success() throws Exception {
        CleaningCompletionRequest request = new CleaningCompletionRequest("Done");
        when(cleaningService.completeCleaning(any(UUID.class), any(CleaningCompletionRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/bed-cleaning/{id}/complete", cleaningId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void verifyCleaning_Success() throws Exception {
        when(cleaningService.verifyCleaning(any(UUID.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/bed-cleaning/{id}/verify", cleaningId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
