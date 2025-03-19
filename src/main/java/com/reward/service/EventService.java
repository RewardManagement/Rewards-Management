package com.reward.service;

import org.springframework.stereotype.Service;

import com.reward.entity.Badges;
import com.reward.entity.Certificates;
import com.reward.entity.Event;
import com.reward.entity.Rewards;
import com.reward.entity.User;
import com.reward.repository.BadgesRepository;
import com.reward.repository.CertificateRepository;
import com.reward.repository.EventRepository;
import com.reward.repository.RewardsRepository;
import com.reward.repository.UserRepository;
import com.reward.exception.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
 
@Service

public class EventService {

    private final EventRepository eventRepository;
    private final PointHistoryService pointHistoryService;
    private final UserRepository userRepository;
    private final BadgesRepository badgeRepository;
    private final CertificateRepository certificateRepository;
    private final RewardsRepository rewardRepository;

    public EventService(EventRepository eventRepository, PointHistoryService pointHistoryService,
                        UserRepository userRepository, BadgesRepository badgeRepository,
                        CertificateRepository certificateRepository, RewardsRepository rewardRepository) {
        this.eventRepository = eventRepository;
        this.pointHistoryService = pointHistoryService;
        this.userRepository = userRepository;
        this.badgeRepository = badgeRepository;
        this.certificateRepository = certificateRepository;
        this.rewardRepository = rewardRepository;
    }
 
    @Transactional
    public Event createEvent(UUID studentId, UUID badgeId, UUID certificateId, UUID rewardId, Integer points) {
        
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Badges badge = badgeId != null ? badgeRepository.findById(badgeId)
            .orElseThrow(() -> new ResourceNotFoundException("Badge not found")) : null;

        Certificates certificate = certificateId != null ? certificateRepository.findById(certificateId)
            .orElseThrow(() -> new ResourceNotFoundException("Certificate not found")) : null;

        Rewards reward = rewardId != null ? rewardRepository.findById(rewardId)
            .orElseThrow(() -> new ResourceNotFoundException("Reward not found")) : null;

        Event event = Event.builder()
                .student(student)
                .badge(badge)
                .certificate(certificate)
                .reward(reward)
                .build();

        event = eventRepository.save(event);

        pointHistoryService.recordPointHistory(studentId, event.getId(), points);

        return event;
    }


}

 