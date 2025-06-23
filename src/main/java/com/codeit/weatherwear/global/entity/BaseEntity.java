package com.codeit.weatherwear.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 엔티티 작성 시 공통적으로 들어가는 값들에 대해 정의
 * <p>
 * ID 생성 및 생성 일시를 매핑해 줌
 */
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class) //Entity의 Auditing 정보 주입 클래스
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @CreatedDate
  @Column(name = "created_at", updatable = false, insertable = false)
  private Instant createdAt;

}
