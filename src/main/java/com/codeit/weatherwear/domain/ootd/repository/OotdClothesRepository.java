package com.codeit.weatherwear.domain.ootd.repository;

import com.codeit.weatherwear.domain.ootd.entity.OotdClothes;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OotdClothesRepository extends JpaRepository<OotdClothes, UUID> {

}
