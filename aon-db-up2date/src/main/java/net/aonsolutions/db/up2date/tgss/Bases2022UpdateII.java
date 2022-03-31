package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Bases2022UpdateII implements Update {

	public static Bases2022UpdateII BASES2022UPDATEIII = new Bases2022UpdateII();

	private static final int DOMAIN = 0;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
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

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date startSeptember2021Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date end2021Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2021BasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2021Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2022Bases = 
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
		SYSTEM_DATA.DOMAIN,
		SYSTEM_DATA.NAME,
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace(SYSTEM_DATA.EXPRESSION
		, "1572.30", "1629.30") 	// 1 
		, "1303.80", "1351.20")		// 2
		, "1134.30", "1175.40")		// 3
		, "1125.90", "1166.70")		// 4,5,6,7
		,   "37.53",   "38.89")		// 8,9,10,11

		, "9.47", "9.82") 			// 1 
		, "7.85", "8.14") 			// 2 
		, "6.83", "7.08") 			// 3 
		, "6.78", "7.03") 			// 4,5,6,7,8,9,10,11 
		,	 
		DSL.date(start2022Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date)))
		;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2021BasesMin.execute();
			
			insert2022Bases.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
