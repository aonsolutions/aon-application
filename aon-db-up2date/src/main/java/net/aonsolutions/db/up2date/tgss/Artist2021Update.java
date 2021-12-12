package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep7;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Artist2021Update implements Update {
	
	public static Artist2021Update ARTIST2019INSERT = new Artist2021Update();

	private Artist2021Update() {
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
		
		// START_DATE 01/01/2019
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);
		
		Date start2019Date = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date startSeptember2021Date = new Date(calendar.getTimeInMillis());


		boolean upgraded = dslContext.fetchCount(
				dslContext.select().from(SYSTEM_DATA)
					.where(SYSTEM_DATA.DOMAIN.eq(-108))
					.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date))
				) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		// DOMAIN = -109, ARTIST
		
		InsertValuesStep7<SystemDataRecord, Integer, String, String, Date, Date, Byte, String> insertArtist =
				
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
				.values(-108, "BASE_CGP_MIN", "BASE_CGC_MIN", start2019Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGP_MAX", "BASE_CGC_MAX", start2019Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MIN", "[\"01\":48.88, \"02\":40.53, \"03\":35.26, \"05\":35.00, \"07\":35.00][GRUPO_COTIZACION] * DIAS_NOMINA", start2019Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MAX_DIA", "(($ in [ [461.00,270.00], [829.00,341.00], [1386.00,407.00], [Double.MAX_VALUE,542.00] ] if $[0] >= BASE_CGC_BRUTA/DIAS_NOMINA)[0][1]) * DIAS_NOMINA", start2019Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MAX_MES", "MAX(4070.10 - SUM(\"BASE_CGC\"), 0)", start2019Date, (Date) null, (byte) 1, (String) null)
				.values(-108, "BASE_CGC_MAX", "MIN(BASE_CGC_MAX_DIA , BASE_CGC_MAX_MES)", start2019Date, (Date) null, (byte) 1, (String) null)
			;
		
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insertArtist.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
