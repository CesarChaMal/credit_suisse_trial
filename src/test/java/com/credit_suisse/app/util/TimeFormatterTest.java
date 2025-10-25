package com.credit_suisse.app.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import com.credit_suisse.app.util.TimeFormatter;

public class TimeFormatterTest {

    @Test
    void testFormatTimeMilliseconds() {
        assertEquals("500 ms", TimeFormatter.formatTime(500));
        assertEquals("999 ms", TimeFormatter.formatTime(999));
    }

    @Test
    void testFormatTimeSeconds() {
        assertEquals("1.00 seconds", TimeFormatter.formatTime(1000));
        assertEquals("2.50 seconds", TimeFormatter.formatTime(2500));
        assertEquals("59.99 seconds", TimeFormatter.formatTime(59990));
    }

    @Test
    void testFormatTimeMinutes() {
        assertEquals("1 min 0.00 sec", TimeFormatter.formatTime(60000));
        assertEquals("2 min 30.50 sec", TimeFormatter.formatTime(150500));
    }

    @Test
    void testFormatComparisonEqual() {
        String result = TimeFormatter.formatComparison(1000, 1000, "A", "B");
        assertEquals("Both strategies performed equally", result);
    }

    @Test
    void testFormatComparisonFirstFaster() {
        String result = TimeFormatter.formatComparison(500, 1000, "Traditional", "RxJava");
        assertEquals("Traditional is 500 ms faster (100.0% improvement)", result);
    }

    @Test
    void testFormatComparisonSecondFaster() {
        String result = TimeFormatter.formatComparison(2000, 1000, "Traditional", "RxJava");
        assertEquals("RxJava is 1.00 seconds faster (100.0% improvement)", result);
    }
}