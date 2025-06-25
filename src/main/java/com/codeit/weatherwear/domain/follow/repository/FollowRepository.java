package com.codeit.weatherwear.domain.follow.repository;

import com.codeit.weatherwear.domain.follow.Follow;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, UUID>, FollowRepositoryCustom {

}
