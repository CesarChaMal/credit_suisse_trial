package com.credit_suisse.app.core;

import java.util.Map;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;

public interface ProcessingStrategy {
    Map<String, Double> processCalculation(String inputPath, InstrumentPriceModifierDao dao);
}