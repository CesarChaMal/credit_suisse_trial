package com.credit_suisse.app.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.credit_suisse.app.model.Instrument;
import com.credit_suisse.app.util.InstrumentUtil;

public class FileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FileProcessor.class);
    
    public List<Instrument> readInstruments(String inputPath) {
        try (BufferedReader reader = Files.newBufferedReader(new File(inputPath).toPath(), Charset.defaultCharset())) {
            return reader.lines()
                    .map(InstrumentUtil::defineOf)
                    .filter(instrument -> instrument != null)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("InputFile exception: ", e);
            throw new RuntimeException("Failed to read file: " + inputPath, e);
        }
    }
}