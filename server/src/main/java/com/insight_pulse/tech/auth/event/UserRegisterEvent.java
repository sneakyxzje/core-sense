package com.insight_pulse.tech.auth.event;


import com.insight_pulse.tech.user.domain.User;

import lombok.Getter;


@Getter
public class UserRegisterEvent {
    private final User user;
    
    public UserRegisterEvent(User user) {
        this.user = user;
    }
}
