package com.codeit.weatherwear.domain.security.config.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@ConfigurationProperties(prefix = "jwt")
@RequiredArgsConstructor
public class JwtProperties {

    private final String issuer;

    private final String secret;

    private final TokenConfig accessToken;

    private final TokenConfig refreshToken;

    @Getter
    @RequiredArgsConstructor
    public static class TokenConfig {

        private final long validitySeconds;
    }
}
