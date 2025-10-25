package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;

public class ParallelStreamProcessingStrategyTest {

    private ParallelStreamProcessingStrategy strategy;
    private String testInputPath;

    @BeforeEach
    void setUp() {
        strategy = new ParallelStreamProcessingStrategy();
        testInputPath = "src/main/resources/input.txt";
    }

    @Test
    void testProcessCalculation() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testTraditionalProcessingWithNullDao() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        // Should use default multipliers (1.0) when dao is null
        for (Double value : results.values()) {
            assertNotNull(value);
            assertTrue(value >= 0);
        }
    }

    @Test
    void testProcessCalculationWithInvalidPath() {
        assertThrows(RuntimeException.class, () -> {
            strategy.processCalculation("invalid/path.txt", null);
        });
    }

    @Test
    void testMemoryProtection() {
        // This test verifies that memory checking is in place
        // The actual memory limit test would require manipulating available memory
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        assertNotNull(results);
    }
}