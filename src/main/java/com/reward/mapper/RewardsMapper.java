package com.reward.mapper;

import com.reward.dto.RewardsDTO;
import com.reward.entity.Rewards;
import java.util.Base64;

public class RewardsMapper {

    public static RewardsDTO toDTO(Rewards reward) {
        return RewardsDTO.builder()
                .id(reward.getId())
                .name(reward.getName())
                .description(reward.getDescription())
                .image(reward.getImage() != null ? Base64.getEncoder().encodeToString(reward.getImage()) : null)
                .points(reward.getPoints())
                .build();
    }

    public static Rewards toEntity(RewardsDTO rewardDTO) {
        return Rewards.builder()
                .id(rewardDTO.getId())
                .name(rewardDTO.getName())
                .description(rewardDTO.getDescription())
                .image(rewardDTO.getImage() != null ? Base64.getDecoder().decode(rewardDTO.getImage()) : null)
                .points(rewardDTO.getPoints())
                .build();
    }

}
