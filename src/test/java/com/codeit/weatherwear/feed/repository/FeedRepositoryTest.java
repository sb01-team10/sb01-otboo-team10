package com.codeit.weatherwear.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.feed.repository.FeedRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class FeedRepositoryTest {

  @Autowired
  private FeedRepository feedRepository;

  @Test
  @DisplayName("Feed 엔티티 저장 및 조회 테스트")
  void feed_save_and_select() {
    // given
    Feed feed = Feed.builder()
        .likeCount(7)
        .commentCount(2)
        .build();

    // when
    Feed savedFeed = feedRepository.save(feed);

    // then
    assertThat(savedFeed.getId()).isNotNull();
    assertThat(savedFeed.getLikeCount()).isEqualTo(7);
    assertThat(savedFeed.getCommentCount()).isEqualTo(2);

    // 추가 조회 테스트
    Feed foundFeed = feedRepository.findById(savedFeed.getId()).orElseThrow();
    assertThat(foundFeed.getLikeCount()).isEqualTo(7);
    assertThat(foundFeed.getCommentCount()).isEqualTo(2);
  }


}