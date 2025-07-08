package com.codeit.weatherwear.domain.clothes.controller;

import com.codeit.weatherwear.domain.clothes.controller.api.ClothApi;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesCreateRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesSearchRequest;
import com.codeit.weatherwear.domain.clothes.dto.request.ClothesUpdateRequest;
import com.codeit.weatherwear.domain.clothes.dto.response.ClothesDto;
import com.codeit.weatherwear.domain.clothes.service.ClothService;
import com.codeit.weatherwear.global.response.PageResponse;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clothes")
public class ClothController implements ClothApi {

  private final ClothService clothService;

  /**
   * 의상을 등록합니다.
   *
   * @param request 등록 의상 정보
   * @return
   */
  @Override
  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<ClothesDto> create(
      @Valid @RequestPart("request") ClothesCreateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    log.info("[옷 등록 요청] 옷 이름: {}, 의상 타입: {}", request.name(), request.type());

    ClothesDto createClothes = clothService.create(request, Optional.ofNullable(image));
    return ResponseEntity.status(HttpStatus.CREATED).body(createClothes);
  }

    /**
     * 의상을 url으로부터 불러옵니다.
     *
     * @param url 구매 사이트 url
     * @return
     */
    @Override
    @GetMapping("/extractions")
    public ResponseEntity<ClothesDto> createFromUrl(@RequestParam String url) {
      log.info("[url로 옷 등록 요청] url: {}", url);

      ClothesDto urlCloth=clothService.createFromUrl(url);
      return ResponseEntity.ok(urlCloth);
    }


  /**
   * 의상을 조회합니다.
   *
   * @param request 조회조건
   * @return
   */
  @Override
  @GetMapping
  public ResponseEntity<PageResponse<ClothesDto>> searchClothes(
    @ModelAttribute @Valid ClothesSearchRequest request) {
      log.info("[옷 목록 조회 요청] ownerId: {}, typeEqual: {}, limit: {}",
          request.ownerId(),request.typeEqual(), request.limit());

    PageResponse<ClothesDto> result = clothService.searchClothes(request);
    return ResponseEntity.ok(result);
  }


  /**
   * 의상을 수정합니다.
   *
   * @param clothesId 수정 요청 ID
   * @param request   수정 요청 DTO
   * @param image     수정 요청 이미지
   * @return
   */
  @Override
  @PreAuthorize("@authorizationEvaluator.isClothOwner(authentication.principal.userId, #clothesId)")
  @PatchMapping(value = "/{id}"
      ,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public ResponseEntity<ClothesDto> update(
      @PathVariable("id") UUID clothesId,
      @RequestPart("request") ClothesUpdateRequest request,
      @RequestPart(value = "image", required = false) MultipartFile image) {
    log.info("[옷 수정 요청] ID: {}, 옷 이름: {}", clothesId, request.name());

    ClothesDto update = clothService.update(clothesId, request,Optional.ofNullable(image));
    return ResponseEntity.ok(update);
  }

  /**
   * 의상을 삭제합니다.
   *
   * @param clothesId 의상 ID
   * @return
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or @authorizationEvaluator.isClothOwner(#clothesId, authentication.principal.userId)")
  @DeleteMapping("/{clothesId}")
  public ResponseEntity<Void> delete(@PathVariable UUID clothesId) {
    log.info("[옷 삭제 요청] ID: {}", clothesId);

    clothService.delete(clothesId);
    return ResponseEntity.noContent().build();
  }

}
