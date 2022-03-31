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

public class Artist2022Update implements Update {
	
	public static Artist2022Update ARTIST2022UPDATE = new Artist2022Update();

	private Artist2022Update() {
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
		
		// START_DATE 01/01/2022
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date start2022Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = dslContext.fetchCount(
				dslContext.select().from(SYSTEM_DATA)
					.where(SYSTEM_DATA.DOMAIN.eq(-108))
					.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
				) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DOMAIN = -108, ARTIST
		
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date end2021Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2021BasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2021Date)
		.where(SYSTEM_DATA.DOMAIN.eq(-108))
		.and(SYSTEM_DATA.NAME.in("BASE_CGC_MIN", "BASE_CGC_MAX_DIA", "BASE_CGC_MAX_MES"))
		.and(SYSTEM_DATA.END_DATE.isNull())
		;

		InsertValuesStep7<SystemDataRecord, Integer, String, String, Date, Date, Byte, String> insert2022Artist =
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
						+ "\"01\":38.89, "
						+ "\"02\":38.89, "
						+ "\"03\":38.89, "
						+ "\"05\":38.89, "
						+ "\"07\":38.89.00][GRUPO_COTIZACION] * DIAS_NOMINA", start2022Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MAX_DIA", "(($ in [ "
						+ "[469.00,275.00], "
						+ "[843.00,347.00], "
						+ "[1410.00,414.00], "
						+ "[Double.MAX_VALUE,551.00] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA", start2022Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MAX_MES", "MAX(4139.40 - SUM(\"BASE_CGC\"), 0)", start2022Date, (Date) null, (byte) 1, (String) null)
			;
		
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2021BasesMin.execute();
			insert2022Artist.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
