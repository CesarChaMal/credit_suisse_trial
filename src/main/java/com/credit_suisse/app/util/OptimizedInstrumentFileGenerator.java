package com.credit_suisse.app.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OptimizedInstrumentFileGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(OptimizedInstrumentFileGenerator.class);
    
    // Pre-computed constants for performance
    private static final LocalDate START_DATE = LocalDate.of(2000, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2017, 1, 1);
    private static final long DATE_RANGE_DAYS = java.time.temporal.ChronoUnit.DAYS.between(START_DATE, END_DATE);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    
    // Pre-generated instrument names for better performance
    private static final String[] INSTRUMENT_NAMES = generateInstrumentNames();
    
    public enum OptimizedFileSize {
        SMALL("Small Optimized (1K)", 1000, "small_optimized.txt"),
        MEDIUM("Medium Optimized (10K)", 10000, "medium_optimized.txt"),
        LARGE("Large Optimized (100K)", 100000, "large_optimized.txt"),
        XLARGE("X-Large Optimized (1M)", 1000000, "xlarge_optimized.txt"),
        MASSIVE("Massive Optimized (10M)", 10000000, "massive_optimized.txt");
        
        private final String displayName;
        private final long recordCount;
        private final String fileName;
        
        OptimizedFileSize(String displayName, long recordCount, String fileName) {
            this.displayName = displayName;
            this.recordCount = recordCount;
            this.fileName = fileName;
        }
        
        public String getDisplayName() { return displayName; }
        public long getRecordCount() { return recordCount; }
        public String getFileName() { return fileName; }
    }
    
    private static String[] generateInstrumentNames() {
        String[] names = new String[100];
        for (int i = 0; i < 100; i++) {
            names[i] = "INSTRUMENT" + (i + 1);
        }
        return names;
    }
    
    public static String generateOptimizedFile(OptimizedFileSize fileSize) throws IOException {
        return generateOptimizedFile(fileSize.getRecordCount(), fileSize.getFileName());
    }
    
    public static String generateOptimizedFile(long recordCount, String fileName) throws IOException {
        Path filePath = Paths.get("src/main/resources/" + fileName);
        Files.createDirectories(filePath.getParent());
        
        long startTime = System.currentTimeMillis();
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Use parallel streams for large files
            if (recordCount > 50000) {
                generateParallelRecords(writer, recordCount);
            } else {
                generateSequentialRecords(writer, recordCount);
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        logger.info("Optimized file generation completed: {} with {} records in {}ms", 
                   fileName, recordCount, duration);
        
        return filePath.toAbsolutePath().toString();
    }
    
    private static void generateSequentialRecords(BufferedWriter writer, long recordCount) throws IOException {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        
        for (long i = 0; i < recordCount; i++) {
            String record = generateOptimizedRecord(i, random);
            writer.write(record);
            writer.newLine();
            
            if (i % 100000 == 0 && i > 0) {
                logger.info("Generated {} records", i);
            }
        }
    }
    
    private static void generateParallelRecords(BufferedWriter writer, long recordCount) throws IOException {
        int batchSize = 10000;
        long batches = (recordCount + batchSize - 1) / batchSize;
        
        for (long batch = 0; batch < batches; batch++) {
            long startIdx = batch * batchSize;
            long endIdx = Math.min(startIdx + batchSize, recordCount);
            
            // Generate batch in parallel
            String[] records = new String[(int)(endIdx - startIdx)];
            IntStream.range(0, (int)(endIdx - startIdx))
                .parallel()
                .forEach(i -> {
                    long actualIndex = startIdx + i;
                    records[i] = generateOptimizedRecord(actualIndex, ThreadLocalRandom.current());
                });
            
            // Write batch sequentially
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
            
            if (batch % 10 == 0 && batch > 0) {
                logger.info("Generated {} records", endIdx);
            }
        }
    }
    
    private static String generateOptimizedRecord(long index, ThreadLocalRandom random) {
        // Optimized instrument name selection
        String instrumentName = getOptimizedInstrumentName(index, random);
        
        // Optimized date generation
        String date = generateOptimizedDate(random);
        
        // Optimized price generation
        double price = random.nextDouble(0.0, 100.0);
        
        return String.format("%s,%s,%.5f", instrumentName, date, price);
    }
    
    private static String getOptimizedInstrumentName(long index, ThreadLocalRandom random) {
        // First 300 records follow pattern: INSTRUMENT1, INSTRUMENT2, INSTRUMENT3
        if (index < 300) {
            return INSTRUMENT_NAMES[(int)(index % 3)];
        }
        
        // After 300, use weighted distribution
        if (index % 5 == 0) {
            // 20% random instruments
            return INSTRUMENT_NAMES[random.nextInt(INSTRUMENT_NAMES.length)];
        } else {
            // 80% cycling through first 10 instruments
            return INSTRUMENT_NAMES[(int)(index % 10)];
        }
    }
    
    private static String generateOptimizedDate(ThreadLocalRandom random) {
        long randomDay = random.nextLong(DATE_RANGE_DAYS);
        LocalDate randomDate = START_DATE.plusDays(randomDay);
        
        // Skip weekends (simple optimization)
        while (randomDate.getDayOfWeek().getValue() > 5) {
            randomDay = random.nextLong(DATE_RANGE_DAYS);
            randomDate = START_DATE.plusDays(randomDay);
        }
        
        return randomDate.format(DATE_FORMATTER);
    }
    
    // Benchmark method
    public static void benchmarkGeneration() {
        try {
            logger.info("Starting benchmark comparison...");
            
            // Test with medium size
            long recordCount = 100000;
            
            // Original method
            long startTime = System.currentTimeMillis();
            InstrumentFileGenerator.generateInstrumentFile(recordCount, "benchmark_original.txt");
            long originalTime = System.currentTimeMillis() - startTime;
            
            // Optimized method
            startTime = System.currentTimeMillis();
            generateOptimizedFile(recordCount, "benchmark_optimized.txt");
            long optimizedTime = System.currentTimeMillis() - startTime;
            
            logger.info("Benchmark Results for {} records:", recordCount);
            logger.info("Original method: {}ms", originalTime);
            logger.info("Optimized method: {}ms", optimizedTime);
            logger.info("Performance improvement: {}%", 
                       ((double)(originalTime - optimizedTime) / originalTime) * 100);
            
        } catch (IOException e) {
            logger.error("Benchmark failed", e);
        }
    }
}