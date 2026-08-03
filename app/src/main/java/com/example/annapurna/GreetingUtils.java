package com.example.annapurna;

import java.util.Calendar;
import java.util.Random;

public class GreetingUtils {

    private static final String[] WELCOME_LINES = {
            "What's cooking today? 🍲",
            "Let's make something special. ✨",
            "Ready for another family recipe? 📖",
            "A recipe is a memory you can taste. ❤️",
            "Every dish has a story. 🥘"
    };

    public static String getTimeBasedGreeting() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Good Morning! 🌅";
        } else if (hour >= 12 && hour < 17) {
            return "Good Afternoon! ☀️";
        } else if (hour >= 17 && hour < 21) {
            return "Good Evening! 🌆";
        } else {
            return "Late Night Cooking? 🌙";
        }
    }

    public static String getRandomWelcomeLine() {
        int index = new Random().nextInt(WELCOME_LINES.length);
        return WELCOME_LINES[index];
    }
}