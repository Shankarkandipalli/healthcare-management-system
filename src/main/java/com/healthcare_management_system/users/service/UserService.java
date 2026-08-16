package com.healthcare_management_system.users.service;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.dtos.UpdatePasswordRequest;
import com.healthcare_management_system.users.dtos.UserDTO;
import com.healthcare_management_system.users.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getCurrentUsers();

    ApiResponse<UserDTO> getUserById(Long id);

    ApiResponse<UserDTO> updateUser(Long userId, UserDTO userDTO);

    ApiResponse<UserDTO> getUserByEmail(String email);

    ApiResponse<UserDTO> getUserByDetails(String phone);

    ApiResponse<List<UserDTO>> getAllUsers();

    ApiResponse<UserDTO> getUserByUsername(String name);

    ApiResponse<UserDTO> uploadProfilePicture(MultipartFile file);

    ApiResponse<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);


}
