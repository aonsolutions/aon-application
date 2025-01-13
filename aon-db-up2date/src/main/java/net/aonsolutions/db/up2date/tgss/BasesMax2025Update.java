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

public class BasesMax2025Update implements Update {

	public static BasesMax2025Update BASESMAX2025UPDATE = new BasesMax2025Update();

	private static final int DOMAIN = 0;
	private static final String BASE_CGC_MAX = "BASE_CGC_MAX";
	private static final String BASE_CGP_MAX = "BASE_CGP_MAX";
	
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
		calendar.set(Calendar.YEAR, 2025);
		Date start2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date))) > 0;

		if ( upgraded ) 
			return;		
		
		// Close 2024
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2024);
		Date start2024Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2024);
		Date end2024Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2024BasesMax =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2024Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2025BasesMax = 
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
		, "4720.50","4909.50")		// 1,2,3,4,5,6,7
		, "157.35" , "163.65")		// 8,9,10,11
		,	 
		DSL.date(start2025Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date)))
		;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2024BasesMax.execute();
			insert2025BasesMax.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
