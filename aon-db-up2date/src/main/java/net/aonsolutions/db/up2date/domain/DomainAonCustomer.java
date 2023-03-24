package net.aonsolutions.db.up2date.domain;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DomainAonCustomer implements Update {

	public static final DomainAonCustomer DOMAIN_AON_CUSTOMER = new DomainAonCustomer();
	
	private DomainAonCustomer() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		try {
			dslContext
			.execute("ALTER TABLE `domain` ADD `aonCustomer` int DEFAULT NULL COMMENT 'Referencia al customer en Aon'");
			System.out.println("\tDomainAonCustomer. aonCustomer ADDED!");
		} catch ( Exception e ) {
			System.out.println("\tDomainAonCustomer. aonCustomer NOT ADDED!");
			e.printStackTrace();
		}
	}

}
