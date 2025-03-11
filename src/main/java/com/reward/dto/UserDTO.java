package com.reward.dto;

import lombok.*;

import java.util.UUID;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String phoneNo;

    private UUID roleId; 
    private String department;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 5, message = "Year cannot be greater than 5")
    private Integer year;
    private String profileImage;
}
