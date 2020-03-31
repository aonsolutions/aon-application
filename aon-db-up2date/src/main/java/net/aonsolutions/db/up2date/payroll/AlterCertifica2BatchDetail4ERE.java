package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static org.jooq.impl.SQLDataType.VARCHAR;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterCertifica2BatchDetail4ERE implements Update {

	public static final AlterCertifica2BatchDetail4ERE ALTER_CERTIFICA2_BATCH_DETAIL_ERE = 
	new AlterCertifica2BatchDetail4ERE();
	
	private AlterCertifica2BatchDetail4ERE() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		Field<String> ereNumber = DSL.field("ere_number", VARCHAR);
		
		
		try {
			dslContext.select(ereNumber).from(CERTIFICA2_BATCH_DETAIL).limit(1).fetch();
		} catch ( Exception e ) {
			dslContext
			.alterTable(CERTIFICA2_BATCH_DETAIL)
			.addColumn(ereNumber, VARCHAR.length(15).nullable(true))
			.execute()
			;			
		}
		

	}

}
