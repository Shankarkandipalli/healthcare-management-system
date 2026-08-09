package com.healthcare_management_system.appointment.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare_management_system.doctor.dtos.DoctorDTO;
import com.healthcare_management_system.enums.AppointmentStatus;
import com.healthcare_management_system.patient.dtos.PatientDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AppointmentDTO {

    private Long id;
    @NotBlank(message = "Doctor ID is required")
    private Long doctorId;
    @NotNull(message = "Start time is required for the appointment.")
    @Future(message = "Appointment must be scheduled for a future date and time.")
    private LocalDateTime startTime;
    private String endTime;
    private String meetingLink;
    private String purposeOfConsultation;
    private String initialSymptoms;
    private AppointmentStatus status;
    private DoctorDTO doctor;
    private PatientDTO patient;

}
