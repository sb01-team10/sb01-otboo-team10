package com.codeit.weatherwear.domain.ootd.repository;

import com.codeit.weatherwear.domain.ootd.entity.Ootd;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OotdRepository extends JpaRepository<Ootd, UUID> {

}
