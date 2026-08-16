package com.healthcare_management_system.users.service;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.dtos.*;

public interface AuthService {

    ApiResponse<UserDTO> registerUser(RegistrationRequest registrationRequest);

    ApiResponse<LoginResponse> loginUser(LoginRequest loginRequest);

    ApiResponse<?> forgetPassword(String email);

    ApiResponse<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
