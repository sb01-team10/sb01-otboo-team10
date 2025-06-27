package com.codeit.weatherwear.domain.follow;

import com.codeit.weatherwear.domain.follow.dto.FollowDto;
import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import com.codeit.weatherwear.domain.follow.dto.request.FollowCreateRequest;
import com.codeit.weatherwear.domain.follow.exception.FollowDuplicatedException;
import com.codeit.weatherwear.domain.follow.repository.FollowRepository;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.request.SortDirection;
import com.codeit.weatherwear.global.response.PageResponse;
import java.time.Instant;
import java.util.List;
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

    //이미 팔로우 한 유저를 팔로우 할 수 없음
    if (followRepository.existsByFolloweeAndFollower(followee, follower)) {
      throw FollowDuplicatedException.withId(followee.getId(), follower.getId());
    }

    Follow follow = followRepository.save(Follow.create(followee, follower));

    FollowDto dto = FollowDto.from(follow);
    log.info("Follow 생성: {}", dto);

    return dto;
  }

  public FollowSummaryDto getSummary(UUID userId, UUID myId) {
    return followRepository.getSummary(userId, myId);
  }

  public PageResponse<FollowDto> getFollowings(UUID followerId, String cursor,
      UUID idAfter, int limit, String nameLike
  ) {
    List<FollowDto> followings = followRepository
        .getFollowings(followerId, cursor, idAfter, limit, nameLike);
    long totalCount = followRepository.countByFollower_Id(followerId);

    return toPageResponse(followings, limit, totalCount);
  }

  public PageResponse<FollowDto> getFollowers(UUID followeeId, String cursor,
      UUID idAfter, int limit, String nameLike
  ) {
    List<FollowDto> followings = followRepository
        .getFollowers(followeeId, cursor, idAfter, limit, nameLike);
    long totalCount = followRepository.countByFollowee_Id(followeeId);
    return toPageResponse(followings, limit, totalCount);
  }

  private PageResponse<FollowDto> toPageResponse(List<FollowDto> followings, int limit, long totalCount) {
    boolean hasNext = followings.size() > limit;
    Instant nextCursor = null;
    UUID nextIdAfter = null;

    if (hasNext) {
      followings.remove(followings.size() - 1);
      FollowDto followDto = followings.get(followings.size() - 1);

      nextCursor = followDto.created();
      nextIdAfter = followDto.id();
    }
    String sortBy = "createdAt";

    return new PageResponse<>(
        followings,
        nextCursor,
        nextIdAfter,
        hasNext,
        totalCount,
        sortBy,
        SortDirection.DESCENDING.name()
    );
  }

  @Transactional
  public void delete(UUID id) {
    followRepository.findById(id).ifPresent(follow -> {
        followRepository.delete(follow);
        log.info("Follow 삭제. id: {}", id);
    });
  }

}
