package com.reward.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data

public class AuthResponse {
    private String token;
    private UUID id;
    private String username;
}