package com.healthcare_management_system.patient.controller;

import com.healthcare_management_system.enums.BloodGroup;
import com.healthcare_management_system.enums.Genotype;
import com.healthcare_management_system.patient.dtos.PatientDTO;
import com.healthcare_management_system.patient.service.PatientService;
import com.healthcare_management_system.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/patient")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientProfiles() {
        ApiResponse<PatientDTO> response = patientService.getPatientProfile();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/profile")
    public ResponseEntity<ApiResponse<PatientDTO>> updatePatientProfile(@RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.updatePatientProfile(patientDTO));

    }

    @GetMapping("/profile/{patientId}")
    public ResponseEntity<ApiResponse<PatientDTO>> getPatientProfileById(@PathVariable Long patientId) {
        ApiResponse<PatientDTO> response = patientService.getPatientProfileById(patientId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/blood-groups")
    public ResponseEntity<ApiResponse<List<BloodGroup>>> getAllBloodGroupEnums() {
        ApiResponse<List<BloodGroup>> response = patientService.getAllBloodGroupEnums();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/genotypes")
    public ResponseEntity<ApiResponse<List<Genotype>>> getAllGenotypeEnums() {
        ApiResponse<List<Genotype>> response = patientService.getAllGenotypeEnums();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
