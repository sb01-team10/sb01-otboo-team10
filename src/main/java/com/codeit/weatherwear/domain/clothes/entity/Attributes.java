package com.codeit.weatherwear.domain.clothes.entity;

import com.codeit.weatherwear.domain.clothes.dto.ClothesAttributeDefUpdateRequest;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "clothes_attribute_def")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Attributes {
    @Id@GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,updatable = false)
    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Column(nullable = false)
    private String name;

    @Type(JsonType.class)
    @Column(name = "selectable_values", columnDefinition = "jsonb")
    private List<String> selectableValues;

    public void update(ClothesAttributeDefUpdateRequest request) {
        if (!this.name.equals(request.name())) {
            throw new IllegalArgumentException("해당 속성명이 일치하지 않습니다.");
        }
        List<String> values = request.selectValues();
        if(values.size() != values.stream().distinct().count()) {
            throw new IllegalArgumentException("중복 선택값이 존재합니다.");
        }

        this.selectableValues.clear();
        this.selectableValues=new ArrayList<>(values);
        this.updatedAt = Instant.now();
    }
}
