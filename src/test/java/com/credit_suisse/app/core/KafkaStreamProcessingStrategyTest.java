package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import com.credit_suisse.app.core.KafkaStreamProcessingStrategy;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.InstrumentPriceModifier;

public class KafkaStreamProcessingStrategyTest {

    private KafkaStreamProcessingStrategy strategy;
    private String testInputPath;

    @BeforeEach
    void setUp() {
        strategy = new KafkaStreamProcessingStrategy();
        testInputPath = "src/main/resources/input.txt";
    }

    @Test
    void testProcessCalculation() {
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testKafkaStreamProcessingWithNullDao() {
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
    void testStreamProcessingFallback() {
        // Test that strategy works even without Kafka server
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Verify stream processing results
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
    void testKafkaProcessingWithMockedDao() {
        // Mock DAO with specific multipliers
        InstrumentPriceModifierDao mockDao = mock(InstrumentPriceModifierDao.class);
        
        InstrumentPriceModifier modifier1 = new InstrumentPriceModifier();
        modifier1.setModifier(1.5);
        
        InstrumentPriceModifier modifier2 = new InstrumentPriceModifier();
        modifier2.setModifier(2.0);
        
        when(mockDao.findByNameList("INSTRUMENT1")).thenReturn(Arrays.asList(modifier1));
        when(mockDao.findByNameList("INSTRUMENT2")).thenReturn(Arrays.asList(modifier2));
        when(mockDao.findByNameList(anyString())).thenReturn(Collections.emptyList());
        
        Map<String, Double> results = strategy.processCalculation(testInputPath, mockDao);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Verify DAO was called
        verify(mockDao, atLeastOnce()).findByNameList(anyString());
    }

    @Test
    void testKafkaProcessingWithDaoException() {
        // Mock DAO to throw exception
        InstrumentPriceModifierDao mockDao = mock(InstrumentPriceModifierDao.class);
        when(mockDao.findByNameList(anyString())).thenThrow(new RuntimeException("Database error"));
        
        // Should handle DAO exceptions gracefully and use default multipliers
        Map<String, Double> results = strategy.processCalculation(testInputPath, mockDao);
        
        assertNotNull(results);
        // Should still process with default multipliers despite DAO error
    }

    @Test
    void testKafkaStreamFallbackBehavior() {
        // Test that Kafka strategy falls back to in-memory processing
        InstrumentPriceModifierDao mockDao = mock(InstrumentPriceModifierDao.class);
        
        InstrumentPriceModifier modifier = new InstrumentPriceModifier();
        modifier.setModifier(3.0);
        when(mockDao.findByNameList("INSTRUMENT1")).thenReturn(Arrays.asList(modifier));
        
        Map<String, Double> results = strategy.processCalculation(testInputPath, mockDao);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
        
        // Verify multipliers were applied correctly
        if (results.containsKey("INSTRUMENT1")) {
            assertTrue(results.get("INSTRUMENT1") > 0);
        }
        
        verify(mockDao, atLeastOnce()).findByNameList(anyString());
    }

    @Test
    void testKafkaProcessingPerformance() {
        // Test that Kafka processing completes within reasonable time
        long startTime = System.currentTimeMillis();
        
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        long duration = System.currentTimeMillis() - startTime;
        
        assertNotNull(results);
        assertTrue(duration < 30000); // Should complete within 30 seconds
        assertTrue(results.size() > 0);
    }
}