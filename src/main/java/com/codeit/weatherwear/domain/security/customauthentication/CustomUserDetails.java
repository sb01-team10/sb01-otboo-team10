package com.codeit.weatherwear.domain.security.customauthentication;

import com.codeit.weatherwear.domain.user.entity.Role;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

    private final UUID userID;
    private final String email;
    private final String password;

    private final Role role;

    public CustomUserDetails(UUID userId, String email, String password, Role role) {
        this.userID = userId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_" + this.role.name()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    // email + password로 로그인하므로 name 대신 email을 return
    @Override
    public String getUsername() {
        return email;
    }

    public UUID getUserId() {
        return this.userID;
    }
}
