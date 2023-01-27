package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.utils.CookieUtils;
import com.mypli.myplaylist.service.MemberService;
import com.mypli.myplaylist.utils.HeaderUtils;
import com.mypli.myplaylist.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/home")
    public String home(Model model,
                       HttpServletRequest request) {

/*        String socialId = SecurityUtils.getCurrentMemberId();
        log.info("home socialId = {}", socialId);
        if (socialId.equals("anonymousUser")) {
            log.info("need refresh");
            String returnUrl = "home";
            redirectAttributes.addAttribute("returnUrl", returnUrl);
            return "redirect:/oauth2/refresh?returnUrl={returnUrl}";
        }*/

        String jwtAccessToken = "";
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if(!flashMap.isEmpty()) jwtAccessToken = (String) flashMap.get("token");

        String refreshToken = CookieUtils.getCookie(request, "refresh-token")
                        .map(cookie -> cookie.getValue())
                        .orElse(null);

        String socialAccessToken = CookieUtils.getCookie(request, "social-access-token")
                        .map(cookie -> cookie.getValue())
                        .orElse(null);

        model.addAttribute("socialAccessToken", socialAccessToken);
        model.addAttribute("jwtAccessToken", jwtAccessToken);
        model.addAttribute("refreshToken", refreshToken);

        return "home";
    }

    @GetMapping("test")
    public String test() {
        return "test";
    }

}
