package com.skillbox.cryptobot.bot.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegexCommandCheckerTest {
    RegexCommandChecker regexCommandChecker = new RegexCommandChecker();
    @Test
    void subscribeMEthodChecker() {
        assertTrue(regexCommandChecker.subscribeMethodChecker("/subscribe 10.5"));
        assertTrue(regexCommandChecker.subscribeMethodChecker("/subscribe 10"));
        assertFalse(regexCommandChecker.subscribeMethodChecker("/subscribe 10.5.5"));
        assertFalse(regexCommandChecker.subscribeMethodChecker("/subscribe 10 "));
        assertFalse(regexCommandChecker.subscribeMethodChecker("/subscribe10"));
        assertFalse(regexCommandChecker.subscribeMethodChecker("/subscribe"));
        assertFalse(regexCommandChecker.subscribeMethodChecker("/subscribe а"));
        assertFalse(regexCommandChecker.subscribeMethodChecker("/subscribe 1,0"));
        assertTrue(regexCommandChecker.subscribeMethodChecker("/subscribe 200000.321531"));
    }
}