package com.reward.service;

import com.reward.dto.UserDTO;
import com.reward.entity.User;
import com.reward.entity.Role;
import com.reward.exception.ResourceNotFoundException;
import com.reward.exception.UnauthorizedException;
import com.reward.exception.AlreadyExistsException;
import com.reward.exception.BadRequestException; 
import com.reward.mapper.UserMapper;
import com.reward.repository.UserRepository;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtutil;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseModel<List<UserDTO>> getAllUsers(String roleName) {
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
    public ResponseModel<UserDTO> getUserById(UUID userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseModel.success(200, "User retrieved successfully", userMapper.toDTO(user));
    }

    @Transactional
    public ResponseModel<String> createOrUpdateUser(UUID userId, String roleName, UserDTO userDTO, MultipartFile image) throws IOException {

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid role provided"));

        User teacher = null;

        if ("STUDENT".equalsIgnoreCase(roleName)) {
            if (userDTO.getTeacherId() == null) {
                throw new BadRequestException("Teacher ID is required for students"); // ✅ Throw BadRequestException
            }
            if (userDTO.getYear() == null) {
                throw new BadRequestException("Year is required for students"); // ✅ Throw BadRequestException
            }

            teacher = userRepository.findByIdAndIsDeletedFalse(userDTO.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));
        } else {
            if (userDTO.getTeacherId() != null) {
                throw new BadRequestException("Teacher ID should not be provided for non-students"); // ✅ Validation
            }
            if (userDTO.getYear() != null) {
                throw new BadRequestException("Year should not be provided for non-students"); // ✅ Validation
            }
        }

        if (userId == null) {
            boolean userExists = userRepository.existsByEmailAndIsDeletedFalse(userDTO.getEmail());
            if (userExists) {
                throw new AlreadyExistsException("A user with this email already exists"); // 🔹 Throw UserAlreadyExistsException
            }
            if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
                throw new BadRequestException("Password is required"); // ✅ Changed to throw BadRequestException
            }
            User newUser = userMapper.toEntity(userDTO);
            newUser.setRole(role);
            newUser.setTeacher(teacher);

            if (image != null && !image.isEmpty()) {
                newUser.setProfilePicture(image.getBytes()); // ✅ Set the image only if valid
            }            

            userRepository.save(newUser);
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
    public ResponseModel<String> changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UnauthorizedException("Incorrect old password"); // ✅ Throw UnauthorizedException
        }

        if (!newPassword.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{6,}$")) {
            throw new BadRequestException("New password must be at least 6 characters long, contain 1 special character, and 1 number");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseModel.success(200, "Password updated successfully", null);
    }

    @Transactional
    public ResponseModel<String> updateProfileImage(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile image file is required"); // ✅ Added validation
        }

        user.setProfilePicture(file.getBytes());
        userRepository.save(user);

        return ResponseModel.success(200, "Profile Image Updated Successfully", null);
    }

    @Transactional
    public ResponseModel<String> loginUser(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            if (authentication.isAuthenticated()) {
                String token = jwtutil.generateToken(email);
                return ResponseModel.success(200, "Login successful", token);
            }
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid email or password"); // ✅ Throwing UnauthorizedException
        }

        throw new UnauthorizedException("Invalid email or password"); // Fallback (should never reach)
    }

}
