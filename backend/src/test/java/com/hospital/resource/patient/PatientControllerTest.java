package com.hospital.resource.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.patient.controller.PatientController;
import com.hospital.resource.patient.dto.PatientRequest;
import com.hospital.resource.patient.dto.PatientResponse;
import com.hospital.resource.patient.service.PatientApplicationService;
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
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientApplicationService patientService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private PatientResponse testResponse;
    private UUID patientId;

    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        testResponse = new PatientResponse(
                patientId, "PT-20260712-0001", "John Doe",
                LocalDate.of(1990, 5, 15), "MALE", "08012345678",
                "123 Test Street", "Jane Doe", "08087654321",
                true, Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createPatient_Success() throws Exception {
        PatientRequest request = new PatientRequest(
                "John Doe", LocalDate.of(1990, 5, 15), "MALE",
                "08012345678", "123 Test Street", "Jane Doe", "08087654321"
        );
        when(patientService.createPatient(any(PatientRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("John Doe"));
    }

    @Test
    @WithMockUser
    void getPatient_Success() throws Exception {
        when(patientService.getPatient(patientId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(patientId.toString()));
    }

    @Test
    @WithMockUser
    void getPatientByNumber_Success() throws Exception {
        when(patientService.getPatientByNumber("PT-20260712-0001")).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/patients/number/{patientNumber}", "PT-20260712-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.patientNumber").value("PT-20260712-0001"));
    }

    @Test
    @WithMockUser
    void searchPatients_Success() throws Exception {
        when(patientService.searchPatients(any(), any(int.class), any(int.class)))
                .thenReturn(new PagedResponse<>(java.util.List.of(testResponse), 0, 20, 1, 1, true));

        mockMvc.perform(get("/api/v1/patients")
                        .param("search", "John")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void updatePatient_Success() throws Exception {
        PatientRequest request = new PatientRequest(
                "John Updated", LocalDate.of(1990, 5, 15), "MALE",
                "08012345678", "456 Updated Street", "Jane Doe", "08087654321"
        );
        when(patientService.updatePatient(eq(patientId), any(PatientRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(put("/api/v1/patients/{id}", patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void deactivatePatient_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/patients/{id}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
