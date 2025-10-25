package com.credit_suisse.app.core;

import java.util.Arrays;
import java.util.List;

public class ProcessingStrategyFactory {
    
    public enum StrategyType {
        PARALLEL_STREAM("Parallel Stream"),
        RXJAVA("RxJava"),
        BATCH("Batch"),
        MANUAL_BATCH("Manual Batch"),
        SINGLE_THREADED("Single-Threaded"),
        FUNCTIONAL("Functional"),
        COMPLETABLE_FUTURE("CompletableFuture"),
        KAFKA_STREAM("Kafka Stream"),
        MODERN_JAVA("Modern Java 24"),
        SPRING_BATCH("Spring Batch");
        
        private final String displayName;
        
        StrategyType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public static ProcessingStrategy createStrategy(StrategyType type) {
        switch (type) {
            case PARALLEL_STREAM:
                return new ParallelStreamProcessingStrategy();
            case RXJAVA:
                return new RxJavaProcessingStrategy();
            case BATCH:
                return new SpringBatchProcessingStrategy();
            case MANUAL_BATCH:
                return new ManualBatchProcessingStrategy();
            case SINGLE_THREADED:
                return new SingleThreadedProcessingStrategy();
            case FUNCTIONAL:
                return new FunctionalProcessingStrategy();
            case COMPLETABLE_FUTURE:
                return new CompletableFutureProcessingStrategy();
            case KAFKA_STREAM:
                return new KafkaStreamProcessingStrategy();
            case MODERN_JAVA:
                return new ModernJavaProcessingStrategy();
            case SPRING_BATCH:
                return new SpringBatchProcessingStrategy();
            default:
                throw new IllegalArgumentException("Unknown strategy type: " + type);
        }
    }
    
    public static List<StrategyType> getAllStrategyTypes() {
        return Arrays.asList(StrategyType.values());
    }
}