package com.codeit.weatherwear.domain.ootd.repository;

import com.codeit.weatherwear.domain.ootd.entity.Ootd;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OotdRepository extends JpaRepository<Ootd, UUID> {

  @Query("select o from Ootd o where o.feed.id = :feedId")
  List<Ootd> findByFeedId(@Param("feedId") UUID feedId);
}
