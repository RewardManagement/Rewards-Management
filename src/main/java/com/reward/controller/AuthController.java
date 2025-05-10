package com.reward.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reward.service.UserService;

import lombok.RequiredArgsConstructor;
import java.util.Map;
import com.reward.dto.AuthRequest;
import com.reward.exception.UnauthorizedException;
import com.reward.responsemodel.ResponseModel;
import com.reward.security.JwtUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;


    @PostMapping("/login")
    public ResponseEntity<ResponseModel<Map<String, String>>>login(@RequestBody AuthRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseModel<String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Token is missing or invalid");
        }

        String token = authHeader.substring(7).trim();
        jwtUtil.invalidateToken(token);  

        return ResponseEntity.ok(ResponseModel.success(200, "Logout successful", null));
    }

}