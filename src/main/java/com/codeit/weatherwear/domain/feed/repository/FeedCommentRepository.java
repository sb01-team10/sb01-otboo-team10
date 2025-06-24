package com.codeit.weatherwear.domain.feed.repository;

import com.codeit.weatherwear.domain.feed.entity.FeedComment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, UUID> {

}
