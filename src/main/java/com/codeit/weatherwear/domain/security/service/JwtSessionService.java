package com.codeit.weatherwear.domain.security.service;

import com.codeit.weatherwear.domain.security.config.properties.JwtProperties;
import com.codeit.weatherwear.domain.security.repository.JwtSessionRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtSessionService {

    private final JwtSessionRepository jwtSessionRepository;
    private final JwtProperties jwtProperties;
    private final Clock clock;

    public enum TokenType {
        ACCESS, REFRESH
    }

    // 액세스 토큰 발급
    public String issueAccessToken(UUID userId) {
        return createTokenWithClaims(
            userId,
            jwtProperties.getAccessToken().getValiditySeconds(),
            TokenType.ACCESS
        );
    }

    // 리프레시 토큰 발급
    public String issueRefreshToken(UUID userId) {
        return createTokenWithClaims(
            userId,
            jwtProperties.getRefreshToken().getValiditySeconds(),
            TokenType.REFRESH
        );
    }

    // 토큰 생성
    private String createTokenWithClaims(UUID userId, long validitySeconds, TokenType tokenType) {
        Instant now = clock.instant();
        Instant expirationTime = now.plusSeconds(validitySeconds);

        JwtBuilder builder = Jwts.builder()
            .header()
            .add("typ", "JWT")
            .and()
            .issuer(jwtProperties.getIssuer())
            .subject(userId.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expirationTime))
            .claim("type", tokenType.name());

        return builder
            .signWith(getSigningKey(), SIG.HS256)
            .compact();
    }

    // 토큰 유효성 인증
    public boolean validateToken(String token) {
        try {

            JwtParser parser = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build();
            parser.parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    // 서명키 생성
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 토큰에서 사용자 ID 추출
    public UUID extractUserId(String token) {
        JwtParser parser = Jwts.parser()
            .setSigningKey(getSigningKey())
            .build();

        String subject = parser
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();

        return UUID.fromString(subject);
    }

}
