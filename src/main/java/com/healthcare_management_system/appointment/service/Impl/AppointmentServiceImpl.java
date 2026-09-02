package com.healthcare_management_system.appointment.service.Impl;

import com.healthcare_management_system.Notification.dtos.NotificationDTO;
import com.healthcare_management_system.Notification.service.NotificationService;
import com.healthcare_management_system.appointment.dtos.AppointmentDTO;
import com.healthcare_management_system.appointment.entitiy.Appointment;
import com.healthcare_management_system.appointment.repository.AppointmentRepository;
import com.healthcare_management_system.appointment.service.AppointmentService;
import com.healthcare_management_system.doctor.entity.Doctor;
import com.healthcare_management_system.doctor.repository.DoctorRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");

    @Override
    @Transactional
    public ApiResponse<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO) {
        log.info("Starting appointment booking process");
        User currentUser = userService.getCurrentUsers();
        log.debug("Current user identified. User ID: {}", currentUser.getId());
        Patient patient = patientRepository.findByUser(currentUser)
                .orElseThrow(() -> {
                    log.warn("Appointment booking failed. Patient profile not found. User ID: {}",
                            currentUser.getId());
                    return new NotFoundException("Patient profile required for booking.");
                });
        log.debug("Patient profile found. Patient ID: {}", patient.getId());
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId())
                .orElseThrow(() -> {
                    log.warn("Appointment booking failed. Doctor not found. Doctor ID: {}",
                            appointmentDTO.getDoctorId());
                    return new NotFoundException("Doctor not found.");
                });
        log.debug("Doctor found. Doctor ID: {}", doctor.getId());
        LocalDateTime startTime = appointmentDTO.getStartTime();
        LocalDateTime endTime = appointmentDTO.getEndTime();
        if (startTime == null || endTime == null) {
            log.warn("Appointment booking failed. Start time or end time is missing");
            throw new BadRequestException("Start time and end time are required.");
        }
        log.info("Validating appointment time. Doctor ID: {}, Start: {}, End: {}",
                doctor.getId(), startTime, endTime);
        if (!endTime.isAfter(startTime)) {
            log.warn("Appointment booking failed. Invalid appointment time range");
            throw new BadRequestException("End time must be after start time.");
        }
        if (startTime.isBefore(LocalDateTime.now().plusHours(1))) {
            log.warn("Appointment booking failed. Appointment must be booked at least 1 hour in advance");
            throw new BadRequestException(
                    "Appointments must be booked at least 1 hour in advance."
            );
        }
        LocalDateTime checkStart = startTime.minusMinutes(60);
        log.debug("Checking doctor availability. Doctor ID: {}, Check Start: {}, End: {}",
                doctor.getId(), checkStart, endTime);
        List<Appointment> overlappingAppointments =
                appointmentRepository.findConflictingAppointments(
                        doctor.getId(),
                        checkStart,
                        endTime
                );
        if (!overlappingAppointments.isEmpty()) {
            log.warn("Doctor unavailable. Doctor ID: {}, Conflicting appointments: {}",
                    doctor.getId(), overlappingAppointments.size());
            throw new BadRequestException(
                    "Doctor is not available at the requested time. Please check their schedule."
            );
        }
        log.debug("Doctor availability confirmed. Doctor ID: {}", doctor.getId());
        String uuid = UUID.randomUUID()
                .toString()
                .replace("-", "");
        String uniqueRoomName = "Shankar-" + uuid.substring(0, 10);
        String meetingLink = "https://meet.jit.si/" + uniqueRoomName;
        log.info("Jitsi meeting room generated successfully");
        Appointment appointment = Appointment.builder()
                .startTime(startTime)
                .endTime(endTime)
                .meetingLink(meetingLink)
                .purposeOfConsultation(appointmentDTO.getPurposeOfConsultation())
                .initialSymptoms(appointmentDTO.getInitialSymptoms())
                .status(AppointmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .build();
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment booked successfully. Appointment ID: {}, Patient ID: {}, Doctor ID: {}",
                savedAppointment.getId(),
                patient.getId(),
                doctor.getId());
        AppointmentDTO responseDTO =
                modelMapper.map(savedAppointment, AppointmentDTO.class);
        sendAppointmentConfirmation(savedAppointment);
        log.debug("Appointment entity mapped to DTO successfully. Appointment ID: {}",
                savedAppointment.getId());
        return ApiResponse.<AppointmentDTO>builder()
                .statusCode(201)
                .message("Appointment booked successfully.")
                .data(responseDTO)
                .build();
    }

    @Override
    public ApiResponse<List<AppointmentDTO>> getMyAppointments() {
        User currentUser = userService.getCurrentUsers();
        Long UserId = currentUser.getId();
        List<Appointment> appointments;
        boolean isDoctor = currentUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("DOCTOR"));

        if (isDoctor) {
            // 1. Check for Doctor profile existence (required to throw the correct exception)
            doctorRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Doctor profile not found."));

            // 2. Efficiently fetch appointments of the Doctor
            appointments = appointmentRepository.findByDoctor_User_IdOrderByIdDesc(UserId);

        } else {

            // 1. Check for Patient profile existence
            patientRepository.findByUser(currentUser)
                    .orElseThrow(() -> new NotFoundException("Patient profile not found."));

            // 2. Efficiently fetch appointments using the User ID to navigate Patient relationship
            appointments = appointmentRepository.findByPatient_User_IdOrderByIdDesc(UserId);
        }
        // Convert the list of entities to DTOs in a single step
        List<AppointmentDTO> appointmentDTOList = appointments.stream()
                .map(appointment -> modelMapper.map(appointment, AppointmentDTO.class))
                .toList();

        return ApiResponse.<List<AppointmentDTO>>builder()
                .statusCode(200)
                .message("Appointments retrieved successfully.")
                .data(appointmentDTOList)
                .build();
    }

    @Override
    public ApiResponse<AppointmentDTO> cancelAppointment(Long appointmentId) {

        User currentUser = userService.getCurrentUsers();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found."));


        // Add security check: only the patient or doctor involved can cancel
        boolean isOwner = appointment.getPatient().getUser().getId().equals(currentUser.getId()) ||
                appointment.getDoctor().getUser().getId().equals(currentUser.getId());

        if (!isOwner) {
            throw new BadRequestException("You do not have permission to cancel this appointment.");
        }

        // Update status
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment savedAppointment = appointmentRepository.save(appointment);

        // NOTE: Notification should be sent to the other party (patient/doctor)
        sendAppointmentCancellation(savedAppointment, currentUser);

        return ApiResponse.<AppointmentDTO>builder()
                .statusCode(200)
                .message("Appointment cancelled successfully.")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<AppointmentDTO> rescheduleAppointment(AppointmentDTO appointmentDTO) {

        log.info("Starting appointment rescheduling process");

        User currentUser = userService.getCurrentUsers();
        log.debug("Current user identified. User ID: {}", currentUser.getId());

        Patient patient = patientRepository.findByUser(currentUser)
                .orElseThrow(() -> {
                    log.warn("Rescheduling failed. Patient profile not found. User ID: {}",
                            currentUser.getId());
                    return new NotFoundException("Patient profile required for rescheduling.");
                });

        Appointment appointment = appointmentRepository.findById(appointmentDTO.getId())
                .orElseThrow(() -> {
                    log.warn("Rescheduling failed. Appointment not found. Appointment ID: {}",
                            appointmentDTO.getId());
                    return new NotFoundException("Appointment not found.");
                });

        log.debug("Appointment found. Appointment ID: {}", appointment.getId());

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            log.warn("Unauthorized rescheduling attempt. Appointment ID: {}, Patient ID: {}",
                    appointment.getId(), patient.getId());
            throw new BadRequestException(
                    "You are not authorized to reschedule this appointment."
            );
        }

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            log.warn("Rescheduling failed. Appointment ID: {}, Status: {}",
                    appointment.getId(), appointment.getStatus());
            throw new BadRequestException(
                    "Only scheduled appointments can be rescheduled."
            );
        }

        LocalDateTime startTime = appointmentDTO.getStartTime();
        LocalDateTime endTime = appointmentDTO.getEndTime();

        if (startTime == null || endTime == null) {
            log.warn("Rescheduling failed. Start time or end time is missing");
            throw new BadRequestException("Start time and end time are required.");
        }

        if (!endTime.isAfter(startTime)) {
            log.warn("Rescheduling failed. Invalid appointment time range");
            throw new BadRequestException("End time must be after start time.");
        }

        if (startTime.isBefore(LocalDateTime.now().plusHours(1))) {
            log.warn("Rescheduling failed. Appointment must be at least 1 hour in advance");
            throw new BadRequestException(
                    "Appointments must be scheduled at least 1 hour in advance."
            );
        }

        Doctor doctor = appointment.getDoctor();

        log.info("Checking doctor availability. Doctor ID: {}, Start: {}, End: {}",
                doctor.getId(), startTime, endTime);

        List<Appointment> conflictingAppointments =
                appointmentRepository.findConflictingAppointments(
                        doctor.getId(),
                        startTime,
                        endTime
                );

        boolean hasConflict = conflictingAppointments.stream()
                .anyMatch(existingAppointment ->
                        !existingAppointment.getId().equals(appointment.getId())
                );

        if (hasConflict) {
            log.warn("Rescheduling failed. Doctor unavailable. Doctor ID: {}",
                    doctor.getId());

            throw new BadRequestException(
                    "Doctor is not available at the requested time."
            );
        }

        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);

        Appointment updatedAppointment =
                appointmentRepository.save(appointment);

        log.info("Appointment rescheduled successfully. Appointment ID: {}, New Start: {}, New End: {}",
                updatedAppointment.getId(),
                updatedAppointment.getStartTime(),
                updatedAppointment.getEndTime());

        AppointmentDTO responseDTO =
                modelMapper.map(updatedAppointment, AppointmentDTO.class);

        sendAppointmentConfirmation(updatedAppointment);

        return ApiResponse.<AppointmentDTO>builder()
                .statusCode(200)
                .message("Appointment rescheduled successfully.")
                .data(responseDTO)
                .build();
    }

    @Override
    public ApiResponse<AppointmentDTO> completeAppoinment(Long appointmentId) {

        User currentUser = userService.getCurrentUsers();

        // 1. Fetch the appointment
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        // Security Check 1: Ensure the current user is the Doctor assigned to this appointment
        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("Only the assigned doctor can mark this appointment as complete.");
        }

        // 2. Update status and end time
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setEndTime(LocalDateTime.now());

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        modelMapper.map(updatedAppointment, AppointmentDTO.class);

        return ApiResponse.<AppointmentDTO>builder()
                .statusCode(200)
                .message("Appointment successfully marked as completed. You may now proceed to create the consultation notes.")
                .build();
    }

    private void sendAppointmentCancellation(Appointment appointment, User cancelingUser) {

        User patientUser = appointment.getPatient().getUser();
        User doctorUser = appointment.getDoctor().getUser();

        // Safety check to ensure the cancellingUser is involved
        boolean isOwner = patientUser.getId().equals(cancelingUser.getId()) || doctorUser.getId().equals(cancelingUser.getId());
        if (!isOwner) {
            log.error("Cancellation initiated by user not associated with appointment. User ID: {}", cancelingUser.getId());
            return;
        }

        String formattedTime = appointment.getStartTime().format(FORMATTER);
        String cancellingPartyName = cancelingUser.getName();


        // --- Common Variables for the Template ---
        Map<String, Object> baseVars = new HashMap<>();
        baseVars.put("cancellingPartyName", cancellingPartyName);
        baseVars.put("appointmentTime", formattedTime);
        baseVars.put("doctorName", appointment.getDoctor().getLastName());
        baseVars.put("patientFullName", patientUser.getName());

        // --- 1. Dispatch Email to Doctor ---
        Map<String, Object> doctorVars = new HashMap<>(baseVars);
        doctorVars.put("recipientName", doctorUser.getName());

        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("DAT Health: Appointment Cancellation")
                .templateName("appointment-cancellation")
                .templateVariables(doctorVars)
                .build();

        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched cancellation email to Doctor: {}", doctorUser.getEmail());


        // --- 2. Dispatch Email to Patient ---
        Map<String, Object> patientVars = new HashMap<>(baseVars);
        patientVars.put("recipientName", patientUser.getName());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("DAT Health: Appointment CANCELED (ID: " + appointment.getId() + ")")
                .templateName("appointment-cancellation")
                .templateVariables(patientVars)
                .build();

        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched cancellation email to Patient: {}", patientUser.getEmail());

    }


    private void sendAppointmentConfirmation(Appointment appointment) {

        // Prepare Patient Notification
        User patientUser = appointment.getPatient().getUser();
        String formattedTime = appointment.getStartTime().format(FORMATTER);


        Map<String, Object> patientVars = new HashMap<>();
        patientVars.put("patientName", patientUser.getName());
        patientVars.put("doctorName", appointment.getDoctor().getUser().getName());
        patientVars.put("appointmentTime", formattedTime);
        patientVars.put("isVirtual", true);
        patientVars.put("meetingLink", appointment.getMeetingLink());
        patientVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("DAT Health: Your Appointment is Confirmed")
                .templateName("patient-appointment")
                .templateVariables(patientVars)
                .build();


        // Dispatch patient email using the low-level service
        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched confirmation email for patient: {}", patientUser.getEmail());


        // --- 2. Prepare Doctor Notification ---
        User doctorUser = appointment.getDoctor().getUser();

        Map<String, Object> doctorVars = new HashMap<>();
        doctorVars.put("doctorName", doctorUser.getName());
        doctorVars.put("patientFullName", patientUser.getName());
        doctorVars.put("appointmentTime", formattedTime);
        doctorVars.put("isVirtual", true);
        doctorVars.put("meetingLink", appointment.getMeetingLink());
        doctorVars.put("initialSymptoms", appointment.getInitialSymptoms());
        doctorVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("DAT Health: New Appointment Booked")
                .templateName("doctor-appointment")
                .templateVariables(doctorVars)
                .build();


        // Dispatch doctor email using the low-level service
        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched new appointment email for doctor: {}", doctorUser.getEmail());
    }


}
