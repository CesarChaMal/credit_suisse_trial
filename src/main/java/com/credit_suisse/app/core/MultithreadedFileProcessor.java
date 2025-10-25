package com.credit_suisse.app.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.credit_suisse.app.model.Instrument;
import com.credit_suisse.app.util.InstrumentUtil;

public class MultithreadedFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(MultithreadedFileProcessor.class);
    private static final int CHUNK_SIZE = 1000;
    
    public List<Instrument> readInstruments(String inputPath) {
        try (BufferedReader reader = Files.newBufferedReader(new File(inputPath).toPath(), Charset.defaultCharset())) {
            List<String> lines = reader.lines().collect(Collectors.toList());
            return processInParallel(lines);
        } catch (IOException e) {
            logger.error("InputFile exception: ", e);
            throw new RuntimeException("Failed to read file: " + inputPath, e);
        }
    }
    
    private List<Instrument> processInParallel(List<String> lines) {
        List<List<String>> chunks = createChunks(lines, CHUNK_SIZE);
        
        List<CompletableFuture<List<Instrument>>> futures = chunks.stream()
            .map(chunk -> CompletableFuture.supplyAsync(() -> 
                chunk.parallelStream()
                    .map(InstrumentUtil::defineOf)
                    .filter(instrument -> instrument != null)
                    .collect(Collectors.toList()),
                ForkJoinPool.commonPool()))
            .collect(Collectors.toList());
        
        return futures.stream()
            .map(CompletableFuture::join)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
    
    private List<List<String>> createChunks(List<String> lines, int chunkSize) {
        List<List<String>> chunks = new ArrayList<>();
        for (int i = 0; i < lines.size(); i += chunkSize) {
            chunks.add(lines.subList(i, Math.min(i + chunkSize, lines.size())));
        }
        return chunks;
    }
}