package com.healthcare_management_system.patient.service;

import com.healthcare_management_system.enums.BloodGroup;
import com.healthcare_management_system.enums.Genotype;
import com.healthcare_management_system.patient.dtos.PatientDTO;
import com.healthcare_management_system.response.ApiResponse;


import java.util.List;

public interface PatientService {

    ApiResponse<PatientDTO> getPatientProfile();

    ApiResponse<PatientDTO> updatePatientProfile(PatientDTO patientDTO);

    ApiResponse<PatientDTO> getPatientProfileById(Long patientId);

    ApiResponse<List<BloodGroup>> getAllBloodGroupEnums();

    ApiResponse<List<Genotype>> getAllGenotypeEnums();
}
