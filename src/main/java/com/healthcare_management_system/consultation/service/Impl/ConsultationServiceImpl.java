package com.healthcare_management_system.consultation.service.Impl;

import com.healthcare_management_system.appointment.entitiy.Appointment;
import com.healthcare_management_system.appointment.repository.AppointmentRepository;
import com.healthcare_management_system.consultation.dtos.ConsultationDTO;
import com.healthcare_management_system.consultation.entity.Consultation;
import com.healthcare_management_system.consultation.repository.ConsultationRepository;
import com.healthcare_management_system.consultation.service.ConsultationService;
import com.healthcare_management_system.enums.AppointmentStatus;
import com.healthcare_management_system.exceptions.BadRequestException;
import com.healthcare_management_system.exceptions.NotFoundException;
import com.healthcare_management_system.patient.entity.Patient;
import com.healthcare_management_system.patient.repository.PatientRepository;
import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.entity.User;
import com.healthcare_management_system.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    @Transactional
    public ApiResponse<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO) {
        log.info("Creating consultation for appointment ID: {}", consultationDTO.getAppointmentId());
        User currentUser = userService.getCurrentUsers();
        Appointment appointment = appointmentRepository.findById(consultationDTO.getAppointmentId())
                .orElseThrow(() -> {
                    log.warn("Appointment not found. Appointment ID: {}", consultationDTO.getAppointmentId());
                    return new NotFoundException("Appointment not found.");
                });
        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized consultation creation. User ID: {}, Appointment ID: {}", currentUser.getId(), appointment.getId());
            throw new BadRequestException("You are not authorized to create consultation notes for this appointment.");
        }
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            log.warn("Invalid appointment status. Appointment ID: {}, Status: {}", appointment.getId(), appointment.getStatus());
            throw new BadRequestException("Consultation can only be created for a scheduled appointment.");
        }
        if (consultationRepository.findByAppointmentId(appointment.getId()).isPresent()) {
            log.warn("Consultation already exists. Appointment ID: {}", appointment.getId());
            throw new BadRequestException("Consultation notes already exist for this appointment.");
        }
        Consultation consultation = Consultation.builder()
                .consultationDate(LocalDateTime.now())
                .subjectiveNote(consultationDTO.getSubjectiveNotes())
                .observationFinding(consultationDTO.getObjectiveFindings())
                .assessment(consultationDTO.getAssessment())
                .plan(consultationDTO.getPlan())
                .appointment(appointment)
                .build();
        Consultation savedConsultation = consultationRepository.save(consultation);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
        log.info("Consultation created successfully. Consultation ID: {}, Appointment ID: {}", savedConsultation.getId(), appointment.getId());
        ConsultationDTO responseDTO = modelMapper.map(savedConsultation, ConsultationDTO.class);
        return ApiResponse.<ConsultationDTO>builder()
                .statusCode(201)
                .message("Consultation notes saved successfully.")
                .data(responseDTO)
                .build();
    }
    @Override
    public ApiResponse<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId) {
        log.info("Fetching consultation. Appointment ID: {}", appointmentId);
        User currentUser = userService.getCurrentUsers();
        Consultation consultation = consultationRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> {
                    log.warn("Consultation not found. Appointment ID: {}", appointmentId);
                    return new NotFoundException("Consultation notes not found for appointment ID: " + appointmentId);
                });
        Appointment appointment = consultation.getAppointment();
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(currentUser.getId());
        boolean isPatient = appointment.getPatient().getUser().getId().equals(currentUser.getId());
        if (!isDoctor && !isPatient) {
            log.warn("Unauthorized consultation access. User ID: {}, Appointment ID: {}", currentUser.getId(), appointmentId);
            throw new BadRequestException("You are not authorized to view this consultation.");
        }
        ConsultationDTO responseDTO = modelMapper.map(consultation, ConsultationDTO.class);
        log.info("Consultation retrieved successfully. Consultation ID: {}", consultation.getId());
        return ApiResponse.<ConsultationDTO>builder()
                .statusCode(200)
                .message("Consultation notes retrieved successfully.")
                .data(responseDTO)
                .build();
    }
    @Override
    @Transactional
    public ApiResponse<List<ConsultationDTO>> getConsultationHistoryForPatient(Long patientId) {
        log.info("Fetching consultation history. Patient ID: {}", patientId);
        User currentUser = userService.getCurrentUsers();
        Patient patient;
        if (patientId == null) {
            patient = patientRepository.findByUser(currentUser)
                    .orElseThrow(() -> {
                        log.warn("Patient profile not found. User ID: {}", currentUser.getId());
                        return new NotFoundException("Patient profile not found.");
                    });
            patientId = patient.getId();
        } else {
            patient = patientRepository.findById(patientId)
                    .orElseThrow(() -> {
                        return new NotFoundException("Patient not found.");});
            if (!patient.getUser().getId().equals(currentUser.getId())) {
                log.warn("Unauthorized consultation history access. User ID: {}, Patient ID: {}", currentUser.getId(), patientId);
                throw new BadRequestException("You are not authorized to view this patient's consultation history.");
            }
        }
        List<Consultation> consultations = consultationRepository.findByAppointmentPatientIdOrderByConsultationDateDesc(patientId);
        List<ConsultationDTO> consultationDTOs = consultations.stream()
                .map(consultation -> modelMapper.map(consultation, ConsultationDTO.class))
                .toList();
        log.info("Consultation history retrieved successfully. Patient ID: {}, Count: {}", patientId, consultationDTOs.size());
        return ApiResponse.<List<ConsultationDTO>>builder()
                .statusCode(200)
                .message(consultationDTOs.isEmpty() ? "No consultation history found." : "Consultation history retrieved successfully.")
                .data(consultationDTOs)
                .build();
    }
}