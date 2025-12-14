package com.insight_pulse.tech.auth.service;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.insight_pulse.tech.auth.dto.login.LoginRequest;
import com.insight_pulse.tech.auth.dto.register.RegisterRequest;
import com.insight_pulse.tech.auth.dto.register.RegisterResponse;
import com.insight_pulse.tech.security.principal.UserDetailsImpl;
import com.insight_pulse.tech.security.token.JwtTokenProvider;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    
    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;
    
    public RegisterResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullname(request.fullname());
        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getId());
    }   

    public ResponseCookie login(LoginRequest request) {
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        int userId = userDetails.getId();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String token = jwtTokenProvider.generateToken(userId, role);
        return ResponseCookie.from("jwt", token)
        .httpOnly(true)
        .secure(false) 
        .path("/")
        .maxAge(24 * 60 * 60)
        .sameSite("Strict")
        .build();
    }

    public ResponseCookie logout() {
        return ResponseCookie.from("jwt")
        .httpOnly(true)
        .secure(false) 
        .path("/")
        .maxAge(0)
        .sameSite("Strict")
        .build();
    }
}
