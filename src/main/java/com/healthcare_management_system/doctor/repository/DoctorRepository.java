package com.healthcare_management_system.doctor.repository;

import com.healthcare_management_system.doctor.entity.Doctor;
import com.healthcare_management_system.enums.Specialization;
import com.healthcare_management_system.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Optional<Doctor> findByUser(User user);

    List<Doctor> findBySpecialization(Specialization specialization);
}
