package com.credit_suisse.app.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

public class ModernJavaProcessingStrategy implements ProcessingStrategy {
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    
    @Override
    public Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao) {
        try {
            // Memory check with modern var keyword
            var runtime = Runtime.getRuntime();
            var maxMemory = runtime.maxMemory();
            var usedMemory = runtime.totalMemory() - runtime.freeMemory();
            
            if (usedMemory > maxMemory * 0.8) {
                throw new OutOfMemoryError("Insufficient memory for processing");
            }
            
            var instruments = fileProcessor.readInstruments(inputPath);
            
            // Use modern Java features for processing
            return processWithModernFeatures(instruments, dao);
            
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during modern Java processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Modern Java processing failed: " + e.getMessage(), e);
        }
    }
    
    private Map<String, Double> processWithModernFeatures(List<Instrument> instruments, InstrumentPriceModifierDao dao) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            // Group instruments using modern collectors
            var groupedInstruments = instruments.stream()
                .collect(Collectors.groupingBy(Instrument::getName));
            
            // Process each group with virtual threads
            var futures = groupedInstruments.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> CompletableFuture.supplyAsync(
                        () -> processInstrumentGroup(entry.getKey(), entry.getValue(), dao),
                        executor
                    )
                ));
            
            // Collect results using modern stream operations
            return futures.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> {
                        try {
                            return entry.getValue().get();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to get result", e);
                        }
                    },
                    (v1, v2) -> v1,
                    TreeMap::new
                ));
                
        } catch (Exception e) {
            throw new RuntimeException("Virtual thread processing failed", e);
        }
    }
    
    private Double processInstrumentGroup(String name, List<Instrument> instruments, InstrumentPriceModifierDao dao) {
        // Enhanced switch expression (Java 14+)
        var calculationType = switch (name.substring(0, Math.min(name.length(), 11))) {
            case "INSTRUMENT1" -> "MEAN";
            case "INSTRUMENT2" -> "SUM";
            case "INSTRUMENT3" -> "CUSTOM";
            default -> "DEFAULT";
        };
        
        // Modern switch with enhanced pattern matching
        var baseValue = switch (calculationType) {
            case "MEAN" -> instruments.stream()
                .mapToDouble(Instrument::getPrice)
                .average()
                .orElse(0.0);
            case "SUM" -> instruments.stream()
                .mapToDouble(Instrument::getPrice)
                .sum();
            case "CUSTOM" -> instruments.stream()
                .mapToDouble(Instrument::getPrice)
                .sum() * 2.0;
            default -> calculator.calculateByType(name, instruments);
        };
        
        var multiplier = multiplierService.getMultiplier(dao, name);
        return baseValue * multiplier;
    }
    
    // Record for structured data (Java 14+)
    public record ProcessingResult(String instrumentName, Double value) {}
}