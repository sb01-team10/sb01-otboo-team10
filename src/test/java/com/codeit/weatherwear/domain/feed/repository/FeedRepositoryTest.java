package com.codeit.weatherwear.domain.feed.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.weatherwear.domain.feed.dto.condition.FeedSearchCondition;
import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.weather.entity.Humidity;
import com.codeit.weatherwear.domain.weather.entity.Precipitation;
import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import com.codeit.weatherwear.domain.weather.entity.Temperature;
import com.codeit.weatherwear.domain.weather.entity.Weather;
import com.codeit.weatherwear.domain.weather.entity.WindSpeed;
import com.codeit.weatherwear.global.config.JpaConfig;
import com.codeit.weatherwear.global.request.SortDirection;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@DataJpaTest
@ActiveProfiles("test")
@Import({JpaConfig.class})
class FeedRepositoryTest {

  @Autowired
  private TestEntityManager testEntityManager;

  @Autowired
  private FeedRepository feedRepository;

  private User user;
  private Feed feed1, feed2, feed3;

  @BeforeEach
  void setUp() {
    Instant now = Instant.now();
    user = createUser();
    feed1 = createFeed(user, "Feed1", now.minusSeconds(60), 5);
    feed2 = createFeed(user, "Feed2", now.minusSeconds(30), 10);
    feed3 = createFeed(user, "Feed3", now, 0);

    testEntityManager.flush();
    testEntityManager.clear();
  }

  private User createUser() {
    User user = User.builder()
        .email("test1@test.com")
        .name("test1")
        .password("test1234")
        .build();
    return testEntityManager.persistAndFlush(user);
  }

  private Location createLocation() {
    Location location = new Location(37.513068, 127.102570, 961159, 1953082, "서울 송파구 신천동");
    return testEntityManager.persistAndFlush(location); // 명시적 저장
  }

  private Weather createWeather() {
    Location location = createLocation();
    Weather weather = Weather.builder()
        .forecastAt(Instant.now())
        .forecastedAt(Instant.now())
        .humidity(Humidity.builder().comparedToDayBefore(0.1).current(0.1).build())
        .location(location)
        .precipitation(
            Precipitation.builder().amount(0.0).probability(0.1).type(PrecipitationsType.NONE)
                .build())
        .temperature(
            Temperature.builder().comparedToDayBefore(10.0).current(23.0).max(30.0).min(20.0)
                .build())
        .skyStatus(SkyStatus.CLEAR)
        .windSpeed(WindSpeed.builder().speed(10).build())
        .build();
    return testEntityManager.persistAndFlush(weather);
  }

  private Feed createFeed(User author, String content, Instant time, int likeCount) {
    Weather weather = createWeather();
    Feed feed = Feed.builder()
        .author(author)
        .content(content)
        .commentCount(0)
        .likeCount(likeCount)
        .weather(weather)
        .build();
    ReflectionTestUtils.setField(feed, "createdAt", time);

    return testEntityManager.persistAndFlush(feed);
  }

  // 엔티티 영속성 문제로 ID 지정이 필요한 부분은 Native SQL로 작성
  private Feed insertFeedWithFixedId(UUID id, String content, int likeCount, Instant createdAt) {
    Weather weather = createWeather();
    UUID weatherId = weather.getId();

    testEntityManager.getEntityManager()
        .createNativeQuery("""
            INSERT INTO feed (id, author_id, content, comment_count, like_count, weather_id, created_at, updated_at)
            VALUES (:id, :authorId, :content, 0, :likeCount, :weatherId, :createdAt, :createdAt)
            """)
        .setParameter("id", id)
        .setParameter("authorId", user.getId())
        .setParameter("content", content)
        .setParameter("likeCount", likeCount)
        .setParameter("weatherId", weatherId)
        .setParameter("createdAt", createdAt)
        .executeUpdate();

    return testEntityManager.find(Feed.class, id);
  }

  private FeedSearchCondition createCondition(String cursor, UUID idAfter, int limit, String sortBy,
      SortDirection sortDirection) {
    return FeedSearchCondition.builder()
        .cursor(cursor)
        .idAfter(idAfter)
        .limit(limit)
        .sortBy(sortBy)
        .sortDirection(sortDirection)
        .build();
  }

