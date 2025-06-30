package com.codeit.weatherwear.domain.feed.controller;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedGetParamRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import com.codeit.weatherwear.domain.feed.service.FeedService;
import com.codeit.weatherwear.global.response.PageResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

  private final FeedService feedService;

  // 피드 목록 조회
  @GetMapping
  public ResponseEntity<PageResponse<FeedDto>> getFeedList(
      @ModelAttribute @Valid FeedGetParamRequest paramRequest
  ) {
    return ResponseEntity.ok(feedService.getFeedList(paramRequest));
  }

  // 피드 등록
  @PostMapping
  public ResponseEntity<FeedDto> createFeed(@RequestBody FeedCreateRequest feedCreateRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(feedService.createFeed(feedCreateRequest));
  }

  // 피드 갱신 (정보 업데이트)
  @PatchMapping("/{feedId}")
  public ResponseEntity<FeedDto> updateFeed(
      @PathVariable UUID feedId,
      @RequestBody FeedUpdateRequest feedUpdateRequest) {
    return ResponseEntity.ok(feedService.updateFeed(feedId, feedUpdateRequest));
  }

  // 피드 삭제
  @DeleteMapping("/{feedId}")
  public ResponseEntity<FeedDto> deleteFeed(@PathVariable UUID feedId) {
    return ResponseEntity.status(HttpStatus.NO_CONTENT).body(feedService.deleteFeed(feedId));
  }
}
