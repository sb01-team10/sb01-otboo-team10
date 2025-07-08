package com.codeit.weatherwear.global.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ThumbnailImageStorage {
  /** 이미지를 업로드하고, 접근 가능한 URL을 반환한다. */
  String upload(MultipartFile file);

  /** 저장된 이미지 URL로부터 객체를 삭제한다. */
  void delete(String url);

  /** 저장된 이미지 URL을 반환한다 (필요 시 presigned URL 포함 가능) */
  String get(String key); // key 또는 uuid
}
