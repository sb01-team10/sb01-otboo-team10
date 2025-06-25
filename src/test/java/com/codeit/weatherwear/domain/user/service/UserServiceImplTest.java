package com.codeit.weatherwear.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.weatherwear.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherwear.domain.user.dto.request.ProfileUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.response.ProfileDto;
import com.codeit.weatherwear.domain.user.dto.response.UserDto;
import com.codeit.weatherwear.domain.user.entity.Gender;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserAlreadyExistsException;
import com.codeit.weatherwear.domain.user.exception.UserNotFoundException;
import com.codeit.weatherwear.domain.user.mapper.UserMapper;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @Test
    void 회원가입_성공() {
        // given
        UserCreateRequest request = new UserCreateRequest("test", "test@test.com", "test");

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByName(request.name())).thenReturn(false);

        User user = User.builder()
            .name(request.name())
            .email(request.email())
            .password(request.password())
            .locked(false)
            .build();

        UserDto dto = new UserDto(
            UUID.randomUUID(),
            Instant.now(),
            request.email(),
            request.name(),
            user.getRole(),
            user.getLinkedOAuthProviders(),
            user.isLocked()
        );

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(dto);

        // when
        UserDto result = userService.create(request);

        // then
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void 회원가입_실패() {
        // given
        UserCreateRequest request = new UserCreateRequest("test", "test@test.com", "test");

        when(userRepository.existsByName(request.name())).thenReturn(true);

        // when & then
        assertThrows(UserAlreadyExistsException.class, () -> userService.create(request));
    }

    @Test
    void 프로필_업데이트_성공() {
        //given
        UUID userId = UUID.randomUUID();

        ProfileUpdateRequest request = new ProfileUpdateRequest(
            "newName",
            Gender.MALE,
            LocalDate.now(),
            null,
            null
        );

        User user = User.builder()
            .name("originalName")
            .build();

        ProfileDto dto = new ProfileDto(
            userId,
            "newName",
            Gender.MALE,
            null,
            null,
            null,
            null
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toProfileDto(user)).thenReturn(dto);

        // when
        ProfileDto result = userService.updateProfile(userId, request);

        // then
        assertThat(result.getName()).isEqualTo("newName");
        assertThat(result.getGender()).isEqualTo(Gender.MALE);
    }

    @Test
    void 프로필_업데이트_실패() {
        // given
        UUID userId = UUID.randomUUID();
        ProfileUpdateRequest request = new ProfileUpdateRequest(
            null,
            null,
            null,
            null,
            null
        );
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserNotFoundException.class, () -> userService.updateProfile(userId, request));
    }

    @Test
    void 잠금상태_변경_성공() {
        // given
        UUID userId = UUID.randomUUID();

        UserLockUpdateRequest request = new UserLockUpdateRequest(true);

        User user = User.builder()
            .locked(false)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.updateLock(userId, request);

        // then
        assertThat(user.isLocked()).isEqualTo(true);
    }

    @Test
    void 프로필_조회_성공() {
        // given
        UUID userId = UUID.randomUUID();

        User user = User.builder()
            .name("test")
            .build();

        ProfileDto dto = new ProfileDto(userId, "test", null, null, null, null, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toProfileDto(user)).thenReturn(dto);

        // when
        ProfileDto result = userService.findProfile(userId);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getName()).isEqualTo("test");
    }

    @Test
    void 비밀번호_변경_성공() {
        // given
        UUID userId = UUID.randomUUID();

        User user = User.builder()
            .id(userId)
            .password("originalPassword")
            .build();

        ChangePasswordRequest request = new ChangePasswordRequest("newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        userService.updatePassword(userId, request);

        // then
        assertThat(user.getPassword()).isEqualTo("newPassword");
    }
}