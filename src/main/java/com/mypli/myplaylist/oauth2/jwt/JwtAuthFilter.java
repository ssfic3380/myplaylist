package com.mypli.myplaylist.oauth2.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypli.myplaylist.utils.HeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //1. Request Header의 "Authorization: Bearer "에서 Access Token을 꺼낸다.
        String accessToken = HeaderUtils.getAccessToken(request);

        //2. Access Token의 유효성 검사를 하고, 정상 토큰이면 해당 토큰에서 Authenticaiton을 가져와서 SecurityContext에 저장한다.
        if (StringUtils.hasText(accessToken) && tokenProvider.validateToken(accessToken)) {
            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            log.info("JwtAuthFilter :: authentication = {}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(authentication));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);

    }
}
