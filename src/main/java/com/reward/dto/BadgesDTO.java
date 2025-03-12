package com.reward.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadgesDTO {
    
    private UUID id;
    private String name;
    private String image;
    private String description;
    private Integer points;
    
   
}