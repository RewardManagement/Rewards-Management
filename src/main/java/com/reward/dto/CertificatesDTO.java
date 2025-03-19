package com.reward.dto;
 
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.*;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificatesDTO {
 
    private UUID id;
 
    @NotNull(message = "Student ID is required")
    private UUID studentId;
 
    @NotBlank(message = "Certificate is required")
    private String fileData;
 
    private String fileName;
 
    private String category;
 
    private String status;
 
    private Integer points;
}