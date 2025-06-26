package com.codeit.weatherwear.domain.user.entity;

import com.codeit.weatherwear.domain.location.entity.Location;
import com.vladmihalcea.hibernate.type.json.JsonType;
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
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
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
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", updatable = true)
    private Instant updatedAt;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private boolean locked = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "temperature_sensitivity")
    private Integer temperatureSensitivity;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Type(JsonType.class)
    @Column(name = "linked_oauth_providers", columnDefinition = "jsonb")
    private List<OAuthProvider> linkedOAuthProviders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @Builder
    private User(UUID id, String email, String name, String password,
        Role role, boolean locked, Gender gender, LocalDate birthDate,
        Integer temperatureSensitivity, String profileImageUrl,
        List<OAuthProvider> linkedOAuthProviders,
        Location location, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.role = role == null ? Role.USER : role;
        this.locked = locked;
        this.gender = gender;
        this.birthDate = birthDate;
        this.temperatureSensitivity = temperatureSensitivity;
        this.profileImageUrl = profileImageUrl;
        this.linkedOAuthProviders = linkedOAuthProviders;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateProfile(String name, Gender gender, LocalDate birthDate, Location location,
        Integer temperatureSensitivity, String profileImageUrl) {
        if (name != null) {
            this.name = name;
        }
        if (gender != null) {
            this.gender = gender;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (location != null) {
            this.location = location;
        }
        if (temperatureSensitivity != null) {
            this.temperatureSensitivity = temperatureSensitivity;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void updateLocked(boolean locked) {
        this.locked = locked;
    }

    public void updatePassword(String password) {
        if (password != null && !password.isBlank()) {
            this.password = password;
        }
    }

    public void updateRole(Role role) {
        if (role != null) {
            this.role = role;
        }
    }
}
