package com.credit_suisse.app.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

public class ManualBatchProcessingStrategy implements ProcessingStrategy {
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    private static final int BATCH_SIZE = 50;
    
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
            Map<String, List<Instrument>> instrumentGroups = new HashMap<>();
            
            // Manual batch processing with explicit loops
            for (int batchStart = 0; batchStart < instruments.size(); batchStart += BATCH_SIZE) {
                int batchEnd = Math.min(batchStart + BATCH_SIZE, instruments.size());
                
                // Process each item in batch manually
                for (int i = batchStart; i < batchEnd; i++) {
                    Instrument instrument = instruments.get(i);
                    String name = instrument.getName();
                    
                    if (!instrumentGroups.containsKey(name)) {
                        instrumentGroups.put(name, new ArrayList<>());
                    }
                    instrumentGroups.get(name).add(instrument);
                }
                
                // Force garbage collection after each batch
                System.gc();
            }
            
            // Calculate results manually
            Map<String, Double> results = new TreeMap<>();
            for (Map.Entry<String, List<Instrument>> entry : instrumentGroups.entrySet()) {
                String name = entry.getKey();
                List<Instrument> instrumentList = entry.getValue();
                
                double baseValue = calculator.calculateByType(name, instrumentList);
                double multiplier = multiplierService.getMultiplier(dao, name);
                results.put(name, baseValue * multiplier);
            }
            
            return results;
            
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during manual batch processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Manual batch processing failed: " + e.getMessage(), e);
        }
    }
}