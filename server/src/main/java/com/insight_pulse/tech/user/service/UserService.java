package com.insight_pulse.tech.user.service;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.quotas.dto.QuotaResponse;
import com.insight_pulse.tech.quotas.service.QuotaService;
import com.insight_pulse.tech.security.principal.UserDetailsImpl;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;
import com.insight_pulse.tech.user.dto.UserDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final QuotaService quotaService;
    public User processOAuthPostLogin(String email, String name, String googleId) {
        return userRepository.findByGoogleId(googleId).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setFullname(name);
            newUser.setGoogleId(googleId);
            return userRepository.save(newUser);
        });
    }

    public UserDTO getUser(UserDetailsImpl currentUser) {
        QuotaResponse quota = quotaService.getQuota(currentUser.getId());
        return new UserDTO(
            currentUser.getId(),
            currentUser.getFullname(),
            currentUser.getEmail(),
            quota.usedCount(),
            quota.subscription().getName(),
            quota.subscription().getAiLimit(),
            quota.subscription().getId()
        );
    }
}