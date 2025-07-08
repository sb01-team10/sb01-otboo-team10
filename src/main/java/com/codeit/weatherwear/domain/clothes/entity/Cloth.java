package com.codeit.weatherwear.domain.clothes.entity;

import com.codeit.weatherwear.domain.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "clothes")
@Getter
public class Cloth {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(updatable = false)
  private UUID id;

  @CreatedDate
  @Column(updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(updatable = true)
  private Instant updatedAt;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ClothType clothType;

  @Column(name = "image_url")
  private String clothesImageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id")
  private User user;

  @BatchSize(size = 100)
  @OneToMany(mappedBy = "cloth", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ClothWithAttributes> clothesWithAttributes;

  public static final String FIELD_CREATED_AT = "createdAt";

  @Builder
  public Cloth(UUID id, Instant createdAt, Instant updatedAt, String name, ClothType clothType,
      String clothesImageUrl, User user, List<ClothWithAttributes> clothesWithAttributes) {
    this.id = id;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.name = name;
    this.clothType = clothType;
    this.clothesImageUrl = clothesImageUrl;
    this.user = user;
    this.clothesWithAttributes = Optional.ofNullable(clothesWithAttributes)
        .orElse(new ArrayList<>());
  }

  public void addAttribute(ClothWithAttributes attributes) {
    this.clothesWithAttributes.add(attributes);
    attributes.setClothes(this);
  }

  public void clearAttributes() {
    this.clothesWithAttributes.clear();
  }

  public void updateCloth(String name, ClothType type) {
    this.name = name;
    this.clothType = type;
  }

  public void updateImageUrl(String imageUrl) {
    this.clothesImageUrl = imageUrl;
  }

}
