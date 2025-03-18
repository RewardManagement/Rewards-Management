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

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public ResponseModel<BadgesDTO> saveOrUpdateBadge(UUID badgeId, BadgesDTO badgesDTO, MultipartFile image) {
        try {
            Badges badge;
            
            if (badgeId != null) {
                // Updating existing badge
                Optional<Badges> optionalBadge = badgesRepository.findById(badgeId);
                if (optionalBadge.isPresent()) {
                    badge = optionalBadge.get();
                } else {
                    return new ResponseModel<>(HttpStatus.NOT_FOUND.value(), "ERROR", "Badge not found", null);
                }
            } else {
                // Creating new badge
                badge = new Badges();
                badge.setId(UUID.randomUUID());
            }

            // Set common fields
            badge.setName(badgesDTO.getName());
            badge.setDescription(badgesDTO.getDescription());
            badge.setPoints(badgesDTO.getPoints());

            // Update image only if provided
            if (image != null && !image.isEmpty()) {
                badge.setImage(image.getBytes());
            }

            // Save badge (either new or updated)
            Badges savedBadge = badgesRepository.save(badge);

            return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", 
                    (badgeId == null ? "Badge created successfully" : "Badge updated successfully"), 
                    BadgesMapper.toDTO(savedBadge));

        } catch (IOException e) {
            return new ResponseModel<>(HttpStatus.BAD_REQUEST.value(), "ERROR", "Failed to process image", null);
        } catch (Exception e) {
            return new ResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ERROR", "Operation failed: " + e.getMessage(), null);
        }
    }

    public ResponseModel<String> deleteBadge(UUID id) {
        Optional<Badges> badgeOptional = badgesRepository.findById(id);
        if (badgeOptional.isPresent()) {
            badgesRepository.softDeleteBadge(id);
            return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badge soft deleted successfully", "Soft Deleted Badge ID: " + id);
        } else {
            return new ResponseModel<>(HttpStatus.NOT_FOUND.value(), "ERROR", "Badge not found", null);
        }
    }
    
}
