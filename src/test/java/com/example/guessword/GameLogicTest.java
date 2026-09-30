package com.example.guessword;

import com.example.guessword.service.GameService;
import com.example.guessword.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameLogicTest {
    @Test void allCorrect() { assertEquals("GGGGG", GameService.evaluate("APPLE", "APPLE")); }
    @Test void noneCorrect() { assertEquals("XXXXX", GameService.evaluate("APPLE", "TIGHT")); }
    @Test void wrongPosition() { assertEquals("OOOOO", GameService.evaluate("ABCDE", "BCDEA")); }
    @Test void mixedGreenOrangeGrey() { assertEquals("OOGOX", GameService.evaluate("APPLE", "PAPER")); }
    @Test void repeatedGuessLetterOnlyMarkedAsOftenAsInTarget() { assertEquals("XXOXG", GameService.evaluate("CRANE", "EERIE")); }

    @Test void usernameRules() {
        assertNull(UserService.validate("Alice", "ab1$x"));
        assertNotNull(UserService.validate("Bob", "ab1$x"));
        assertNotNull(UserService.validate("Alice1", "ab1$x"));
    }
    @Test void passwordRules() {
        assertNotNull(UserService.validate("Alice", "ab1$"));
        assertNotNull(UserService.validate("Alice", "abcd$"));
        assertNotNull(UserService.validate("Alice", "abcd1"));
        assertNotNull(UserService.validate("Alice", "12345$"));
        assertNull(UserService.validate("Alice", "ab1%x"));
        assertNull(UserService.validate("Alice", "ab1*x"));
    }
}
