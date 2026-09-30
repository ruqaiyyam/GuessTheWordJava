package com.example.guessword.model;

import jakarta.persistence.*;

@Entity
@Table(name = "words")
public class Word {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false, length = 5) private String text;

    protected Word() {}
    public Word(String text) { this.text = text; }
    public Long getId() { return id; }
    public String getText() { return text; }
}
