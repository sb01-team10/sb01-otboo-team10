package com.codeit.weatherwear.domain.security.config;

import com.codeit.weatherwear.domain.security.SecurityRequestMatcher;
import com.codeit.weatherwear.domain.security.customauthentication.CustomAuthenticationFailureHandler;
import com.codeit.weatherwear.domain.security.customauthentication.CustomAuthenticationFilter;
import com.codeit.weatherwear.domain.security.customauthentication.CustomAuthenticationSuccessHandler;
import com.codeit.weatherwear.domain.security.customauthentication.CustomUserDetailsService;
import com.codeit.weatherwear.domain.security.customauthentication.jwt.JwtAuthenticationFilter;
import com.codeit.weatherwear.domain.security.customauthentication.jwt.JwtLogoutHandler;
import com.codeit.weatherwear.domain.security.customauthentication.oauth2.CustomOAuth2SuccessHandler;
import com.codeit.weatherwear.domain.security.customauthentication.oauth2.CustomOAuth2UserService;
import com.codeit.weatherwear.domain.security.service.JwtSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

  @Bean
  SecurityFilterChain chain(HttpSecurity httpSecurity,
      CustomAuthenticationFilter customAuthenticationFilter,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      JwtLogoutHandler jwtLogoutHandler,
      CustomOAuth2UserService customOAuth2UserService,
      @Qualifier("customOAuth2SuccessHandler") AuthenticationSuccessHandler customOAuth2SuccessHandler,
      AuthenticationFailureHandler customAuthenticationFailureHandler)
      throws Exception {

    httpSecurity
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(SecurityRequestMatcher.PUBLIC_MATCHERS).permitAll()
            .requestMatchers("/api/**").hasRole("USER")
            .anyRequest().authenticated()
        )
        .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterAfter(jwtAuthenticationFilter, CustomAuthenticationFilter.class)
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService))
            .successHandler(customOAuth2SuccessHandler)
            .failureHandler(customAuthenticationFailureHandler)
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .csrf(csrf -> csrf
            .csrfTokenRepository(cookieCsrfTokenRepository()) // CSRF 토큰을 쿠키에 저장
            // CSRF 보호 미적용 경로
            .ignoringRequestMatchers(SecurityRequestMatcher.NON_API, SecurityRequestMatcher.SIGN_UP,
                SecurityRequestMatcher.SIGN_IN)
            .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())  // 요청에서 CSRF 토큰을 읽어 저장
            // 세션 방식 인증 시스템이 아니므로 세션 전략 null로 설정
            .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
        )
        .logout(logout -> logout
            .logoutRequestMatcher(SecurityRequestMatcher.SIGN_OUT)
            .deleteCookies("refresh_token")    // 쿠키 삭제
            .addLogoutHandler(jwtLogoutHandler) // JwtSession 삭제 & 토큰 블랙리스트 추가 핸들러
            .logoutSuccessHandler((request, response, authentication) -> {
              response.setStatus(HttpStatus.NO_CONTENT.value());
            })
        )
        .exceptionHandling(exceptionHandler -> exceptionHandler
            .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                new AntPathRequestMatcher("/api/**")))
    ;

    return httpSecurity.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      CustomUserDetailsService customUserDetailsService) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return new ProviderManager(provider);
  }

  @Bean
  public CustomAuthenticationFilter customAuthenticationFilter(
      ObjectMapper objectMapper,
      @Qualifier("customAuthenticationSuccessHandler") AuthenticationSuccessHandler authenticationSuccessHandler,
      AuthenticationFailureHandler authenticationFailureHandler,
      AuthenticationManager authenticationManager) {

    CustomAuthenticationFilter filter = new CustomAuthenticationFilter(objectMapper);
    // handler 설정
    filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
    filter.setAuthenticationFailureHandler(authenticationFailureHandler);
    // /api/auth/sign-in 경로에 적용
    filter.setFilterProcessesUrl("/api/auth/sign-in");
    // authenticationManager 지정
    filter.setAuthenticationManager(authenticationManager);
    return filter;
  }

  @Bean
  @Qualifier("customAuthenticationSuccessHandler")
  public AuthenticationSuccessHandler customAuthenticationSuccessHandler(
      ObjectMapper objectMapper, JwtSessionService jwtSessionService) {
    return new CustomAuthenticationSuccessHandler(objectMapper, jwtSessionService);
  }

  @Bean
  public AuthenticationFailureHandler customAuthenticationFailureHandler(
      ObjectMapper objectMapper) {
    return new CustomAuthenticationFailureHandler(objectMapper);
  }

  @Bean
  @Qualifier("customOAuth2SuccessHandler")
  public AuthenticationSuccessHandler customOAuth2SuccessHandler(
      JwtSessionService jwtSessionService) {
    return new CustomOAuth2SuccessHandler(jwtSessionService);
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_USER");
  }

  @Bean
  CookieCsrfTokenRepository cookieCsrfTokenRepository() {
    CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
    csrfTokenRepository.setCookieName("XSRF-TOKEN");
    csrfTokenRepository.setHeaderName("X-XSRF-TOKEN");
    return csrfTokenRepository;
  }
}
