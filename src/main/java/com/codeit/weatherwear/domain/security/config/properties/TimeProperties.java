package com.codeit.weatherwear.domain.security.config.properties;

import java.time.ZoneId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "time")
@Getter
@RequiredArgsConstructor
public class TimeProperties {

    private final ZoneId zoneId;
}
