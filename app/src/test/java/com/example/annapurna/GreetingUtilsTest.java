package com.example.annapurna;

import org.junit.Test;
import static org.junit.Assert.*;

public class GreetingUtilsTest {

    @Test
    public void testGetTimeBasedGreetingNotNull() {
        String greeting = GreetingUtils.getTimeBasedGreeting();
        assertNotNull(greeting);
        assertFalse(greeting.isEmpty());
    }

    @Test
    public void testGetRandomWelcomeLineNotNull() {
        String line = GreetingUtils.getRandomWelcomeLine();
        assertNotNull(line);
        assertFalse(line.isEmpty());
    }
}
