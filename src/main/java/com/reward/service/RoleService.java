package com.reward.service;

import com.reward.entity.Role;
import com.reward.exception.ResourceNotFoundException;
import com.reward.repository.RoleRepository;
import com.reward.responsemodel.ResponseModel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public ResponseModel<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            return ResponseModel.error(404, "No roles found");
        }
        return ResponseModel.success(200, "Roles retrieved successfully", roles);
    }

    @Transactional
    public ResponseModel<Role> createRole(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            return ResponseModel.error(400, "Role already exists");
        }
        Role savedRole = roleRepository.save(role);
        return ResponseModel.success(201, "Role created successfully", savedRole);
    }

    @Transactional
    public ResponseModel<Role> updateRole(UUID roleId, String newRoleName) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        if (roleRepository.existsByRoleName(newRoleName)) {
            return ResponseModel.error(400, "Role name already exists");
        }

        role.setRoleName(newRoleName);
        Role updatedRole = roleRepository.save(role);

        return ResponseModel.success(200, "Role updated successfully", updatedRole);
    }

    @Transactional
    public ResponseModel<String> deleteRole(UUID roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
        roleRepository.delete(role);
        return ResponseModel.success(200, "Role deleted successfully", null);
    }
}
