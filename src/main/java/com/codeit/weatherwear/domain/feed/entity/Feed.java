package com.codeit.weatherwear.domain.feed.entity;

import com.codeit.weatherwear.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "feed")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(name = "like_count")
  private int likeCount = 0;

  @Column(name = "comment_count")
  private int commentCount = 0;

  @Builder
  private Feed(int likeCount, int commentCount) {
    this.likeCount = likeCount;
    this.commentCount = commentCount;
  }

  public void updateLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public void increaseLikeCount() {
    this.likeCount++;
  }

  public void decreaseLikeCount() {
    this.likeCount--;
  }

  public void updateCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public void increaseCommentCount() {
    this.commentCount++;
  }

  public void decreaseCommentCount() {
    this.commentCount--;
  }


}
