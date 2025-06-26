package com.codeit.weatherwear.domain.feed.controller;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feeds")
@RequiredArgsConstructor
public class FeedController {

  // 피드 목록 조회
  @GetMapping
  public ResponseEntity<List<FeedDto>> getFeedList(
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) UUID idAfter,
      @RequestParam int limit,
      @RequestParam String sortBy,
      @RequestParam String sortDirection,
      @RequestParam(required = false) String keywordLike,
      @RequestParam(required = false) String skyStatusEqual,
      @RequestParam(required = false) String precipitationTypeEqual,
      @RequestParam(required = false) UUID authorIdEqual
  ) {

    return null;
  }

  // 피드 등록
  @PostMapping
  public ResponseEntity<FeedDto> createFeed(@RequestBody FeedCreateRequest feedCreateRequest) {

    return null;
  }

  // 피드 갱신 (정보 업데이트)
  @PatchMapping("/{feedId}")
  public ResponseEntity<FeedDto> updateFeed(@PathVariable UUID feedId) {
    return null;
  }

  // 피드 삭제
  @DeleteMapping("/{feedId}")
  public ResponseEntity<FeedDto> deleteFeed(@PathVariable UUID feedId) {
    return null;
  }
}
