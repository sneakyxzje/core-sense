package com.insight_pulse.tech.security.context;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.insight_pulse.tech.security.principal.UserDetailsImpl;

@Component
public class CurrentUserProvider {
    
    public UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }
    
    public int getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
