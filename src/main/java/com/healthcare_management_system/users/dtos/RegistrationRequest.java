package com.healthcare_management_system.users.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthcare_management_system.enums.Specialization;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    private List<String> roles;
    //@NotBlank(message = "Specialization cannot be blank")
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    private String licenseNumber;



}
