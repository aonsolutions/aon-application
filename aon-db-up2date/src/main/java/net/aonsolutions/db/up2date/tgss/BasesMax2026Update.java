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

public class BasesMax2026Update implements Update {

	public static BasesMax2026Update BASESMAX2026UPDATE = new BasesMax2026Update();

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
		calendar.set(Calendar.YEAR, 2026);
		Date start2026Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(start2026Date))) > 0;

		if ( upgraded ) 
			return;		
		
		// Close 2025
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);
		Date start2025Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2025);
		Date end2025Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2025BasesMax =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2025Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2026BasesMax = 
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
		, "4909.50","5101.20")		// 1,2,3,4,5,6,7
		, "163.65" , "170.04")		// 8,9,10,11
		,	 
		DSL.date(start2026Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date)))
		;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2025BasesMax.execute();
			insert2026BasesMax.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
