package com.codeit.weatherwear.domain.feed.service;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherSummaryDto;
import java.util.List;
import java.util.UUID;

public interface FeedService {

  List<FeedDto> getFeedList(String cursor, UUID idAfter, int limit, String sortBy,
      String sortDirection, String keywordLike, String skyStatusEqual,
      String precipitationTypeEqual, UUID authorIdEqual);

  FeedDto createFeed(FeedCreateRequest feedCreateRequest);

  FeedDto updateFeed(UUID feedId);

  FeedDto deleteFeed(UUID feedId);

  WeatherSummaryDto getMockWeatherSummaryDto();

}
