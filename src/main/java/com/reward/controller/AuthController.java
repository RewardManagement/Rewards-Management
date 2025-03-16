package com.reward.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reward.service.UserService;
import com.reward.dto.AuthRequest;
import com.reward.responsemodel.ResponseModel;

@RestController
@RequestMapping ("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    public ResponseEntity<ResponseModel<String>> login(@RequestBody AuthRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword()));
    }

}