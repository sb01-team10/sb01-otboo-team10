package com.codeit.weatherwear.domain.user.service;

import com.codeit.weatherwear.domain.location.dto.LocationDto;
import com.codeit.weatherwear.domain.location.entity.Location;
import com.codeit.weatherwear.domain.location.service.LocationService;
import com.codeit.weatherwear.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherwear.domain.user.dto.request.ProfileUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserRoleUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserSearchRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserSortDirection;
import com.codeit.weatherwear.domain.user.dto.response.ProfileDto;
import com.codeit.weatherwear.domain.user.dto.response.UserDto;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserAlreadyExistsException;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.mapper.UserMapper;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import com.codeit.weatherwear.global.response.PageResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final LocationService locationService;

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
            location = locationService.create(locationDto);
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

        String newPassword = changePasswordRequest.password();

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
    public PageResponse searchUsers(UserSearchRequest userSearchRequest) {
        String cursor = userSearchRequest.cursor();
        UUID idAfter = userSearchRequest.idAfter();
        int limit = userSearchRequest.limit();
        String sortBy = userSearchRequest.sortBy();
        UserSortDirection sortDirection = userSearchRequest.sortDirection();
        String emailLike = userSearchRequest.emailLike();
        Role roleEqual = userSearchRequest.roleEqual();
        Boolean locked = userSearchRequest.locked();

        Slice<User> slice = userRepository.searchUsers(cursor, idAfter, limit, sortBy,
            sortDirection, emailLike, roleEqual, locked);

        List<User> users = slice.getContent();
        List<UserDto> userDtos = users.stream()
            .map(userMapper::toUserDto)
            .toList();

        boolean hasNext = slice.hasNext();

        // 커서 구하기
        User lastUser = (users.size() > 0) ? users.get(users.size() - 1) : null;
        Object nextCursor = null;
        UUID nextIdAfter = null;
        if (lastUser != null && hasNext) {
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

        return new PageResponse(
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
