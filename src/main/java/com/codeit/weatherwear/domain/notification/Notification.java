package com.codeit.weatherwear.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Notification {

  @Id
  @GeneratedValue
  private UUID id;

  @CreatedDate
  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private UUID receiverId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  private Notification(UUID receiverId, String title, String content) {
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }

  public static Notification create(UUID receiverId, String title, String content) {
    return new Notification(receiverId, title, content);
  }
}
