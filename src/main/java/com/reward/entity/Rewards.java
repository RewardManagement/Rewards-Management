package com.reward.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rewards {

@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private UUID id;

@Column(nullable = false, length = 255)
private String name;

@Column(columnDefinition = "TEXT")
private String description;

@Lob 
@Column(nullable = true)
private byte[] image;

@Column(nullable = false)
private Integer points;

@Builder.Default
@Column(nullable = true)
private Boolean isDeleted = false; 

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


