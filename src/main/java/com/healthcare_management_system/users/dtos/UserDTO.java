package com.healthcare_management_system.users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare_management_system.role.dtos.RoleDTO;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String phoneNumber;
    private String email;
    @JsonIgnore
    private String password;
    private String profilePicture;
    private String specialization;
    private String licenseNumber;
    private List<RoleDTO> roles;
    private LocalDateTime createdAt;

}
