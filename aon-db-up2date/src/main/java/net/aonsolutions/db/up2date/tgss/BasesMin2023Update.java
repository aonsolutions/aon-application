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

public class BasesMin2023Update implements Update {

	public static BasesMin2023Update BASESMIN2023UPDATE = new BasesMin2023Update();

	private static final int DOMAIN = 0;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
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
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
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

		UpdateConditionStep<SystemDataRecord> close2022BasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2022Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2023ProvisionalBases = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, PROVISIONAL_BASES)
		.set(SYSTEM_DATA.EXPRESSION, "FALSO()")
		.set(SYSTEM_DATA.START_DATE, start2023Date)
		.set(SYSTEM_DATA.COMMENTS, "Delete as soon as possible :-)")
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2023BasesMin = 
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
		DSL.replace(SYSTEM_DATA.EXPRESSION
		, "1166.70", "("+PROVISIONAL_BASES+" ? 1260.00 : 1166.70)")	// 4,5,6,7
		,   "38.89", "("+PROVISIONAL_BASES+" ? 42.00 : 38.89)")		// 8,9,10,11
		,	 
		DSL.date(start2023Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date)))
		;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2022BasesMin.execute();
			
			insert2023BasesMin.execute();
			insert2023ProvisionalBases.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
