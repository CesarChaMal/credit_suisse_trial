package com.credit_suisse.app;

import java.util.Map;

import com.credit_suisse.app.core.*;
import com.credit_suisse.app.util.TimeFormatter;

public class Application {

	public static void main(String[] args) {
		String inputPath = "src/main/resources/input.txt";
		CalculatorEngine calculator = new CalculatorEngine(inputPath);
		
		// Test all strategies using factory
		Map<String, Double> results = null;
		
		System.out.println("=== Performance Results ===");
		for (ProcessingStrategyFactory.StrategyType strategyType : ProcessingStrategyFactory.getAllStrategyTypes()) {
			calculator.setProcessingStrategy(ProcessingStrategyFactory.createStrategy(strategyType));
			long startTime = System.currentTimeMillis();
			Map<String, Double> strategyResults = calculator.calculate(null);
			long duration = System.currentTimeMillis() - startTime;
			
			if (results == null) results = strategyResults;
			System.out.println(strategyType.getDisplayName() + " processing: " + TimeFormatter.formatTime(duration));
		}
		
		System.out.println("\nResults: " + results);
	}
}
