package com.reward.controller;

import com.reward.dto.BadgesDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.BadgesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/badges")
public class BadgesController {

    @Autowired
    private BadgesService badgesService;

    // ✅ Retrieve all badges
    @GetMapping
    public ResponseEntity<ResponseModel<List<BadgesDTO>>> getAllBadges() {
        ResponseModel<List<BadgesDTO>> response = badgesService.getAllBadges();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // ✅ Retrieve badge by ID
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<BadgesDTO>> getBadgeById(@PathVariable UUID id) {
        ResponseModel<BadgesDTO> response = badgesService.getBadgeById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(consumes = "multipart/form-data")
public ResponseEntity<ResponseModel<BadgesDTO>> saveOrUpdateBadge(
        @RequestParam(required = false) UUID badgeId,  
        @RequestParam String name,
        @RequestParam String description,
        @RequestParam int points,
        @RequestParam(value = "image", required = false) MultipartFile image) {

    BadgesDTO badgesDTO = new BadgesDTO();
    badgesDTO.setName(name);
    badgesDTO.setDescription(description);
    badgesDTO.setPoints(points);

    ResponseModel<BadgesDTO> response = badgesService.saveOrUpdateBadge(badgeId, badgesDTO, image);
    return ResponseEntity.status(HttpStatus.OK).body(response);
}


    // ✅ Soft delete a badge
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<String>> deleteBadge(@PathVariable UUID id) {
        ResponseModel<String> response = badgesService.deleteBadge(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
