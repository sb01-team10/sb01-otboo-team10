package com.codeit.weatherwear.security.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "jwt_sessions")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JwtSession {

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

    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID userId;

    @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
    private String accessToken;

    @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
    private String refreshToken;

    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private Instant expirationTime;

    public JwtSession(UUID userId, String accessToken, String refreshToken,
        Instant expirationTime) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
    }
}
