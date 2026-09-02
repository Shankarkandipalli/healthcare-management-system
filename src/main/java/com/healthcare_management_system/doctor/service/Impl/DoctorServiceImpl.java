package com.healthcare_management_system.doctor.service.Impl;

import com.healthcare_management_system.doctor.dtos.DoctorDTO;
import com.healthcare_management_system.doctor.entity.Doctor;
import com.healthcare_management_system.doctor.repository.DoctorRepository;
import com.healthcare_management_system.doctor.service.DoctorService;
import com.healthcare_management_system.enums.Specialization;

import com.healthcare_management_system.exceptions.NotFoundException;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.entity.User;
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
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<DoctorDTO> getDoctorProfile() {
        log.info("Attempting to retrieve patient profile for current user");
        User user = userService.getCurrentUsers();
        log.debug("Current user identified. User ID: {}, Email: {}", user.getId(), user.getEmail());
        Doctor doctor = doctorRepository.findByUser(user).orElseThrow(() -> {
            log.warn("Patient profile not found. User ID: {}, Email: {}", user.getId(), user.getEmail());
            return new NotFoundException("Patient profile not found for user: " + user.getEmail());
        });
        log.info("Patient profile retrieved successfully. Patient ID: {}, User ID: {}", doctor.getId(), user.getId());
        DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);
        log.debug("Patient entity mapped to PatientDTO successfully. Patient ID: {}", doctor.getId());
        return ApiResponse.<DoctorDTO>builder()
                .statusCode(200)
                .message("Patient profile retrieved successfully")
                .data(doctorDTO)
                .build();
    }

    @Override
    public ApiResponse<?> updateDoctorProfile(DoctorDTO doctorDTO) {
        log.info("Attempting to retrieve patient profile for current user for Update");
        User currentUser = userService.getCurrentUsers();
        log.debug("Current user identified. User ID: {}, Email: {} || ", currentUser.getId(), currentUser.getEmail());

        Doctor doctor = doctorRepository.findByUser(currentUser)
                .orElseThrow(() -> {
                    log.warn("Patient profile not found. User ID: {}|| Email: {}", currentUser.getId(), currentUser.getEmail());
                    return new NotFoundException("Patient profile not found for user: " + currentUser.getEmail());
                });
        log.info("Patient profile retrieved successfully. Patient ID: {}, || User ID: {}", doctor.getId(), currentUser.getId());

        if (doctorDTO.getFirstName() != null) {
            doctor.setFirstName(doctorDTO.getFirstName());
        }
        if (doctorDTO.getLastName() != null) {
            doctor.setLastName(doctorDTO.getLastName());
        }
        if (doctorDTO.getSpecialization() != null) {
            doctor.setSpecialization(doctorDTO.getSpecialization());
        }
        if (doctorDTO.getLicenseNumber() != null) {
            doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
        }
        Doctor updateDoctor = doctorRepository.save(doctor);
        log.info("Patient profile updated successfully. Patient ID: {}, User ID: {}", updateDoctor.getId(), currentUser.getId());

        DoctorDTO updatedDoctorDto = modelMapper.map(doctor, DoctorDTO.class);
        return ApiResponse.builder()
                .message("Doctor profile updated successfully").statusCode(200).data(updatedDoctorDto).build();
    }

    @Override
    @Transactional
    public ApiResponse<List<DoctorDTO>> getAllDoctors() {
        log.info("Fetching all doctors");
        List<Doctor> doctors = doctorRepository.findAll();
        log.debug("Total doctors retrieved: {}", doctors.size());
        List<DoctorDTO> doctorDTOs = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .toList();
        log.info("All doctors retrieved successfully. Total doctors: {}", doctorDTOs.size());
        return ApiResponse.<List<DoctorDTO>>builder()
                .statusCode(200)
                .message("Doctors retrieved successfully")
                .data(doctorDTOs)
                .build();
    }

    @Override
    public ApiResponse<DoctorDTO> getDoctorById(Long doctorId) {
        log.info("Attempting to retrieve patient profile for patient ID: {}", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> {
                    log.warn("Doctor profile not found for patient ID: {}", doctorId);
                    return new NotFoundException("Doctor profile not found for patient ID: " + doctorId);
                });
        log.info("Doctor profile retrieved successfully for patient ID: {}", doctorId);
        DoctorDTO doctorDTO = modelMapper.map(doctor, DoctorDTO.class);
        log.debug("doctor entity mapped to PatientDTO successfully for patient ID: {}", doctor);
        return ApiResponse.<DoctorDTO>builder()
                .statusCode(200)
                .message("Doctor profile retrieved successfully")
                .data(doctorDTO)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization) {
        log.info("Searching doctors by specialization: {}", specialization);
        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        if (doctors.isEmpty()) {
            log.warn("No doctors found for specialization: {}", specialization);
            throw new NotFoundException("No doctors found for specialization: " + specialization);
        }
        List<DoctorDTO> doctorDTOs = doctors.stream()
                .map(doctor -> modelMapper.map(doctor, DoctorDTO.class))
                .toList();
        log.info("Doctors retrieved successfully. Specialization: {}, Count: {}", specialization, doctorDTOs.size());
        return ApiResponse.<List<DoctorDTO>>builder()
                .statusCode(200)
                .message("Doctors retrieved successfully")
                .data(doctorDTOs)
                .build();
    }


    @Override
    public ApiResponse<List<Specialization>> getAllSpecializationEnums() {
        log.info("Retrieving all Specialization enums");
        List<Specialization> specializations = List.of(Specialization.values());
        log.debug("Specializations enums retrieved successfully: {}", specializations);
        return ApiResponse.<List<Specialization>>builder()
                .statusCode(200)
                .message("Specialization enums retrieved successfully")
                .data(specializations)
                .build();
    }
}
