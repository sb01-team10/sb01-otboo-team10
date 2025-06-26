package com.codeit.weatherwear.domain.user.init;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminUserInitializerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserInitializer adminUserInitializer;

    @Test
    void 관리자_생성_성공() throws Exception {
        // given
        when(userRepository.existsByEmail("system@otboo.io")).thenReturn(false);
        when(userRepository.existsByName("admin")).thenReturn(false);

        // save()가 호출될 때 어떤 User 객체가 저장되었는지 확인하기 위해 ArgumentCaptor로 캡처
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(
            invocationOnMock -> invocationOnMock.getArgument(0));

        // when
        adminUserInitializer.run(null);

        // then
        verify(userRepository).save(captor.capture());  // 호출된 user 객체를 captor에 저장
        User savedUser = captor.getValue(); // 캡처된 인자 꺼내서 검증
        assertThat(savedUser.getEmail()).isEqualTo("system@otboo.io");
        assertThat(savedUser.getName()).isEqualTo("admin");
    }
}