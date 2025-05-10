package com.reward.mapper;
 
import com.reward.dto.CertificatesDTO;
import com.reward.entity.Certificates;
import org.springframework.stereotype.Component;
 
import java.util.Base64;
 
@Component
public class CertificatesMapper {
 
    public CertificatesDTO toDTO(Certificates certificates) {
        return CertificatesDTO.builder()
                .id(certificates.getId())
                .studentName(certificates.getStudent() != null ? certificates.getStudent().getName() : null)
                .category(certificates.getCategory())
                .status(certificates.getStatus())
                .points(certificates.getPoints())
                .fileName(certificates.getFileName())
                .createdAt(certificates.getCreatedAt())
                .fileData(certificates.getFileData() != null
                    ? Base64.getEncoder().encodeToString(certificates.getFileData())  
                    : null)
                .build();
    }
 
    public Certificates toEntity(CertificatesDTO certificatesDTO) {
        Certificates certificates = Certificates.builder()
                .id(certificatesDTO.getId())
                .category(certificatesDTO.getCategory())
                .status(certificatesDTO.getStatus())
                .points(certificatesDTO.getPoints())
                .build();
 
        // Convert Base64-encoded file data to byte[]
        if (certificatesDTO.getFileData() != null) {
            certificates.setFileData(Base64.getDecoder().decode(certificatesDTO.getFileData()));
        }
 
        return certificates;
    }
}