package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;

public class ProcessingStrategyFactoryTest {

    @Test
    void testCreateAllStrategies() {
        for (ProcessingStrategyFactory.StrategyType type : ProcessingStrategyFactory.getAllStrategyTypes()) {
            ProcessingStrategy strategy = ProcessingStrategyFactory.createStrategy(type);
            assertNotNull(strategy);
        }
    }

    @Test
    void testGetAllStrategyTypes() {
        List<ProcessingStrategyFactory.StrategyType> types = ProcessingStrategyFactory.getAllStrategyTypes();
        assertEquals(10, types.size());
    }

    @Test
    void testStrategyTypeDisplayNames() {
        assertEquals("Parallel Stream", ProcessingStrategyFactory.StrategyType.PARALLEL_STREAM.getDisplayName());
        assertEquals("RxJava", ProcessingStrategyFactory.StrategyType.RXJAVA.getDisplayName());
        assertEquals("CompletableFuture", ProcessingStrategyFactory.StrategyType.COMPLETABLE_FUTURE.getDisplayName());
        assertEquals("Kafka Stream", ProcessingStrategyFactory.StrategyType.KAFKA_STREAM.getDisplayName());
        assertEquals("Modern Java 24", ProcessingStrategyFactory.StrategyType.MODERN_JAVA.getDisplayName());
    }

    @Test
    void testCreateStrategyWithNull() {
        assertThrows(NullPointerException.class, () -> {
            ProcessingStrategyFactory.createStrategy(null);
        });
    }
}