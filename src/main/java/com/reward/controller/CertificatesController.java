package com.reward.controller;
 
import com.reward.dto.CertificatesDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.CertificatesService;
 
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
 
 
import java.io.IOException;
import java.util.List;
import java.util.UUID;
 
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificatesController {
 
    private final CertificatesService certificatesService;
 
 
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping
    public ResponseEntity<ResponseModel<List<CertificatesDTO>>> getAllCertificates(
            @RequestParam(required = false) UUID userId) {
        return ResponseEntity.ok(certificatesService.getAllCertificates(userId));
    }
    
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{certificateId}")
    public ResponseEntity<ResponseModel<CertificatesDTO>> getCertificateById(@PathVariable UUID certificateId) {
        return ResponseEntity.ok(certificatesService.getCertificateById(certificateId));
    }
 
    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel<String>> createCertificate(
            @RequestParam(value = "studentId", required = false) UUID studentId,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
 
        return ResponseEntity.ok(certificatesService.createCertificate(studentId, file));
    }
 
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @DeleteMapping("/{certificateId}")
    public ResponseEntity<ResponseModel<String>> deleteCertificate(@PathVariable UUID certificateId) {
        return ResponseEntity.ok(certificatesService.softDeleteCertificate(certificateId));
    }
 
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/{certificateId}/review")
    public ResponseEntity<ResponseModel<String>> reviewCertificate(
            @PathVariable UUID certificateId,
            @RequestParam(value = "reviewerId", required = false) UUID reviewerId,  
            @RequestParam(value = "points", required = false) Integer points,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "category", required = false) String category) {
 
        return ResponseEntity.ok(certificatesService.reviewCertificate(certificateId, reviewerId, points, status, category));
    }
 
}