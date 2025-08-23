package com.credit_suisse.app.web.controller;

import com.credit_suisse.app.core.CalculatorEngine;
import com.credit_suisse.app.core.ProcessingStrategyFactory;
import com.credit_suisse.app.core.ProcessingStrategyFactory.StrategyType;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.InstrumentPriceModifier;
import com.credit_suisse.app.util.TimeFormatter;
import com.credit_suisse.app.util.InstrumentFileGenerator;
import com.credit_suisse.app.util.OptimizedInstrumentFileGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class WelcomeController {

	private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

	@Autowired
	InstrumentPriceModifierDao instrumentPriceModifierDao;
	
	@GetMapping("/")
	public String welcome(Model model) {
		try {
			List<InstrumentPriceModifier> modifiers = instrumentPriceModifierDao.findAll();
			logger.debug(Arrays.toString(modifiers.toArray()));
			model.addAttribute("modifiers", modifiers);
		} catch (Exception e) {
			logger.warn("Database not available, using default multipliers: {}", e.getMessage());
			model.addAttribute("modifiers", "Database not initialized - using default multipliers (1.0)");
		}
		
		// Ensure default files exist
		ensureDefaultFilesExist();
		
		// Show page immediately with modifiers, performance data will load async
		model.addAttribute("instruments", "");
		model.addAttribute("traditionalTime", "");
		model.addAttribute("rxJavaTime", "");
		model.addAttribute("performanceComparison", "");

		return "welcome";
	}
	
	@GetMapping("/performance")
	public String performanceData(@RequestParam(defaultValue = "SMALL") String fileSize, Model model) {
		try {
			// Determine input path based on file size
			String inputPath = getInputPathForSize(fileSize);
			
			// Validate file exists, generate if missing
			if (!new java.io.File(inputPath).exists()) {
				logger.info("File not found, generating: {}", inputPath);
				generateFileForSize(fileSize);
			}
			
			CalculatorEngine calculator = new CalculatorEngine(inputPath);
			
			// Add file size info to model
			model.addAttribute("currentFileSize", fileSize);
			model.addAttribute("fileSizes", InstrumentFileGenerator.FileSize.values());
			
			// Test all strategies using factory
			Map<String, Long> timings = new java.util.HashMap<>();
			Map<String, Double> results = null;
			
			// Process strategies with retry logic for database concurrency issues
			for (StrategyType strategyType : ProcessingStrategyFactory.getAllStrategyTypes()) {
					try {
						calculator.setProcessingStrategy(ProcessingStrategyFactory.createStrategy(strategyType));
						long startTime = System.currentTimeMillis();
						
						// Add retry logic for database concurrency issues
						Map<String, Double> strategyResults = null;
						int retries = 3;
						while (retries > 0 && strategyResults == null) {
							try {
								strategyResults = calculator.calculate(instrumentPriceModifierDao);
							} catch (org.springframework.dao.ConcurrencyFailureException e) {
								retries--;
								if (retries > 0) {
									logger.warn("Database concurrency issue for {}, retrying... ({} attempts left)", 
										strategyType.getDisplayName(), retries);
									Thread.sleep(100); // Brief pause before retry
								} else {
									throw e;
								}
							}
						}
						
						long duration = System.currentTimeMillis() - startTime;
						timings.put(strategyType.name(), duration);
						if (results == null) results = strategyResults;
						
						logger.info("{} processing: {}", strategyType.getDisplayName(), TimeFormatter.formatTime(duration));
					} catch (Exception e) {
						logger.warn("{} strategy failed: {}", strategyType.getDisplayName(), e.getMessage());
						timings.put(strategyType.name(), -1L); // Mark as failed
					}
				}
			
			// Add results to model
			model.addAttribute("instruments", results);
			model.addAttribute("parallelStreamTime", TimeFormatter.formatTime(timings.get("PARALLEL_STREAM")));
			model.addAttribute("rxJavaTime", TimeFormatter.formatTime(timings.get("RXJAVA")));
			model.addAttribute("batchTime", TimeFormatter.formatTime(timings.get("BATCH")));
			model.addAttribute("manualBatchTime", TimeFormatter.formatTime(timings.get("MANUAL_BATCH")));
			model.addAttribute("singleThreadTime", TimeFormatter.formatTime(timings.get("SINGLE_THREADED")));
			model.addAttribute("functionalTime", TimeFormatter.formatTime(timings.get("FUNCTIONAL")));
			model.addAttribute("asyncTime", TimeFormatter.formatTime(timings.get("COMPLETABLE_FUTURE")));
			model.addAttribute("kafkaTime", TimeFormatter.formatTime(timings.get("KAFKA_STREAM")));
			model.addAttribute("modernJavaTime", TimeFormatter.formatTime(timings.get("MODERN_JAVA")));
            model.addAttribute("springBatchTime", TimeFormatter.formatTime(timings.get("SPRING_BATCH")));
			// Build performance comparison string with sorting and file info
			java.util.List<java.util.Map.Entry<String, Long>> sortedTimings = timings.entrySet().stream()
				.sorted(java.util.Map.Entry.comparingByValue())
				.collect(java.util.stream.Collectors.toList());
			
			// Count records in file
			long recordCount = 0;
			try {
				recordCount = java.nio.file.Files.lines(java.nio.file.Paths.get(inputPath)).count();
			} catch (Exception e) {
				logger.warn("Could not count records in file: {}", e.getMessage());
			}
			
			StringBuilder comparison = new StringBuilder();
			comparison.append(String.format("📊 Dataset: %s (%,d records) | 🏆 Performance Ranking:\n", 
				java.nio.file.Paths.get(inputPath).getFileName(), recordCount));
			
			int rank = 1;
			for (int i = 0; i < sortedTimings.size(); i++) {
				java.util.Map.Entry<String, Long> entry = sortedTimings.get(i);
				ProcessingStrategyFactory.StrategyType type = ProcessingStrategyFactory.StrategyType.valueOf(entry.getKey());
				
				if (entry.getValue() == -1L) {
					// Skip failed strategies in ranking
					if (i > 0) comparison.append(", ");
					comparison.append(String.format("%s: FAILED", type.getDisplayName()));
				} else {
					if (i > 0) comparison.append(", ");
					comparison.append(String.format("%d. %s: %s", 
						rank++, type.getDisplayName(), TimeFormatter.formatTime(entry.getValue())));
				}
			}
			model.addAttribute("performanceComparison", comparison.toString());
			
		} catch (RuntimeException e) {
			logger.error("Processing failed: {}", e.getMessage());
			model.addAttribute("instruments", "Processing failed: " + e.getMessage());
			model.addAttribute("traditionalTime", "Error");
			model.addAttribute("rxJavaTime", "Error");
			model.addAttribute("performanceComparison", "Processing failed due to: " + e.getMessage());
		}

		return "performance-fragment";
	}
	
	@GetMapping("/generate")
	public String generateFile(Model model) {
		model.addAttribute("fileSizes", InstrumentFileGenerator.FileSize.values());
		model.addAttribute("optimizedSizes", OptimizedInstrumentFileGenerator.OptimizedFileSize.values());
		return "file-generator";
	}
	
	@PostMapping("/generate")
	public String generateFilePost(@RequestParam(defaultValue = "SMALL") String fileSize,
								   @RequestParam(required = false) String generatorType,
								   @RequestParam(defaultValue = "false") boolean optimized, 
								   Model model) {
		try {
			logger.info("Generating file with size: '{}', generatorType: '{}', optimized: {}", fileSize, generatorType, optimized);
			
			// Set optimized flag based on generatorType if not explicitly set
			if (generatorType != null) {
				optimized = "optimized".equals(generatorType);
			}
			
			if (fileSize == null || fileSize.trim().isEmpty()) {
				throw new IllegalArgumentException("File size parameter is required");
			}
			
			long startTime = System.currentTimeMillis();
			String filePath;
			String fileName;
			
			if (optimized) {
				OptimizedInstrumentFileGenerator.OptimizedFileSize size = 
					OptimizedInstrumentFileGenerator.OptimizedFileSize.valueOf(fileSize.trim().toUpperCase());
				filePath = OptimizedInstrumentFileGenerator.generateOptimizedFile(size);
				fileName = size.getFileName();
			} else {
				InstrumentFileGenerator.FileSize size = InstrumentFileGenerator.FileSize.valueOf(fileSize.trim().toUpperCase());
				filePath = InstrumentFileGenerator.generateInstrumentFile(size);
				fileName = size.getFileName();
			}
			
			long duration = System.currentTimeMillis() - startTime;
			model.addAttribute("success", String.format("File generated successfully: %s in %dms", fileName, duration));
			model.addAttribute("generatedFile", fileName);
			model.addAttribute("generationTime", duration);
			
		} catch (Exception e) {
			logger.error("File generation failed", e);
			model.addAttribute("error", "File generation failed: " + e.getMessage());
		}
		model.addAttribute("fileSizes", InstrumentFileGenerator.FileSize.values());
		model.addAttribute("optimizedSizes", OptimizedInstrumentFileGenerator.OptimizedFileSize.values());
		return "file-generator";
	}
	
	private String getInputPathForSize(String fileSize) {
		switch (fileSize.toUpperCase()) {
			case "SMALL": 
				String smallPath = "src/main/resources/small_optimized.txt";
				return new java.io.File(smallPath).exists() ? smallPath : "src/main/resources/small_input.txt";
			case "MEDIUM": 
				String mediumPath = "src/main/resources/medium_optimized.txt";
				return new java.io.File(mediumPath).exists() ? mediumPath : "src/main/resources/medium_input.txt";
			case "LARGE": 
				String largePath = "src/main/resources/large_optimized.txt";
				return new java.io.File(largePath).exists() ? largePath : "src/main/resources/large_input.txt";
			case "XLARGE": 
				String xlargePath = "src/main/resources/xlarge_optimized.txt";
				return new java.io.File(xlargePath).exists() ? xlargePath : "src/main/resources/huge_input.txt";
			default: return "src/main/resources/small_optimized.txt";
		}
	}
	
	@GetMapping("/benchmark")
	public String runBenchmark(Model model) {
		try {
			OptimizedInstrumentFileGenerator.benchmarkGeneration();
			model.addAttribute("benchmarkSuccess", "Benchmark completed successfully. Check logs for results.");
		} catch (Exception e) {
			logger.error("Benchmark failed: {}", e.getMessage());
			model.addAttribute("benchmarkError", "Benchmark failed: " + e.getMessage());
		}
		return "file-generator";
	}
	
	@GetMapping("/single-strategy")
	public String singleStrategy(@RequestParam(required = false) String strategy, 
								 @RequestParam(defaultValue = "SMALL") String fileSize, 
								 Model model) {
		try {
			logger.info("Single strategy request - strategy: '{}', fileSize: '{}'", strategy, fileSize);
			
			if (strategy == null || strategy.trim().isEmpty()) {
				model.addAttribute("error", "Strategy parameter is required");
				return "single-strategy-fragment";
			}
			
			String inputPath = getInputPathForSize(fileSize);
			logger.info("Using input path: {}", inputPath);
			
			if (!new java.io.File(inputPath).exists()) {
				logger.warn("Input file not found: {}, generating...", inputPath);
				generateFileForSize(fileSize);
			}
			
			CalculatorEngine calculator = new CalculatorEngine(inputPath);
			
			StrategyType strategyType;
			try {
				strategyType = mapStrategyName(strategy.trim());
			} catch (IllegalArgumentException e) {
				model.addAttribute("error", "Unknown strategy: " + strategy);
				return "single-strategy-fragment";
			}
			
			calculator.setProcessingStrategy(ProcessingStrategyFactory.createStrategy(strategyType));
			long startTime = System.currentTimeMillis();
			Map<String, Double> results = calculator.calculate(instrumentPriceModifierDao);
			long duration = System.currentTimeMillis() - startTime;
			
			// Count records for single strategy
			long recordCount = 0;
			try {
				recordCount = java.nio.file.Files.lines(java.nio.file.Paths.get(inputPath)).count();
			} catch (Exception e) {
				logger.warn("Could not count records in file: {}", e.getMessage());
			}
			
			model.addAttribute("instruments", results);
			model.addAttribute("strategyName", strategyType.getDisplayName());
			model.addAttribute("executionTime", TimeFormatter.formatTime(duration));
			model.addAttribute("fileSize", fileSize);
			model.addAttribute("recordCount", String.format("%,d", recordCount));
			model.addAttribute("fileName", java.nio.file.Paths.get(inputPath).getFileName().toString());
			
			logger.info("Single strategy '{}' completed in {}ms", strategy, duration);
			
		} catch (Exception e) {
			logger.error("Single strategy execution failed", e);
			model.addAttribute("error", "Execution failed: " + e.getMessage());
		}
		
		return "single-strategy-fragment";
	}
	
	@GetMapping("/check-datasets")
	public @ResponseBody Map<String, Boolean> checkDatasets() {
		Map<String, Boolean> availability = new java.util.HashMap<>();
		
		// Check if optimized files exist
		for (OptimizedInstrumentFileGenerator.OptimizedFileSize size : OptimizedInstrumentFileGenerator.OptimizedFileSize.values()) {
			String filePath = "src/main/resources/" + size.getFileName();
			boolean exists = new java.io.File(filePath).exists();
			availability.put(size.name(), exists);
		}
		
		return availability;
	}
	
	@PostMapping("/performance-custom")
	public String performanceCustom(@RequestParam("file") org.springframework.web.multipart.MultipartFile file, Model model) {
		try {
			if (file.isEmpty()) {
				throw new IllegalArgumentException("File is empty");
			}
			
			// Save uploaded file to system temp directory
			java.io.File tempFile = java.io.File.createTempFile("custom_instrument_", ".txt");
			file.transferTo(tempFile);
			String tempPath = tempFile.getAbsolutePath();
			
			CalculatorEngine calculator = new CalculatorEngine(tempPath);
			
			// Test all strategies using factory
			Map<String, Long> timings = new java.util.HashMap<>();
			Map<String, Double> results = null;
			
			for (StrategyType strategyType : ProcessingStrategyFactory.getAllStrategyTypes()) {
				calculator.setProcessingStrategy(ProcessingStrategyFactory.createStrategy(strategyType));
				long startTime = System.currentTimeMillis();
				Map<String, Double> strategyResults = calculator.calculate(instrumentPriceModifierDao);
				long duration = System.currentTimeMillis() - startTime;
				
				timings.put(strategyType.name(), duration);
				if (results == null) results = strategyResults;
			}
			
			// Add results to model
			model.addAttribute("instruments", results);
			model.addAttribute("parallelStreamTime", TimeFormatter.formatTime(timings.get("PARALLEL_STREAM")));
			model.addAttribute("rxJavaTime", TimeFormatter.formatTime(timings.get("RXJAVA")));
			model.addAttribute("batchTime", TimeFormatter.formatTime(timings.get("BATCH")));
			model.addAttribute("manualBatchTime", TimeFormatter.formatTime(timings.get("MANUAL_BATCH")));
			model.addAttribute("singleThreadTime", TimeFormatter.formatTime(timings.get("SINGLE_THREADED")));
			model.addAttribute("functionalTime", TimeFormatter.formatTime(timings.get("FUNCTIONAL")));
			model.addAttribute("asyncTime", TimeFormatter.formatTime(timings.get("COMPLETABLE_FUTURE")));
			model.addAttribute("kafkaTime", TimeFormatter.formatTime(timings.get("KAFKA_STREAM")));
			model.addAttribute("modernJavaTime", TimeFormatter.formatTime(timings.get("MODERN_JAVA")));
			model.addAttribute("springBatchTime", TimeFormatter.formatTime(timings.get("SPRING_BATCH")));
			
			// Build performance comparison
			StringBuilder comparison = new StringBuilder();
			for (StrategyType type : ProcessingStrategyFactory.getAllStrategyTypes()) {
				if (comparison.length() > 0) comparison.append(", ");
				comparison.append(type.getDisplayName()).append(": ")
					.append(TimeFormatter.formatTime(timings.get(type.name())));
			}
			model.addAttribute("performanceComparison", "Custom file (" + file.getOriginalFilename() + "): " + comparison.toString());
			
			// Clean up temp file
			tempFile.delete();
			
		} catch (Exception e) {
			logger.error("Custom file processing failed: {}", e.getMessage());
			model.addAttribute("error", "Processing failed: " + e.getMessage());
		}
		
		return "performance-fragment";
	}
	
	private void ensureDefaultFilesExist() {
		try {
			for (OptimizedInstrumentFileGenerator.OptimizedFileSize size : OptimizedInstrumentFileGenerator.OptimizedFileSize.values()) {
				String filePath = "src/main/resources/" + size.getFileName();
				if (!new java.io.File(filePath).exists()) {
					logger.info("Generating missing file: {}", size.getFileName());
					OptimizedInstrumentFileGenerator.generateOptimizedFile(size);
				}
			}
		} catch (Exception e) {
			logger.warn("Failed to generate default files: {}", e.getMessage());
		}
	}
	
	private void generateFileForSize(String fileSize) {
		try {
			// Try optimized generator first
			OptimizedInstrumentFileGenerator.OptimizedFileSize size = 
				OptimizedInstrumentFileGenerator.OptimizedFileSize.valueOf(fileSize);
			OptimizedInstrumentFileGenerator.generateOptimizedFile(size);
		} catch (Exception e) {
			logger.warn("Optimized generator failed, trying legacy generator: {}", e.getMessage());
			try {
				// Fallback to legacy generator
				InstrumentFileGenerator.FileSize legacySize = mapToLegacySize(fileSize);
				InstrumentFileGenerator.generateInstrumentFile(legacySize);
			} catch (Exception legacyError) {
				logger.error("Both generators failed for size {}: {}", fileSize, legacyError.getMessage());
			}
		}
	}
	
	private InstrumentFileGenerator.FileSize mapToLegacySize(String fileSize) {
		switch (fileSize.toUpperCase()) {
			case "SMALL": return InstrumentFileGenerator.FileSize.SMALL;
			case "MEDIUM": return InstrumentFileGenerator.FileSize.MEDIUM;
			case "LARGE": return InstrumentFileGenerator.FileSize.LARGE;
			case "XLARGE": return InstrumentFileGenerator.FileSize.HUGE;
			default: return InstrumentFileGenerator.FileSize.SMALL;
		}
	}
	
	private StrategyType mapStrategyName(String strategy) {
		switch (strategy.toUpperCase()) {
			case "PARALLEL_STREAM": return StrategyType.PARALLEL_STREAM;
			case "RXJAVA": return StrategyType.RXJAVA;
			case "BATCH": return StrategyType.BATCH;
			case "MANUAL_BATCH": return StrategyType.MANUAL_BATCH;
			case "SINGLE_THREADED": return StrategyType.SINGLE_THREADED;
			case "FUNCTIONAL": return StrategyType.FUNCTIONAL;
			case "COMPLETABLE_FUTURE": return StrategyType.COMPLETABLE_FUTURE;
			case "KAFKA_STREAM": return StrategyType.KAFKA_STREAM;
			case "MODERN_JAVA": return StrategyType.MODERN_JAVA;
			case "SPRING_BATCH": return StrategyType.SPRING_BATCH;
			default: throw new IllegalArgumentException("Unknown strategy: " + strategy);
		}
	}

}