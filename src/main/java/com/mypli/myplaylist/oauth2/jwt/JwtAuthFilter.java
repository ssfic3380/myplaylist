package com.mypli.myplaylist.oauth2.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypli.myplaylist.controller.api.ApiResponseHeader;
import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.repository.MemberRepository;
import com.mypli.myplaylist.utils.CookieUtils;
import com.mypli.myplaylist.utils.HeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    private final static String REFRESH_TOKEN = "refresh-token";
    private final int COOKIE_PERIOD = 60 * 60 * 24; //하루

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //1. Request Header의 "Authorization: Bearer "에서 Access Token을 꺼낸다.
        //String accessToken = HeaderUtils.getAccessToken(request);
        String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTE1MTE3MzIxODQxODcxODk0OTEiLCJyb2xlIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjc0NTAzNzgxfQ.k62qblFOw0cdWnXIXF0dvr3x7WCGRVX51aYEN-lXT95P1A-2ce0pVkdVKtoptfJSgB8CpcoheE4G-lwFzxMM5Q";
        String refreshToken = CookieUtils.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        log.info("Filter AccessToken = {}", accessToken);
        log.info("Filter RefreshToken = {}", refreshToken);

        //2. Access Token의 유효성 검사를 하고, 정상 토큰이면 해당 토큰에서 Authenticaiton을 가져와서 SecurityContext에 저장한다.
        if (StringUtils.hasText(accessToken)) {

            if (tokenProvider.validateToken(accessToken)) { //AccessToken 정상
                Authentication authentication = tokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Filter Passed");
            }

            else if (StringUtils.hasText(refreshToken)) { //AccessToken 비정상, RefreshToken 존재
                if (tokenProvider.validateToken(refreshToken)) { //RefreshToken은 일단 정상

                    String oauthId = tokenProvider.parseClaims(accessToken).getSubject();
                    Member member = memberRepository.findBySocialId(oauthId);

                    if (compareWithDB(refreshToken, member)) { //DB의 RefreshToken과도 같으면
                        JwtToken newToken = tokenProvider.renewToken(accessToken, refreshToken); //accessToken 재발급, refreshToken 3일 이하시 재발급
                        accessToken = newToken.getAccessToken();
                        refreshToken = newToken.getRefreshToken();

                        //Header에 AccessToken 추가
                        HeaderUtils.setAccessToken(response, accessToken);

                        //Cookie에 RefreshToken 추가
                        CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
                        CookieUtils.addCookie(response, REFRESH_TOKEN, refreshToken, COOKIE_PERIOD);

                        //DB의 RefreshToken 갱신
                        member.updateJwtRefreshToken(refreshToken);

                    }
                    else { //RefreshToken이 만료됐거나, DB와 일치하지 않을 경우
                        setErrorResponse(response, HttpStatus.BAD_REQUEST, "RefreshToken Expired");
                        return;
                    }
                }
            }
        }

        chain.doFilter(request, response);

    }

    private boolean compareWithDB(String refreshToken, Member member) {
        return refreshToken.equals(member.getJwtRefreshToken());
    }

    public void setErrorResponse(HttpServletResponse response, HttpStatus status, String msg) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
        String json = new ObjectMapper().writeValueAsString(new ApiResponseHeader(status.value(), msg));
        response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}