package com.codeit.weatherwear.domain.security.config;

import com.codeit.weatherwear.domain.security.config.properties.JwtProperties;
import com.codeit.weatherwear.domain.security.config.properties.TimeProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, TimeProperties.class})
public class PropertiesConfig {

}
