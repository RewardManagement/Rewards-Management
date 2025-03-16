package com.reward.mapper;

import com.reward.dto.UserDTO;
import com.reward.entity.Role;
import com.reward.entity.User;
import com.reward.repository.UserRepository;
import com.reward.repository.RoleRepository;
import com.reward.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .roleId(user.getRole() != null ? user.getRole().getId() : null)
                .teacherId(user.getTeacher() != null ? user.getTeacher().getId() : null)
                .department(user.getDepartment())
                .year(user.getYear())
                .profileImage(user.getProfilePicture() != null 
                    ? Base64.getEncoder().encodeToString(user.getProfilePicture())  
                    : null)
                .build();
    }

    public User toEntity(UserDTO userDTO) {
        User user = User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword())) 
                .phoneNo(userDTO.getPhoneNo())
                .department(userDTO.getDepartment())
                .year(userDTO.getYear())
                .build();

        // Assign Role (if provided)
        if (userDTO.getRoleId() != null) {
            Role role = roleRepository.findById(userDTO.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            user.setRole(role);
        }

        // Assign Teacher (if provided, and only for students)
        if (userDTO.getTeacherId() != null) {
            User teacher = userRepository.findByIdAndIsDeletedFalse(userDTO.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

            user.setTeacher(teacher);
        }

        return user;
    }
}
