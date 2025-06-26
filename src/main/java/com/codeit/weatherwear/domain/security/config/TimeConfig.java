package com.codeit.weatherwear.domain.security.config;

import com.codeit.weatherwear.domain.security.config.properties.TimeProperties;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    @Bean
    public Clock clock(TimeProperties timeProperties) {
        return Clock.system(timeProperties.getZoneId());
    }
}
