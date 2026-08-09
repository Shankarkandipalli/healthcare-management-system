package com.healthcare_management_system.appointment.entitiy;

import com.healthcare_management_system.consultation.entity.Consultation;
import com.healthcare_management_system.doctor.entity.Doctor;
import com.healthcare_management_system.enums.AppointmentStatus;
import com.healthcare_management_system.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointments")
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String startTime;
    private String endTime;
    private String meetingLink;
    private String purposeOfConsultation;
    private String initialSymptoms;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Consultation consultation;

}
