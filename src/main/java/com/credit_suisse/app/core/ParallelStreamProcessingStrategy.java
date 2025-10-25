package com.credit_suisse.app.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

public class ParallelStreamProcessingStrategy implements ProcessingStrategy {
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    
    @Override
    public Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao) {
        try {
            List<Instrument> instruments = fileProcessor.readInstruments(inputPath);
            
            // Check memory before processing
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            
            if (usedMemory > maxMemory * 0.8) {
                throw new OutOfMemoryError("Insufficient memory for processing");
            }
            
            return instruments.stream()
                    .collect(Collectors.groupingBy(Instrument::getName))
                    .entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String name = entry.getKey();
                            double baseValue = calculator.calculateByType(name, entry.getValue());
                            double multiplier = multiplierService.getMultiplier(dao, name);
                            return baseValue * multiplier;
                        },
                        (v1, v2) -> v1,
                        TreeMap::new
                    ));
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Processing failed: " + e.getMessage(), e);
        }
    }
}