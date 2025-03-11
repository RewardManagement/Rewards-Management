package com.reward.controller;

import com.reward.dto.CertificatesDTO;
import com.reward.responsemodel.ResponseModel;
import com.reward.service.CertificatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;


import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificatesController {

    private final CertificatesService certificatesService;

    
    @GetMapping
    public ResponseEntity<ResponseModel<List<CertificatesDTO>>> getAllCertificates(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String status) {  // Added second parameter
        return ResponseEntity.ok(certificatesService.getAllCertificates(userId, status));
    }
    
    @GetMapping("/{certificateId}")
    public ResponseEntity<ResponseModel<CertificatesDTO>> getCertificateById(@PathVariable UUID certificateId) {
        return ResponseEntity.ok(certificatesService.getCertificateById(certificateId));
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseModel<String>> createCertificate(
            @RequestParam("studentId") UUID studentId,
            @RequestParam("category") String category,
            @RequestParam("status") String status,
            @RequestParam("points") int points,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        return ResponseEntity.ok(certificatesService.createCertificate(studentId, category, status, points, file));
    }

    @DeleteMapping("/{certificateId}")
    public ResponseEntity<ResponseModel<String>> deleteCertificate(@PathVariable UUID certificateId) {
        return ResponseEntity.ok(certificatesService.deleteCertificate(certificateId));
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseModel<String>> uploadCertificateFile(@RequestParam UUID certificateId, @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(certificatesService.uploadCertificateFile(certificateId, file));
    }

    @PutMapping("/{certificateId}")
public ResponseEntity<ResponseModel<String>> updateCertificate(
        @PathVariable UUID certificateId,
        @RequestParam("category") String category,
        @RequestParam("status") String status,
        @RequestParam("points") int points,
        @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

    return ResponseEntity.ok(certificatesService.updateCertificate(certificateId, category, status, points, file));
}


    
}
