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

    @Pattern(
    regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
    message = "Invalid email format. Must be in example@domain.com format"
    )
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 5, message = "Password must be at least 5 characters long")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{5,}$",
        message = "Password must contain at least one number and one special character"
    )
    private String password;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phoneNo;

    private String role; 
    
    private UUID teacherId;

    private String department;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 5, message = "Year cannot be greater than 5")
    private Integer year;

    private String profileImage;
}
