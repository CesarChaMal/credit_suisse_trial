package com.credit_suisse.app;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

//import com.credit_suisse.app.config.SpringRootConfig;
import com.credit_suisse.app.core.CalculatorEngine;
import com.credit_suisse.app.core.module.OnFlyModule;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.Instrument;
import com.credit_suisse.app.model.newInstrument;

public class Application {

    @Autowired
    static public ApplicationContext ctx;

	public static void main(String[] args) {
		String inputPath = "src/main/resources/input.txt";
		InstrumentPriceModifierDao instrumentPriceModifierDao = null;
		Instrument newInstrument = new newInstrument("INSTRUMENT3", 4.0d, new Date());
		newInstrument.setInstrumentCalculateBehavior(new OnFlyModule(){
			@Override
			public Double calculate() {
				double sum = 0;
				int counter = 0;
				for (Instrument i : getInstruments()) {
					sum += i.getPrice();
					counter++;
				}
				return sum*2;
			}
		});
		
		CalculatorEngine calculator = CalculatorEngine.getInstance(inputPath);
		calculator.addModule(newInstrument);
		calculator.calculate(instrumentPriceModifierDao);
	}
}
