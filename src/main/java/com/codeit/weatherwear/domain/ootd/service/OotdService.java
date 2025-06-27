package com.codeit.weatherwear.domain.ootd.service;

import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.ootd.dto.response.OotdDto;
import java.util.List;
import java.util.UUID;

public interface OotdService {

  // OOTD 생성
  List<OotdDto> createOotdList(Feed feed, List<UUID> clothIds);

  // OOTD 값 전달
  List<OotdDto> findOotdByFeedId(UUID feedId);

  // OOTD 삭제
  List<OotdDto> deleteOotdByFeedId(UUID feedId);

}
