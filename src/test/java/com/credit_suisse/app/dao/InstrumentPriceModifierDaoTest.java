package com.credit_suisse.app.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.credit_suisse.app.model.InstrumentPriceModifier;

public class InstrumentPriceModifierDaoTest {

    private EmbeddedDatabase db;

    InstrumentPriceModifierDao instrumentPriceModifierDao;
    
    @BeforeEach
    void setUp() {
        //db = new EmbeddedDatabaseBuilder().addDefaultScripts().build();
    	db = new EmbeddedDatabaseBuilder()
    		.setType(EmbeddedDatabaseType.H2)
    		.addScript("db/sql/h2/create-db.sql")
    		.addScript("db/sql/h2/insert-data.sql")
    		.build();
    }

    @Test
    void testFindByname() {
    	NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(db);
    	InstrumentPriceModifierDaoImpl instrumentPriceModifierDao = new InstrumentPriceModifierDaoImpl();
    	instrumentPriceModifierDao.setNamedParameterJdbcTemplate(template);
    	
    	InstrumentPriceModifier instrument = instrumentPriceModifierDao.findByName("INSTRUMENT1");
    	List<InstrumentPriceModifier> instrumentList = instrumentPriceModifierDao.findByNameList("INSTRUMENT1");
  
//    	System.out.println(instrument.getId());
    	
    	assertNotNull(instrument);
    	assertEquals(new Long(1), instrumentList.get(0).getId());
    	assertEquals("INSTRUMENT1", instrumentList.get(0).getName());
    	assertEquals(1.05, instrumentList.get(0).getModifier(), 0.001);
    }

    @AfterEach
    void tearDown() {
        db.shutdown();
    }

}