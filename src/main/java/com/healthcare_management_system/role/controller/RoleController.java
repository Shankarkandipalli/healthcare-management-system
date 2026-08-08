package com.healthcare_management_system.role.controller;

import com.healthcare_management_system.response.ApiResponse;
import com.healthcare_management_system.role.dtos.RoleDTO;
import com.healthcare_management_system.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@RequestBody @Valid RoleDTO roleDto) {
        ApiResponse<RoleDTO> response = roleService.createRole(roleDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleById(@PathVariable Long roleId) {
        ApiResponse<RoleDTO> response = roleService.getRoleById(roleId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        ApiResponse<List<RoleDTO>> response = roleService.getAllRoles(Sort.by(Sort.Direction.ASC, "roleName"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/{roleId}")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(@RequestBody @Valid RoleDTO roleDto, @PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.updateRole(roleId, roleDto));
    }

    @GetMapping("/name/{roleName}")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleByName(@PathVariable String roleName) {
        return ResponseEntity.ok(roleService.getRoleByName(roleName));
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<ApiResponse<?>> deleteRoleById(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.deleteRoleById(roleId));
    }

}
