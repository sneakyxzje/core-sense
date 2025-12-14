package com.insight_pulse.tech.security.principal;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.insight_pulse.tech.user.domain.User;

public class UserDetailsImpl implements UserDetails {
    
    private final int id;
    private final String fullname;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(int id, String fullname, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl from(User u) {
        GrantedAuthority authority = () -> u.getRole().name();
        return new UserDetailsImpl(u.getId(), u.getFullname(), u.getEmail(), u.getPassword(), List.of(authority));
    }


    public int getId() { return id; }

    public String getFullname() { return fullname;}

    public String getEmail() { return email; }

    @Override
    public String getUsername() { return email; }

    public String getPassword() { return password;}
    
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
}
