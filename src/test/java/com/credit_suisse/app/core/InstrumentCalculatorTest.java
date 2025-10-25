package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import com.credit_suisse.app.core.InstrumentCalculator;
import com.credit_suisse.app.model.Instrument;
import com.credit_suisse.app.model.Instrument1;

public class InstrumentCalculatorTest {

    private InstrumentCalculator calculator = new InstrumentCalculator();

    @Test
    void testCalculateMean() {
        List<Instrument> instruments = Arrays.asList(
            createInstrument("INSTRUMENT1", 10.0),
            createInstrument("INSTRUMENT1", 20.0),
            createInstrument("INSTRUMENT1", 30.0)
        );
        
        double result = calculator.calculateByType("INSTRUMENT1", instruments);
        assertEquals(20.0, result, 0.001);
    }

    @Test
    void testCalculateCustom() {
        List<Instrument> instruments = Arrays.asList(
            createInstrument("INSTRUMENT3", 5.0),
            createInstrument("INSTRUMENT3", 10.0)
        );
        
        double result = calculator.calculateByType("INSTRUMENT3", instruments);
        assertEquals(30.0, result, 0.001); // (5 + 10) * 2
    }

    @Test
    void testCalculateNewest10Sum() {
        List<Instrument> instruments = Arrays.asList(
            createInstrument("INSTRUMENT4", 1.0),
            createInstrument("INSTRUMENT4", 2.0),
            createInstrument("INSTRUMENT4", 3.0)
        );
        
        double result = calculator.calculateByType("INSTRUMENT4", instruments);
        assertEquals(6.0, result, 0.001);
    }

    private Instrument createInstrument(String name, double price) {
        return new Instrument1(name, price, new Date());
    }
}