package net.aonsolutions.db.up2date.accounting;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterRbankAddSepaMandateRef implements Update {

	public static final AlterRbankAddSepaMandateRef ALTER_RBANK_ADD_SEPA_MANDATE_REF = new AlterRbankAddSepaMandateRef();
	
	private AlterRbankAddSepaMandateRef() {
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
			.execute("ALTER TABLE `rbank` ADD `sepa_mandate_ref` char(20) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mandate reference'");
			System.out.println("\tAlterRbankAddSepaMandateRef. sepa_mandate_ref ADDED!");
	//		;
		} catch ( DataAccessException  e) {
			System.out.println("\tAlterRbankAddSepaMandateRef. sepa_mandate_ref NOT ADDED!");
			e.printStackTrace();
		}
		
	}

}
