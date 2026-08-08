package com.healthcare_management_system.users.service;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.dtos.UserDTO;
import com.healthcare_management_system.users.entity.User;

import java.util.List;

public interface UserService {

    User getCurrentUser();
    ApiResponse<UserDTO>getUserById(String id);
    ApiResponse<UserDTO>updateUser(UserDTO userDTO);
    ApiResponse<UserDTO>getUserByEmail(String email);
    ApiResponse<UserDTO> getUserByDetails(String phone);
    ApiResponse<List<UserDTO>> getAllUsers();
    ApiResponse<UserDTO>getUserByUsername(String username);
    ApiResponse<UserDTO>uploadProfilePicture(UserDTO userDTO);




}
