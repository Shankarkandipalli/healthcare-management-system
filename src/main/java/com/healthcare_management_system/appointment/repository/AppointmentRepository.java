package com.healthcare_management_system.appointment.repository;

import com.healthcare_management_system.appointment.entitiy.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
