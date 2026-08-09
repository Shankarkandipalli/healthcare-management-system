package com.healthcare_management_system.appointment.service;

import com.healthcare_management_system.appointment.dtos.AppointmentDTO;
import com.healthcare_management_system.response.ApiResponse;

import java.util.List;

public interface AppointmentService {

    ApiResponse<AppointmentDTO> bookAppointment(AppointmentDTO appointment);

    ApiResponse<List<AppointmentDTO>> getMyAppointments();

    ApiResponse<AppointmentDTO> cancelAppointment(Long appointmentId);

    ApiResponse<AppointmentDTO> rescheduleAppointment(AppointmentDTO appointment);

    ApiResponse<AppointmentDTO> completeAppoinment(Long appointmentId);

}
