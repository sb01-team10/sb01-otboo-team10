package com.codeit.weatherwear.feed.repository;

import com.codeit.weatherwear.feed.entity.FeedComment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedCommentRepository extends JpaRepository<FeedComment, UUID> {

}
