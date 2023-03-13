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

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/redirect")
    public String oauth2Redirect(@RequestParam String token, RedirectAttributes redirectAttributes) {

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
}
