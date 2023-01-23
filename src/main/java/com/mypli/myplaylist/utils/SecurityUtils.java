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
    //
    //(*) JWT 방식으로 구현 시, SecurityContext를 세션에 저장하지 않고, 하나의 요청을 처리할 때 SecurityContext 하나가 생성되고 응답이 끝나면 버려짐
    //    => TODO: JwtAuthFilter의 doInternalFilter에서(accessToken 확인할 때) 저장하긴 하니까, 한번 확인해보자
    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("Security Context에 인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
