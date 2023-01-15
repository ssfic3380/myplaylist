package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.utils.CookieUtils;
import com.mypli.myplaylist.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/social")
    public String socialSuccess(Model model,
                                @RequestParam(value = "provider", required = false) String provider,
                                @RequestParam(value = "oauthId", required = false) String oauthId,
                                HttpServletResponse response) {

        model.addAttribute("provider", provider);
        model.addAttribute("oauthId", oauthId);

        String acct = response.getHeader("Authorization").substring("Bearer ".length());
        String reft = response.getHeader("refresh-token");

        log.info("acct = {}", acct);
        log.info("reft = {}", reft);

        return "social-success";
    }

    @GetMapping("/auth/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "refresh-token");

        return "redirect:social-success";
    }

}
