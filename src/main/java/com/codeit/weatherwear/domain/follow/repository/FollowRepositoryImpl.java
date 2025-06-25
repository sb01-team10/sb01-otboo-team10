package com.codeit.weatherwear.domain.follow.repository;

import static com.codeit.weatherwear.domain.follow.QFollow.follow;

import com.codeit.weatherwear.domain.follow.dto.FollowSummaryDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
