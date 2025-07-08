package com.codeit.weatherwear.domain.security.customauthentication.oauth2;

import com.codeit.weatherwear.domain.security.customauthentication.CustomUserDetails;
import com.codeit.weatherwear.domain.security.entity.JwtSession;
import com.codeit.weatherwear.domain.security.service.JwtSessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtSessionService jwtSessionService;

  @Override
  @Transactional
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {

    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    UUID userId = userDetails.getUserId();

    // 기존 토큰 무효화 & 새 토큰 발급
    jwtSessionService.invalidateToken(userId);
    JwtSession jwtSession = jwtSessionService.createJwtSession(userId);

    // refresh_token 쿠키 설정
    Cookie refreshTokenCookie = new Cookie("refresh_token", jwtSession.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setSecure(false);
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30일
    response.addCookie(refreshTokenCookie);

    getRedirectStrategy().sendRedirect(request, response, "/#/recommendation");
  }

}
