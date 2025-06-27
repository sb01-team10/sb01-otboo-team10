package com.codeit.weatherwear.domain.security.customauthentication;

import com.codeit.weatherwear.domain.auth.dto.SignInRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " +
                request.getMethod());
        }

        // Request Body에서 email, password 추출
        try {
            SignInRequest signInRequest = objectMapper.readValue(request.getInputStream(),
                SignInRequest.class);
            String email = signInRequest.email();
            String password = signInRequest.password();

            // Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                email, password);

            // request에서 IP 등을 추출해서 authenticationToken.setDetails()로 인증 객체에 부가 정보를 넣음
            setDetails(request, authenticationToken);

            return this.getAuthenticationManager().authenticate(authenticationToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
