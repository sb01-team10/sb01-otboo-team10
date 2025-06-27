package com.codeit.weatherwear.domain.feed.service.impl;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedGetParamRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.feed.exception.FeedNotFoundException;
import com.codeit.weatherwear.domain.feed.mapper.FeedMapper;
import com.codeit.weatherwear.domain.feed.repository.FeedRepository;
import com.codeit.weatherwear.domain.feed.service.FeedService;
import com.codeit.weatherwear.domain.follow.dto.UserSummaryDto;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.domain.weather.dto.response.PrecipitationDto;
import com.codeit.weatherwear.domain.weather.dto.response.TemperatureDto;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherSummaryDto;
import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

  private final UserRepository userRepository;
  private final FeedMapper feedMapper;
  private final FeedRepository feedRepository;

  @Transactional
  @Override
  public List<FeedDto> getFeedList(FeedGetParamRequest paramRequest) {
    log.info("Request Get Feed List");

    // todo: 우선적으로 불러오기만 할 것 (페이지네이션은 이후 구현)
    List<Feed> feedList = feedRepository.findAll();

    return feedList.stream().map(this::toFeedDto).collect(Collectors.toList());
  }

  @Transactional
  @Override
  public FeedDto createFeed(FeedCreateRequest feedCreateRequest) {
    log.info("Request Create Feed - authorId: {}", feedCreateRequest.getAuthorId());

    User author = userRepository.findById(feedCreateRequest.getAuthorId())
        .orElseThrow(UserNotFoundException::new);

    Feed feed = feedMapper.toEntity(author, feedCreateRequest);
    Feed saved = feedRepository.save(feed);

    return toFeedDto(saved);
  }

  @Transactional
  @Override
  public FeedDto updateFeed(UUID feedId, FeedUpdateRequest feedUpdateRequest) {
    log.info("Request Update Feed - feedId: {}", feedId);

    Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new FeedNotFoundException(feedId));
    feed.updateContent(feedUpdateRequest.getContent());

    return toFeedDto(feed);
  }

  @Transactional
  @Override
  public FeedDto deleteFeed(UUID feedId) {
    log.info("Request Delete Feed - feedId: {}", feedId);

    Feed feed = feedRepository.findById(feedId).orElseThrow(() -> new FeedNotFoundException(feedId));
    feedRepository.delete(feed);

    return toFeedDto(feed);
  }

  // 임시로 만들어진 WeatherSummeryDto 인스턴스를 반환합니다.
  @Override
  public WeatherSummaryDto getMockWeatherSummaryDto() {

    PrecipitationDto precipitation = PrecipitationDto.builder()
        .type(PrecipitationsType.NONE)
        .amount(0.1)
        .probability(0.1)
        .build();

    TemperatureDto temperature = TemperatureDto.builder()
        .current(0.1)
        .comparedToDayBefore(0.1)
        .min(0.1)
        .max(0.1)
        .build();

    return WeatherSummaryDto.builder()
        .weatherId(UUID.randomUUID())
        .skyStatus(SkyStatus.CLEAR)
        .precipitation(precipitation)
        .temperature(temperature)
        .build();
  }

  private FeedDto toFeedDto(Feed feed) {
    UserSummaryDto authorDto = UserSummaryDto.from(feed.getAuthor());
    WeatherSummaryDto weatherSummaryDto = getMockWeatherSummaryDto();
    // todo: OOTD 등록 - ootd 도메인
    // todo: likedByMe 로직 필요 - feedLike 도메인

    return feedMapper.toDto(feed, authorDto, weatherSummaryDto, null, false);
  }
}
