package com.reward.dto;

import lombok.*;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardsDTO {
    
    private UUID id;    
    private String name;
    private String description;
    private String image; 
    private Integer points;
    

}
