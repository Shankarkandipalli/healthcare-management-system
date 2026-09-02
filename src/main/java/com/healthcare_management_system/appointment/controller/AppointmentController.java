package com.healthcare_management_system.appointment.controller;

import com.healthcare_management_system.appointment.dtos.AppointmentDTO;
import com.healthcare_management_system.appointment.entitiy.Appointment;
import com.healthcare_management_system.appointment.service.AppointmentService;
import com.healthcare_management_system.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<AppointmentDTO>> bookAppointment(@RequestBody @Valid AppointmentDTO appointment) {
        return ResponseEntity.ok(appointmentService.bookAppointment(appointment));
    }
    @GetMapping
    public  ResponseEntity<ApiResponse<List<AppointmentDTO>>> getMyAppointments(){
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    @PutMapping("/cancel/{appointmentId}")
    public  ResponseEntity<ApiResponse<AppointmentDTO>> cancelAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.cancelAppointment(appointmentId));
    }

    @PutMapping("/complete/{appointmentId}")
    //@PreAuthorize(("hasAuthority('DOCTOR')"))
    public  ResponseEntity<ApiResponse<?>> completeAppointment(@PathVariable Long appointmentId){
        return ResponseEntity.ok(appointmentService.completeAppoinment(appointmentId));
    }

}
