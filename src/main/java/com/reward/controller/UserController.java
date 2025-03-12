package com.reward.controller;

import com.reward.dto.UserDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @GetMapping
    public ResponseEntity<ResponseModel<List<UserDTO>>> getAllUsers(@RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getAllUsers(role));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseModel<UserDTO>> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping
    public ResponseEntity<ResponseModel<String>> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @PutMapping("/profile")
    public ResponseEntity<ResponseModel<String>> updateUserProfile(@RequestParam UUID userId, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUserProfile(userId, userDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseModel<String>> softDeleteUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.softDeleteUser(userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @PutMapping("/{userId}/change-password")
    public ResponseEntity<ResponseModel<String>> changePassword(
            @PathVariable UUID userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        return ResponseEntity.ok(userService.changePassword(userId, oldPassword, newPassword));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{userId}/image")
    public ResponseEntity<ResponseModel<byte[]>> getProfileImage(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserProfileImage(userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @PostMapping("/profile/image")
    public ResponseEntity<ResponseModel<String>> uploadProfileImage(@RequestParam UUID userId, @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.saveProfileImage(userId, file));
    }

    @PutMapping("/profile/image")
    public ResponseEntity<ResponseModel<String>> updateProfileImage(
            @RequestParam UUID userId, 
            @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.updateProfileImage(userId, file));
    }
    
    @DeleteMapping("/profile/image")
    public ResponseEntity<ResponseModel<String>> deleteProfileImage(@RequestParam UUID userId) {
        return ResponseEntity.ok(userService.deleteProfileImage(userId));
    }
}
