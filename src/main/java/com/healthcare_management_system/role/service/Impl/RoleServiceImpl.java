package com.healthcare_management_system.role.service.Impl;

import com.healthcare_management_system.exceptions.BadRequestException;
import com.healthcare_management_system.exceptions.NotFoundException;
import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.role.dtos.RoleDTO;
import com.healthcare_management_system.role.entity.Role;
import com.healthcare_management_system.role.repository.RoleRepository;
import com.healthcare_management_system.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public ApiResponse<RoleDTO> createRole(RoleDTO roleDto) {
        log.info("Attempting to create role with name: {}", roleDto.getName());
        if (roleRepository.findByName(roleDto.getName().toUpperCase()).isPresent()) {
            log.warn("Role creation failed. Role '{}' already exists.", roleDto.getName());
            throw new BadRequestException("Role already exists.");
        }
        Role role = modelMapper.map(roleDto, Role.class);
        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with name: {}", savedRole.getName());
        return ApiResponse.<RoleDTO>builder().statusCode(201)
                .message("Role created successfully")
                .data(modelMapper.map(savedRole, RoleDTO.class))
                .build();
    }
    @Override
    public ApiResponse<RoleDTO> getRoleById(Long id) {
        log.info("Attempting to retrieve role with ID: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role retrieval failed. Role with ID '{}' not found.", id);
                    return new NotFoundException("Role not found." + id);
                });
        log.info("Role retrieved successfully. ID: {},Name: {}", role.getId(), role.getName());
        return ApiResponse.<RoleDTO>builder().statusCode(200)
                .message("Role retrieved successfully")
                .data(modelMapper.map(role, RoleDTO.class))
                .build();
    }
    @Override
    public ApiResponse<RoleDTO> updateRole(Long id, RoleDTO role) {
        log.info("Attempting to update role with ID: {}", id);
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role update failed. Role with ID '{}' not found.", id);
                    return new NotFoundException("Role not found." + id);
                });
        existingRole.setName(role.getName().toUpperCase());
        Role updatedRole = roleRepository.save(existingRole);
        log.info("Role updated successfully. ID: {}, Name: {}", updatedRole.getId(), updatedRole.getName());
        return ApiResponse.<RoleDTO>builder().statusCode(200)
                .message("Role updated successfully")
                .data(modelMapper.map(updatedRole, RoleDTO.class))
                .build();
    }
    @Override
    public ApiResponse<RoleDTO> getRoleByName(String name) {
        log.info("Attempting to retrieve role with name: {}", name);
        Role role = roleRepository.findByName(name.toUpperCase())
                .orElseThrow(() -> {
                    log.error("Role retrieval failed. Role with name '{}' not found.", name);
                    return new NotFoundException("Role not found." + name);
                });
        log.info("Role retrieved successfully. ID: {}, Name: {}", role.getId(), role.getName());
        return ApiResponse.<RoleDTO>builder().statusCode(200)
                .message("Role retrieved successfully")
                .data(modelMapper.map(role, RoleDTO.class))
                .build();
    }
    @Override
    public ApiResponse<?> deleteRoleById(Long id) {
        log.info("Attempting to delete role with ID: {}", id);
        if (!roleRepository.existsById(id)) {
            log.error("Role deletion failed. Role with ID '{}' not found.", id);
            throw new NotFoundException("Role not found." + id);
        }
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Role deletion failed. Role with ID '{}' not found.", id);
                    return new NotFoundException("Role not found." + id);
                });
        roleRepository.delete(role);
        log.info("Role deleted successfully. ID: {}", id);
        return ApiResponse.builder().statusCode(200)
                .message("Role deleted successfully")
                .build();
    }
    @Override
    public ApiResponse<List<RoleDTO>> getAllRoles(Sort roleName) {
        List<Role> getAllRole = roleRepository.findAll();
        List<RoleDTO> getAllRoles = getAllRole.stream().map(role -> modelMapper.map(role, RoleDTO.class)).toList();
        log.info("Retrieved all roles successfully. Total roles: {}", getAllRoles.size());
        return ApiResponse.<List<RoleDTO>>builder()
                .statusCode(200)
                .message("Roles retrieved successfully")
                .data(getAllRoles)
                .build();
    }
}
