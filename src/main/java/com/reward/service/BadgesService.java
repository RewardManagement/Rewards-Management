package com.reward.service;

import com.reward.dto.BadgesDTO;
import com.reward.entity.Badges;
import com.reward.mapper.BadgesMapper;
import com.reward.repository.BadgesRepository;
import com.reward.responsemodel.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
public class BadgesService {

    @Autowired
    private BadgesRepository badgesRepository;

    public ResponseModel<List<BadgesDTO>> getAllBadges() {
        List<Badges> badgesList = badgesRepository.findAll();
        List<BadgesDTO> badgesDTOList = badgesList.stream()
                .map(BadgesMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badges retrieved successfully", badgesDTOList);
    }

    public ResponseModel<BadgesDTO> getBadgeById(UUID id) {
        Optional<Badges> optionalBadge = badgesRepository.findById(id);
        if (optionalBadge.isPresent()) {
            return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badge found", BadgesMapper.toDTO(optionalBadge.get()));
        } else {
            return new ResponseModel<>(HttpStatus.NOT_FOUND.value(), "ERROR", "Badge not found", null);
        }
    }

    public ResponseModel<BadgesDTO> createBadge(BadgesDTO badgesDTO, MultipartFile image) {
        try {
            // Check if ID is provided and already exists in the database
            if (badgesDTO.getId() != null && badgesRepository.existsById(badgesDTO.getId())) {
                return new ResponseModel<>(HttpStatus.CONFLICT.value(), "ERROR", "Badge already exists with ID: " + badgesDTO.getId(), null);
            }
            
            // Convert DTO to entity and save
            Badges badge = BadgesMapper.toEntity(badgesDTO);
            badge.setId(null); // Ensure a new ID is generated
            Badges savedBadge = badgesRepository.save(badge);
    
            return new ResponseModel<>(HttpStatus.CREATED.value(), "SUCCESS", "Badge created successfully", BadgesMapper.toDTO(savedBadge));
        } catch (Exception e) {
            return new ResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR", "Failed to create badge: " + e.getMessage(), null);
        }
    }
    
    public ResponseModel<BadgesDTO> updateBadge(UUID id, BadgesDTO badgesDTO, MultipartFile image) {
        Optional<Badges> optionalBadge = badgesRepository.findById(id);
        if (optionalBadge.isPresent()) {
            Badges badge = optionalBadge.get();
            badge.setName(badgesDTO.getName());
            badge.setImage(badgesDTO.getImage() != null ? Base64.getDecoder().decode(badgesDTO.getImage()) :null);
            badge.setDescription(badgesDTO.getDescription());
            badge.setPoints(badgesDTO.getPoints());
            
            Badges updatedBadge = badgesRepository.save(badge);

            return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badge updated successfully", BadgesMapper.toDTO(updatedBadge));
        } else {
            return new ResponseModel<>(HttpStatus.NOT_FOUND.value(), "ERROR", "Badge not found", null);
        }
    }

    public ResponseModel<String> deleteBadge(UUID id) {
        if (badgesRepository.existsById(id)) {
            badgesRepository.softDeleteBadge(id);
        return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badge soft deleted successfully", "Soft Deleted Badge ID: " + id);
    } else {
        return new ResponseModel<>(HttpStatus.NOT_FOUND.value(), "ERROR", "Badge not found", null);
    }
    }
}
