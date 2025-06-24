package com.codeit.weatherwear.domain.user.controller;

import com.codeit.weatherwear.domain.user.dto.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.UserDto;
import com.codeit.weatherwear.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
