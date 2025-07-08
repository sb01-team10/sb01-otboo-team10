package com.codeit.weatherwear.global.storage.s3;

import com.codeit.weatherwear.global.exception.s3.S3DeleteException;
import com.codeit.weatherwear.global.exception.s3.S3PresignedException;
import com.codeit.weatherwear.global.exception.s3.S3UploadException;
import com.codeit.weatherwear.global.storage.ThumbnailImageStorage;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ThumbnailImageStorage implements ThumbnailImageStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  @Value("${AWS_S3_BUCKET}")
  private String bucket;

  @Value("${AWS_S3_PRESIGNED_URL_EXPIRATION}")
  private long presignedUrlExpirationSeconds;

  // S3에 이미지를 저장하고 해당 파일의 key를 반환
  @Override
  public String upload(MultipartFile file) {
    String key = "image/" + UUID.randomUUID();

    PutObjectRequest request=
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(file.getContentType())
            .build();

    try {
      s3Client.putObject(
          request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
      log.info("[S3 업로드 성공] key: {}", key);
    } catch (IOException | SdkClientException | S3Exception e) {
      log.error("[S3 업로드 실패] 파일명: {}, 오류: {}", file.getOriginalFilename(), e.toString());
      throw new S3UploadException();
    }
    return key;
  }

  //주어진 URL에 해당하는 S3객체 삭제
  @Override
  public void delete(String url) {
    String key = extractKeyFromUrl(url);
    log.info("[S3 삭제 요청] key: {}", key);
    try{
      s3Client.deleteObject(DeleteObjectRequest.builder().
          bucket(bucket)
          .key(key)
          .build());
      log.info("[S3 삭제 완료] key: {}", key);
    }catch (SdkClientException | S3Exception e) {
      log.error("[S3 삭제 실패] key: {}, 오류: {}", key, e.toString());
      throw new S3DeleteException();
    }
  }

  // 주어진 key에 대해 Presigned URL을 생성하여 반환
  // Presigned URL은 일시적으로 접근 가능한 S3 다운로드 링크
  @Override
  public String get(String key) {
    GetObjectRequest getObjectRequest=
        GetObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .build();
    GetObjectPresignRequest presignRequest =
        GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(presignedUrlExpirationSeconds))
            .getObjectRequest(getObjectRequest)
            .build();
    try {
      PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
      log.info("[Presigned URL 생성 완료] key: {}, url: {}", key, presignedRequest.url());
      return presignedRequest.url().toString();
    } catch (SdkClientException | S3Exception e) {
      log.error("[Presigned URL 생성 실패] key: {}, 오류: {}", key, e.toString());
      throw new S3PresignedException();
    }
  }

  // https://bucket.s3.region.amazonaws.com/key 형식에서 key만 추출
  private String extractKeyFromUrl(String url) {
    URI uri = URI.create(url);
    String key=uri.getPath().substring(1);
    log.debug("[S3 Key 추출] URL: {}, 추출된 key: {}", url, key);
    return key;
  }

}
