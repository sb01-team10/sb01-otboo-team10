package com.codeit.weatherwear.domain.feed.service.impl;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.feed.service.FeedService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

  private final UserRepository userRepository;

  @Override
  public List<FeedDto> getFeedList(String cursor, UUID idAfter, int limit, String sortBy,
      String sortDirection, String keywordLike, String skyStatusEqual,
      String precipitationTypeEqual, UUID authorIdEqual) {
    return List.of();
  }

  @Override
  public FeedDto createFeed(FeedCreateRequest feedCreateRequest) {

//    User author = userRepository.findById(feedCreateRequest.getAuthorId())
//        .orElseThrow(UserNotFoundException::new);
//
//    Feed feed = Feed.builder()
//        .author(author)
//        .content(feedCreateRequest.getContent())
//        .likeCount(0)
//        .commentCount(0)
//        .build();

    return null;
  }

  @Override
  public FeedDto updateFeed(UUID feedId) {
    return null;
  }

  @Override
  public FeedDto deleteFeed(UUID feedId) {
    return null;
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
}
