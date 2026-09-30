package com.example.guessword.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** One word given to a user on a given day, with all guesses made for it. */
@Entity
@Table(name = "games")
public class Game {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private User user;
    @Column(nullable = false, length = 5) private String word;
    @Column(nullable = false) private LocalDate gameDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private GameStatus status = GameStatus.IN_PROGRESS;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("guessNumber ASC")
    private List<Guess> guesses = new ArrayList<>();

    protected Game() {}
    public Game(User user, String word, LocalDate gameDate) { this.user = user; this.word = word; this.gameDate = gameDate; }
    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getWord() { return word; }
    public LocalDate getGameDate() { return gameDate; }
    public GameStatus getStatus() { return status; }
    public void setStatus(GameStatus status) { this.status = status; }
    public List<Guess> getGuesses() { return guesses; }
}
