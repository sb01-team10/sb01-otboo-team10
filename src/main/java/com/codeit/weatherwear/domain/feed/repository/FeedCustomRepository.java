package com.codeit.weatherwear.domain.feed.repository;

import com.codeit.weatherwear.domain.feed.dto.condition.FeedSearchCondition;
import com.codeit.weatherwear.domain.feed.entity.Feed;
import java.util.List;
import org.springframework.data.domain.Slice;

public interface FeedCustomRepository {
  List<Feed> searchFeeds(FeedSearchCondition condition);
}
