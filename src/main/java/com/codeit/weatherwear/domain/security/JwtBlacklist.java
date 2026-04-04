package com.codeit.weatherwear.domain.security;

import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtBlacklist {

  private static final String KEY_PREFIX = "jwt:blacklist:";

  private final StringRedisTemplate redisTemplate;

  public void addBlacklist(String accessToken, Instant expirationTime) {
    Duration ttl = Duration.between(Instant.now(), expirationTime);
    if (ttl.isNegative() || ttl.isZero()) {
      return;
    }
    redisTemplate.opsForValue().set(KEY_PREFIX + accessToken, "", ttl);
  }

  public boolean existsInBlacklist(String accessToken) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + accessToken));
  }
}
