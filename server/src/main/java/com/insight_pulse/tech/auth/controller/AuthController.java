package com.insight_pulse.tech.auth.controller;

import java.net.URI;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.insight_pulse.tech.auth.dtos.login.LoginRequest;
import com.insight_pulse.tech.auth.dtos.register.RegisterRequest;
import com.insight_pulse.tech.auth.dtos.register.RegisterResponse;
import com.insight_pulse.tech.auth.service.AuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id()) 
            .toUri();
        return ResponseEntity
            .created(location) 
            .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        ResponseCookie response = authService.login(request);
        return ResponseEntity.ok().header("Set-Cookie", response.toString()).body("Login Successfully");
    }
}
