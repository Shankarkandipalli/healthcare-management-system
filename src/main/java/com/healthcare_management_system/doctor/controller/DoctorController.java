package com.healthcare_management_system.doctor.controller;

import com.healthcare_management_system.doctor.dtos.DoctorDTO;
import com.healthcare_management_system.doctor.service.DoctorService;
import com.healthcare_management_system.enums.Specialization;
import com.healthcare_management_system.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorDTO>> getDoctorById() {
        ApiResponse<DoctorDTO> response = doctorService.getDoctorProfile();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PutMapping("/update/profile")
    public ResponseEntity<ApiResponse<?>> updateDoctorProfiles(@RequestBody DoctorDTO doctorDTO) {
        ApiResponse<?> response = doctorService.updateDoctorProfile(doctorDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }
    @GetMapping("/profile/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorDTO>>getDoctorById(@PathVariable Long doctorId){
        return ResponseEntity.ok(doctorService.getDoctorById(doctorId));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DoctorDTO>>> searchDoctorsBySpecialization(
            @RequestParam Specialization specialization) {
        return ResponseEntity.ok(
                doctorService.searchDoctorsBySpecialization(specialization));
    }

    @GetMapping("/specializations")
    public ResponseEntity<ApiResponse<List<Specialization>>> getAllSpecializations() {
        return ResponseEntity.ok(
                doctorService.getAllSpecializationEnums()
        );
    }


}
