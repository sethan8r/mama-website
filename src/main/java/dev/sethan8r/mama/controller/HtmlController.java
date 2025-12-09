package dev.sethan8r.mama.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HtmlController {

    @GetMapping("/")
    public String index() {
        return "pages/home";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/admin")
    public String adminHome() {
        return "admin/admin-home";
    }

    @GetMapping("/404")
    public String handle404() {
        return "error/404";
    }

    @GetMapping("/403")
    public String handle403() {
        return "error/403";
    }
}
