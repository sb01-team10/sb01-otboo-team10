package com.codeit.weatherwear.domain.feed.service;

import com.codeit.weatherwear.domain.feed.dto.request.FeedCreateRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedGetParamRequest;
import com.codeit.weatherwear.domain.feed.dto.request.FeedUpdateRequest;
import com.codeit.weatherwear.domain.feed.dto.response.FeedDto;
import com.codeit.weatherwear.domain.weather.dto.response.WeatherSummaryDto;
import java.util.List;
import java.util.UUID;

public interface FeedService {

  List<FeedDto> getFeedList(FeedGetParamRequest paramRequest);

  FeedDto createFeed(FeedCreateRequest feedCreateRequest);

  FeedDto updateFeed(UUID feedId, FeedUpdateRequest feedUpdateRequest);

  FeedDto deleteFeed(UUID feedId);

  WeatherSummaryDto getMockWeatherSummaryDto();

}
