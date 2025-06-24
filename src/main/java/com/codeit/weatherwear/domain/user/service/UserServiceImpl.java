package com.codeit.weatherwear.domain.user.service;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.location.repository.LocationRepository;
import com.codeit.weatherwear.domain.user.dto.ProfileDto;
import com.codeit.weatherwear.domain.user.dto.ProfileUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.UserDto;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserAlreadyExistsException;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.mapper.UserMapper;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final LocationRepository locationRepository;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest) {
        // 중복 검사
        if (userRepository.existsByName(userCreateRequest.name()) ||
            userRepository.existsByEmail(userCreateRequest.email())) {
            throw new UserAlreadyExistsException();
        }

        User user = userRepository.save(
            User.builder()
                .name(userCreateRequest.name())
                .email(userCreateRequest.email())
                // TODO: Spring Security 적용 후 PasswordEncoder 적용
                .password(userCreateRequest.password())
                .locked(false)
                .build()
        );

        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public ProfileDto updateProfile(UUID userId, ProfileUpdateRequest profileUpdateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        // Location 생성
        Location location = null;
        LocationDto locationDto = profileUpdateRequest.location();
        if (locationDto != null) {
            String locationName = locationDto.locationNames().stream()
                .filter(s -> s != null && !s.isBlank())
                .collect(Collectors.joining(" "));
            location = locationRepository.save(
                new Location(
                    locationDto.latitude(),
                    locationDto.longitude(),
                    locationDto.x(),
                    locationDto.y(),
                    locationName
                )
            );
        }

        // TODO: S3 세팅 완료되면 ProfileImageUrl도 업데이트

        user.updateProfile(
            profileUpdateRequest.name(),
            profileUpdateRequest.gender(),
            profileUpdateRequest.birthDate(),
            location,
            profileUpdateRequest.temperatureSensitivity(),
            null);

        return userMapper.toProfileDto(user);
    }
    
}
