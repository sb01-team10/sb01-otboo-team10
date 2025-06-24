package com.codeit.weatherwear.location.repository;

import com.codeit.weatherwear.location.entity.Location;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

}
