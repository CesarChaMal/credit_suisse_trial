package com.credit_suisse.app.core;

import java.util.List;
import com.credit_suisse.app.model.Instrument;

public class InstrumentCalculator {
    
    public double calculateByType(String instrumentName, List<Instrument> instruments) {
        switch (instrumentName) {
            case "INSTRUMENT1":
                return calculateMean(instruments);
            case "INSTRUMENT2":
                return calculateNovember2014Mean(instruments);
            case "INSTRUMENT3":
                return calculateCustom(instruments);
            default:
                return calculateNewest10Sum(instruments);
        }
    }
    
    private double calculateMean(List<Instrument> instruments) {
        return instruments.stream().mapToDouble(Instrument::getPrice).average().orElse(0.0);
    }
    
    private double calculateNovember2014Mean(List<Instrument> instruments) {
        return instruments.stream()
                .filter(this::isNovember2014)
                .mapToDouble(Instrument::getPrice)
                .average().orElse(0.0);
    }
    
    private double calculateCustom(List<Instrument> instruments) {
        return instruments.stream().mapToDouble(Instrument::getPrice).sum() * 2;
    }
    
    private double calculateNewest10Sum(List<Instrument> instruments) {
        return instruments.stream()
                .sorted((a, b) -> b.getDate().compareTo(a.getDate()))
                .limit(10)
                .mapToDouble(Instrument::getPrice)
                .sum();
    }
    
    private boolean isNovember2014(Instrument instrument) {
        return instrument.getDate().getMonth() == 10 && (instrument.getDate().getYear() + 1900) == 2014;
    }
}