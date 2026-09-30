package com.example.guessword;

import com.example.guessword.model.*;
import com.example.guessword.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class GuessWordApplication {
    public static void main(String[] args) { SpringApplication.run(GuessWordApplication.class, args); }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    /** Seeds 20 words and a default admin account on first start. */
    @Bean
    CommandLineRunner seed(WordRepository words, UserRepository users, PasswordEncoder enc) {
        return args -> {
            if (words.count() == 0) {
                List.of("APPLE","BRAVE","CRANE","DREAM","EAGLE","FLAME","GRAPE","HOUSE","IMAGE","JOLLY",
                        "KNIFE","LEMON","MANGO","NIGHT","OCEAN","PLANT","QUEEN","RIVER","STONE","TIGER")
                    .forEach(w -> words.save(new Word(w)));
            }
            if (users.findByUsername("admin").isEmpty()) {
                users.save(new User("admin", enc.encode("Admin$123"), Role.ADMIN));
            }
        };
    }
}
