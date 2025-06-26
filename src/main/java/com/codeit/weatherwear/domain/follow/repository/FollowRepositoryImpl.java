package com.codeit.weatherwear.domain.follow.repository;

import static com.codeit.weatherwear.domain.follow.QFollow.follow;

import com.codeit.weatherwear.domain.follow.dto.FollowDto;
import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import com.codeit.weatherwear.domain.follow.dto.UserSummaryDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  @Override
  public FollowSummaryDto getSummary(UUID userId, UUID myId) {
    Long followerCount = queryFactory
        .select(follow.count())
        .from(follow)
        .where(follow.followee.id.eq(userId))
        .fetchOne();

    Long followingCount = queryFactory
        .select(follow.count())
        .from(follow)
        .where(follow.follower.id.eq(userId))
        .fetchOne();

    UUID followedByMeId = queryFactory
        .select(follow.id)
        .from(follow)
        .where(
            follow.followee.id.eq(userId),
            follow.follower.id.eq(myId)
        )
        .fetchOne();
    boolean followedByMe = followedByMeId != null;

    Long followingMeCount = queryFactory.select(follow.count())
        .from(follow)
        .where(
            follow.follower.id.eq(userId),
            follow.followee.id.eq(myId)
        )
        .fetchOne();

    return new FollowSummaryDto(
        userId,
        followerCount,
        followingCount,
        followedByMe,
        followedByMeId,
        followingMeCount > 0
    );
  }

  @Override
  public List<FollowDto> getFollowings(UUID followerId, String cursor,
      UUID idAfter, int limit, String nameLike
  ) {
    return queryFactory
        .select(Projections.constructor(FollowDto.class,
            follow.id,
            follow.createdAt,
            Projections.constructor(UserSummaryDto.class,
                follow.followee.id,
                follow.followee.name,
                follow.followee.profileImageUrl
            ),
            Projections.constructor(UserSummaryDto.class,
                follow.follower.id,
                follow.follower.name,
                follow.follower.profileImageUrl
            )
        ))
        .from(follow)
        .where(
            follow.follower.id.eq(followerId),
            cursor(cursor, idAfter),
            nameLike(nameLike)
        )
        .orderBy(follow.createdAt.desc())
        .limit(limit + 1)
        .fetch();
  }

  @Override
  public List<FollowDto> getFollowers(UUID followeeId, String cursor,
      UUID idAfter, int limit, String nameLike
  ) {
    return queryFactory
        .select(Projections.constructor(FollowDto.class,
            follow.id,
            follow.createdAt,
            Projections.constructor(UserSummaryDto.class,
                follow.followee.id,
                follow.followee.name,
                follow.followee.profileImageUrl
            ),
            Projections.constructor(UserSummaryDto.class,
                follow.follower.id,
                follow.follower.name,
                follow.follower.profileImageUrl
            )
        ))
        .from(follow)
        .where(
            follow.followee.id.eq(followeeId),
            cursor(cursor, idAfter),
            nameLike(nameLike)
        )
        .orderBy(follow.createdAt.desc())
        .limit(limit + 1)
        .fetch();
  }

  private BooleanExpression nameLike(String nameLike) {
    if (nameLike == null || nameLike.isBlank()) {
      return null;
    }
    return follow.follower.name.containsIgnoreCase(nameLike);
  }

  private BooleanExpression cursor(String cursor, UUID idAfter) {
    if (cursor == null || idAfter == null) {
      return null;
    }

    Instant createdAtCursor = Instant.parse(cursor);
    return follow.createdAt.lt(createdAtCursor)
        .or(follow.createdAt.eq(createdAtCursor).and(follow.id.lt(idAfter)));
  }
}
