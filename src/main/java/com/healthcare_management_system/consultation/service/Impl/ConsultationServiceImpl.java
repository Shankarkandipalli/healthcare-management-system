package com.healthcare_management_system.consultation.service.Impl;

import com.healthcare_management_system.appointment.repository.AppointmentRepository;
import com.healthcare_management_system.consultation.dtos.ConsultationDTO;
import com.healthcare_management_system.consultation.repository.ConsultationRepository;
import com.healthcare_management_system.consultation.service.ConsultationService;
import com.healthcare_management_system.patient.repository.PatientRepository;
import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final PatientRepository patientRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO) {
        return null;
    }

    @Override
    public ApiResponse<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId) {
        return null;
    }

    @Override
    public ApiResponse<List<ConsultationDTO>> getConsultationHistoryForPatient(Long patientId) {
        return null;
    }
}
