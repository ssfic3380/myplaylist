package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.utils.CookieUtils;
import com.mypli.myplaylist.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/home")
    public String home(Model model,
                       @RequestParam(value = "jwtAccessToken", required = false) String jwtAccessToken,
                       @RequestParam(value = "socialAccessToken", required = false) String socialAccessToken,
                       HttpServletRequest request) {

        String refreshToken = CookieUtils.getCookie(request, "refresh-token")
                        .map(cookie -> cookie.getValue())
                        .orElse(null);


        model.addAttribute("socialAccessToken", socialAccessToken);
        model.addAttribute("jwtAccessToken", jwtAccessToken);
        model.addAttribute("refreshToken", refreshToken);

        return "home";
    }

}
