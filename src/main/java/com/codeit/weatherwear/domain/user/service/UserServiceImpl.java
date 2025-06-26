package com.codeit.weatherwear.domain.user.service;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.location.repository.LocationRepository;
import com.codeit.weatherwear.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherwear.domain.user.dto.request.ProfileUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserSortDirection;
import com.codeit.weatherwear.domain.user.dto.response.ProfileDto;
import com.codeit.weatherwear.domain.user.dto.response.UserDto;
import com.codeit.weatherwear.domain.user.dto.response.UserPageResponse;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserAlreadyExistsException;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.mapper.UserMapper;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final LocationRepository locationRepository;
    private final PasswordEncoder passwordEncoder;

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
                .password(passwordEncoder.encode(userCreateRequest.password()))
                .locked(false)
                .build()
        );

        return userMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public ProfileDto findProfile(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());
        return userMapper.toProfileDto(user);
    }

    @Transactional
    @Override
    public ProfileDto updateProfile(UUID userId, ProfileUpdateRequest profileUpdateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        // Location 생성
        // TODO: Location 생성 로직은 LocationService에 위임
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

    @Transactional
    @Override
    public UUID updateLock(UUID userId, UserLockUpdateRequest userLockUpdateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        user.updateLocked(userLockUpdateRequest.locked());

        return user.getId();
    }

    @Transactional
    @Override
    public void updatePassword(UUID userId, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());

        String newPassword = passwordEncoder.encode(changePasswordRequest.password());

        user.updatePassword(newPassword);
    }

    @Transactional
    @Override
    public UserDto updateRole(UUID userId, UserRoleUpdateRequest userRoleUpdateRequest) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException());
        user.updateRole(userRoleUpdateRequest.role());

        return userMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserPageResponse<UserDto> searchUsers(String cursor, UUID idAfter, int limit,
        String sortBy, UserSortDirection sortDirection, String emailLike, Role roleEqual,
        Boolean locked) {

        Slice<User> slice = userRepository.searchUsers(cursor, idAfter, limit, sortBy,
            sortDirection, emailLike, roleEqual, locked);

        List<User> users = slice.getContent();
        List<UserDto> userDtos = users.stream()
            .map(userMapper::toUserDto)
            .toList();

        // 커서 구하기
        User lastUser = (users.size() > 0) ? null : users.get(users.size() - 1);
        Object nextCursor = null;
        UUID nextIdAfter = null;
        if (lastUser != null) {
            switch (sortBy) {
                case "email":
                    nextCursor = lastUser.getEmail();
                    break;
                case "createdAt":
                    nextCursor = lastUser.getCreatedAt();
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 정렬 기준입니다.");
            }
            nextIdAfter = lastUser.getId();
        }

        return new UserPageResponse<>(
            userDtos,
            nextCursor,
            nextIdAfter,
            slice.hasNext(),
            userRepository.getTotalCount(emailLike, roleEqual, locked),
            sortBy,
            sortDirection.name()
        );
    }

}
