package com.example.guessword.service;

import com.example.guessword.model.*;
import com.example.guessword.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z]{5,}$");
    private static final String SPECIALS = "$%*)";

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, PasswordEncoder encoder) { this.users = users; this.encoder = encoder; }

    /** Returns an error message, or null when both values are valid. */
    public static String validate(String username, String password) {
        if (username == null || !USERNAME.matcher(username).matches())
            return "Username must have at least 5 letters (upper/lower case letters only).";
        if (password == null || password.length() < 5)
            return "Password must be at least 5 characters.";
        boolean alpha = password.chars().anyMatch(Character::isLetter);
        boolean digit = password.chars().anyMatch(Character::isDigit);
        boolean special = password.chars().anyMatch(c -> SPECIALS.indexOf(c) >= 0);
        if (!(alpha && digit && special))
            return "Password must contain a letter, a digit and one of the special characters $ % * )";
        return null;
    }

    public User register(String username, String password) {
        String error = validate(username, password);
        if (error != null) throw new BusinessException(error);
        if (users.findByUsername(username).isPresent()) throw new BusinessException("Username already taken.");
        return users.save(new User(username, encoder.encode(password), Role.PLAYER));
    }

    public Optional<User> authenticate(String username, String password) {
        return users.findByUsername(username).filter(u -> encoder.matches(password, u.getPasswordHash()));
    }

    public Optional<User> find(Long id) { return users.findById(id); }
    public Optional<User> findByUsername(String name) { return users.findByUsername(name); }
}
