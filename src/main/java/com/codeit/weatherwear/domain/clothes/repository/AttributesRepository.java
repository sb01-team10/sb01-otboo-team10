package com.codeit.weatherwear.domain.clothes.repository;

import com.codeit.weatherwear.domain.clothes.entity.Attributes;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributesRepository extends JpaRepository<Attributes, UUID>,AttributesCustomRepository {
    Optional<Attributes> findByName(String name);
    boolean existsByName(String name);
}
