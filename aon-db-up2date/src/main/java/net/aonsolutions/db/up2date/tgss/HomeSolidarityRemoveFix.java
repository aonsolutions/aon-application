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

public class HomeSolidarityRemoveFix implements Update {
	
	public static final HomeSolidarityRemoveFix HOME_SOLIDARITY_REMOVE_FIX = new HomeSolidarityRemoveFix();

	
	private static final int DOMAIN = -106;

	private HomeSolidarityRemoveFix() {
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
		
		// START_DATE 01/01/2023
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);
		
		Date startOf2025Date = new Date(calendar.getTimeInMillis());
		
		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.like("BASE_SOLIDARIDAD_%"))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2025Date))
			) >= 3;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			// II
			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, DOMAIN)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.EXPRESSION.eq("REMOVE()"))
			.and(SYSTEM_DATA.NAME.eq("BASE_SOLIDARIDAD_II"))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2025Date))
			.execute()
			;
			
			// III
			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, DOMAIN)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.EXPRESSION.eq("REMOVE()"))
			.and(SYSTEM_DATA.NAME.eq("BASE_SOLIDARIDAD_III"))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2025Date))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
