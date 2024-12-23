package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FPPercentage2024Fix implements Update {

	public static final FPPercentage2024Fix FPPERCENTAGE2024FIX = new FPPercentage2024Fix();

	private FPPercentage2024Fix() {
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
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2023);
		Date end2023Date = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_FP"))
			.and(SYSTEM_DATA.END_DATE.isNull())) > 0;


		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// Close old PORCENTAJE_FP
			// UPDATE system_data 
			// SET end_date = NULL 
			// WHERE name = 'PORCENTAJE_FP' 
			// AND end_date = '2023-12-31' 
			// AND domain <> -101;
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
			.where(SYSTEM_DATA.DOMAIN.ne(-101))
			.and(SYSTEM_DATA.END_DATE.eq(end2023Date))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_FP"))
			.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
