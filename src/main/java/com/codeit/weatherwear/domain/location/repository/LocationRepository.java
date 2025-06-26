package com.codeit.weatherwear.domain.location.repository;

import com.codeit.weatherwear.domain.location.entity.Location;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByLatitudeAndLongitudeAndXAndYAndName(double latitude, double longitude,
        int x, int y, String name);
}
