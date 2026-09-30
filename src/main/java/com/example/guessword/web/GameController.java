package com.example.guessword.web;

import com.example.guessword.model.*;
import com.example.guessword.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GameController {
    private final UserService users;
    private final GameService games;
    public GameController(UserService users, GameService games) { this.users = users; this.games = games; }

    private User current(HttpSession s) { return users.find((Long) s.getAttribute("userId")).orElseThrow(); }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession s, Model m) {
        User u = current(s);
        var today = games.todaysGames(u);
        m.addAttribute("username", u.getUsername());
        m.addAttribute("todays", today);
        m.addAttribute("remaining", Math.max(0, GameService.MAX_WORDS_PER_DAY - today.size()));
        m.addAttribute("hasOpen", today.stream().anyMatch(g -> g.getStatus() == GameStatus.IN_PROGRESS));
        return "dashboard";
    }

    @PostMapping("/game/start")
    public String start(HttpSession s, RedirectAttributes ra) {
        try { return "redirect:/game/" + games.startOrResume(current(s)).getId(); }
        catch (BusinessException e) { ra.addFlashAttribute("error", e.getMessage()); return "redirect:/dashboard"; }
    }

    @GetMapping("/game/{id}")
    public String show(@PathVariable Long id, HttpSession s, Model m, RedirectAttributes ra) {
        try {
            Game g = games.getOwned(current(s), id);
            m.addAttribute("game", g);
            m.addAttribute("rows", games.board(g));
            m.addAttribute("over", g.getStatus() != GameStatus.IN_PROGRESS);
            m.addAttribute("attemptsLeft", GameService.MAX_GUESSES - g.getGuesses().size());
            return "game";
        } catch (BusinessException e) { ra.addFlashAttribute("error", e.getMessage()); return "redirect:/dashboard"; }
    }

    @PostMapping("/game/{id}/guess")
    public String guess(@PathVariable Long id, @RequestParam String guess, HttpSession s, RedirectAttributes ra) {
        try {
            Game g = games.submitGuess(current(s), id, guess == null ? "" : guess.trim());
            if (g.getStatus() == GameStatus.WON) ra.addFlashAttribute("finishMessage", "Congratulations! You guessed the word " + g.getWord() + "!");
            else if (g.getStatus() == GameStatus.LOST) ra.addFlashAttribute("finishMessage", "Better luck next time! The word was " + g.getWord() + ".");
        } catch (BusinessException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/game/" + id;
    }
}
