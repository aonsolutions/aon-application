package net.aonsolutions.db.up2date.payroll;

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

public class MaxEmbargable4DailyFix implements Update {

	public static final MaxEmbargable4DailyFix MAXEMBARGABLE4DAILYFIX = new MaxEmbargable4DailyFix();
	
	private MaxEmbargable4DailyFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("MAX_EMBARGABLE_DIA"))
		.and(SYSTEM_DATA.EXPRESSION.like("%DIAS_NOMINA%"))
		) > 0;

		if ( upgraded ) 
			return;		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.YEAR, 2000);
			
			Date start2000Date = new Date(calendar.getTimeInMillis());
			
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0 )
			.set(SYSTEM_DATA.NAME, "MAX_EMBARGABLE" )
			.set(SYSTEM_DATA.COMMENTS, "Máximo embargable" )
			.set(SYSTEM_DATA.EXPRESSION, "def(total_liquido){ "
					+ " MODALIDAD_MENSUAL ? "
					+ " MAX_EMBARGABLE_MES(total_liquido) : "
					+ " MAX_EMBARGABLE_DIA(total_liquido / DIAS_NOMINA ) * DIAS_NOMINA "
					+ "}")
			.set(SYSTEM_DATA.START_DATE,  start2000Date )
			.set(SYSTEM_DATA.READ_ONLY, (byte) 0 )
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}
}
