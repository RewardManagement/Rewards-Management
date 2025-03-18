package com.reward.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardsDTO {

    private UUID id;    

    @NotBlank(message = "Name is required and cannot be empty")
    private String name;

    @NotBlank(message = "Description is required and cannot be empty")
    private String description;

    private String image;  

    @NotNull(message = "Points cannot be null")
    private Integer points;
}
