package com.codeit.weatherwear.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * 사용자 정보를 저장하는 엔티티 클래스.
 * <p>
 * {@code BaseEntity}를 상속받아 생성 뿐만 아니라 수정 시 수정 시간을 자동으로 업데이트.
 */
@MappedSuperclass
@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {

  @LastModifiedDate
  @Column(name = "updated_at", updatable = true)
  private Instant updatedAt;

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = Instant.now();
  }

}
