package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Map;
import com.credit_suisse.app.core.ParallelStreamProcessingStrategy;
import com.credit_suisse.app.core.RxJavaProcessingStrategy;
import com.credit_suisse.app.core.SpringBatchProcessingStrategy;
import com.credit_suisse.app.core.ManualBatchProcessingStrategy;
import com.credit_suisse.app.core.KafkaStreamProcessingStrategy;
import com.credit_suisse.app.core.ModernJavaProcessingStrategy;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;

public class ProcessingStrategyTest {

    private String testInputPath;
    private InstrumentPriceModifierDao mockDao;

    @BeforeEach
    void setUp() {
        testInputPath = "src/main/resources/input.txt";
        mockDao = mock(InstrumentPriceModifierDao.class);
    }

    @Test
    void testTraditionalProcessingStrategy() {
        ParallelStreamProcessingStrategy strategy = new ParallelStreamProcessingStrategy();
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testRxJavaProcessingStrategy() {
        RxJavaProcessingStrategy strategy = new RxJavaProcessingStrategy();
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testBatchProcessingStrategy() {
        SpringBatchProcessingStrategy strategy = new SpringBatchProcessingStrategy();
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testManualBatchProcessingStrategy() {
        ManualBatchProcessingStrategy strategy = new ManualBatchProcessingStrategy();
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testKafkaStreamProcessingStrategy() {
        KafkaStreamProcessingStrategy strategy = new KafkaStreamProcessingStrategy();
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testModernJavaProcessingStrategy() {
        ModernJavaProcessingStrategy strategy = new ModernJavaProcessingStrategy();
        Map<String, Double> results = strategy.processCalculation(testInputPath, null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    void testAllStrategiesProduceValidResults() {
        ParallelStreamProcessingStrategy traditional = new ParallelStreamProcessingStrategy();
        RxJavaProcessingStrategy rxJava = new RxJavaProcessingStrategy();
        ManualBatchProcessingStrategy manualBatch = new ManualBatchProcessingStrategy();
        KafkaStreamProcessingStrategy kafkaStream = new KafkaStreamProcessingStrategy();
        ModernJavaProcessingStrategy modernJava = new ModernJavaProcessingStrategy();
        
        Map<String, Double> traditionalResults = traditional.processCalculation(testInputPath, null);
        Map<String, Double> rxJavaResults = rxJava.processCalculation(testInputPath, null);
        Map<String, Double> manualBatchResults = manualBatch.processCalculation(testInputPath, null);
        Map<String, Double> kafkaResults = kafkaStream.processCalculation(testInputPath, null);
        Map<String, Double> modernJavaResults = modernJava.processCalculation(testInputPath, null);
        
        // Verify all strategies produce valid results
        assertNotNull(traditionalResults);
        assertNotNull(rxJavaResults);
        assertNotNull(manualBatchResults);
        assertNotNull(kafkaResults);
        assertNotNull(modernJavaResults);
        
        assertTrue(traditionalResults.size() > 0);
        assertTrue(rxJavaResults.size() > 0);
        assertTrue(manualBatchResults.size() > 0);
        assertTrue(kafkaResults.size() > 0);
        assertTrue(modernJavaResults.size() > 0);
    }
}