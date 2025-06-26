package com.codeit.weatherwear.domain.location.service;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.location.repository.LocationRepository;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Transactional
    @Override
    public Location create(LocationDto locationDto) {
        String locationName = locationDto.locationNames().stream()
            .filter(s -> s != null && !s.isBlank())
            .collect(Collectors.joining(" "));
        Location location = new Location(
            locationDto.latitude(),
            locationDto.longitude(),
            locationDto.x(),
            locationDto.y(),
            locationName
        );

        // 이미 존재하면 기존 객체 반환
        return locationRepository.findByLatitudeAndLongitudeAndXAndYAndName(
            location.getLatitude(),
            location.getLongitude(),
            location.getX(),
            location.getY(),
            location.getName()
        ).orElseGet(() -> locationRepository.save(location));
    }
}
