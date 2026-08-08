package com.healthcare_management_system.users.dtos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.healthcare_management_system.enums.Specialization;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

    @NotBlank(message = "Name cannot be blank")
    private String name;
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    private List<String> roles;
    private Specialization specialization;
    @NotBlank(message = "Specialization cannot be blank")
    private String Specialization;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;



}
