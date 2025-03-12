package com.reward.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificates {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_certificate_user"))
    private User student; // Foreign key reference to User table (student)

   
    @Lob
    @Column(name = "file_data", nullable = false) 
    private byte[] fileData; 

    @Builder.Default
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false; 

    @Column(name = "points")
    private Integer points;

    @Column(name = "status", nullable = false)
    private String status; // "Pending", "Approved", "Rejected"

    @Column(name = "category")
    private String category; // "Technical", "Cultural", "Sports"

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
