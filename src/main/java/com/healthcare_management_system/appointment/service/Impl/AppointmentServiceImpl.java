package com.healthcare_management_system.appointment.service.Impl;

import com.healthcare_management_system.Notification.service.NotificationService;
import com.healthcare_management_system.appointment.dtos.AppointmentDTO;
import com.healthcare_management_system.appointment.repository.AppointmentRepository;
import com.healthcare_management_system.appointment.service.AppointmentService;
import com.healthcare_management_system.doctor.repository.DoctorRepository;
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

    @Override
    public ApiResponse<AppointmentDTO> bookAppointment(AppointmentDTO appointment) {
        return null;
    }

    @Override
    public ApiResponse<List<AppointmentDTO>> getMyAppointments() {
        return null;
    }

    @Override
    public ApiResponse<AppointmentDTO> cancelAppointment(Long appointmentId) {
        return null;
    }

    @Override
    public ApiResponse<AppointmentDTO> rescheduleAppointment(AppointmentDTO appointment) {
        return null;
    }

    @Override
    public ApiResponse<AppointmentDTO> completeAppoinment(Long appointmentId) {
        return null;
    }


}
