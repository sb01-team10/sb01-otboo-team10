package com.codeit.weatherwear.domain.security.customauthentication.oauth2;

import com.codeit.weatherwear.domain.security.customauthentication.CustomUserDetails;
import com.codeit.weatherwear.domain.user.entity.OAuthProvider;
import com.codeit.weatherwear.domain.user.entity.Role;
import com.codeit.weatherwear.domain.user.entity.User;
import com.codeit.weatherwear.domain.user.repository.UserRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  private final UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);
    String provider = userRequest.getClientRegistration().getRegistrationId();  // google / kakao

    String email = null;
    String name = null;

    if (provider.equals("google")) {
      email = oAuth2User.getAttribute("email");
      name = oAuth2User.getAttribute("name");
    } else if (provider.equals("kakao")) {
      Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
      Map<String, Object> profile =
          kakaoAccount != null ? (Map<String, Object>) kakaoAccount.get("profile") : null;
      name = profile != null ? (String) profile.get("nickname") : "kakao_user";
      // 카카오의 경우 이메일을 닉네임+kakao.com으로
      email = name + "@kakao.com";
    } else {
      throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + provider);
    }

    User user = userRepository.findByEmail(email)
        .orElse(null);
    if (user == null) {
      // 새로운 회원으로 추가
      user = userRepository.save(User.builder()
          .name(name)
          .email(email)
          .password("")
          .linkedOAuthProviders(List.of(OAuthProvider.valueOf(provider)))
          .role(Role.USER)
          .build());
    }
    return new CustomUserDetails(user.getId()
        , user.getEmail(), user.getPassword(), user.getRole(), user.isLocked(),
        user.getTempPasswordExpirationTime());
  }
}
