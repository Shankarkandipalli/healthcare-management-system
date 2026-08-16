
package com.healthcare_management_system.users.service.Impl;

import com.healthcare_management_system.Notification.dtos.NotificationDTO;
import com.healthcare_management_system.Notification.service.NotificationService;
import com.healthcare_management_system.codeGenerator.CodeGenerator;
import com.healthcare_management_system.doctor.entity.Doctor;
import com.healthcare_management_system.doctor.repository.DoctorRepository;
import com.healthcare_management_system.exceptions.BadRequestException;
import com.healthcare_management_system.exceptions.NotFoundException;
import com.healthcare_management_system.patient.entity.Patient;
import com.healthcare_management_system.patient.repository.PatientRepository;
import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.role.entity.Role;
import com.healthcare_management_system.role.repository.RoleRepository;
import com.healthcare_management_system.security.JwtService;
import com.healthcare_management_system.users.dtos.*;
import com.healthcare_management_system.users.entity.PasswordResetCode;
import com.healthcare_management_system.users.entity.User;
import com.healthcare_management_system.users.repository.PasswordResetRepo;
import com.healthcare_management_system.users.repository.UserRepository;
import com.healthcare_management_system.users.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CodeGenerator codeGenerator;
    private final RoleRepository roleRepository;
    private final PasswordResetRepo passwordResetRepo;
    private final ModelMapper modelMapper;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final NotificationService notificationService;


    @Override
    @Transactional
    public ApiResponse<UserDTO> registerUser(RegistrationRequest registrationRequest) {
        log.info("Registering user with email: {}", registrationRequest.getEmail());

        // 1. Check if user already exists
        if (userRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            log.warn("Registration failed. Email already exists: {}", registrationRequest.getEmail());
            throw new BadRequestException("User email already exists: " + registrationRequest.getEmail());
        }

        // 2. Process requested roles
        List<String> requestedRoleNames = registrationRequest.getRoles() == null
                || registrationRequest.getRoles().isEmpty()
                ? List.of("PATIENT")
                : registrationRequest.getRoles().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(String::toUpperCase)
                .distinct()
                .toList();

        // 3. Validate doctor-specific information
        boolean isDoctor = requestedRoleNames.contains("DOCTOR");

        if (isDoctor && !StringUtils.hasText(registrationRequest.getLicenseNumber())) {
            throw new BadRequestException("License number is required for doctor registration.");
        }

        // 4. Fetch roles from database
        List<Role> roles = requestedRoleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new NotFoundException("Role not found: " + roleName)))
                .toList();

        // 5. Create user
        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber(registrationRequest.getPhoneNumber())
                .roles(new HashSet<>(roles))
                .build();

        // 6. Save user
        User savedUser = userRepository.save(user);

        log.info("New user registered: {} with {} roles.", savedUser.getEmail(), roles.size());

        // 7. Process Profile Creation
        for (Role role : roles) {
            String roleName = role.getName();

            switch (roleName) {
                case "PATIENT":
                    createPatientProfile(savedUser);
                    log.info("Patients  profile created: {}", savedUser.getEmail());
                    break;

                case "DOCTOR":
                    createDoctorProfile(registrationRequest, savedUser);
                    log.info("Doctors  profile created: {}", savedUser.getEmail());
                    break;

                case "ADMIN":
                    log.info("Admin role assigned to user: {}", savedUser.getEmail());
                    break;

                default:
                    log.warn("Assigned role '{}' has no corresponding profile creation logic.", roleName);
                    break;
            }
        }
        // 8. Send welcome email
        sendRegistrationEmail(registrationRequest, savedUser);

        // 9. Convert User entity to DTO
        UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);

        // 10. Return success response
        return ApiResponse.<UserDTO>builder()
                .statusCode(201)
                .message("Registration successful. A welcome email has been sent to you.")
                .data(userDTO)
                .build();
    }

    private void sendRegistrationEmail(RegistrationRequest request, User user) {
        NotificationDTO welcomeEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Welcome to STAR Health!")
                .templateName("welcome")
                .message("Thank you for registering. Your account is ready.")
                .templateVariables(Map.of(
                        "name", request.getName()))
                .build();

        notificationService.sendEmail(welcomeEmail, user);
    }

    private void createPatientProfile(User user) {
        Patient patient = Patient.builder()
                .user(user)
                .build();

        patientRepository.save(patient);
        log.info("Patient profile created: {}", user.getEmail());
    }

    private void createDoctorProfile(RegistrationRequest request, User user) {
        Doctor doctor = Doctor.builder()
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .user(user)
                .build();

        doctorRepository.save(doctor);
        log.info("Doctor profile created: {}", user.getEmail());
    }

    @Override
    public ApiResponse<LoginResponse> loginUser(LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("Login failed. User not found: {}", loginRequest.getEmail());
                    return new NotFoundException("User not found: " + loginRequest.getEmail());
                });
        log.debug("User found for login: {}", user.getEmail());
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("Login failed. Wrong password for email: {}", user.getEmail());
            throw new BadRequestException("Wrong password.");
        }
        log.debug("Password verified successfully for email: {}", user.getEmail());
        String token = jwtService.generateToken(user.getEmail());
        log.debug("JWT token generated successfully for email: {}", user.getEmail());
        LoginResponse loginResponse = LoginResponse.builder()
                .name(user.getName())
                .email(user.getEmail())
                .roles(user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .token(token)
                .build();
        log.info("Login successful for email: {} with roles: {}", user.getEmail(),
                user.getRoles().stream().map(Role::getName).toList());
        return ApiResponse.<LoginResponse>builder()
                .statusCode(200)
                .message("Login Successful")
                .data(loginResponse)
                .build();
    }


    @Override
    public ApiResponse<?> forgetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found: " + email));
        passwordResetRepo.deleteByUserId(user.getId());
        String codeGenerators = codeGenerator.generateUniqueCode();
        PasswordResetCode passwordResetCode = PasswordResetCode.builder()
                .user(user)
                .code(codeGenerators)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();
//send email reset link out
        NotificationDTO passwordResetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(Map.of( // Using Map.of() for concise, immutable map creation
                        "name", user.getName())).build();
        notificationService.sendEmail(passwordResetEmail, user);
        return ApiResponse.builder()
                .statusCode(200)
                .message("Password reset code sent to your email")
                .build();
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(5);
    }

    @Override
    public ApiResponse<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {

        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();
        log.info("CODE IS: " + code);
        log.info("NEW PASSWORD IS: " + newPassword);

        // Find and validate code
        PasswordResetCode resetCode = passwordResetRepo.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Invalid reset code"));
        // Check expiration first
        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetRepo.delete(resetCode); // Clean up expired code
            throw new BadRequestException("Reset code has expired");
        }
        //update the password
        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        // Delete the code immediately after successful use
        passwordResetRepo.delete(resetCode);

        // Send password confirmation email
        NotificationDTO passwordResetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Updated Successfully")
                .templateName("password-update-confirmation")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();
        notificationService.sendEmail(passwordResetEmail, user);
        return ApiResponse.builder()
                .statusCode(200)
                .message("Password updated successfully")
                .build();
    }
}

