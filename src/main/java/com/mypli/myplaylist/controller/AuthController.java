package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.domain.Member;
import com.mypli.myplaylist.oauth2.jwt.JwtTokenProvider;
import com.mypli.myplaylist.oauth2.userdetails.UserDetailsImpl;
import com.mypli.myplaylist.repository.MemberRepository;
import com.mypli.myplaylist.service.MemberService;
import com.mypli.myplaylist.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final static Long THREE_DAYS_MSEC = 259200000L;
    private final static String REFRESH_TOKEN = "refresh-token";

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/redirect")
    public String oauth2Redirect(@RequestParam("token") String jwtAccessToken,
                                 RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String socialId = authentication.getName();
        log.info("socialId = {}", socialId);
        Member member = memberService.findBySocialId(socialId);
        String socialAccessToken = member.getSocialAccessToken();

        /**
         * TODO: 여기서 accessToken 2개를 어떻게든 저장하고 redirectAttribute 없이 home으로 redirect 시키자
         */
        //이거는 테스트용
        redirectAttributes.addAttribute("socialAccessToken", socialAccessToken);
        redirectAttributes.addAttribute("jwtAccessToken", jwtAccessToken);
        return "redirect:/home?jwtAccessToken={jwtAccessToken}&socialAccessToken={socialAccessToken}";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {

        CookieUtils.deleteCookie(request, response, "refresh-token");
        return "redirect:/home";
    }
}
