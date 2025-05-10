package com.reward.controller;

import com.reward.dto.UserDTO;
import com.reward.exception.UnauthorizedException;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.reward.security.JwtUtil;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtService;


    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping
    public ResponseEntity<ResponseModel<?>> getUsers(
            @RequestParam(required = false) UUID userId, 
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getUsers(userId, role));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @PostMapping
    public ResponseEntity<ResponseModel<String>> createOrUpdateUser(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String roleName,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @Valid @ModelAttribute UserDTO userDTO
    ) throws IOException {
        return ResponseEntity.ok(userService.createOrUpdateUser(userId, roleName, userDTO, image));
    }


    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseModel<String>> softDeleteUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.softDeleteUser(userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @PutMapping("/change-password")
    public ResponseEntity<ResponseModel<String>> changePassword(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String oldPassword,
            @RequestParam(required = false) String newPassword) {
                if (token == null || !token.startsWith("Bearer ")) {
                    throw new UnauthorizedException("Invalid or missing token");
                }
                
                String jwt = token.substring(7);
                String userEmail = jwtService.extractUserName(jwt); 
        return ResponseEntity.ok(userService.changePassword(userEmail, oldPassword, newPassword));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @PutMapping("/profile/image")
    public ResponseEntity<ResponseModel<String>> updateProfileImage(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) MultipartFile file) throws IOException {
                if (token == null || !token.startsWith("Bearer ")) {
                    throw new UnauthorizedException("Invalid or missing token");
                }
                
                String jwt = token.substring(7);
                String userEmail = jwtService.extractUserName(jwt); 
        return ResponseEntity.ok(userService.updateProfileImage(userEmail, file));
    }
    
}
