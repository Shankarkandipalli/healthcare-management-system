
package com.healthcare_management_system.users.service.Impl;


import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.dtos.UserDTO;
import com.healthcare_management_system.users.entity.User;
import com.healthcare_management_system.users.repository.UserRepository;
import com.healthcare_management_system.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
   // private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;


    @Override
    public User getCurrentUser() {
        return null;
    }

    @Override
    public ApiResponse<UserDTO> getUserById(String id) {
        return null;
    }

    @Override
    public ApiResponse<UserDTO> updateUser(UserDTO userDTO) {
        return null;
    }

    @Override
    public ApiResponse<UserDTO> getUserByEmail(String email) {
        return null;
    }

    @Override
    public ApiResponse<UserDTO> getUserByDetails(String phone) {
        return null;
    }

    @Override
    public ApiResponse<List<UserDTO>> getAllUsers() {
        return null;
    }

    @Override
    public ApiResponse<UserDTO> getUserByUsername(String username) {
        return null;
    }

    @Override
    public ApiResponse<UserDTO> uploadProfilePicture(UserDTO userDTO) {
        return null;
    }
}

