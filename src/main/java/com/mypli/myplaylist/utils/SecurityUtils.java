package com.mypli.myplaylist.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    //Controller 등에서 현재 로그인한 사용자의 oauthId를 가져오기 위해서 사용
    //JwtAuthFilter의 doInternalFilter에서 저장 (accessToken 확인할 때)
    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
