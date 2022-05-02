package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.HolidayDetail.HOLIDAY_DETAIL;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class Holidays2022Update implements Update {

	public static final Holidays2022Update HOLIDAYS2022UPDATE = new Holidays2022Update();
	
	private Holidays2022Update() {
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
		calendar.set(Calendar.DAY_OF_MONTH, 26);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.YEAR, 2022);

		Date wrongDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 25);
		Date newDate = new Date(calendar.getTimeInMillis());
		String description = "Santiago";
		
		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
				.select()
				.from(HOLIDAY_DETAIL)
				.where(HOLIDAY_DETAIL.DOMAIN.eq(0))
				.and(HOLIDAY_DETAIL.DESCRIPTION.eq(description))
				.and(HOLIDAY_DETAIL.DATE.eq(new java.sql.Date(wrongDate.getTime())))
				.fetchOptional(HOLIDAY_DETAIL.ID)
				.ifPresent(holidayDatailId -> 
					dslContext.update(HOLIDAY_DETAIL)
						.set(HOLIDAY_DETAIL.DATE, new java.sql.Date(newDate.getTime()))
						.where(HOLIDAY_DETAIL.ID.eq(holidayDatailId))
						.execute()
				);
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

}
