package net.aonsolutions.db.up2date.accounting;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterBankStatementAddNordigenInternalId implements Update {

	public static final AlterBankStatementAddNordigenInternalId ALTER_BANK_STATEMENT_ADD_NORDIGEN_INTERNAL_ID = new AlterBankStatementAddNordigenInternalId();

	private AlterBankStatementAddNordigenInternalId() {}
	
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
			.execute("ALTER TABLE `bank_statement` ADD `nordigen_id` varchar(64) CHARACTER SET latin1 COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Identificador interno de Nordigen'");
			System.out.println("\tAlterBankStatementAddNordigenInternalId. nordigen_id ADDED!");
		} catch ( DataAccessException  e) {
			System.out.println("\tAlterBankStatementAddNordigenInternalId. nordigen_id NOT ADDED!");
			e.printStackTrace();
		}
		
	}
		
}
