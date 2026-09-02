package com.hospital.resource.staff;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.staff.controller.ShiftController;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.service.ShiftApplicationService;
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
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShiftController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShiftApplicationService shiftService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private ShiftResponse testResponse;
    private UUID shiftId;

    @BeforeEach
    void setUp() {
        shiftId = UUID.randomUUID();
        testResponse = new ShiftResponse(
                shiftId, "Morning Shift", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(16, 0),
                UUID.randomUUID(), 3, 10, "SCHEDULED",
                Instant.now(), Instant.now()
        );
    }

    @Test
    @WithMockUser
    void createShift_Success() throws Exception {
        ShiftRequest request = new ShiftRequest(
                "Morning Shift", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(16, 0),
                UUID.randomUUID(), 3, 10
        );
        when(shiftService.createShift(any(ShiftRequest.class), any(UUID.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/v1/shifts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.shiftName").value("Morning Shift"));
    }

    @Test
    @WithMockUser
    void getShift_Success() throws Exception {
        when(shiftService.getShift(shiftId)).thenReturn(testResponse);

        mockMvc.perform(get("/api/v1/shifts/{id}", shiftId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(shiftId.toString()));
    }

    @Test
    @WithMockUser
    void assignStaff_Success() throws Exception {
        ShiftAssignmentRequest request = new ShiftAssignmentRequest(UUID.randomUUID(), shiftId);
        ShiftAssignmentResponse assignmentResponse = new ShiftAssignmentResponse(
                UUID.randomUUID(), request.staffId(), shiftId,
                "CONFIRMED", UUID.randomUUID(), Instant.now()
        );
        when(shiftService.assignStaff(any(ShiftAssignmentRequest.class), any(UUID.class)))
                .thenReturn(assignmentResponse);

        mockMvc.perform(post("/api/v1/shifts/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @WithMockUser
    void getStaffingLevel_Success() throws Exception {
        StaffingLevelResponse staffingResponse = new StaffingLevelResponse(
                shiftId, 3, 5, 10, true, 0
        );
        when(shiftService.getStaffingLevel(shiftId)).thenReturn(staffingResponse);

        mockMvc.perform(get("/api/v1/shifts/{shiftId}/staffing-level", shiftId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isFullyStaffed").value(true));
    }
}
