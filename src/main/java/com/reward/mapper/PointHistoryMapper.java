package com.reward.mapper;

import com.reward.dto.PointHistoryDTO;
import com.reward.entity.PointHistory;
import com.reward.entity.Event;
import com.reward.entity.User;

import lombok.Builder;

@Builder
public class PointHistoryMapper {

    public static PointHistoryDTO toDTO(PointHistory pointHistory) {
        return PointHistoryDTO.builder()
                .id(pointHistory.getId())
                .studentId(pointHistory.getStudent().getId())
                .eventId(pointHistory.getEvent().getId())
                .pointsChanged(pointHistory.getPointsChanged())
                .build();
    }

    public static PointHistory toEntity(PointHistoryDTO dto, User student, Event event) {
        return PointHistory.builder()
                .student(student)
                .event(event)
                .pointsChanged(dto.getPointsChanged())
                .build();
    }
}
