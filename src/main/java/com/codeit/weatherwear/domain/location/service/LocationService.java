package com.codeit.weatherwear.domain.location.service;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.location.entity.Location;

public interface LocationService {

    Location create(LocationDto locationDto);
}
