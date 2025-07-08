package com.codeit.weatherwear.domain.security.customauthentication;

import com.codeit.weatherwear.domain.user.entity.Role;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Slf4j
public class CustomUserDetails implements UserDetails, OAuth2User {

  private final UUID userID;
  private final String email;
  private final String password;
  private final Role role;
  private final boolean locked;
  private final Instant tempPasswordExpirationTime;

  private Map<String, Object> attributes;

  public CustomUserDetails(UUID userId, String email, String password, Role role,
      boolean locked, Instant tempPasswordExpirationTime) {
    this.userID = userId;
    this.email = email;
    this.password = password;
    this.role = role;
    this.locked = locked;
    this.tempPasswordExpirationTime = tempPasswordExpirationTime;
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

  @Override
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    if (tempPasswordExpirationTime != null && tempPasswordExpirationTime.isBefore(Instant.now())) {
      log.info("만료된 임시 비밀번호");
      return false;
    }
    return true;
  }

  public UUID getUserId() {
    return this.userID;
  }

  // OAuth2

  @Override
  public String getName() {
    if (attributes != null) {
      return (String) attributes.get("name");
    }
    return null;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }
}
