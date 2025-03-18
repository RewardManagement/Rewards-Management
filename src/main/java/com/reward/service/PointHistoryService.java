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
import java.util.stream.Collectors;

@Service
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PointHistoryMapper pointHistoryMapper;

    public PointHistoryService(PointHistoryRepository pointHistoryRepository, 
                               UserRepository userRepository, 
                               EventRepository eventRepository, 
                               PointHistoryMapper pointHistoryMapper) {
        this.pointHistoryRepository = pointHistoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.pointHistoryMapper = pointHistoryMapper;
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

    public ResponseModel<List<PointHistoryDTO>> getPointHistoryByStudent(UUID studentId) {
        List<PointHistoryDTO> historyList = pointHistoryRepository.findByStudentId(studentId)
                .stream()
                .map(pointHistoryMapper::toDTO)  
 
                .collect(Collectors.toList());
    
        return ResponseModel.success(200, "Point history retrieved successfully", historyList);
    }
    
    
}
