package com.reward.service;

import com.reward.dto.PointsDTO;
import com.reward.dto.UserDTO;
import com.reward.entity.User;
import com.reward.entity.Points;
import com.reward.entity.Role;
import com.reward.exception.ResourceNotFoundException;
import com.reward.exception.UnauthorizedException;
import com.reward.exception.AlreadyExistsException;
import com.reward.exception.BadRequestException;
import com.reward.mapper.PointsMapper;
import com.reward.mapper.UserMapper;
import com.reward.repository.UserRepository;
import com.reward.repository.PointsRepository;
import com.reward.repository.RoleRepository;
import com.reward.responsemodel.ResponseModel;
import com.reward.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PointsRepository pointsRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtutil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseModel<?> getUsers(UUID userId, String roleName) {
        if (userId != null) {
            User user = userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return ResponseModel.success(200, "User retrieved successfully", userMapper.toDTO(user));
        }

        if (roleName != null && !roleRepository.existsByRoleName(roleName)) {
            throw new ResourceNotFoundException("Invalid role provided");
        }

        List<User> users = (roleName == null)
                ? userRepository.findByIsDeletedFalse()
                : userRepository.findByRoleName(roleName);

        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }

        List<UserDTO> userDTOs = users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseModel.success(200, "Users retrieved successfully", userDTOs);
    }


    @Transactional
    public ResponseModel<String> createOrUpdateUser(UUID userId, String roleName, UserDTO userDTO, MultipartFile image) throws IOException {

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid role provided"));

        User teacher = null;

        if ("STUDENT".equalsIgnoreCase(roleName)) {
            if (userDTO.getTeacherId() == null) {
                throw new BadRequestException("Teacher ID is required for students");
            }
            if (userDTO.getYear() == null) {
                throw new BadRequestException("Year is required for students");
            }

            teacher = userRepository.findByIdAndIsDeletedFalse(userDTO.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        } else {
            if (userDTO.getTeacherId() != null) {
                throw new BadRequestException("Teacher ID should not be provided for non-students"); 
            }
            if (userDTO.getYear() != null) {
                throw new BadRequestException("Year should not be provided for non-students"); 
            }
        }

        if (userId == null) {
            boolean userExists = userRepository.existsByEmailAndIsDeletedFalse(userDTO.getEmail());
            if (userExists) {
                throw new AlreadyExistsException("A user with this email already exists"); 
            }
            if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                throw new BadRequestException("Password is required");
            }
            User newUser = userMapper.toEntity(userDTO);
            newUser.setRole(role);
            newUser.setTeacher(teacher);

            if (image != null && !image.isEmpty()) {
                newUser.setProfilePicture(image.getBytes()); 
            }            

            userRepository.save(newUser);
            if ("STUDENT".equalsIgnoreCase(roleName)) {
                PointsDTO pointsDTO = PointsDTO.builder()
                        .studentName(newUser.getName())
                        .profilePic(newUser.getProfilePicture() != null 
                            ? Base64.getEncoder().encodeToString(newUser.getProfilePicture()) 
                            : null) 
                        .pointBalance(0)
                        .totalPoints(0)
                        .totalSpent(0)
                        .build();

                Points studentPoints = PointsMapper.toEntity(pointsDTO);
                studentPoints.setStudent(newUser);
                studentPoints.setUpdatedAt(LocalDateTime.now());

                pointsRepository.save(studentPoints);
            }
            return ResponseModel.success(201, "User created successfully", null);
        } else {
            User existingUser = userRepository.findByIdAndIsDeletedFalse(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            existingUser.setName(userDTO.getName());
            existingUser.setEmail(userDTO.getEmail());
            existingUser.setPhoneNo(userDTO.getPhoneNo());
            existingUser.setDepartment(userDTO.getDepartment());

            if ("STUDENT".equalsIgnoreCase(roleName)) {
                existingUser.setTeacher(teacher);
                existingUser.setYear(userDTO.getYear());
            }

            userRepository.save(existingUser);
            return ResponseModel.success(200, "User updated successfully", null);
        }
    }

    @Transactional
    public ResponseModel<String> softDeleteUser(UUID userId) {
        if (!userRepository.existsByIdAndIsDeletedFalse(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.softDeleteUser(userId);
        return ResponseModel.success(200, "User soft deleted successfully", null);
    }

    @Transactional
    public ResponseModel<String> changePassword(String userEmail, String oldPassword, String newPassword) {
        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                if (oldPassword == null || oldPassword.trim().isEmpty()) {
                    throw new BadRequestException("Old password is required");
                }
            
                if (newPassword == null || newPassword.trim().isEmpty()) {
                    throw new BadRequestException("New password is required");
                }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UnauthorizedException("Incorrect old password"); 
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{5,}$")) {
            throw new BadRequestException("New password must be at least 5 characters long, contain 1 special character, and 1 number");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseModel.success(200, "Password updated successfully", null);
    }

    @Transactional
    public ResponseModel<String> updateProfileImage(String userEmail, MultipartFile file) throws IOException {
        User user = userRepository.findByEmailAndIsDeletedFalse(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file is required"); 
        }

        user.setProfilePicture(file.getBytes());
        userRepository.save(user);

        return ResponseModel.success(200, "Profile Image Updated Successfully", null);
    }

    @Transactional
    public ResponseModel<Map<String, String>> loginUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (authentication.isAuthenticated()) {
                String token = jwtutil.generateToken(email);

                // Fetch user details from database
                User user = userRepository.findByEmailAndIsDeletedFalse(email)
                        .orElseThrow(() -> new UnauthorizedException("User not found"));

                // Get role (assuming user has one role)
                String role = user.getRole().getRoleName();

                // Prepare response
                Map<String, String> responseData = new HashMap<>();
                responseData.put("token", token);
                responseData.put("role", role);
                responseData.put("userId", user.getId().toString()); // Include user ID
                if (user.getTeacher() != null) responseData.put("teacherId", user.getTeacher().getId().toString()); // Include teacher ID

                return ResponseModel.success(200, "Login successful", responseData);
            }
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid email or password"); 
        }

        throw new UnauthorizedException("Invalid email or password"); 
    }


}
