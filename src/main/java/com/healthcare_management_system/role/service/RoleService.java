package com.healthcare_management_system.role.service;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.role.dtos.RoleDTO;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface RoleService {

    ApiResponse<RoleDTO> createRole(RoleDTO role);

    ApiResponse<RoleDTO> getRoleById(Long id);

    ApiResponse<RoleDTO> updateRole(Long id, RoleDTO role);

    ApiResponse<RoleDTO> getRoleByName(String name);

    ApiResponse<?> deleteRoleById(Long id);

    ApiResponse<List<RoleDTO>> getAllRoles(Sort roleName);

}
