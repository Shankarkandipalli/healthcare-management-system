package com.healthcare_management_system.doctor.service.Impl;

import com.healthcare_management_system.doctor.dtos.DoctorDTO;
import com.healthcare_management_system.doctor.repository.DoctorRepository;
import com.healthcare_management_system.doctor.service.DoctoraService;
import com.healthcare_management_system.enums.Specialization;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class DoctorServiceImpl implements DoctoraService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<DoctorDTO> getDoctorProfile() {
        return null;
    }

    @Override
    public ApiResponse<?> updateDoctorProfile(DoctorDTO doctorDTO) {
        return null;
    }

    @Override
    public ApiResponse<List<DoctorDTO>> getAllDoctors() {
        return null;
    }

    @Override
    public ApiResponse<DoctorDTO> getDoctorById(Long doctorId) {
        return null;
    }

    @Override
    public ApiResponse<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization) {
        return null;
    }

    @Override
    public ApiResponse<List<Specialization>> getAllSpecializationEnums() {
        return null;
    }
}
