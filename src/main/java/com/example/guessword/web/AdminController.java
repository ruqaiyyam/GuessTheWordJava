package com.example.guessword.web;

import com.example.guessword.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class AdminController {
    private final UserService users;
    private final GameService games;
    public AdminController(UserService users, GameService games) { this.users = users; this.games = games; }

    @GetMapping("/admin")
    public String admin(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        @RequestParam(required = false) String username, Model m) {
        LocalDate d = date != null ? date : LocalDate.now();
        m.addAttribute("date", d);
        m.addAttribute("daily", games.dailyReport(d));
        if (username != null && !username.isBlank()) {
            m.addAttribute("searched", username);
            users.findByUsername(username.trim()).ifPresentOrElse(
                u -> m.addAttribute("userReport", games.userReport(u)),
                () -> m.addAttribute("userError", "No such user."));
        }
        return "admin";
    }
}
