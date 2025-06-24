package com.codeit.weatherwear.security.repository;

import com.codeit.weatherwear.security.entity.JwtSession;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JwtSessionRepository extends JpaRepository<JwtSession, UUID> {

}
