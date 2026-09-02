package com.healthcare_management_system.consultation.controller;

import com.healthcare_management_system.consultation.dtos.ConsultationDTO;
import com.healthcare_management_system.consultation.service.ConsultationService;
import com.healthcare_management_system.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consultations")
public class ConsultationController {


    private final ConsultationService consultationService;

    @PostMapping
   // @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<ApiResponse<ConsultationDTO>> createConsultation(
            @RequestBody ConsultationDTO consultationDTO) {
        return ResponseEntity.ok(consultationService.createConsultation(consultationDTO));
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<ApiResponse<ConsultationDTO>> getConsultationByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(consultationService.getConsultationByAppointmentId(appointmentId));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<ConsultationDTO>>> getConsultationHistoryForPatient(
            @RequestParam(required = false) Long patientId) {
        return ResponseEntity.ok(consultationService.getConsultationHistoryForPatient(patientId));
    }
}
