package com.healthcare_management_system.consultation.service;

import com.healthcare_management_system.consultation.dtos.ConsultationDTO;
import com.healthcare_management_system.response.ApiResponse;

import java.util.List;

public interface ConsultationService {
    ApiResponse<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO);

    ApiResponse<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId);

    ApiResponse<List<ConsultationDTO>> getConsultationHistoryForPatient(Long patientId);
}
