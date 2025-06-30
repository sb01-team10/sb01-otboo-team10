package com.codeit.weatherwear.domain.feed.service.impl;

import com.codeit.weatherwear.domain.feed.dto.condition.FeedSearchCondition;
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
import com.codeit.weatherwear.domain.ootd.dto.response.OotdDto;
import com.codeit.weatherwear.domain.ootd.service.OotdService;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.domain.weather.dto.response.PrecipitationDto;
import com.codeit.weatherwear.domain.weather.dto.response.TemperatureDto;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherSummaryDto;
import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import com.codeit.weatherwear.global.response.PageResponse;
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
  private final OotdService ootdService;

  @Transactional
  @Override
  public PageResponse<FeedDto> getFeedList(FeedGetParamRequest paramRequest) {
    log.info("Request Get Feed List");

    FeedSearchCondition condition = paramRequest.toSearchCondition();
    int originalLimit = condition.getLimit();

    condition.setLimit(originalLimit + 1);

    List<Feed> feedList = feedRepository.searchFeeds(condition);
    boolean hasNext = feedList.size() > originalLimit;

    List<Feed> resultList = hasNext ? feedList.subList(0, originalLimit) : feedList;
    List<FeedDto> feedDtoList = resultList.stream().map(this::toFeedDto)
        .collect(Collectors.toList());

    return toPageResponse(feedDtoList, condition, hasNext);
  }

  private PageResponse<FeedDto> toPageResponse(List<FeedDto> dtoList, FeedSearchCondition condition,
      boolean hasNext) {

    UUID nextIdAfter = null;
    Object nextCursor = null;
    if (hasNext && !dtoList.isEmpty()) {
      nextIdAfter = dtoList.get(dtoList.size() - 1).getId();
      nextCursor =
          condition.getSortBy().equals("createdAt") ? dtoList.get(dtoList.size() - 1).getCreatedAt()
              : dtoList.get(dtoList.size() - 1).getLikeCount();
    }

    return new PageResponse<>(
        dtoList,
        nextCursor,
        nextIdAfter,
        hasNext,
        dtoList.size(),
        condition.getSortBy(),
        condition.getSortDirection().name()
    );
  }

  @Transactional
  @Override
  public FeedDto createFeed(FeedCreateRequest feedCreateRequest) {
    log.info("Request Create Feed - authorId: {}", feedCreateRequest.getAuthorId());

    User author = userRepository.findById(feedCreateRequest.getAuthorId())
        .orElseThrow(UserNotFoundException::new);

    Feed feed = feedMapper.toEntity(author, feedCreateRequest);
    Feed saved = feedRepository.save(feed);
    List<OotdDto> ootdList = ootdService.createOotdList(feed, feedCreateRequest.getClothesIds());

    return toFeedDto(saved, ootdList);
  }

  @Transactional
  @Override
  public FeedDto updateFeed(UUID feedId, FeedUpdateRequest feedUpdateRequest) {
    log.info("Request Update Feed - feedId: {}", feedId);

    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(() -> new FeedNotFoundException(feedId));
    feed.updateContent(feedUpdateRequest.getContent());

    return toFeedDto(feed);
  }

  @Transactional
  @Override
  public FeedDto deleteFeed(UUID feedId) {
    log.info("Request Delete Feed - feedId: {}", feedId);

    Feed feed = feedRepository.findById(feedId)
        .orElseThrow(() -> new FeedNotFoundException(feedId));
    feedRepository.delete(feed);
    List<OotdDto> ootds = ootdService.deleteOotdByFeedId(feedId);

    return toFeedDto(feed, ootds);
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

  // 생성/삭제
  private FeedDto toFeedDto(Feed feed, List<OotdDto> ootds) {
    UserSummaryDto authorDto = UserSummaryDto.from(feed.getAuthor());
    WeatherSummaryDto weatherSummaryDto = getMockWeatherSummaryDto();

    // todo: likedByMe 로직 필요 - feedLike 도메인

    return feedMapper.toDto(feed, authorDto, weatherSummaryDto, ootds, false);
  }

  // 일반적인 상황 (조회/갱신)
  private FeedDto toFeedDto(Feed feed) {
    UserSummaryDto authorDto = UserSummaryDto.from(feed.getAuthor());
    WeatherSummaryDto weatherSummaryDto = getMockWeatherSummaryDto();
    List<OotdDto> ootds = ootdService.findOotdByFeedId(feed.getId());

    // todo: likedByMe 로직 필요 - feedLike 도메인

    return feedMapper.toDto(feed, authorDto, weatherSummaryDto, ootds, false);
  }

}
