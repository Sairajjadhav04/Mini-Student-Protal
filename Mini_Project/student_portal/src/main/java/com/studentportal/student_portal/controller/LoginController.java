package com.studentportal.student_portal.controller;

import com.studentportal.student_portal.model.User;
import com.studentportal.student_portal.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final UserRepository users;

    public LoginController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Authentication authentication) {
        User user = users.findByUsername(authentication.getName()).orElseThrow();

        return switch (user.getRole()) {
            case STUDENT -> "redirect:/student/dashboard";
            case FACULTY -> "redirect:/faculty/dashboard";
            case ADMIN -> "redirect:/admin/dashboard";
        };
    }
}
