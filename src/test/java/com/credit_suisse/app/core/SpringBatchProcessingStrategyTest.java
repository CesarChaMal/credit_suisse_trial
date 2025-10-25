package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;
import com.credit_suisse.app.core.SpringBatchProcessingStrategy;

public class SpringBatchProcessingStrategyTest {

    private SpringBatchProcessingStrategy strategy;
    private String testInputPath;

    @BeforeEach
    void setUp() {
        strategy = new SpringBatchProcessingStrategy();
        testInputPath = "src/main/resources/input.txt";
    }

    @Test
    void testProcessCalculation() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testBatchProcessingWithNullDao() {
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
}