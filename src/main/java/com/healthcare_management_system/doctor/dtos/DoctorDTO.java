package com.healthcare_management_system.doctor.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare_management_system.enums.Specialization;
import com.healthcare_management_system.users.dtos.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DoctorDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private Specialization specialization;
    private String licenseNumber;
    private UserDTO user;
}
