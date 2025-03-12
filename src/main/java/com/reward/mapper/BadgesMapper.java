package com.reward.mapper;

import com.reward.dto.BadgesDTO;
import com.reward.entity.Badges;
import java.util.Base64;

public class BadgesMapper {

    public static BadgesDTO toDTO(Badges badges) {
        if (badges == null) {
            return null;
        }

        return BadgesDTO.builder()
                .id(badges.getId())
                .name(badges.getName())
                .image(badges.getImage() != null ? Base64.getEncoder().encodeToString(badges.getImage()) : null)
                .description(badges.getDescription())
                .points(badges.getPoints())
                
                
                .build();
    }

    public static Badges toEntity(BadgesDTO badgesDTO) {
        if (badgesDTO == null) {
            return null;
        }

        return Badges.builder()
                .id(badgesDTO.getId())  // ID should be auto-generated; only map if necessary
                .name(badgesDTO.getName())
                .image(badgesDTO.getImage() != null ? Base64.getDecoder().decode(badgesDTO.getImage()) : null)
                .description(badgesDTO.getDescription())
                .points(badgesDTO.getPoints())
                
                
                .build();
    }
}

