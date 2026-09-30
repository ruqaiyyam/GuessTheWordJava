package com.example.guessword.repo;

import com.example.guessword.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByUserAndGameDate(User user, LocalDate date);
    Optional<Game> findFirstByUserAndGameDateAndStatus(User user, LocalDate date, GameStatus status);
    List<Game> findByGameDate(LocalDate date);
    List<Game> findByUserOrderByGameDateDesc(User user);
}
