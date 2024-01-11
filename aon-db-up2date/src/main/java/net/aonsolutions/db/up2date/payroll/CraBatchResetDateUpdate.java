package net.aonsolutions.db.up2date.payroll;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CraBatchResetDateUpdate implements Update {
	
	public static final CraBatchResetDateUpdate CRABATCHRESETDATEUPDATE = new CraBatchResetDateUpdate();

	private CraBatchResetDateUpdate() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.transaction( config -> resetTimeCraBatch(dslContext));
	}

	private void resetTimeCraBatch(DSLContext dslContext) {
		dslContext.execute("UPDATE cra_batch SET cra_batch.outcome_file_date = DATE_FORMAT(cra_batch.outcome_file_date, '%Y-%m-%d 00:00:00')");
	}

	
}
