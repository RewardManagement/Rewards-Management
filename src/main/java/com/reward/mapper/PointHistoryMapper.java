package com.reward.mapper;

import com.reward.dto.PointHistoryDTO;
import com.reward.entity.PointHistory;
import org.springframework.stereotype.Component;

@Component
public class PointHistoryMapper {
    public static PointHistoryDTO toDTO(PointHistory pointHistory) {
        String fileName = "Unknown";

        if (pointHistory.getEvent().getCertificate() != null) {
            fileName = pointHistory.getEvent().getCertificate().getFileName(); // ✅ Get certificate name
        } else if (pointHistory.getEvent().getBadge() != null) {
            fileName = pointHistory.getEvent().getBadge().getName(); // ✅ Get badge name
        } else if (pointHistory.getEvent().getReward() != null) {
            fileName = pointHistory.getEvent().getReward().getName(); // ✅ Get reward name
        }

        return PointHistoryDTO.builder()
                .id(pointHistory.getId())
                .fileName(fileName) // ✅ Set dynamically
                .pointsChanged(pointHistory.getPointsChanged())
                .createdAt(pointHistory.getCreatedAt())
                .isReward(pointHistory.getEvent().getReward() != null)
                .build();
    }
}