  @Test
  @DisplayName("createdAt 기준 오름차순 정렬 및 페이지네이션이 제대로 동작한다")
  void pagination_with_createdAt_asc() {
    int clientLimit = 1;
    int repoLimit = clientLimit + 1;
    // when & then
    // 페이지1: [feed1, feed2]
    List<Feed> page1 = feedRepository.searchFeeds(
        createCondition(null, null, clientLimit, "createdAt", SortDirection.ASCENDING),
        repoLimit
    );
    assertThat(page1).hasSize(repoLimit);
    assertThat(page1.get(0).getId()).isEqualTo(feed1.getId());

    // 페이지2: [feed2, feed3]
    List<Feed> page2 = feedRepository.searchFeeds(
        createCondition(
            page1.get(0).getCreatedAt().toString(), // feed1 시간
            page1.get(0).getId(),                   // feed1 ID
            clientLimit,
            "createdAt",
            SortDirection.ASCENDING),
        repoLimit
    );
    assertThat(page2).hasSize(repoLimit);
    assertThat(page2.get(0).getId()).isEqualTo(feed2.getId());

    // 페이지3: [feed3]
    List<Feed> page3 = feedRepository.searchFeeds(
        createCondition(
            page2.get(0).getCreatedAt().toString(), // feed2 시간
            page2.get(0).getId(),                   // feed2 ID
            clientLimit,
            "createdAt",
            SortDirection.ASCENDING),
        repoLimit
    );
    assertThat(page3).hasSize(clientLimit);
    assertThat(page3.get(0).getId()).isEqualTo(feed3.getId());
  }

