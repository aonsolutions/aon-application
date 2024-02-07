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

    public void check(File file) {
	try {
	    dslContext.transaction( configuration ->  {
		IvlcccParser.parse(file, new JooqIvl2Contract(configuration.dsl(), parentDomainName)); 
		throw new RollbackException();
	    });
	} catch ( RollbackException rollback ) {
	    
	}
    }
    
    public void insert(File file) {
	dslContext.transaction( configuration ->  {
	    IvlcccParser.parse(file, new JooqIvl2Contract(configuration.dsl(), parentDomainName));   
	    // Implicit commit executed here
	});
    }
    
    public void insert(InputStream is) {
	dslContext.transaction( configuration ->  {
	    IvlcccParser.parse(is, new JooqIvl2Contract(configuration.dsl(), parentDomainName));   
	    // Implicit commit executed here
	});
    }
    
    public static void insert(DSLContext dslContext, String parentDomainName, byte [] data ) throws IOException {
	try ( InputStream is = new ByteArrayInputStream(data)) {
	    new Ivl2Aon(dslContext, parentDomainName).insert(is);
	}
    }

    public static void insert(DSLContext dslContext, String parentDomainName, InputStream is ) {
	new Ivl2Aon(dslContext, parentDomainName).insert(is);
    }

}
