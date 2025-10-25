package com.credit_suisse.app.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

@Component
public class KafkaStreamProcessingStrategy implements ProcessingStrategy {
    
    @Value("${processing.enable-kafka:false}")
    private boolean kafkaEnabled;
    
    @Value("${kafka.topic.instrument-data:instrument-data}")
    private String instrumentTopic;
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    private final Map<String, Double> results = new ConcurrentHashMap<>();
    
    @Override
    public Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao) {
        try {
            // Check memory before processing
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            
            if (usedMemory > maxMemory * 0.8) {
                throw new OutOfMemoryError("Insufficient memory for processing");
            }
            
            List<Instrument> instruments = fileProcessor.readInstruments(inputPath);
            results.clear();
            
            // Use embedded Kafka for processing simulation
            return processWithEmbeddedKafka(instruments, dao);
            
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during Kafka stream processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Kafka stream processing failed: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Double> processWithEmbeddedKafka(List<Instrument> instruments, InstrumentPriceModifierDao dao) {
        try {
            if (!kafkaEnabled) {
                return processInMemoryStream(instruments, dao);
            }
            
            // Simulate Kafka processing - in real implementation would use KafkaTemplate
            // For now, fallback to in-memory processing
            return processInMemoryStream(instruments, dao);
            
        } catch (Exception e) {
            // Fallback to in-memory stream processing
            return processInMemoryStream(instruments, dao);
        }
    }
    
    private Map<String, Double> processInMemoryStream(List<Instrument> instruments, InstrumentPriceModifierDao dao) {
        // Simulate Kafka stream processing with in-memory operations
        return instruments.parallelStream()
            .collect(Collectors.groupingBy(Instrument::getName))
            .entrySet()
            .parallelStream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    String name = entry.getKey();
                    List<Instrument> instrumentList = entry.getValue();
                    
                    // Simulate stream processing with micro-batches
                    double baseValue = instrumentList.stream()
                        .mapToDouble(i -> i.getPrice())
                        .reduce(0.0, (a, b) -> {
                            // Simulate Kafka stream aggregation
                            try { Thread.sleep(1); } catch (InterruptedException e) {}
                            return calculator.calculateByType(name, instrumentList);
                        });
                    
                    double multiplier = multiplierService.getMultiplier(dao, name);
                    return baseValue * multiplier;
                },
                (v1, v2) -> v1,
                TreeMap::new
            ));
    }
}