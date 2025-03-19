package com.reward.service;

import com.reward.dto.PointHistoryDTO;
import com.reward.entity.Event;
import com.reward.entity.PointHistory;
import com.reward.entity.User;
import com.reward.exception.ResourceNotFoundException;
import com.reward.mapper.PointHistoryMapper;
import com.reward.repository.EventRepository;
import com.reward.repository.PointHistoryRepository;
import com.reward.repository.UserRepository;
import com.reward.responsemodel.ResponseModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public PointHistoryService(PointHistoryRepository pointHistoryRepository, 
                               UserRepository userRepository, 
                               EventRepository eventRepository) {
        this.pointHistoryRepository = pointHistoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public void recordPointHistory(UUID studentId, UUID eventId, int points) {
        User student = userRepository.findByIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        PointHistory pointHistory = PointHistory.builder()
                .student(student)
                .event(event)
                .pointsChanged(points)  // Positive for earning, negative for spending
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    @Transactional
        public ResponseModel<List<PointHistoryDTO>> getPointHistoryByStudent(UUID studentId) {
        List<PointHistory> pointHistories = pointHistoryRepository.findByStudentId(studentId);

        if (pointHistories.isEmpty()) {
                throw new ResourceNotFoundException("No point history found for student ID: " + studentId);
        }

        List<PointHistoryDTO> pointHistoryDTOs = pointHistories.stream()
                .map(PointHistoryMapper::toDTO)
                .toList();

        return ResponseModel.success(200, "Point history retrieved successfully", pointHistoryDTOs);
        }
}
