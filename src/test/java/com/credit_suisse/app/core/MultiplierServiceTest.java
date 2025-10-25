package com.credit_suisse.app.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import com.credit_suisse.app.core.MultiplierService;
import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.model.InstrumentPriceModifier;

public class MultiplierServiceTest {

    private MultiplierService service = new MultiplierService();

    @Test
    void testGetMultiplierWithDao() {
        InstrumentPriceModifierDao dao = mock(InstrumentPriceModifierDao.class);
        InstrumentPriceModifier modifier = new InstrumentPriceModifier();
        modifier.setModifier(1.5);
        
        when(dao.findByNameList("INSTRUMENT1")).thenReturn(Arrays.asList(modifier));
        
        double result = service.getMultiplier(dao, "INSTRUMENT1");
        assertEquals(1.5, result, 0.001);
    }

    @Test
    void testGetMultiplierWithNullDao() {
        double result = service.getMultiplier(null, "INSTRUMENT1");
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void testGetMultiplierWithEmptyList() {
        InstrumentPriceModifierDao dao = mock(InstrumentPriceModifierDao.class);
        when(dao.findByNameList("INSTRUMENT1")).thenReturn(Collections.emptyList());
        
        double result = service.getMultiplier(dao, "INSTRUMENT1");
        assertEquals(1.0, result, 0.001);
    }

    @Test
    void testGetMultiplierWithNullList() {
        InstrumentPriceModifierDao dao = mock(InstrumentPriceModifierDao.class);
        when(dao.findByNameList("INSTRUMENT1")).thenReturn(null);
        
        double result = service.getMultiplier(dao, "INSTRUMENT1");
        assertEquals(1.0, result, 0.001);
    }
}