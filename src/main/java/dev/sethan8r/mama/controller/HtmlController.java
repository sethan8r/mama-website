package dev.sethan8r.mama.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/admin/categories")
    public String adminCategories() {
        return "admin/admin-categories";
    }

    @GetMapping("/admin/works")
    public String adminWorks() {
        return "admin/admin-works";
    }

    @GetMapping("/admin/products")
    public String adminProducts() {
        return "admin/admin-products";
    }

    @GetMapping("/admin/products/{slug}")
    public String adminProductEdit() {
        return "admin/admin-product-edit";
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
