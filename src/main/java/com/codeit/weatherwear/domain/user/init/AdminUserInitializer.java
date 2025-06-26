package com.codeit.weatherwear.domain.user.init;

import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 사용자 관리 - 어드민 기능 서버 시작 시 어드민 계정 자동 초기화 email: admin@mail.com name: admin password: admin
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;

    // TODO: passwordEncoder 적용

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String name = "admin";
        String email = "system@otboo.io";
        String password = "otboo1!";

        if (!userRepository.existsByEmail(email) && !userRepository.existsByName(name)) {
            User admin = userRepository.save(
                User.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .role(Role.ADMIN)
                    .build()
            );
            log.info("Admin User Created: {}", email);
        } else {
            log.info("Admin User({}) Already Exists", email);
        }
    }
}
