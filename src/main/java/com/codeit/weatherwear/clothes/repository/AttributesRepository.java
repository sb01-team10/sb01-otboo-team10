package com.codeit.weatherwear.clothes.repository;

import com.codeit.weatherwear.clothes.entity.Attributes;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributesRepository extends JpaRepository<Attributes, UUID> {

}
