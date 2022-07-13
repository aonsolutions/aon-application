package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FellowsITIMS2022UpdateII implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final FellowsITIMS2022UpdateII FELLOWSITIMS2022UPDATEII = new FellowsITIMS2022UpdateII();

	private FellowsITIMS2022UpdateII() {
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

		Date start2022Date = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.eq("IT_E"))
			.and(SYSTEM_COST.START_DATE.eq(start2022Date))) > 0;


		if ( upgraded )
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date end2021Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");			

			// Close old  IT_E & IMS_E
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2021Date)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.in("IT_E", "IMS_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			dslContext
			.insertInto(SYSTEM_COST)
			// Insert new IT_E
			.set(SYSTEM_COST.DOMAIN, -105)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IT_E" )
			.set(SYSTEM_COST.START_DATE, start2022Date)
			.set(SYSTEM_COST.DESCRIPTION, "IT")
			.set(SYSTEM_COST.EXPRESSION, "3.64" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.newRecord()
			// Insert new IMS_E
			.set(SYSTEM_COST.DOMAIN, -105)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IMS_E" )
			.set(SYSTEM_COST.START_DATE, start2022Date)
			.set(SYSTEM_COST.DESCRIPTION, "IMS")
			.set(SYSTEM_COST.EXPRESSION, "2.87" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
