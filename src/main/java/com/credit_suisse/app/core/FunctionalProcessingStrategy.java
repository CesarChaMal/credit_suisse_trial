package com.credit_suisse.app.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

public class FunctionalProcessingStrategy implements ProcessingStrategy {
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    
    @Override
    public Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao) {
        try {
            checkMemory();
            
            // Pure functional pipeline with method chaining
            return fileProcessor.readInstruments(inputPath)
                .stream()
                .collect(Collectors.groupingBy(Instrument::getName))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> processInstrumentGroup(entry.getKey(), entry.getValue(), dao),
                    (v1, v2) -> v1,
                    TreeMap::new
                ));
                
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during functional processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Functional processing failed: " + e.getMessage(), e);
        }
    }
    
    // Pure function for memory checking
    private void checkMemory() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        
        if (usedMemory > maxMemory * 0.8) {
            throw new OutOfMemoryError("Insufficient memory for processing");
        }
    }
    
    // Pure function for processing instrument groups
    private Double processInstrumentGroup(String name, List<Instrument> instruments, InstrumentPriceModifierDao dao) {
        return calculateBaseValue(name, instruments)
            .andThen(applyMultiplier(name, dao))
            .apply(instruments);
    }
    
    // Higher-order function for base value calculation
    private Function<List<Instrument>, Double> calculateBaseValue(String name, List<Instrument> instruments) {
        return instrumentList -> calculator.calculateByType(name, instrumentList);
    }
    
    // Higher-order function for multiplier application
    private Function<Double, Double> applyMultiplier(String name, InstrumentPriceModifierDao dao) {
        return baseValue -> baseValue * multiplierService.getMultiplier(dao, name);
    }
}