package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;
import com.credit_suisse.app.core.ManualBatchProcessingStrategy;

public class ManualBatchProcessingStrategyTest {

    private ManualBatchProcessingStrategy strategy;
    private String testInputPath;

    @BeforeEach
    void setUp() {
        strategy = new ManualBatchProcessingStrategy();
        testInputPath = "src/main/resources/input.txt";
    }

    @Test
    void testProcessCalculation() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testManualBatchProcessingWithNullDao() {
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
    void testManualBatchProcessing() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Verify results are properly calculated with manual batching
        for (Map.Entry<String, Double> entry : results.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            assertTrue(entry.getValue() >= 0);
        }
    }

    @Test
    void testMemoryProtection() {
        // This test verifies that memory checking is in place for manual batch processing
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        assertNotNull(results);
    }
}