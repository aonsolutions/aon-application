package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static org.jooq.impl.SQLDataType.VARCHAR;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterAgreement4SSNumber implements Update {

	public static final AlterAgreement4SSNumber ALTER_AGREEMENT_SSNUM = new AlterAgreement4SSNumber();
	
	private AlterAgreement4SSNumber() {}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		Field<String> ssNumber = DSL.field("ss_number", VARCHAR, "Codigo convenio Seguridad Social");
		
		try {
			dslContext.select(ssNumber).from(AGREEMENT).limit(1).fetch();
		} catch ( Exception e ) {
			dslContext
			.alterTable(AGREEMENT)
			.addColumn(ssNumber, VARCHAR.length(20).nullable(true))
			.execute()
			;			
		}
		

	}

}
