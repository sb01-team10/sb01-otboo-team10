package com.codeit.weatherwear.domain.follow;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.weatherwear.domain.follow.dto.FollowDto;
import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import com.codeit.weatherwear.domain.follow.repository.FollowRepository;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.config.JpaConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
@Import({JpaConfig.class})
class FollowRepositoryTest {

  @Autowired
  FollowRepository followRepository;
  @Autowired
  UserRepository userRepository;

  UUID followingMeId;
  UUID aId;
  UUID bId;
  UUID cId;

  @BeforeEach
  void setUp() {
    User alice = User.builder()
        .email("alice@test.com")
        .name("alice")
        .password("alice1234")
        .build();

    User bob = User.builder()
        .email("bob@test.com")
        .name("bob")
        .password("bob1234")
        .build();

    User ccc = User.builder()
        .email("c@c.com")
        .name("ccc")
        .password("ccc1234")
        .build();

    userRepository.save(alice);
    userRepository.save(bob);
    userRepository.save(ccc);

    Follow follow = followRepository.save(Follow.create(bob, alice));
    Follow follow1 = followRepository.save(Follow.create(bob, ccc));

    followingMeId = follow.getId();
    aId = alice.getId();
    bId = bob.getId();
    cId = ccc.getId();
  }

  @Test
  @DisplayName("follow summary 조회")
  void getSummary() {
    FollowSummaryDto summary = followRepository.getSummary(bId, aId);

    assertThat(summary.followeeId()).isEqualTo(bId);
    assertThat(summary.followerCount()).isEqualTo(2);
    assertThat(summary.followingCount()).isEqualTo(0);
    assertThat(summary.followedByMe()).isTrue();
    assertThat(summary.followedByMeId()).isEqualTo(followingMeId);
    assertThat(summary.followingMe()).isFalse();
  }

  @Test
  @DisplayName("following 목록 조회")
  void getFollowing() {
    int limit = 20;
    List<FollowDto> followings = followRepository
        .getFollowings(aId, null, null, limit, null);

    assertThat(followings)
        .hasSize(1)
        .satisfiesExactly(followDto -> {
          assertThat(followDto.follower().userId()).isEqualTo(aId);
          assertThat(followDto.followee().userId()).isEqualTo(bId);
        });
  }

  @Test
  @DisplayName("follower 목록 조회")
  void getFollower() {
    int limit = 20;
    List<FollowDto> followers = followRepository
        .getFollowers(bId, null, null, limit, null);

    assertThat(followers)
        .hasSize(2)
        .allSatisfy(follower -> assertThat(follower.followee().userId()).isEqualTo(bId))
        .satisfiesExactly(
            follower1 -> assertThat(follower1.follower().userId()).isEqualTo(cId),
            follower2 -> assertThat(follower2.follower().userId()).isEqualTo(aId)
        );
  }
}