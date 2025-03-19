package com.reward.service;

import com.reward.dto.BadgesDTO;
import com.reward.entity.Badges;
import com.reward.exception.AlreadyExistsException;
import com.reward.exception.BadRequestException;
import com.reward.exception.ResourceNotFoundException;
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
        List<Badges> badgesList = badgesRepository.findAllByIsDeletedFalse();
        List<BadgesDTO> badgesDTOList = badgesList.stream()
                .map(BadgesMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badges retrieved successfully", badgesDTOList);
    }

    public ResponseModel<BadgesDTO> getBadgeById(UUID id) {
        Badges badge = badgesRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

        return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badge found", BadgesMapper.toDTO(badge));
    }

    public ResponseModel<BadgesDTO> saveOrUpdateBadge(UUID badgeId, BadgesDTO badgesDTO, MultipartFile image) {
        try {
            Badges badge;

            if (badgeId != null) {
                // Updating existing badge
                badge = badgesRepository.findByIdAndIsDeletedFalse(badgeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));
            } else {
                // Creating new badge
                if (badgesRepository.existsByNameAndIsDeletedFalse(badgesDTO.getName())) {
                    throw new AlreadyExistsException("Badge with name '" + badgesDTO.getName() + "' already exists");
                }
                badge = new Badges();
                
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
            badgesRepository.save(badge);

            return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS",
                    (badgeId == null ? "Badge created successfully" : "Badge updated successfully"),
                    null);

        } catch (IOException e) {
            throw new BadRequestException("Failed to process image: " + e.getMessage());
        } catch (Exception e) {
            throw new BadRequestException("Operation failed: " + e.getMessage());
        }
    }

    public ResponseModel<String> deleteBadge(UUID id) {
        Badges badge = badgesRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge not found"));

        badgesRepository.softDeleteBadge(id);

        return new ResponseModel<>(HttpStatus.OK.value(), "SUCCESS", "Badge soft deleted successfully", "Soft Deleted Badge ID: " + id);
    }
}