  @Test
  @DisplayName("createdAt 기준 내림차순 정렬 및 페이지네이션이 제대로 동작한다")
  void pagination_with_createdAt_desc() {
    int clientLimit = 1;
    int repoLimit = clientLimit + 1;
    // when & then
    // 페이지1: [feed3, feed2]
    List<Feed> page1 = feedRepository.searchFeeds(
        createCondition(null, null, clientLimit, "createdAt", SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page1).hasSize(repoLimit);
    assertThat(page1.get(0).getId()).isEqualTo(feed3.getId());

    // 페이지2: [feed2, feed1]
    List<Feed> page2 = feedRepository.searchFeeds(
        createCondition(
            page1.get(0).getCreatedAt().toString(), // feed3 시간
            page1.get(0).getId(),                   // feed3 ID
            clientLimit,
            "createdAt",
            SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page2).hasSize(repoLimit);
    assertThat(page2.get(0).getId()).isEqualTo(feed2.getId());

    // 페이지3: [feed1]
    List<Feed> page3 = feedRepository.searchFeeds(
        createCondition(
            page2.get(0).getCreatedAt().toString(), // feed2 시간
            page2.get(0).getId(),                   // feed2 ID
            clientLimit,
            "createdAt",
            SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page3).hasSize(clientLimit);
    assertThat(page3.get(0).getId()).isEqualTo(feed1.getId());
  }

  @Test
  @DisplayName("likeCount 기준 오름차순 정렬 및 페이지네이션이 제대로 동작한다")
  void pagination_with_likeCount_asc() {
    int clientLimit = 1;
    int repoLimit = clientLimit + 1;
    // when & then
    // 페이지1: [feed3, feed1]
    List<Feed> page1 = feedRepository.searchFeeds(
        createCondition(null, null, clientLimit, "likeCount", SortDirection.ASCENDING),
        repoLimit
    );
    assertThat(page1).hasSize(repoLimit);
    assertThat(page1.get(0).getId()).isEqualTo(feed3.getId());

    // 페이지2: [feed1, feed2]
    List<Feed> page2 = feedRepository.searchFeeds(
        createCondition(
            String.valueOf(page1.get(0).getLikeCount()),  // feed1 시간
            page1.get(0).getId(),                         // feed1 ID
            clientLimit,
            "likeCount",
            SortDirection.ASCENDING),
        repoLimit
    );
    assertThat(page2).hasSize(repoLimit);
    assertThat(page2.get(0).getId()).isEqualTo(feed1.getId());

    // 페이지3: [feed2]
    List<Feed> page3 = feedRepository.searchFeeds(
        createCondition(
            String.valueOf(page2.get(0).getLikeCount()),   // feed2 시간
            page2.get(0).getId(),                          // feed2 ID
            clientLimit,
            "likeCount",
            SortDirection.ASCENDING),
        repoLimit
    );
    assertThat(page3).hasSize(clientLimit);
    assertThat(page3.get(0).getId()).isEqualTo(feed2.getId());
  }

  @Test
  @DisplayName("likeCount 기준 내림차순 정렬 및 페이지네이션이 제대로 동작한다")
  void pagination_with_likeCount_desc() {
    int clientLimit = 1;
    int repoLimit = clientLimit + 1;
    // when & then
    // 페이지1: [feed2, feed1]
    List<Feed> page1 = feedRepository.searchFeeds(
        createCondition(null, null, clientLimit, "likeCount", SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page1).hasSize(repoLimit);
    assertThat(page1.get(0).getId()).isEqualTo(feed2.getId());

    // 페이지2: [feed1, feed3]
    List<Feed> page2 = feedRepository.searchFeeds(
        createCondition(
            String.valueOf(page1.get(0).getLikeCount()),  // feed1 시간
            page1.get(0).getId(),                         // feed1 ID
            clientLimit,
            "likeCount",
            SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page2).hasSize(repoLimit);
    assertThat(page2.get(0).getId()).isEqualTo(feed1.getId());

    // 페이지3: [feed3]
    List<Feed> page3 = feedRepository.searchFeeds(
        createCondition(
            String.valueOf(page2.get(0).getLikeCount()),   // feed3 시간
            page2.get(0).getId(),                          // feed3 ID
            clientLimit,
            "likeCount",
            SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page3).hasSize(clientLimit);
    assertThat(page3.get(0).getId()).isEqualTo(feed3.getId());
  }

  @Test
  @DisplayName("likeCount 기준 정렬 시 값이 같으면 id 순서로 정렬한다.")
  void pagination_same_likeCount() {
    // given
    UUID uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    UUID uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

    Feed sameLike1 = insertFeedWithFixedId(uuid1, "sameLike1", 120, Instant.now());
    Feed sameLike2 = insertFeedWithFixedId(uuid2, "sameLike2", 120, Instant.now().minusSeconds(30));

    int clientLimit = 2;
    int repoLimit = clientLimit + 1;
    // when & then
    List<Feed> page = feedRepository.searchFeeds(
        createCondition(null, null, clientLimit, "likeCount", SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page).hasSize(repoLimit);
    assertThat(page.get(0).getId()).isEqualTo(uuid2);
    assertThat(page.get(0).getContent()).isEqualTo(sameLike2.getContent());
    assertThat(page.get(1).getId()).isEqualTo(uuid1);
    assertThat(page.get(1).getContent()).isEqualTo(sameLike1.getContent());
  }

  @Test
  @DisplayName("createdAt 기준 정렬 시 값이 같으면 id 순서로 정렬한다.")
  void pagination_same_createdAt() {
    // given
    UUID uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000003");
    UUID uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000004");

    Instant time = Instant.now();
    Feed sameCreate1 = insertFeedWithFixedId(uuid1, "sameCreate1", 0, time);
    Feed sameCreate2 = insertFeedWithFixedId(uuid2, "sameCreate2", 0, time);

    int clientLimit = 2;
    int repoLimit = clientLimit + 1;
    // when & then
    List<Feed> page = feedRepository.searchFeeds(
        createCondition(null, null, clientLimit, "createdAt", SortDirection.DESCENDING),
        repoLimit
    );
    assertThat(page).hasSize(repoLimit);
    assertThat(page.get(0).getId()).isEqualTo(uuid2);
    assertThat(page.get(0).getContent()).isEqualTo(sameCreate2.getContent());
    assertThat(page.get(1).getId()).isEqualTo(uuid1);
    assertThat(page.get(1).getContent()).isEqualTo(sameCreate1.getContent());
  }


}