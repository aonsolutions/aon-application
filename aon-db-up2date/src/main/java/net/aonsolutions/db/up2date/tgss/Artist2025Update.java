package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep7;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Artist2025Update implements Update {
	
	public static Artist2025Update ARTIST2025UPDATE = new Artist2025Update();

	private Artist2025Update() {
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
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		// START_DATE 01/01/2025
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);
		
		Date start2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = dslContext.fetchCount(
				dslContext.select().from(SYSTEM_DATA)
					.where(SYSTEM_DATA.DOMAIN.eq(-108))
					.and(SYSTEM_DATA.START_DATE.eq(start2025Date))
				) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DOMAIN = -108, ARTIST
		
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2024);
		Date end2024Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2024BasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2024Date)
		.where(SYSTEM_DATA.DOMAIN.eq(-108))
		.and(SYSTEM_DATA.NAME.in("BASE_CGC_MIN", "BASE_CGC_MAX_DIA", "BASE_CGC_MAX_MES"))
		.and(SYSTEM_DATA.END_DATE.isNull())
		;

		InsertValuesStep7<SystemDataRecord, Integer, String, String, Date, Date, Byte, String> insert2025Artist =
			dslContext.insertInto(
				SYSTEM_DATA, 
				SYSTEM_DATA.DOMAIN, 
				SYSTEM_DATA.NAME, 
				SYSTEM_DATA.
				EXPRESSION, 
				SYSTEM_DATA.START_DATE, 
				SYSTEM_DATA.END_DATE, 
				SYSTEM_DATA.READ_ONLY, 
				SYSTEM_DATA.COMMENTS)
				.values(-108, "BASE_CGC_MIN", "["
						+ "\"01\":64.30, "
						+ "\"02\":53.32, "
						+ "\"03\":46.39, "
						+ "\"05\":46.04, "
						+ "\"07\":46.04][GRUPO_COTIZACION] * DIAS_NOMINA", start2025Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MAX_DIA", "(($ in [ "
						+ "[555.00,327.00], "
						+ "[999.00,412.00], "
						+ "[1672.00,492.00], "
						+ "[Double.MAX_VALUE,653.10] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA", start2025Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MAX_MES", "MAX(4909.50 - SUM(\"BASE_CGC\"), 0)", start2025Date, (Date) null, (byte) 1, (String) null)
			;
		
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2024BasesMin.execute();
			insert2025Artist.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
