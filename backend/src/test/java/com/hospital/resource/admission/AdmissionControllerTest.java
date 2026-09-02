package com.hospital.resource.admission;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.admission.controller.AdmissionController;
import com.hospital.resource.admission.dto.*;
import com.hospital.resource.admission.service.AdmissionApplicationService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdmissionController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdmissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdmissionApplicationService admissionService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private AdmissionResponse testResponse;
    private UUID admissionId;

    @BeforeEach
    void setUp() {
        admissionId = UUID.randomUUID();
        testResponse = new AdmissionResponse(
                admissionId, "ADM-20260712-0001",
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "ADMITTED", "Test notes", null, null,
                Instant.now(), null, true, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createAdmission_Success() throws Exception {
        AdmissionRequest request = new AdmissionRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "Test admission"
        );
        when(admissionService.createAdmission(any(AdmissionRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/admissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.admissionNumber").value("ADM-20260712-0001"));
    }

    @Test
    @WithMockUser
    void getAdmission_Success() throws Exception {
        when(admissionService.getAdmission(admissionId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/admissions/{id}", admissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(admissionId.toString()));
    }

    @Test
    @WithMockUser
    void searchAdmissions_Success() throws Exception {
        when(admissionService.searchAdmissions(any(AdmissionSearchRequest.class)))
                .thenReturn(new com.hospital.resource.common.dto.PagedResponse<>(
                        java.util.List.of(testResponse), 0, 20, 1, 1, true));

        mockMvc.perform(get("/api/v1/admissions")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void dischargeAdmission_Success() throws Exception {
        DischargeRequest request = new DischargeRequest("RECOVERED", "Discharged");
        when(admissionService.dischargeAdmission(eq(admissionId), any(DischargeRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/api/v1/admissions/{id}/discharge", admissionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
