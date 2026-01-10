package com.insight_pulse.tech.auth.service;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
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
import com.insight_pulse.tech.auth.event.UserRegisterEvent;
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

    private final ApplicationEventPublisher eventPublisher;
    public RegisterResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullname(request.fullname());
        User saved = userRepository.save(user);
        eventPublisher.publishEvent(new UserRegisterEvent(user));
        return new RegisterResponse(saved.getId());
    }   

    public List<ResponseCookie> login(LoginRequest request) {
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        int userId = userDetails.getId();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        String token = jwtTokenProvider.generateToken(userId, role);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        jwtTokenProvider.storeToken(String.valueOf(userId), refreshToken);
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false) 
                .path("/")
                .maxAge(7 * 24 * 60 * 60) 
                .sameSite("Strict") 
                .build();

            ResponseCookie accessTokenCookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(false) 
                .path("/")
                .maxAge(jwtTokenProvider.getExpiryInSeconds())
                .sameSite("Strict")
                .build();
            return List.of(refreshTokenCookie, accessTokenCookie);
    }

    /* 
        Return về 1 list, chứa 2 cái AT và RT cho client
    */
    public List<ResponseCookie> refreshToken(String refreshToken) {
        String userIdStr = jwtTokenProvider.getUserIdFromRefreshToken(refreshToken);
        if (userIdStr == null) {
            throw new RuntimeException("userId not found");
        }

        Integer userId = Integer.parseInt(userIdStr);
        User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        String role = user.getRole().name();

        jwtTokenProvider.deleteRefreshToken(refreshToken);

        String newAccessToken = jwtTokenProvider.generateToken(userId, role);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        jwtTokenProvider.storeToken(userIdStr, newRefreshToken);

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", newRefreshToken)
                .httpOnly(true)
                .secure(false) 
                .path("/")
                .maxAge(7 * 24 * 60 * 60) 
                .sameSite("Strict") 
                .build();

        ResponseCookie accessTokenCookie = ResponseCookie.from("jwt", newAccessToken)
                .httpOnly(true)
                .secure(false) 
                .path("/")
                .maxAge(jwtTokenProvider.getExpiryInSeconds())
                .sameSite("Strict")
                .build();
        return List.of(refreshTokenCookie, accessTokenCookie);
    }

    public List<ResponseCookie> logout(String refreshToken) {
        jwtTokenProvider.deleteRefreshToken(refreshToken);
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "").path("/").maxAge(0).build();
        ResponseCookie rtCookie = ResponseCookie.from("refresh_token", "").path("/").maxAge(0).build();
    
    return List.of(jwtCookie, rtCookie);
    }
}
