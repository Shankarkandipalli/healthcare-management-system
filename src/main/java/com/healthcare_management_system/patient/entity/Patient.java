package com.healthcare_management_system.patient.entity;

import com.healthcare_management_system.appointment.entitiy.Appointment;
import com.healthcare_management_system.enums.BloodGroup;
import com.healthcare_management_system.enums.Genotype;
import com.healthcare_management_system.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "patients")
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;
    @Enumerated(EnumType.STRING)
    private Genotype genotype;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments;


}
