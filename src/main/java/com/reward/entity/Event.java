package com.reward.entity;
 
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(
        name = "student_id", 
        referencedColumnName = "id", 
        nullable = false, 
        foreignKey = @ForeignKey(name = "fk_event_student")
    )
    private User student;

    @ManyToOne
    @JoinColumn(
        name = "badge_id", 
        referencedColumnName = "id", 
        nullable = true, 
        foreignKey = @ForeignKey(name = "fk_event_badge")
    )
    private Badges badge;

    @ManyToOne
    @JoinColumn(
        name = "reward_id", 
        referencedColumnName = "id", 
        nullable = true, 
        foreignKey = @ForeignKey(name = "fk_event_reward")
    )
    private Rewards reward;

    @ManyToOne
    @JoinColumn(
        name = "certificate_id", 
        referencedColumnName = "id", 
        nullable = true, 
        foreignKey = @ForeignKey(name = "fk_event_certificate")
    )
    private Certificates certificate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
