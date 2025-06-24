package com.codeit.weatherwear.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor
public class Notification {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private Instant createdAt;

  @Column(nullable = false)
  private UUID receiverId;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String content;

  private Notification(UUID receiverId, String title, String content) {
    this.createdAt = Instant.now();
    this.receiverId = receiverId;
    this.title = title;
    this.content = content;
  }

  public static Notification create(UUID receiverId, String title, String content) {
    return new Notification(receiverId, title, content);
  }
}
