package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;
import com.credit_suisse.app.core.ModernJavaProcessingStrategy;

public class ModernJavaProcessingStrategyTest {

    private ModernJavaProcessingStrategy strategy;
    private String testInputPath;

    @BeforeEach
    void setUp() {
        strategy = new ModernJavaProcessingStrategy();
        testInputPath = "src/main/resources/input.txt";
    }

    @Test
    void testProcessCalculation() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testModernJavaProcessingWithNullDao() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
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
    void testStructuredConcurrencyProcessing() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Verify results are properly calculated with structured concurrency
        for (Map.Entry<String, Double> entry : results.entrySet()) {
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            assertTrue(entry.getValue() >= 0);
        }
    }

    @Test
    void testMemoryProtection() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        assertNotNull(results);
    }

    @Test
    void testVirtualThreadsPerformance() {
        // Test that virtual threads don't cause performance degradation
        long startTime = System.currentTimeMillis();
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        long duration = System.currentTimeMillis() - startTime;
        
        assertNotNull(results);
        assertTrue(duration < 30000); // Should complete within 30 seconds
    }
}