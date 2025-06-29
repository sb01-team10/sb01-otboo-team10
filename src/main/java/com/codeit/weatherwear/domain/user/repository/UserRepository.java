package com.codeit.weatherwear.domain.user.repository;

import com.codeit.weatherwear.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, UserCustomRepository {

    boolean existsByName(String name);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
