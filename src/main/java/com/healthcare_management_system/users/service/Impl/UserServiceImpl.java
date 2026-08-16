
package com.healthcare_management_system.users.service.Impl;


import com.healthcare_management_system.Notification.dtos.NotificationDTO;
import com.healthcare_management_system.Notification.service.NotificationService;
import com.healthcare_management_system.exceptions.BadRequestException;
import com.healthcare_management_system.exceptions.NotFoundException;
import com.healthcare_management_system.exceptions.UnauthorizedException;
import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.users.dtos.UpdatePasswordRequest;
import com.healthcare_management_system.users.dtos.UserDTO;
import com.healthcare_management_system.users.entity.User;
import com.healthcare_management_system.users.repository.UserRepository;
import com.healthcare_management_system.users.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public User getCurrentUsers() {
        log.debug("Fetching currently authenticated user");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            log.warn("Unable to fetch current user: authentication is missing or invalid");
            throw new UnauthorizedException("User is not authenticated");
        }
        String email = authentication.getName();
        log.debug("Authenticated user identified with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Authenticated user not found in database. Email: {}", email);
                    return new NotFoundException("User not found");
                });
        log.info("Current user retrieved successfully. User ID: {}", user.getId());
        return user;
    }

    @Override
    @Transactional
    public ApiResponse<UserDTO> getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("User not found with ID: {}", id);
            return new NotFoundException("User not found with ID: " + id);
        });
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        log.info("User retrieved successfully. User ID | {}", id);
        return ApiResponse.<UserDTO>builder()
                .message("User retrieved successfully")
                .statusCode(200)
                .data(userDTO)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserDTO> updateUser(Long userId, UserDTO userDTO) {
        log.info("Updating user. User ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with ID | {}", userId);
                    return new NotFoundException("User not found with ID: " + userId);
                });
        if (userDTO.getName() != null) {
            user.setName(userDTO.getName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getProfilePicture() != null) {
            user.setProfilePicture(userDTO.getProfilePicture());
        }
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully. User ID: {}", userId);
        UserDTO updatedUserDTO = modelMapper.map(updatedUser, UserDTO.class);
        return ApiResponse.<UserDTO>builder()
                .statusCode(200)
                .message("User updated successfully")
                .data(updatedUserDTO)
                .build();
    }

    @Override
    public ApiResponse<UserDTO> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user Email id is NotFound" + email));
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return ApiResponse.<UserDTO>builder()
                .message("User retrieved successfully")
                .statusCode(200)
                .data(userDTO)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserDTO> getUserByDetails(String phone) {
        User currentUser = getCurrentUsers();
        log.info("Fetching user by phone number: {}", phone);
       /* User userByDetails = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> {
                    log.warn("User not found with phone number: {}", phone);
                    return new NotFoundException("User not found with phone number: " + phone);
                });*/
        UserDTO userDTO = modelMapper.map(currentUser, UserDTO.class);

        log.info("User retrieved successfully. User ID: {}", currentUser.getId());

        return ApiResponse.<UserDTO>builder()
                .statusCode(200)
                .message("User retrieved successfully")
                .data(userDTO)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<List<UserDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll(
                Sort.by(Sort.Direction.DESC, "id"));
        log.debug("Total users retrieved: {}", users.size());
        List<UserDTO> userDTOs = users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class)).toList();
        log.info("All users retrieved successfully. Total users: {}", userDTOs.size());
        return ApiResponse.<List<UserDTO>>builder()
                .statusCode(200)
                .message("Users retrieved successfully")
                .data(userDTOs)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserDTO> getUserByUsername(String name) {
        log.info("Fetching user by username: {}", name);

        User user = userRepository.findByName(name)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", name);
                    return new NotFoundException("User not found with username: " + name);
                });
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        log.info("User retrieved successfully. User ID {}|", user.getId());
        return ApiResponse.<UserDTO>builder()
                .statusCode(200)
                .message("User retrieved successfully")
                .data(userDTO)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserDTO> uploadProfilePicture(MultipartFile file) {
        log.info("Profile picture upload request received");
        if (file == null || file.isEmpty()) {
            log.warn("Profile picture upload failed. File is empty");
            throw new BadRequestException("Profile picture is required");
        }
        User user = getCurrentUsers();
        log.info("Uploading profile picture for user: {}", user.getEmail());
        try {
            String uploadDir = "uploads/profile-pictures/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName
                        .substring(originalFileName.lastIndexOf("."))
                        .toLowerCase();
            }
            String newFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING
            );
            String fileUrl = "/profile-picture/" + newFileName;

            if (user.getProfilePicture() != null
                    && !user.getProfilePicture().isBlank()) {
                String oldFileName = Paths.get(user.getProfilePicture()).getFileName().toString();
                Path oldFile = uploadPath.resolve(oldFileName);
                if (Files.exists(oldFile)) {
                    Files.delete(oldFile);
                    log.info("Old profile picture deleted for user: {}", user.getEmail());
                }
            }
            user.setProfilePicture(fileUrl);
            userRepository.save(user);

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);

            log.info("Profile picture uploaded successfully for user: {}", user.getEmail());
            return ApiResponse.<UserDTO>builder()
                    .statusCode(200)
                    .message("Profile picture uploaded successfully.")
                    .data(userDTO)
                    .build();
        } catch (IOException e) {
            log.error(
                    "Failed to upload profile picture for user: {}",
                    user.getEmail()
            );
            throw new RuntimeException("Failed to upload profile picture.");
        }
    }

    @Override
    @Transactional
    public ApiResponse<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        log.info("Password update request received");
        User user = getCurrentUsers();
        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();

        if (oldPassword == null || newPassword == null) {
            log.warn("Password update failed. Old password or new password is missing for user: {}", user.getEmail());
            throw new BadRequestException("Old Password or New Password is Required");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("Password update failed. Old password is incorrect for user: {}", user.getEmail());
            throw new BadRequestException("Old Password is not correct");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password updated successfully for user: {}", user.getEmail());

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your Password Was Successfully Changed")
                .templateName("password-change")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();
        notificationService.sendEmail(notificationDTO, user);
        log.info("Password change notification sent to user: {}", user.getEmail());
        return ApiResponse.builder()
                .statusCode(200)
                .message("Password Changed Successfully")
                .build();
    }
}

