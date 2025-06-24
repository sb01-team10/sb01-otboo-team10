package com.codeit.weatherwear.domain.user.dto;

import com.codeit.weatherwear.domain.user.entity.OAuthProvider;
import com.codeit.weatherwear.domain.user.entity.Role;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserDto {

    private final UUID id;
    private final Instant createdAt;
    private final String email;
    private final String name;
    private final Role role;
    private final List<OAuthProvider> linkedOAuthProviders;
    private final boolean locked;

}
