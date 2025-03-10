package com.reward.controller;

import com.reward.entity.Role;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @GetMapping
    public ResponseEntity<ResponseModel<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PostMapping
    public ResponseEntity<ResponseModel<Role>> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ResponseModel<Role>> updateRole(@PathVariable UUID roleId, @RequestParam String newRoleName) {
        return ResponseEntity.ok(roleService.updateRole(roleId, newRoleName));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ResponseModel<String>> deleteRole(@PathVariable UUID roleId) {
        return ResponseEntity.ok(roleService.deleteRole(roleId));
    }
}
