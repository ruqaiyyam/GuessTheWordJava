# Guess the Word (Java / Spring Boot)

Wordle-style game with Player and Admin roles.

**Stack:** Java 17, Spring Boot 3, Spring Data JPA, Thymeleaf, H2 (file database in `./data`).

## Run
```
mvn spring-boot:run      # then open http://localhost:8080
mvn test
```

## Accounts
- Admin (seeded): `admin` / `Admin$123` -> daily report and per-user report at `/admin`
- Players: register at `/register`

## Rules implemented
- Username: 5+ letters. Password: 5+ chars with a letter, a digit and one of `$ % * )`
- 20 five-letter uppercase words seeded on first start
- Random word per game, max 3 words per user per day (an unfinished game is resumed)
- 5 guesses per word; green = right place, orange = wrong place, grey = not in word
- Win / lose popup, OK returns to dashboard
- Words given, guesses and date stored in `games` and `guesses` tables
