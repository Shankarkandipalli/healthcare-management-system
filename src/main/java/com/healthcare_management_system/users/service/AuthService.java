package com.healthcare_management_system.users.service;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.dtos.LoginRequest;
import com.healthcare_management_system.users.dtos.RegistrationRequest;
import com.healthcare_management_system.users.dtos.ResetPasswordRequest;
import com.healthcare_management_system.users.dtos.UserDTO;

public interface AuthService {
    ApiResponse<UserDTO> registerUser(RegistrationRequest registractionRequest);
    ApiResponse<UserDTO>loginUser(LoginRequest loginRequest);
    ApiResponse<?> forgetPassword(String email);
    ApiResponse<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
