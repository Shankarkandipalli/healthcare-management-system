package com.healthcare_management_system.users.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {

    private String name;
    private String email;
    private String token;
    private List<String> roles;

}

