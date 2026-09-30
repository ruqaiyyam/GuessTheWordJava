package com.example.guessword.web;

import com.example.guessword.model.*;
import com.example.guessword.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService users;
    public AuthController(UserService users) { this.users = users; }

    @GetMapping("/") public String root() { return "redirect:/login"; }

    @GetMapping("/login")
    public String loginForm(@RequestParam(required = false) String registered, Model m) {
        if (registered != null) m.addAttribute("info", "Registration successful. Please log in.");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model m) {
        var user = users.authenticate(username, password);
        if (user.isEmpty()) { m.addAttribute("error", "Invalid username or password."); return "login"; }
        session.setAttribute("userId", user.get().getId());
        session.setAttribute("username", user.get().getUsername());
        session.setAttribute("role", user.get().getRole().name());
        return user.get().getRole() == Role.ADMIN ? "redirect:/admin" : "redirect:/dashboard";
    }

    @GetMapping("/register") public String registerForm() { return "register"; }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, Model m) {
        try { users.register(username, password); return "redirect:/login?registered"; }
        catch (BusinessException e) { m.addAttribute("error", e.getMessage()); return "register"; }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) { session.invalidate(); return "redirect:/login"; }
}
