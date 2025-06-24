package com.codeit.weatherwear.domain.user.service;

import com.codeit.weatherwear.domain.user.dto.UserCreateRequest;
import com.codeit.weatherwear.domain.user.dto.UserDto;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.exception.UserAlreadyExistsException;
import com.codeit.weatherwear.domain.user.mapper.UserMapper;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
}
