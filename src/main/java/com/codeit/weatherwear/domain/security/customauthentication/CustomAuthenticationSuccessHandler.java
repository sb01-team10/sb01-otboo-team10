package com.codeit.weatherwear.domain.security.customauthentication;

import com.codeit.weatherwear.domain.security.entity.JwtSession;
import com.codeit.weatherwear.domain.security.service.JwtSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;
  private final JwtSessionService jwtSessionService;


  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    // 인증된 사용자 정보
    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
    UUID userId = userDetails.getUserId();

    // 기존에 로그인된 계정이 있을 경우, 강제로 로그아웃 처리
    jwtSessionService.invalidateToken(userId);

    // 토큰 발급
    JwtSession jwtSession = jwtSessionService.createJwtSession(userId);

    response.setStatus(HttpStatus.OK.value());
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // response body에 액세스 토큰, response cookie에 리프레시 토큰 삽입
    Cookie refreshTokenCookie = new Cookie("refresh_token", jwtSession.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);   // 리프레시 토큰은 서버에서만 사용하므로 자바 스크립트에서 접근 x -> true로 서렂ㅇ
    refreshTokenCookie.setSecure(false); // https가 아닌 http 환경에서 개발, 배포하므로 false
    refreshTokenCookie.setPath("/");
    refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 쿠키 만료 시간 30일
    refreshTokenCookie.setAttribute("SameSite", "Lax");
    response.addCookie(refreshTokenCookie);
    objectMapper.writeValue(response.getWriter(), jwtSession.getAccessToken());
  }
}
