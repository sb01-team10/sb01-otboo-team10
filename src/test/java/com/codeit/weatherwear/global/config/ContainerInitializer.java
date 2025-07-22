package com.codeit.weatherwear.global.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public class ContainerInitializer implements
    ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>("postgres:17-alpine")
          .withDatabaseName("weatherwear")
          .withUsername("user")
          .withPassword("password");

  static {
    POSTGRES.start();
  }

  @Override
  @DynamicPropertySource
  public void initialize(ConfigurableApplicationContext applicationContext) {
    TestPropertyValues.of(
        "spring.datasource.url=" + POSTGRES.getJdbcUrl(),
        "spring.datasource.username=" + POSTGRES.getUsername(),
        "spring.datasource.password=" + POSTGRES.getPassword()
    ).applyTo(applicationContext.getEnvironment());
  }
}
