package com.healthcare_management_system.doctor.service;

import com.healthcare_management_system.doctor.dtos.DoctorDTO;
import com.healthcare_management_system.enums.Specialization;
import com.healthcare_management_system.response.ApiResponse;

import java.util.List;

public interface DoctoraService {

    ApiResponse<DoctorDTO> getDoctorProfile();

    ApiResponse<?> updateDoctorProfile(DoctorDTO doctorDTO);

    ApiResponse<List<DoctorDTO>> getAllDoctors();

    ApiResponse<DoctorDTO> getDoctorById(Long doctorId);

    ApiResponse<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization);

    ApiResponse<List<Specialization>> getAllSpecializationEnums();
}
