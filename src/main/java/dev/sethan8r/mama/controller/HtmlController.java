package dev.sethan8r.mama.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HtmlController {

    @GetMapping("/404")
    public String handle404() {
        return "error/404";
    }
}
