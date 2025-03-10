package com.reward.service;

import com.reward.dto.UserDTO;
import com.reward.entity.User;
import com.reward.entity.Role;
import com.reward.exception.ResourceNotFoundException;
import com.reward.exception.UnauthorizedException;
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

    public ResponseModel<List<UserDTO>> getAllUsers(String roleName) {
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

    public ResponseModel<UserDTO> getUserById(UUID userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseModel.success(200, "User retrieved successfully", userMapper.toDTO(user));
    }


    //now no password

    public ResponseModel<String> createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO); // Convert DTO to Entity
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        Role role = roleRepository.findById(userDTO.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

    
        user.setRole(role);
        userRepository.save(user);
        return ResponseModel.success(201, "User created successfully", null);
    }
    
    @Transactional
    public ResponseModel<String> softDeleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
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
            throw new UnauthorizedException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseModel.success(200, "Password updated successfully", null);
    }


    @Transactional
    public ResponseModel<String> updateUserProfile(UUID userId, UserDTO userDTO) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNo(userDTO.getPhoneNo());
        user.setDepartment(userDTO.getDepartment());
        user.setYear(userDTO.getYear());

        userRepository.save(user);
        return ResponseModel.success(200, "User profile updated successfully", null);
    }


    @Transactional
    public ResponseModel<byte[]> getUserProfileImage(UUID userId) {
    User user = userRepository.findByIdAndIsDeletedFalse(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (user.getProfilePicture() == null) {
        throw new ResourceNotFoundException("Profile image not found");
    }

    return ResponseModel.success(200, "Profile image retrieved", user.getProfilePicture());
}


    @Transactional
    public ResponseModel<String> saveProfileImage(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setProfilePicture(file.getBytes());
        userRepository.save(user);

        return ResponseModel.success(201, "Profile Image Uploaded", null);
    }

    @Transactional
    public ResponseModel<String> updateProfileImage(UUID userId, MultipartFile file) throws IOException {
        return saveProfileImage(userId, file);
    }

    @Transactional
    public ResponseModel<String> deleteProfileImage(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getProfilePicture() == null) {
            throw new ResourceNotFoundException("No profile image found");
        }

        user.setProfilePicture(null);
        userRepository.save(user);

        return ResponseModel.success(200, "Profile Image Removed", null);
    }

    public String loginUser (String email,String password){
        Authentication authentication = authenticationManager.authenticate (new UsernamePasswordAuthenticationToken(email,password));
        //SecurityContextHolder.getContext().setAuthentication(authentication);
        if (authentication.isAuthenticated()) {
            return jwtutil.generateToken(email);
        } else {
            return "fail";
        }
    }
}
