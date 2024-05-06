package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
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

public class Art1512024Update implements Update {

	private static final String CGC_E_TEMP = "CGC_E_TEMP";
	public static Art1512024Update ART1512024UPDATE = new Art1512024Update();

	
	private Art1512024Update() {
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.set(Calendar.YEAR, 2023);		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		Date start2023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2023);		
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date end2023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2024);		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		Date start2024Date = new Date(calendar.getTimeInMillis());
		
		boolean upgraded = dslContext.fetchCount(
		dslContext.select().from(SYSTEM_COST)
		.where(SYSTEM_COST.DOMAIN.eq(0))
		.and(SYSTEM_COST.CODE.eq(CGC_E_TEMP))
		.and(SYSTEM_COST.START_DATE.eq(start2024Date))
		) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// Close 2023 CGC_E_TEMP
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2023Date )
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq(CGC_E_TEMP))
			.and(SYSTEM_COST.END_DATE.isNull())
			.and(SYSTEM_COST.START_DATE.le(start2023Date))
			.execute()
			;
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)0)
			.set(SYSTEM_COST.CODE, CGC_E_TEMP)
			.set(SYSTEM_COST.START_DATE, start2024Date)
			.set(SYSTEM_COST.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_COST.DESCRIPTION, "ART. 151. COT. ADICIONAL CONTRATOS CORTA DURACI\u00F3N")
			.set(SYSTEM_COST.EXPRESSION, "(ART_151_CORTA_DURACION && FIN == FIN_CONTRATO)? 31.22 :HIDE()"
			)
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
