package com.healthcare_management_system.patient.service.Impl;

import com.healthcare_management_system.enums.BloodGroup;
import com.healthcare_management_system.enums.Genotype;
import com.healthcare_management_system.exceptions.NotFoundException;
import com.healthcare_management_system.patient.dtos.PatientDTO;
import com.healthcare_management_system.patient.entity.Patient;
import com.healthcare_management_system.patient.repository.PatientRepository;
import com.healthcare_management_system.patient.service.PatientService;
import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.entity.User;
import com.healthcare_management_system.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<PatientDTO> getPatientProfile() {
        log.info("Attempting to retrieve patient profile for current user");
        User user = userService.getCurrentUsers();
        log.debug("Current user identified. User ID: {}, Email: {}", user.getId(), user.getEmail());
        Patient patient = patientRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.warn("Patient profile not found. User ID: {}, Email: {}", user.getId(), user.getEmail());
                    return new NotFoundException("Patient profile not found for user: " + user.getEmail());
                });
        log.info("Patient profile retrieved successfully. Patient ID: {}, User ID: {}", patient.getId(), user.getId());
        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);
        log.debug("Patient entity mapped to PatientDTO successfully. Patient ID: {}", patient.getId());
        return ApiResponse.<PatientDTO>builder()
                .statusCode(200)
                .message("Patient profile retrieved successfully")
                .data(patientDTO)
                .build();
    }

    @Override
    public ApiResponse<PatientDTO> updatePatientProfile(PatientDTO patientDTO) {
        User user = userService.getCurrentUsers();
        log.info("Attempting to retrieve patient profile for current user for Update");
        User currentUser = userService.getCurrentUsers();
        log.debug("Current user identified. User ID: {}, Email: {} || ", user.getId(), user.getEmail());
        Patient patient = patientRepository.findByUser(currentUser)
                .orElseThrow(() -> {
                    log.warn("Patient profile not found. User ID: {}|| Email: {}", user.getId(), user.getEmail());
                    return new NotFoundException("Patient profile not found for user: " + user.getEmail());
                });
        log.info("Patient profile retrieved successfully. Patient ID: {}, || User ID: {}", patient.getId(), user.getId());
        if (patientDTO.getFirstName() != null) {
            patient.setFirstName(patientDTO.getFirstName());
        }
        if (patientDTO.getLastName() != null) {
            patient.setLastName(patientDTO.getLastName());
        }
        if (patientDTO.getDateOfBirth() != null) {
            patient.setDateOfBirth(patientDTO.getDateOfBirth());
        }
        if (patientDTO.getGender() != null) {
            patient.setGender(patientDTO.getGender());
        }
        if (patientDTO.getBloodGroup() != null) {
            patient.setBloodGroup(patientDTO.getBloodGroup());
        }
        if (patientDTO.getGenotype() != null) {
            patient.setGenotype(patientDTO.getGenotype());
        }
        if (patientDTO.getPhoneNumber() != null) {
            patient.setPhoneNumber(patientDTO.getPhoneNumber());
        }

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient profile updated successfully. Patient ID: {}, User ID: {}", updatedPatient.getId(), user.getId());
        PatientDTO updatedPatientDTO = modelMapper.map(updatedPatient, PatientDTO.class);
        log.debug("Updated Patient entity mapped to PatientDTO successfully. Patient ID: {}", updatedPatient.getId());
        return ApiResponse.<PatientDTO>builder()
                .message("Patient profile updated successfully")
                .statusCode(200)
                .data(updatedPatientDTO)
                .build();
    }

    @Override
    public ApiResponse<PatientDTO> getPatientProfileById(Long patientId) {
        log.info("Attempting to retrieve patient profile for patient ID: {}", patientId);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> {
                    log.warn("Patient profile not found for patient ID: {}", patientId);
                    return new NotFoundException("Patient profile not found for patient ID: " + patientId);
                });
        log.info("Patient profile retrieved successfully for patient ID: {}", patientId);
        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);
        log.debug("Patient entity mapped to PatientDTO successfully for patient ID: {}", patientId);
        return ApiResponse.<PatientDTO>builder()
                .statusCode(200)
                .message("Patient profile retrieved successfully")
                .data(patientDTO)
                .build();
    }

    @Override
    public ApiResponse<List<BloodGroup>> getAllBloodGroupEnums() {
        log.info("Retrieving all BloodGroup enums");
        List<BloodGroup> bloodGroups = List.of(BloodGroup.values());
        log.debug("BloodGroup enums retrieved successfully: {}", bloodGroups);
        return ApiResponse.<List<BloodGroup>>builder()
                .statusCode(200)
                .message("Blood group enums retrieved successfully")
                .data(bloodGroups)
                .build();
    }

    @Override
    public ApiResponse<List<Genotype>> getAllGenotypeEnums() {
        log.info("Retrieving all Genotype enums");
        List<Genotype> genotypes = List.of(Genotype.values());
        log.debug("Genotype enums retrieved successfully: {}", genotypes);
        return ApiResponse.<List<Genotype>>builder()
                .statusCode(200)
                .message("Genotype enums retrieved successfully")
                .data(genotypes)
                .build();
    }
}
