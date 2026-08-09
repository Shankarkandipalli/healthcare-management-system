package com.healthcare_management_system.patient.repository;

import com.healthcare_management_system.patient.entity.Patient;
import com.healthcare_management_system.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByUser(User user);
}
