package com.mypli.myplaylist.controller;

import com.mypli.myplaylist.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/")
    public String home(Model model) {
        String i = "something changed";
        return "home";
    }
}
