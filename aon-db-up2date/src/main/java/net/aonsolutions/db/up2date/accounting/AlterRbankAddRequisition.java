package net.aonsolutions.db.up2date.accounting;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterRbankAddRequisition implements Update {

	public static final AlterRbankAddRequisition ALTER_RBANK_ADD_REQUISITION = new AlterRbankAddRequisition();
	
	private AlterRbankAddRequisition() {
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
			.execute("ALTER TABLE `rbank` ADD `requisition` varchar(50) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Requisition de nordigen'");
			System.out.println("\tAlterRBankAddRequisition. requisition ADDED!");
	//		;
		} catch ( DataAccessException  e) {
			System.out.println("\tAlterRbankAddRequisition. requisition NOT ADDED!");
			e.printStackTrace();
		}
		
	}

}
