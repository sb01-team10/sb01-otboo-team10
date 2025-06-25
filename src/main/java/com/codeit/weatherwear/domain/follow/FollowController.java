package com.codeit.weatherwear.domain.follow;

import com.codeit.weatherwear.domain.follow.dto.FollowDto;
import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import com.codeit.weatherwear.domain.follow.dto.request.FollowCreateRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController {

  private final FollowService followService;

  @PostMapping
  public ResponseEntity<FollowDto> postFollow(
      @RequestBody @Valid FollowCreateRequest followCreateRequest
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(followService.create(followCreateRequest));
  }

  @GetMapping("/summary")
  public ResponseEntity<FollowSummaryDto> getSummary(@RequestParam UUID userId) {
    //이 부분은 나중에 security 관련 세팅이 완료되면 인증 정보를 바탕으로 한 현재 로그인된 유저의 id를 가져올 예정
    UUID myId = UUID.randomUUID();
    return ResponseEntity.ok(followService.getSummary(userId, myId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    followService.delete(id);
    return ResponseEntity.noContent().build();
  }

}
