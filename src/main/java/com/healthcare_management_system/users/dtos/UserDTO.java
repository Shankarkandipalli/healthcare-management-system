package com.healthcare_management_system.users.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healthcare_management_system.role.dtos.RoleDTO;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private int id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private String profilePicture;
    private List<RoleDTO> roles;
    private LocalDateTime createdAt;
    private String phoneNumber;
}
