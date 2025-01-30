package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class MEI2025Rollback implements Update {
	
	private static final String PORCENTAJE_MEI_E = "PORCENTAJE_MEI_E";

	private static final String PORCENTAJE_MEI = "PORCENTAJE_MEI";

	public static final MEI2025Rollback MEI2025ROLLBACK = new MEI2025Rollback();


	private MEI2025Rollback() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// START_DATE 01/01/2025
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);
		
		Date startOf2025Date = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.YEAR, 2024);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		Date startOf2024Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2025Date))
			.and(SYSTEM_DATA.NAME.in(PORCENTAJE_MEI, PORCENTAJE_MEI_E))
			) == 0;

		// IF NO EXISTS
		if ( upgraded ) 
			return;
		
		
		
		dslContext.transaction( config -> {
			
			// DISABLED FOREING_KEY FOR INSERT
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// DELETE 2025 
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2025Date))
			.and(SYSTEM_DATA.NAME.in(PORCENTAJE_MEI, PORCENTAJE_MEI_E))
			.execute();
			;

			// OPEN 2024 
			dslContext
			.update(SYSTEM_DATA)
			.setNull(SYSTEM_DATA.END_DATE)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2024Date))
			.and(SYSTEM_DATA.NAME.in(PORCENTAJE_MEI, PORCENTAJE_MEI_E))
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
