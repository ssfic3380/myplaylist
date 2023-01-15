package com.mypli.myplaylist.oauth2.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mypli.myplaylist.domain.Role;
import com.mypli.myplaylist.dto.GlobalResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenService tokenService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        log.info("doFilterInternal");

        String accessToken = tokenService.getAccessToken(request); //Access Token은 "Authentication: Bearer ${accessToken}"으로
        log.info("accessToken = {}", accessToken);
        String refreshToken = tokenService.getRefreshToken(request); //Refresh Token은 Cookie로
        log.info("refreshToken = {}", refreshToken);

        if (accessToken != null) {
            if (tokenService.verifyToken(accessToken)) { //AccessToken 유효
                String oauthId = tokenService.getUid(accessToken);
                setAuthentication(oauthId);
                log.info("AccessToken Authenticated");
            }
            else if (refreshToken != null) { //AccessToken 만료
                if (tokenService.verifyRefreshToken(refreshToken)) { //RefreshToken 유효
                    String oauthId = tokenService.getUid(refreshToken);

                    JwtToken newToken = tokenService.generateToken(oauthId, Role.ROLE_USER.getKey());
                    String newAccessToken = newToken.getAccessToken();
                    response.setHeader("access-token", newAccessToken);
                    response.setContentType("application/json;charset=UTF-8");

                    setAuthentication(tokenService.getUid(newAccessToken));
                }
                else { //RefreshToken 만료 또는 DB와 다름 (새로운 로그인 필요)
                    jwtExceptionHandler(response, "RefreshToken Expired", HttpStatus.BAD_REQUEST);
                    return;
                }
            }
        }
        else { //RefreshToken만 있을 때
            if (tokenService.verifyRefreshToken(refreshToken)) { //RefreshToken 유효
                String oauthId = tokenService.getUid(refreshToken);
                //TODO: refresh token에 oauthId 넣지 말도록 변경
            }
        }

        chain.doFilter(request, response);
    }

    public void setAuthentication(String oauthId) {
        Authentication auth = tokenService.createAuthentication(oauthId);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public void jwtExceptionHandler(HttpServletResponse response, String msg, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        try {
            String json = new ObjectMapper().writeValueAsString(new GlobalResDto(msg, status.value()));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
