package com.hospital.resource.assessment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.assessment.controller.ClinicalAssessmentController;
import com.hospital.resource.assessment.dto.ClinicalAssessmentRequest;
import com.hospital.resource.assessment.dto.ClinicalAssessmentResponse;
import com.hospital.resource.assessment.service.ClinicalAssessmentApplicationService;
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

@WebMvcTest(ClinicalAssessmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClinicalAssessmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClinicalAssessmentApplicationService assessmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private ClinicalAssessmentResponse testResponse;
    private UUID assessmentId;

    @BeforeEach
    void setUp() {
        assessmentId = UUID.randomUUID();
        testResponse = new ClinicalAssessmentResponse(
                assessmentId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "HIGH", "EMERGENCY", "NEGATIVE", "Test notes",
                false, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createAssessment_Success() throws Exception {
        ClinicalAssessmentRequest request = new ClinicalAssessmentRequest(
                UUID.randomUUID(), UUID.randomUUID(),
                "HIGH", "EMERGENCY", "NEGATIVE", "Test notes"
        );
        when(assessmentService.createAssessment(any(ClinicalAssessmentRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/assessments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.severityLevel").value("HIGH"));
    }

    @Test
    @WithMockUser
    void getPatientTimeline_Success() throws Exception {
        when(assessmentService.getPatientTimeline(any(UUID.class)))
                .thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/assessments/patient/{patientId}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser
    void getAdmissionTimeline_Success() throws Exception {
        when(assessmentService.getAdmissionTimeline(any(UUID.class)))
                .thenReturn(List.of(testResponse));

        mockMvc.perform(get("/api/v1/assessments/admission/{admissionId}", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void getLatestByAdmission_Success() throws Exception {
        when(assessmentService.getLatestByAdmission(any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/assessments/admission/{admissionId}/latest", UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.severityLevel").value("HIGH"));
    }
}
