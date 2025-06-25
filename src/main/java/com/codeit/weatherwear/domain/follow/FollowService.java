package com.codeit.weatherwear.domain.follow;

import com.codeit.weatherwear.domain.follow.dto.FollowDto;
import com.codeit.weatherwear.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

  private final FollowRepository followRepository;
  private final UserRepository userRepository;

  @Transactional
  public FollowDto create(FollowCreateRequest request) {
    User follower = userRepository.findById(request.followerId())
        .orElseThrow(UserNotFoundException::new);
    User followee = userRepository.findById(request.followeeId())
        .orElseThrow(UserNotFoundException::new);

    Follow follow = followRepository.save(Follow.create(followee, follower));

    FollowDto dto = FollowDto.from(follow);
    log.info("Follow 생성: {}", dto);

    return dto;
  }

  @Transactional
  public void delete(UUID id) {
    followRepository.findById(id).ifPresent(follow -> {
        followRepository.delete(follow);
        log.info("Follow 삭제. id: {}", id);
    });
  }

}
