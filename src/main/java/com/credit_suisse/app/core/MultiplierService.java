package com.credit_suisse.app.core;

import java.util.List;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.InstrumentPriceModifier;

public class MultiplierService {
    
    public double getMultiplier(InstrumentPriceModifierDao dao, String instrumentName) {
        if (dao == null) return 1.0;
        
        try {
            List<InstrumentPriceModifier> modifiers = dao.findByNameList(instrumentName);
            return (modifiers != null && !modifiers.isEmpty()) ? modifiers.get(0).getModifier() : 1.0;
        } catch (Exception e) {
            // Database table not found or other DB error, return default multiplier
            return 1.0;
        }
    }
}