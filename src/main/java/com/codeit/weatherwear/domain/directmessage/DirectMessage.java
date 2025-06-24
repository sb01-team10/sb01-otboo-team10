package com.codeit.weatherwear.domain.directmessage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "direct_messages")
@Getter
@NoArgsConstructor
public class DirectMessage {

  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private Instant createdAt;

//  @ManyToOne(fetch = FetchType.LAZY, optional = false)
//  @JoinColumn(name = "sender_id",  nullable = false)
//  private User sender;
//
//  @ManyToOne(fetch = FetchType.LAZY, optional = false)
//  @JoinColumn(name = "receiver_id", nullable = false)
//  private User receiver;
//
//  @Column(nullable = false)
//  private String content;
//
//  private DirectMessage(User sender, User receiver, String content) {
//    this.createdAt = Instant.now();
//    this.sender = sender;
//    this.receiver = receiver;
//    this.content = content;
//  }
//
//  public static DirectMessage create(User sender, User receiver, String content) {
//    return new DirectMessage(sender, receiver, content);
//  }

}
