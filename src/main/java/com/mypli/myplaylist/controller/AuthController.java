package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.oauth2.jwt.JwtToken;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
import com.mypli.myplaylist.service.MemberService;
import com.mypli.myplaylist.utils.CookieUtils;
import com.mypli.myplaylist.utils.HeaderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final JwtTokenProvider tokenProvider;

    private final static String REFRESH_TOKEN = "refresh-token";
    private final int COOKIE_PERIOD = 60 * 60 * 24; //하루

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/redirect")
    public String oauth2Redirect(@RequestParam String token, RedirectAttributes redirectAttributes) {

        //TODO: oauth2 인증 성공했으면 SecurityContext 안에 있을 것 같은데 없음; (JwtFilter에서 setAuthentication 해주는거 아니면 안됨)
//        String socialId = SecurityUtils.getCurrentMemberId();
//        log.info("socialId = {}", socialId);

        redirectAttributes.addFlashAttribute("token", token);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(Principal principal, HttpServletRequest request, HttpServletResponse response) {

        String socialId = null;
        if (principal != null) socialId = principal.getName();
        log.info("logout: {}", socialId);

        memberService.updateJwtRefreshToken(socialId, "");

        CookieUtils.deleteCookie(request, response, "refresh-token");

        return "redirect:/";
    }

    @GetMapping("/refresh")
    public String jwtRefresh(HttpServletRequest request, HttpServletResponse response,
                             @RequestParam String returnUrl, RedirectAttributes redirectAttributes) {

        //String accessToken = HeaderUtils.getAccessToken(request);
        String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTE1MTE3MzIxODQxODcxODk0OTEiLCJyb2xlIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjc0NTAzNzgxfQ.k62qblFOw0cdWnXIXF0dvr3x7WCGRVX51aYEN-lXT95P1A-2ce0pVkdVKtoptfJSgB8CpcoheE4G-lwFzxMM5Q";
        String refreshToken = CookieUtils.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));

        if (accessToken == null) return "redirect:/oauth2/login"; //AccessToken이 아예 존재하지 않으면 로그인으로 이동

        if (StringUtils.hasText(refreshToken) && tokenProvider.validateToken(refreshToken)) { //RefreshToken은 일단 정상
            String socialId = tokenProvider.parseClaims(accessToken).getSubject();
            Member member = memberService.findBySocialId(socialId);

            if (compareWithDB(refreshToken, member)) { //DB의 RefreshToken과도 같으면
                JwtToken newToken = tokenProvider.renewToken(accessToken, refreshToken); //accessToken 재발급, refreshToken 3일 이하시 재발급
                accessToken = newToken.getAccessToken();
                refreshToken = newToken.getRefreshToken();
                log.info("newAccessToken = {}", accessToken);
                log.info("newRefreshToken = {}", refreshToken);

                //Header에 AccessToken 추가
                HeaderUtils.setAccessToken(response, accessToken);

                //Cookie에 RefreshToken 추가
                CookieUtils.deleteCookie(request, response, REFRESH_TOKEN);
                CookieUtils.addCookie(response, REFRESH_TOKEN, refreshToken, COOKIE_PERIOD);

                //DB의 RefreshToken 갱신
                memberService.updateJwtRefreshToken(socialId, refreshToken);

                //TODO: 이렇게 redirectAttribute를 쓰기 싫음
                redirectAttributes.addAttribute("returnUrl", returnUrl);
                return "redirect:/{returnUrl}";
            }
        }

        return "redirect:/oauth2/login";
    }

    private boolean compareWithDB(String refreshToken, Member member) {
        if (member == null) return false;
        return refreshToken.equals(member.getJwtRefreshToken());
    }

}
