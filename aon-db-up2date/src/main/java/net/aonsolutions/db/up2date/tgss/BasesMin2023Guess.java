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

public class BasesMin2023Guess implements Update {

	public static BasesMin2023Guess BASESMIN2023GUESS = new BasesMin2023Guess();

	private static final int DOMAIN = 0;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_CGC_MIN_HOUR = "BASE_CGC_MIN_HORA";
	private static final String PROVISIONAL_BASES = "BASES_PROVISONALES";
	
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
		Date start2023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_HOUR))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))) > 0;

		if ( upgraded ) 
			return;		
		
		// Close 2022  
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		Date start2022Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date end2022Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2022BasesMinHour =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2022Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_HOUR))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2023BasesMinHour = 
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
		DSL.replace(SYSTEM_DATA.EXPRESSION
		,   "7.03", "("+PROVISIONAL_BASES+" ? 7.60 : 7.03)")		// 8,9,10,11
		,   "7.08", "("+PROVISIONAL_BASES+" ? 7.65 : 7.08)")		// 3
		,   "8.14", "("+PROVISIONAL_BASES+" ? 8.80 : 8.14)")		// 2
		,   "9.82", "("+PROVISIONAL_BASES+" ? 10.61 : 9.82)")		// 1
		,	 
		DSL.date(start2023Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_HOUR))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date)))
		;

		UpdateConditionStep<SystemDataRecord> update2023BasesMin = dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION,
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 

		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace(SYSTEM_DATA.EXPRESSION
		
		,   "7.03", "("+PROVISIONAL_BASES+" ? 7.60 : 7.03)")		// 8,9,10,11
		,   "7.08", "("+PROVISIONAL_BASES+" ? 7.65 : 7.08)")		// 3
		,   "8.14", "("+PROVISIONAL_BASES+" ? 8.80 : 8.14)")		// 2
		,   "9.82", "("+PROVISIONAL_BASES+" ? 10.61 : 9.82)")		// 1
		
		,   "1175.40", "("+PROVISIONAL_BASES+" ? 1269.40 : 1175.40)")	// 3
		,   "1351.20", "("+PROVISIONAL_BASES+" ? 1459.40 : 1351.20)")	// 2
		,   "1629.30", "("+PROVISIONAL_BASES+" ? 1759.60 : 1629.30)")	// 1
		)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))
		;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			update2023BasesMin.execute();
			close2022BasesMinHour.execute();
			insert2023BasesMinHour.execute();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
