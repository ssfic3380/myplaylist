package com.mypli.myplaylist.oauth2.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //유효한 자격증명을 제공하지 않고(로그인(인증) 없이) 접근하려 하면 401  ==> 로그인 페이지로 redirect로 수정
        
        //log.error("Responding with unauthorized error.", authException);
        /*response.sendError(
                HttpServletResponse.SC_UNAUTHORIZED,
                authException.getLocalizedMessage());*/

        log.error("Responding with unauthorized error");
        response.sendRedirect("/oauth2/login");
    }
}
