package com.codeit.weatherwear.domain.feed.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedGetParamRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.feed.exception.FeedNotFoundException;
import com.codeit.weatherwear.domain.feed.mapper.FeedMapper;
import com.codeit.weatherwear.domain.feed.repository.FeedRepository;
import com.codeit.weatherwear.domain.follow.dto.UserSummaryDto;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.user.entity.Gender;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.domain.weather.dto.response.PrecipitationDto;
import com.codeit.weatherwear.domain.weather.dto.response.TemperatureDto;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherSummaryDto;
import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private FeedRepository feedRepository;

  @Mock
  private FeedMapper feedMapper;

  @InjectMocks
  private FeedServiceImpl feedService;

  private FeedCreateRequest mockRequest;
  private UUID authorId;
  private UUID weatherId;
  private List<UUID> clothedIds;
  private String mockContent;

  private Location mockLocation;
  private User mockAuthor;
  private UUID feedId;
  private Feed mockFeed;

  private FeedGetParamRequest mockFeedQuery;

  private WeatherSummaryDto mockWeatherDto;
  private PrecipitationDto mockPrecipitation;
  private TemperatureDto mockTemperature;
  private UserSummaryDto mockAuthorDto;
  private FeedDto mockFeedDto;

  private Feed updateMockFeed;
  private FeedDto updateFeedDto;
  private String updateContent;

  @BeforeEach
  void setUp() {
    mockLocation = new Location(37.513068, 127.102570, 961159, 1953082, "서울 송파구 신천동");

    authorId = UUID.randomUUID();
    weatherId = UUID.randomUUID();
    clothedIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

    mockContent = "Mock Feed Content";

    mockAuthor = User.builder()
        .id(authorId)
        .email("test@example.com")
        .name("홍길동")
        .password("!password1234")
        .role(Role.USER)
        .locked(false)
        .gender(Gender.FEMALE)
        .birthDate(LocalDate.of(2000, 1, 1))
        .temperatureSensitivity(10)
        .profileImageUrl(null)
        .location(mockLocation)
        .createdAt(Instant.now())
        .updatedAt(Instant.now())
        .build();

    mockAuthorDto = UserSummaryDto.from(mockAuthor);

    mockRequest = FeedCreateRequest.builder()
        .authorId(authorId)
        .weatherId(weatherId)
        .clothesIds(clothedIds)
        .content(mockContent)
        .build();

    feedId = UUID.randomUUID();
    mockFeed = Feed.builder()
        .author(mockAuthor)
        .content(mockContent)
        .commentCount(0)
        .likeCount(0)
        .build();
    ReflectionTestUtils.setField(mockFeed, "id", feedId);

    mockFeedQuery = FeedGetParamRequest.builder()
        .limit(10)
        .sortBy("createdAt")
        .sortDirection("ASCENDING")
        .build();

    mockPrecipitation = PrecipitationDto.builder()
        .type(PrecipitationsType.NONE)
        .amount(0.1)
        .probability(0.1)
        .build();

    mockTemperature = TemperatureDto.builder()
        .current(0.1)
        .comparedToDayBefore(0.1)
        .min(0.1)
        .max(0.1)
        .build();

    mockWeatherDto = WeatherSummaryDto.builder()
        .weatherId(UUID.randomUUID())
        .skyStatus(SkyStatus.CLEAR)
        .precipitation(mockPrecipitation)
        .temperature(mockTemperature)
        .build();

    mockFeedDto = FeedDto.builder()
        .id(mockFeed.getId())
        .createdAt(mockFeed.getCreatedAt())
        .updatedAt(mockFeed.getUpdatedAt())
        .author(mockAuthorDto)
        .weather(mockWeatherDto)
        .ootds(null)
        .content(mockFeed.getContent())
        .commentCount(mockFeed.getCommentCount())
        .likeCount(mockFeed.getLikeCount())
        .likedByMe(false)
        .build();

    updateContent = "수정된 메시지";
    updateMockFeed = Feed.builder()
        .author(mockAuthor)
        .content(updateContent)
        .commentCount(0)
        .likeCount(0)
        .build();
    ReflectionTestUtils.setField(updateMockFeed, "id", feedId);

    updateFeedDto = FeedDto.builder()
        .id(updateMockFeed.getId())
        .createdAt(updateMockFeed.getCreatedAt())
        .updatedAt(updateMockFeed.getUpdatedAt())
        .author(mockAuthorDto)
        .weather(mockWeatherDto)
        .ootds(null)
        .content(updateMockFeed.getContent())
        .commentCount(updateMockFeed.getCommentCount())
        .likeCount(updateMockFeed.getLikeCount())
        .likedByMe(false)
        .build();

    ReflectionTestUtils.setField(mockFeed, "id", feedId);

  }

  @Test
  @DisplayName("Feed를 성공적으로 생성하여 FeedDto를 리턴합니다.")
  void createFeed_success() {
    // given
    given(userRepository.findById(authorId)).willReturn(Optional.ofNullable(mockAuthor));
    given(feedMapper.toEntity(mockAuthor, mockRequest)).willReturn(mockFeed);
    given(feedRepository.save(mockFeed)).willReturn(mockFeed);
    given(feedMapper.toDto(eq(mockFeed), eq(mockAuthorDto), any(WeatherSummaryDto.class), isNull(),
        eq(false))).willReturn(mockFeedDto);

    // when
    FeedDto result = feedService.createFeed(mockRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(mockFeed.getId());
    assertThat(result.getAuthor().userId()).isEqualTo(mockAuthorDto.userId());
    assertThat(result.getOotds()).isNull();
    assertThat(result.isLikedByMe()).isFalse();

    // verify
    verify(userRepository).findById(authorId);
    verify(feedMapper).toEntity(mockAuthor, mockRequest);
    verify(feedRepository).save(mockFeed);
    verify(feedMapper).toDto(eq(mockFeed), eq(mockAuthorDto), any(WeatherSummaryDto.class),
        isNull(), eq(false));
  }

  @Test
  @DisplayName("사용자를 찾지 못해 피드 생성에 실패합니다.")
  void createFeed_failed_cannot_find_user() {
    // given
    UUID failedId = UUID.randomUUID();
    FeedCreateRequest failedRequest = FeedCreateRequest.builder()
        .authorId(failedId)
        .weatherId(weatherId)
        .clothesIds(clothedIds)
        .content("실패 테스트")
        .build();

    given(userRepository.findById(failedId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> feedService.createFeed(failedRequest))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  @DisplayName("전체 피드 리스트를 성공적으로 불러와 전달한다 - 페이지네이션 적용 안 한 상태")
  void getFeedList_success() {
    // given
    List<Feed> feedList = List.of(mockFeed);
    given(feedRepository.findAll()).willReturn(feedList);
    given(feedMapper.toDto(eq(mockFeed), eq(mockAuthorDto), any(WeatherSummaryDto.class), isNull(),
        eq(false))).willReturn(mockFeedDto);

    // when
    List<FeedDto> resultList = feedService.getFeedList(mockFeedQuery);
    // then
    assertThat(resultList).isNotNull();
    assertThat(resultList).hasSize(1);
    assertThat(resultList.get(0)).isEqualTo(mockFeedDto);

    // verify
    verify(feedMapper, times(feedList.size())).toDto(eq(mockFeed), eq(mockAuthorDto),
        any(WeatherSummaryDto.class), isNull(), eq(false));
  }

  @Test
  @DisplayName("피드를 성공적으로 수정한다")
  void updateFeed_success() {
    // given
    FeedUpdateRequest updateRequest = FeedUpdateRequest.builder().content(updateContent).build();

    given(feedRepository.findById(feedId)).willReturn(Optional.of(mockFeed));

    given(feedMapper.toDto(eq(mockFeed), eq(mockAuthorDto), any(WeatherSummaryDto.class), isNull(),
        eq(false))).willReturn(updateFeedDto);

    // when
    FeedDto result = feedService.updateFeed(feedId, updateRequest);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(feedId);
    assertThat(result.getContent()).isEqualTo(updateContent);

    // verify
    assertThat(mockFeed.getContent()).isEqualTo(updateContent);
  }

  @Test
  @DisplayName("피드를 조회하지 못 해 피드 수정을 실패한다")
  void updateFeed_failed_cannot_find_feed() {
    // given
    UUID failedId = UUID.randomUUID();
    FeedUpdateRequest failedRequest = FeedUpdateRequest.builder()
        .content("실패 테스트")
        .build();

    given(feedRepository.findById(failedId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> feedService.updateFeed(failedId, failedRequest))
        .isInstanceOf(FeedNotFoundException.class);
  }

  @Test
  @DisplayName("피드 삭제를 성공한다")
  void deleteFeed_success() {
    // given
    given(feedRepository.findById(feedId)).willReturn(Optional.of(mockFeed));
    given(feedMapper.toDto(eq(mockFeed), eq(mockAuthorDto), any(WeatherSummaryDto.class), isNull(),
        eq(false))).willReturn(mockFeedDto);

    // when
    FeedDto result = feedService.deleteFeed(feedId);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(mockFeed.getId());
    assertThat(result.getContent()).isEqualTo(mockFeed.getContent());

    // verify
    verify(feedRepository).findById(feedId);
    verify(feedRepository).delete(mockFeed);
    verify(feedMapper).toDto(eq(mockFeed), eq(mockAuthorDto), any(WeatherSummaryDto.class),
        isNull(), eq(false));
  }

  @Test
  @DisplayName("피드를 조회하지 못 해 피드 삭제를 실패한다")
  void deleteFeed_failed_cannot_find_feed() {
    // given
    UUID failedId = UUID.randomUUID();

    given(feedRepository.findById(failedId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> feedService.deleteFeed(failedId))
        .isInstanceOf(FeedNotFoundException.class);
  }

}