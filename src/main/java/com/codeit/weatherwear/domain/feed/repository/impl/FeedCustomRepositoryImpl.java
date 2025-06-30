package com.codeit.weatherwear.domain.feed.repository.impl;

import com.codeit.weatherwear.domain.feed.dto.condition.FeedSearchCondition;
import com.codeit.weatherwear.domain.feed.entity.Feed;
import com.codeit.weatherwear.domain.feed.entity.QFeed;
import com.codeit.weatherwear.domain.feed.repository.FeedCustomRepository;
import com.codeit.weatherwear.domain.weather.entity.PrecipitationsType;
import com.codeit.weatherwear.domain.weather.entity.SkyStatus;
import com.codeit.weatherwear.global.request.SortDirection;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
@RequiredArgsConstructor
public class FeedCustomRepositoryImpl implements FeedCustomRepository {

  private final JPAQueryFactory queryFactory;
  List<String> ALLOWED_SORT_FIELDS = List.of("createdAt", "likeCount");


  @Override
  public List<Feed> searchFeeds(FeedSearchCondition condition) {
    QFeed feed = QFeed.feed;

    return queryFactory
        .selectFrom(feed)
        .where(
            idAfter(feed, condition.getIdAfter(), condition.getSortDirection()),
            keywordLike(condition.getKeywordLike()),
            skyStatusEqual(condition.getSkyStatusEqual()),
            precipitationTypeEqual(condition.getPrecipitationsTypeEqual()),
            authorIdEqual(condition.getAuthorIdEqual())
        )
        .orderBy(sortBy(condition.getSortBy(), condition.getSortDirection()))
        .limit(condition.getLimit())
        .fetch();
  }

  private BooleanExpression idAfter(QFeed feed, UUID idAfter, SortDirection direction) {
    if (idAfter == null) {
      return null;
    }
    QFeed subFeed = new QFeed("subFeed");

    JPQLQuery<Instant> createdAtSubQuery = JPAExpressions
        .select(subFeed.createdAt)
        .from(subFeed)
        .where(subFeed.id.eq(idAfter));

    if (direction == SortDirection.ASCENDING) {
      return feed.createdAt.gt(createdAtSubQuery)
          .or( // OR 조건 시작
              feed.createdAt.eq(createdAtSubQuery)
                  .and(feed.id.gt(idAfter)) // 동일 시간일 때 ID 비교
          );
    } else {
      return feed.createdAt.lt(createdAtSubQuery)
          .or( // OR 조건 시작
              feed.createdAt.eq(createdAtSubQuery)
                  .and(feed.id.lt(idAfter)) // 동일 시간일 때 ID 비교
          );
    }
  }

  private BooleanExpression keywordLike(String keyword) {
    return StringUtils.hasText(keyword) ? QFeed.feed.content.containsIgnoreCase(keyword) : null;
  }

  private BooleanExpression skyStatusEqual(SkyStatus skyStatus) {
    return skyStatus != null ? QFeed.feed.weather.skyStatus.eq(skyStatus) : null;
  }

  private BooleanExpression precipitationTypeEqual(PrecipitationsType type) {
    return type != null ? QFeed.feed.weather.precipitation.type.eq(type) : null;
  }

  private BooleanExpression authorIdEqual(UUID authorId) {
    return authorId != null ? QFeed.feed.author.id.eq(authorId) : null;
  }

  private OrderSpecifier<?>[] sortBy(String sortBy, SortDirection direction) {

    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      log.warn("Unsupported Sort Field: {}", sortBy);
      // todo: Exception 생성 필요
      throw new IllegalArgumentException("Unsupported sort field");
    }

    PathBuilder<Feed> path = new PathBuilder<>(Feed.class, "feed");
    Order order = direction == SortDirection.ASCENDING ? Order.ASC : Order.DESC;

    OrderSpecifier<?> primarySort = switch (sortBy) {
      case "createdAt" -> new OrderSpecifier<>(order, path.getDateTime("createdAt", Instant.class));
      case "likeCount" -> new OrderSpecifier<>(order, path.getNumber("likeCount", Integer.class));
      default -> throw new UnsupportedOperationException("Sort field not implemented: " + sortBy);
    };

    OrderSpecifier<UUID> secondarySort = new OrderSpecifier<>(order, path.get("id", UUID.class));

    return new OrderSpecifier<?>[]{primarySort, secondarySort};
  }
}
