package com.codeit.weatherwear.domain.follow;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import com.codeit.weatherwear.domain.follow.repository.FollowRepository;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.config.JpaConfig;
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
  UUID followerId;
  UUID followeeId;

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

    userRepository.save(alice);
    userRepository.save(bob);

    Follow follow = followRepository.save(Follow.create(bob, alice));
    followingMeId = follow.getId();
    followerId = alice.getId();
    followeeId = bob.getId();
  }

  @Test
  @DisplayName("follow summary 조회")
  void getSummary() {
    FollowSummaryDto summary = followRepository.getSummary(followeeId, followerId);

    assertThat(summary.followeeId()).isEqualTo(followeeId);
    assertThat(summary.followerCount()).isEqualTo(1);
    assertThat(summary.followingCount()).isEqualTo(0);
    assertThat(summary.followedByMe()).isTrue();
    assertThat(summary.followedByMeId()).isEqualTo(followingMeId);
    assertThat(summary.followingMe()).isFalse();
  }
}