package com.healthcare_management_system.consultation.entity;

import com.healthcare_management_system.appointment.entitiy.Appointment;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "consultations")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String consultationDate;
    private String consultationTime;

    @Lob
    private  String subjectiveNote;
    @Lob
    private String  observationFinding;
    @Lob
    private String assessment;
    @Lob
    private String plan;

    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private Appointment appointment;
}
