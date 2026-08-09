package com.healthcare_management_system.patient.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare_management_system.enums.BloodGroup;
import com.healthcare_management_system.enums.Genotype;
import com.healthcare_management_system.users.dtos.UserDTO;
import com.healthcare_management_system.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phoneNumber;
    private BloodGroup bloodGroup;
    private Genotype genotype;
    private UserDTO user;



}
