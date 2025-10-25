package com.credit_suisse.app.core;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;

public class CalculatorEngine {

	private static final Logger logger = LoggerFactory.getLogger(CalculatorEngine.class);

	private String inputPath;
	private ProcessingStrategy processingStrategy;
	
	public CalculatorEngine(String inputPath) {
		logger.debug("Input file path: {}", inputPath);
		this.inputPath = inputPath;
		this.processingStrategy = new ParallelStreamProcessingStrategy();
	}

	public void setProcessingStrategy(ProcessingStrategy strategy) {
		this.processingStrategy = strategy;
	}
	
	public Map<String, Double> calculate(InstrumentPriceModifierDao dao) {
		return processingStrategy.processCalculation(inputPath, dao);
	}
}
