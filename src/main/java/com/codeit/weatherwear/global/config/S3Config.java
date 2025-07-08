package com.codeit.weatherwear.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/** AWS S3 관련 설정을 구성하는 Configuration 클래스입니다. S3Client와 S3Presigner Bean을 생성하여 DI(의존성 주입)에 사용됩니다. */
@Configuration
public class S3Config {
  @Value("${AWS_S3_ACCESS_KEY")
  private String accessKey;

  @Value("${AWS_S3_SECRET_KEY")
  private String secretKey;

  @Value("${AWS_S3_REGION")
  private String region;

  /** S3Client Bean 생성 - 일반적인 S3 작업(업로드, 다운로드 등)을 수행하기 위한 클라이언트 */
  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
  /** S3Presigner Bean 생성 - Presigned URL을 생성할 때 사용하는 클라이언트 */
  @Bean
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }
}
