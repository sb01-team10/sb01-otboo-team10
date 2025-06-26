package com.codeit.weatherwear.domain.ootd.entity;

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
@Table(name = "ootd_clothes")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OotdClothes {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private Instant updatedAt;

  //  @ManyToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "feed_id", nullable = false)
  // todo: Clothes 엔티티 추가되면 해당 부분 수정 예정
  @Column(name = "clothes_id")
  private UUID clothesId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ootd_id", nullable = false)
  private Ootd ootd;

  @Builder
  private OotdClothes(UUID clothesId, Ootd ootd) {
    this.clothesId = clothesId;
    this.ootd = ootd;
  }
}
