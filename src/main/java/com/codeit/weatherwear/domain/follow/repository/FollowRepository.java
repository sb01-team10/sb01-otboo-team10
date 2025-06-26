package com.codeit.weatherwear.domain.follow.repository;

import com.codeit.weatherwear.domain.follow.Follow;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface FollowRepository extends
    JpaRepository<Follow, UUID>, FollowRepositoryCustom, QuerydslPredicateExecutor<Follow> {

  long countByFollower_Id(UUID followerId);

  long countByFollowee_Id(UUID followeeId);
}
