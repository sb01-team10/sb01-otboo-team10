package com.codeit.weatherwear.domain.user.controller;

import com.codeit.weatherwear.domain.user.dto.request.ChangePasswordRequest;
import com.codeit.weatherwear.domain.user.dto.request.ProfileUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.request.UserLockUpdateRequest;
import com.codeit.weatherwear.domain.user.dto.response.ProfileDto;
import com.codeit.weatherwear.domain.user.dto.response.UserDto;
import com.codeit.weatherwear.domain.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<UserDto> createUser(
        @Valid @RequestBody UserCreateRequest userCreateRequest) {
        UserDto result = userService.create(userCreateRequest);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/profiles")
    ResponseEntity<ProfileDto> findProfile(@PathVariable UUID userId) {
        ProfileDto result = userService.findProfile(userId);
        return ResponseEntity.ok(result);
    }

    @PatchMapping(value = "/{userId}/profiles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ProfileDto> updateProfile(
        @PathVariable UUID userId,
        @Valid @RequestPart ProfileUpdateRequest profileUpdateRequest,
        @RequestPart(required = false) MultipartFile profileImage) {
        // TODO: S3 세팅 후 프로필 이미지 업데이트 추가
        ProfileDto result = userService.updateProfile(userId, profileUpdateRequest);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{userId}/lock")
    ResponseEntity<UUID> updateLock(@PathVariable UUID userId,
        @RequestBody UserLockUpdateRequest userLockUpdateRequest) {
        UUID result = userService.updateLock(userId, userLockUpdateRequest);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{userId}/password")
    ResponseEntity<Void> updatePassword(@PathVariable UUID userId,
        @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.updatePassword(userId, changePasswordRequest);
        return ResponseEntity.ok().build();
    }
}
