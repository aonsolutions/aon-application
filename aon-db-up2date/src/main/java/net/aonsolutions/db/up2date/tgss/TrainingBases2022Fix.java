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

public class TrainingBases2022Fix implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final TrainingBases2022Fix TRAININGBASES2022FIX = new TrainingBases2022Fix();
	
	private TrainingBases2022Fix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date startDate = new Date(calendar.getTimeInMillis());
		
		boolean upgraded = dslContext.fetchCount(
				DSL
				.select()
				.from(SYSTEM_DATA)
				.where(SYSTEM_DATA.DOMAIN.in(-101, -105))
				.and(SYSTEM_DATA.START_DATE.eq(startDate))
				.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
				.and(SYSTEM_DATA.EXPRESSION.eq("1166.70"))
				) == 2 ;
				
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-101, -105))
			.and(SYSTEM_DATA.START_DATE.eq(startDate))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
			.execute()
			;
			
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN,-101)
			.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "1166.70")
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN,-105)
			.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
			.set(SYSTEM_DATA.START_DATE, startDate)
			.set(SYSTEM_DATA.EXPRESSION, "1166.70")
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

}
