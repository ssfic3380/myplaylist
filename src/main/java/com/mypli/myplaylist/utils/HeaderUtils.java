package com.mypli.myplaylist.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HeaderUtils {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);

        if (StringUtils.hasText(headerValue) && headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    public static void setAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(HEADER_AUTHORIZATION, TOKEN_PREFIX + accessToken);
    }

}
