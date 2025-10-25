package com.credit_suisse.app.core;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;
import com.credit_suisse.app.model.InstrumentPriceModifier;
import com.credit_suisse.app.util.InstrumentUtil;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RxJavaProcessingStrategy implements ProcessingStrategy {
    
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
            
            return Observable.fromCallable(() -> fileProcessor.readInstruments(inputPath))
                    .subscribeOn(Schedulers.io())
                    .flatMap(instruments -> Observable.fromIterable(instruments))
                    .groupBy(Instrument::getName)
                    .flatMap(group -> calculateForGroup(group, dao))
                    .toMap(result -> result.instrumentName, result -> result.value)
                    .onErrorReturn(throwable -> {
                        throw new RuntimeException("RxJava processing failed: " + throwable.getMessage(), throwable);
                    })
                    .blockingGet();
        } catch (OutOfMemoryError e) {
            throw new RuntimeException("Memory limit exceeded during RxJava processing", e);
        } catch (Exception e) {
            throw new RuntimeException("RxJava processing failed: " + e.getMessage(), e);
        }
    }
    
    private Observable<CalculationResult> calculateForGroup(io.reactivex.rxjava3.observables.GroupedObservable<String, Instrument> group, InstrumentPriceModifierDao dao) {
        return group.toList()
                .map(instruments -> {
                    if (instruments.isEmpty()) return new CalculationResult("", 0.0);
                    
                    String name = instruments.get(0).getName();
                    double baseValue = calculator.calculateByType(name, instruments);
                    double multiplier = multiplierService.getMultiplier(dao, name);
                    
                    return new CalculationResult(name, baseValue * multiplier);
                })
                .subscribeOn(Schedulers.computation())
                .toObservable();
    }
    
    private static class CalculationResult {
        final String instrumentName;
        final Double value;
        
        CalculationResult(String instrumentName, Double value) {
            this.instrumentName = instrumentName;
            this.value = value;
        }
    }
}