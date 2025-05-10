package com.reward.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgesDTO {

    
    private UUID id;

    @NotBlank(message = "Badge name cannot be blank")
    private String name;

    private String image;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Points cannot be null")
    
    private Integer points;
}
