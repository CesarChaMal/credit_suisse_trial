package com.credit_suisse.app.dao;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.credit_suisse.app.dao.InstrumentPriceModifierDao;
import com.credit_suisse.app.dao.InstrumentPriceModifierDaoImpl;
import com.credit_suisse.app.model.InstrumentPriceModifier;

public class InstrumentPriceModifierDaoTest {

    private EmbeddedDatabase db;

    InstrumentPriceModifierDao instrumentPriceModifierDao;
    
    @Before
    public void setUp() {
        //db = new EmbeddedDatabaseBuilder().addDefaultScripts().build();
    	db = new EmbeddedDatabaseBuilder()
    		.setType(EmbeddedDatabaseType.H2)
    		.addScript("db/sql/h2/create-db.sql")
    		.addScript("db/sql/h2/insert-data.sql")
    		.build();
    }

    @Test
    public void testFindByname() {
    	NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(db);
    	InstrumentPriceModifierDaoImpl instrumentPriceModifierDao = new InstrumentPriceModifierDaoImpl();
    	instrumentPriceModifierDao.setNamedParameterJdbcTemplate(template);
    	
    	InstrumentPriceModifier instrument = instrumentPriceModifierDao.findByName("INSTRUMENT1");
    	List<InstrumentPriceModifier> instrumentList = instrumentPriceModifierDao.findByNameList("INSTRUMENT1");
  
//    	System.out.println(instrument.getId());
    	
    	Assert.assertNotNull(instrument);
    	Assert.assertEquals(new Long(1), instrumentList.get(0).getId());
    	Assert.assertEquals("INSTRUMENT1", instrumentList.get(0).getName());
    	Assert.assertEquals(1.05, instrumentList.get(0).getModifier(), 0.001);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

}