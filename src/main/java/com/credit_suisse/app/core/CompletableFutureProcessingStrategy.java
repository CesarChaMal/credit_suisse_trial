package com.credit_suisse.app.core;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;

public class CompletableFutureProcessingStrategy implements ProcessingStrategy {
    
    private final MultithreadedFileProcessor fileProcessor = new MultithreadedFileProcessor();
    private final InstrumentCalculator calculator = new InstrumentCalculator();
    private final MultiplierService multiplierService = new MultiplierService();
    private final ForkJoinPool customThreadPool = new ForkJoinPool(4);
    
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
            
            // Group instruments and create async tasks
            Map<String, CompletableFuture<Double>> futures = instruments.stream()
                .collect(Collectors.groupingBy(Instrument::getName))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> processGroupAsync(entry.getKey(), entry.getValue(), dao)
                ));
            
            // Wait for all futures to complete and collect results
            Map<String, Double> results = new TreeMap<>();
            for (Map.Entry<String, CompletableFuture<Double>> entry : futures.entrySet()) {
                results.put(entry.getKey(), entry.getValue().join());
            }
            
            return results;
            
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during async processing", e);
        } catch (Exception e) {
            throw new RuntimeException("Async processing failed: " + e.getMessage(), e);
        } finally {
            customThreadPool.shutdown();
        }
    }
    
    private CompletableFuture<Double> processGroupAsync(String name, List<Instrument> instruments, InstrumentPriceModifierDao dao) {
        return CompletableFuture
            .supplyAsync(() -> calculator.calculateByType(name, instruments), customThreadPool)
            .thenCompose(baseValue -> 
                CompletableFuture.supplyAsync(() -> multiplierService.getMultiplier(dao, name), customThreadPool)
                    .thenApply(multiplier -> baseValue * multiplier)
            );
    }
}