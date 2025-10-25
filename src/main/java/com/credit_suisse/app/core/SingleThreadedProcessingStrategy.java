package com.credit_suisse.app.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

public class SingleThreadedProcessingStrategy implements ProcessingStrategy {
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    
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
            Map<String, java.util.ArrayList<Instrument>> groups = new HashMap<>();
            
            // Single-threaded sequential processing - one item at a time
            for (Instrument instrument : instruments) {
                String name = instrument.getName();
                if (!groups.containsKey(name)) {
                    groups.put(name, new java.util.ArrayList<>());
                }
                groups.get(name).add(instrument);
                
                // Simulate slower processing with small delay
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Processing interrupted", e);
                }
            }
            
            // Calculate results one by one (no parallelism)
            Map<String, Double> results = new TreeMap<>();
            for (Map.Entry<String, java.util.ArrayList<Instrument>> entry : groups.entrySet()) {
                String name = entry.getKey();
                List<Instrument> instrumentList = entry.getValue();
                
                double baseValue = calculator.calculateByType(name, instrumentList);
                double multiplier = multiplierService.getMultiplier(dao, name);
                results.put(name, baseValue * multiplier);
            }
            
            return results;
            
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during single-threaded processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Single-threaded processing failed: " + e.getMessage(), e);
        }
    }
}