package com.insight_pulse.tech.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.security.principal.UserDetailsImpl;
import com.insight_pulse.tech.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal UserDetailsImpl currentUser) {
        return ResponseEntity.ok(new UserDTO(currentUser.getId(), currentUser.getFullname(), currentUser.getEmail()));
    } 
}
