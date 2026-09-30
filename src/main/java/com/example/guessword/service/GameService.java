package com.example.guessword.service;

import com.example.guessword.model.*;
import com.example.guessword.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
    public static final int WORD_LENGTH = 5, MAX_GUESSES = 5, MAX_WORDS_PER_DAY = 3;

    public record Tile(String letter, String css) {}
    public record DailyReport(LocalDate date, long users, long correctGuesses) {}
    public record UserDayReport(LocalDate date, long wordsTried, long correctGuesses) {}

    private final GameRepository games;
    private final WordRepository words;
    private final Random random = new SecureRandom();

    public GameService(GameRepository games, WordRepository words) { this.games = games; this.words = words; }

    /** G = right place, O = wrong place, X = not in word. Handles repeated letters correctly. */
    public static String evaluate(String target, String guess) {
        char[] result = new char[WORD_LENGTH];
        int[] remaining = new int[26];
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == target.charAt(i)) result[i] = 'G';
            else { result[i] = 'X'; remaining[target.charAt(i) - 'A']++; }
        }
        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result[i] != 'G' && remaining[guess.charAt(i) - 'A'] > 0) {
                result[i] = 'O';
                remaining[guess.charAt(i) - 'A']--;
            }
        }
        return new String(result);
    }

    /** Resumes today's unfinished game, or starts a new one if the daily limit allows. */
    @Transactional
    public Game startOrResume(User user) {
        LocalDate today = LocalDate.now();
        Optional<Game> open = games.findFirstByUserAndGameDateAndStatus(user, today, GameStatus.IN_PROGRESS);
        if (open.isPresent()) return open.get();
        List<Game> todays = games.findByUserAndGameDate(user, today);
        if (todays.size() >= MAX_WORDS_PER_DAY)
            throw new BusinessException("You have already played " + MAX_WORDS_PER_DAY + " words today. Come back tomorrow!");
        Set<String> used = todays.stream().map(Game::getWord).collect(Collectors.toSet());
        List<String> pool = words.findAll().stream().map(Word::getText).filter(w -> !used.contains(w)).toList();
        if (pool.isEmpty()) throw new BusinessException("No words available.");
        return games.save(new Game(user, pool.get(random.nextInt(pool.size())), today));
    }

    @Transactional(readOnly = true)
    public Game getOwned(User user, Long gameId) {
        Game g = games.findById(gameId).orElseThrow(() -> new BusinessException("Game not found."));
        if (!g.getUser().getId().equals(user.getId())) throw new BusinessException("Game not found.");
        return g;
    }

    @Transactional
    public Game submitGuess(User user, Long gameId, String guess) {
        Game g = getOwned(user, gameId);
        if (g.getStatus() != GameStatus.IN_PROGRESS) throw new BusinessException("This game is already over.");
        if (guess == null || !guess.matches("[A-Z]{5}")) throw new BusinessException("Enter a 5-letter word in UPPER CASE letters only.");
        g.getGuesses().add(new Guess(g, g.getGuesses().size() + 1, guess));
        if (guess.equals(g.getWord())) g.setStatus(GameStatus.WON);
        else if (g.getGuesses().size() >= MAX_GUESSES) g.setStatus(GameStatus.LOST);
        return games.save(g);
    }

    /** Board of MAX_GUESSES rows: earlier guesses in order (coloured) then empty rows. */
    @Transactional(readOnly = true)
    public List<List<Tile>> board(Game game) {
        List<List<Tile>> rows = new ArrayList<>();
        for (int r = 0; r < MAX_GUESSES; r++) {
            List<Tile> row = new ArrayList<>();
            if (r < game.getGuesses().size()) {
                String guess = game.getGuesses().get(r).getGuessText();
                String eval = evaluate(game.getWord(), guess);
                for (int i = 0; i < WORD_LENGTH; i++) {
                    char e = eval.charAt(i);
                    row.add(new Tile(String.valueOf(guess.charAt(i)), e == 'G' ? "green" : e == 'O' ? "orange" : "grey"));
                }
            } else {
                for (int i = 0; i < WORD_LENGTH; i++) row.add(new Tile("", "empty"));
            }
            rows.add(row);
        }
        return rows;
    }

    @Transactional(readOnly = true)
    public List<Game> todaysGames(User user) { return games.findByUserAndGameDate(user, LocalDate.now()); }

    @Transactional(readOnly = true)
    public DailyReport dailyReport(LocalDate date) {
        List<Game> day = games.findByGameDate(date);
        long users = day.stream().map(g -> g.getUser().getId()).distinct().count();
        long correct = day.stream().filter(g -> g.getStatus() == GameStatus.WON).count();
        return new DailyReport(date, users, correct);
    }

    @Transactional(readOnly = true)
    public List<UserDayReport> userReport(User user) {
        Map<LocalDate, List<Game>> byDate = games.findByUserOrderByGameDateDesc(user).stream()
            .collect(Collectors.groupingBy(Game::getGameDate, TreeMap::new, Collectors.toList()));
        List<UserDayReport> out = new ArrayList<>();
        byDate.forEach((d, list) -> out.add(new UserDayReport(d, list.size(),
            list.stream().filter(g -> g.getStatus() == GameStatus.WON).count())));
        Collections.reverse(out);
        return out;
    }
}
