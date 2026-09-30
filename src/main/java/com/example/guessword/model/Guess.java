package com.example.guessword.model;

import jakarta.persistence.*;

@Entity
@Table(name = "guesses")
public class Guess {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Game game;
    @Column(nullable = false) private int guessNumber;
    @Column(nullable = false, length = 5) private String guessText;

    protected Guess() {}
    public Guess(Game game, int guessNumber, String guessText) { this.game = game; this.guessNumber = guessNumber; this.guessText = guessText; }
    public Long getId() { return id; }
    public Game getGame() { return game; }
    public int getGuessNumber() { return guessNumber; }
    public String getGuessText() { return guessText; }
}
