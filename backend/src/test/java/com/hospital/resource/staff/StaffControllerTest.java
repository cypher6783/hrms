package com.hospital.resource.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.staff.controller.StaffController;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.service.StaffApplicationService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StaffController.class)
@AutoConfigureMockMvc(addFilters = false)
class StaffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StaffApplicationService staffService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private StaffResponse testResponse;
    private UUID staffId;

    @BeforeEach
    void setUp() {
        staffId = UUID.randomUUID();
        testResponse = new StaffResponse(
                staffId, "STF-TEST0001", "Dr. John Smith",
                "DOCTOR", "CARDIOLOGY", "CURRENT", null,
                UUID.randomUUID(), BigDecimal.valueOf(10),
                "ACTIVE", Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createStaff_Success() throws Exception {
        StaffRequest request = new StaffRequest(
                "Dr. John Smith", "DOCTOR", "CARDIOLOGY",
                "CURRENT", null, UUID.randomUUID(),
                BigDecimal.valueOf(10), "ACTIVE"
        );
        when(staffService.createStaff(any(StaffRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Dr. John Smith"));
    }

    @Test
    @WithMockUser
    void getStaff_Success() throws Exception {
        when(staffService.getStaff(staffId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/staff/{id}", staffId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(staffId.toString()));
    }

    @Test
    @WithMockUser
    void searchStaff_Success() throws Exception {
        when(staffService.searchStaff(any(StaffSearchRequest.class)))
                .thenReturn(new PagedResponse<>(List.of(testResponse), 0, 20, 1, 1, true));

        mockMvc.perform(get("/api/v1/staff")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser
    void getStaffWorkload_Success() throws Exception {
        StaffWorkloadResponse workloadResponse = new StaffWorkloadResponse(
                staffId, "STF-TEST0001",
                BigDecimal.valueOf(5), BigDecimal.valueOf(10),
                BigDecimal.valueOf(50), 3, false
        );
        when(staffService.getStaffWorkload(staffId)).thenReturn(workloadResponse);

        mockMvc.perform(get("/api/v1/staff/{id}/workload", staffId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isOverloaded").value(false));
    }
}
