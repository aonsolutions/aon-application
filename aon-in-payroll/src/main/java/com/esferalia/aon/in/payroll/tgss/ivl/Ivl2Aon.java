package com.esferalia.aon.in.payroll.tgss.ivl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class Ivl2Aon {
    
    private static class RollbackException extends RuntimeException {
	
    }
    
    private DSLContext dslContext;
    private String parentDomainName ;
    
    private static Settings getSettings() {
	Settings settings = new Settings();
	settings.setRenderSchema(false);
	settings.setParamType(ParamType.INLINED);
	return settings;
    }

    public Ivl2Aon(Connection connection, String parentDomainName) {
	this.parentDomainName = parentDomainName;
	this.dslContext = DSL.using(connection, getSettings());
    }

    public Ivl2Aon(DSLContext dslContext, String parentDomainName) {
	this.dslContext = dslContext;
	this.parentDomainName = parentDomainName;
    }

    public void check(File file, IvlParserListener ...listeners) {
	try {
	    dslContext.transaction( configuration ->  {
		IvlParserListener ivlListener = new IvlCompositeListener().add(new JooqIvl2Contract(configuration.dsl(), parentDomainName)).add(listeners);
		IvlcccParser.parse(file, ivlListener); 
		throw new RollbackException();
	    });
	} catch ( RollbackException rollback ) {
	    
	}
    }
    
    public void insert(File file, IvlParserListener ...listeners) {
	dslContext.transaction( configuration ->  {
	    IvlParserListener ivlListener = new IvlCompositeListener().add(new JooqIvl2Contract(configuration.dsl(), parentDomainName)).add(listeners);
	    IvlcccParser.parse(file, ivlListener);   
	    // Implicit commit executed here
	});
    }
    
    public void insert(InputStream is, IvlParserListener ...listeners) {
	dslContext.transaction( configuration ->  {
	    IvlParserListener ivlListener = new IvlCompositeListener().add(new JooqIvl2Contract(configuration.dsl(), parentDomainName)).add(listeners);
	    IvlcccParser.parse(is, ivlListener);   
	    // Implicit commit executed here
	});
    }
    
    public static void insert(DSLContext dslContext, String parentDomainName, byte [] data, IvlParserListener ...listeners ) throws IOException {
	try ( InputStream is = new ByteArrayInputStream(data)) {
	    new Ivl2Aon(dslContext, parentDomainName).insert(is, listeners);
	}
    }

    public static void insert(DSLContext dslContext, String parentDomainName, InputStream is, IvlParserListener ...listeners) {
	new Ivl2Aon(dslContext, parentDomainName).insert(is, listeners);
    }

}
