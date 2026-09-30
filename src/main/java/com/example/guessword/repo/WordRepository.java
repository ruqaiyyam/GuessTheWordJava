package com.example.guessword.repo;

import com.example.guessword.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {}
