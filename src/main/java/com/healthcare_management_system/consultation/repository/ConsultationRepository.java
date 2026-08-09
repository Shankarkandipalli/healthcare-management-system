package com.healthcare_management_system.consultation.repository;

import com.healthcare_management_system.consultation.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Integer> {

    Optional<Consultation> findByAppointmentId(Long appointmentId);

    List<Consultation> findByAppointmentPatientIdOrderByConsultationDateDesc(Long patientId);
}
