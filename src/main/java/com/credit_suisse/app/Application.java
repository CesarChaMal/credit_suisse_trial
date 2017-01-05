package com.credit_suisse.app;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.credit_suisse.app.config.SpringRootConfig;
import com.credit_suisse.app.core.CalculatorEngine;
import com.credit_suisse.app.core.module.OnFlyModule;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.dao.InstrumentPriceModifierDaoImpl;
import com.credit_suisse.app.model.Instrument;
import com.credit_suisse.app.model.InstrumentPriceModifier;
import com.credit_suisse.app.model.newInstrument;
import com.credit_suisse.app.util.InstrumentUtil;

//@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
//@ContextConfiguration(locations = {"file:src/test/**/applicationContext-test.xml"})
public class Application {

	private static final String HELP_MESSAGE = "Example : java -jar aggregator.jar file=c:\\temp\\big_input.txt";

    @Autowired
    static public ApplicationContext ctx;

	public static void main(String[] args) {

		String inputPath = null;

//		if (args.length == 0) {
//			System.out.println(HELP_MESSAGE);
//			System.exit(1);
//		}
//		for (String param : args) {
//			String[] p = param.split("=");
//			switch (p[0]) {
//			case "file":
//				inputPath = p[1];
//				break;
//			default:
//				System.out.println(HELP_MESSAGE);
//				System.exit(1);
//			}
//		}

//		inputPath = "c:\\temp\\big_input.txt";
		inputPath = "c:\\temp\\input.txt";

//		ctx = new AnnotationConfigApplicationContext(SpringRootConfig.class);
//		ctx = new ClassPathXmlApplicationContext("file:src/main/**/spring-bean-config.xml");
//		InstrumentPriceModifierDao instrumentPriceModifierDao = ctx.getBean(InstrumentPriceModifierDao.class);
		InstrumentPriceModifierDao instrumentPriceModifierDao = new InstrumentPriceModifierDaoImpl();

		Instrument newInstrument = new newInstrument("INSTRUMENT3", 4.0d, new Date());
//		Instrument newInstrument = new newInstrument("INSTRUMENT3", 6.0d, DefinerInstrument.getDate("03-Jan-2017"));
		newInstrument.setInstrumentCalculateBehavior(new OnFlyModule(){
			@Override
			public Double calculate() {
				double sum = 0;
				int counter = 0;
				for (Instrument i : getInstruments()) {
//					System.out.println(i.getName());
//					System.out.println(i.getPrice());
					sum += i.getPrice();
					counter++;
				}
				return sum*2;
			}
		});
		
//		CalculatorEngine calculator = new CalculatorEngine(inputPath);
		CalculatorEngine calculator = CalculatorEngine.getInstance(inputPath);
		calculator.addModule(newInstrument);
		calculator.calculate(instrumentPriceModifierDao);

	}
}
