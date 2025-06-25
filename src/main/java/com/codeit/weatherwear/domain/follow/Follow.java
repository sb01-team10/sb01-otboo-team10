package com.codeit.weatherwear.domain.follow;

import com.codeit.weatherwear.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "follows", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"followee_id", "follower_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY,  optional = false)
  @JoinColumn(name = "followee_id", nullable = false)
  private User followee;

  @ManyToOne(fetch = FetchType.LAZY,  optional = false)
  @JoinColumn(name = "follower_id", nullable = false)
  private User follower;

  private Follow(User followee, User follower) {
    this.followee = followee;
    this.follower = follower;
  }

  public static Follow create(User followee, User follower) {
    return new Follow(followee, follower);
  }

}
