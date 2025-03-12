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

    private String fileData; // Base64 encoded file content

    private String category;

    @NotBlank(message = "Status is required")
    private String status; 

    private Integer points;
}
