package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Map;
import com.credit_suisse.app.core.CalculatorEngine;
import com.credit_suisse.app.core.ParallelStreamProcessingStrategy;
import com.credit_suisse.app.core.RxJavaProcessingStrategy;
import com.credit_suisse.app.core.SpringBatchProcessingStrategy;
import com.credit_suisse.app.core.ManualBatchProcessingStrategy;
import com.credit_suisse.app.core.KafkaStreamProcessingStrategy;

public class CalculatorEngineTest {

    @Test
    void testInstanceCreation() {
        CalculatorEngine engine1 = new CalculatorEngine("src/main/resources/input.txt");
        CalculatorEngine engine2 = new CalculatorEngine("src/main/resources/input.txt");
        
        assertNotSame(engine1, engine2);
    }

    @Test
    void testSetProcessingStrategy() {
        CalculatorEngine engine = new CalculatorEngine("src/main/resources/input.txt");
        
        engine.setProcessingStrategy(new ParallelStreamProcessingStrategy());
        Map<String, Double> traditionalResults = engine.calculate(null);
        
        engine.setProcessingStrategy(new RxJavaProcessingStrategy());
        Map<String, Double> rxJavaResults = engine.calculate(null);
        
        engine.setProcessingStrategy(new SpringBatchProcessingStrategy());
        Map<String, Double> batchResults = engine.calculate(null);
        
        engine.setProcessingStrategy(new ManualBatchProcessingStrategy());
        Map<String, Double> manualBatchResults = engine.calculate(null);
        
        engine.setProcessingStrategy(new KafkaStreamProcessingStrategy());
        Map<String, Double> kafkaResults = engine.calculate(null);
        
        assertNotNull(traditionalResults);
        assertNotNull(rxJavaResults);
        assertNotNull(batchResults);
        assertNotNull(manualBatchResults);
        assertNotNull(kafkaResults);
        assertEquals(traditionalResults.size(), rxJavaResults.size());
        assertEquals(traditionalResults.size(), batchResults.size());
        assertEquals(traditionalResults.size(), manualBatchResults.size());
        assertEquals(traditionalResults.size(), kafkaResults.size());
    }

    @Test
    void testCalculateWithNullDao() {
        CalculatorEngine engine = new CalculatorEngine("src/main/resources/input.txt");
        engine.setProcessingStrategy(new ParallelStreamProcessingStrategy());
        
        Map<String, Double> results = engine.calculate(null);
        
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }
}