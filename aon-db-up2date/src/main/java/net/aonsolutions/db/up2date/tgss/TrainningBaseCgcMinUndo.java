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

public class TrainningBaseCgcMinUndo implements Update {

	public static TrainningBaseCgcMinUndo TRAINNINGBASECGCMINUNDO = new TrainningBaseCgcMinUndo();

	private static final int DOMAIN = -101;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	
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
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);
		Date start2023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2023);
		Date startJune2023Date = new Date(calendar.getTimeInMillis());


		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(startJune2023Date))) == 1;

		if ( upgraded ) 
			return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
				dslContext
				.insertInto(SYSTEM_DATA, 
				SYSTEM_DATA.DOMAIN,
				SYSTEM_DATA.NAME,
				SYSTEM_DATA.EXPRESSION,
				SYSTEM_DATA.START_DATE,
				SYSTEM_DATA.END_DATE
				)
				.select(
				DSL.select(
				DSL.val(DOMAIN),
				SYSTEM_DATA.NAME,
				DSL.replace(SYSTEM_DATA.EXPRESSION, "* COEFICIENTE_PARCIALIDAD", ""),	 
				DSL.val(startJune2023Date),
				SYSTEM_DATA.END_DATE
				)
				.from(SYSTEM_DATA)
				.where(SYSTEM_DATA.DOMAIN.eq(0))
				.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN))
				.and(SYSTEM_DATA.START_DATE.eq(start2023Date)))
				.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
