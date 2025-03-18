
package com.reward.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor

public class StudentBadgesId implements Serializable {
    private UUID studentId;
    private UUID badgeId;
}