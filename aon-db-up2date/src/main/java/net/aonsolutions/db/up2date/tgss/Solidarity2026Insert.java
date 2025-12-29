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

public class Solidarity2026Insert implements Update {
	
	private static final String PORCENTAJE_SOLIDARIDAD_I = "PORCENTAJE_SOLIDARIDAD_I";
	private static final String PORCENTAJE_SOLIDARIDAD_II = "PORCENTAJE_SOLIDARIDAD_II";
	private static final String PORCENTAJE_SOLIDARIDAD_III = "PORCENTAJE_SOLIDARIDAD_III";
	
	private static final String PORCENTAJE_SOLIDARIDAD_I_E = "PORCENTAJE_SOLIDARIDAD_I_E";
	private static final String PORCENTAJE_SOLIDARIDAD_II_E = "PORCENTAJE_SOLIDARIDAD_II_E";
	private static final String PORCENTAJE_SOLIDARIDAD_III_E = "PORCENTAJE_SOLIDARIDAD_III_E";

	public static final Solidarity2026Insert SOLIDARITY2026INSERT = new Solidarity2026Insert();


	private Solidarity2026Insert() {
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
		calendar.set(Calendar.YEAR, 2026);
		
		Date startOf2026Date = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.YEAR, 2025);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date endOf2025Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2025);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		Date startOf2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.and(SYSTEM_DATA.NAME.in(
					PORCENTAJE_SOLIDARIDAD_I,
					PORCENTAJE_SOLIDARIDAD_II,
					PORCENTAJE_SOLIDARIDAD_III,
					PORCENTAJE_SOLIDARIDAD_I_E,
					PORCENTAJE_SOLIDARIDAD_II_E,
					PORCENTAJE_SOLIDARIDAD_III_E))
			) >= 1;

		// IF ALREADY EXISTS
		if ( upgraded ) 
			return;
		
		
		
		dslContext.transaction( config -> {
			
			// DISABLED FOREING_KEY FOR INSERT
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// CLOSE 2025 
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, endOf2025Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2025Date))
			.and(SYSTEM_DATA.NAME.in(
					PORCENTAJE_SOLIDARIDAD_I,
					PORCENTAJE_SOLIDARIDAD_II,
					PORCENTAJE_SOLIDARIDAD_III,
					PORCENTAJE_SOLIDARIDAD_I_E,
					PORCENTAJE_SOLIDARIDAD_II_E,
					PORCENTAJE_SOLIDARIDAD_III_E))
			.execute()
			;
			
			// INSERT 2026
			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_SOLIDARIDAD_I)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.19" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_SOLIDARIDAD_I_E)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.96" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_SOLIDARIDAD_II)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.21" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_SOLIDARIDAD_II_E)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "1.04" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_SOLIDARIDAD_III)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.24" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_SOLIDARIDAD_III_E)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "1.22" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
