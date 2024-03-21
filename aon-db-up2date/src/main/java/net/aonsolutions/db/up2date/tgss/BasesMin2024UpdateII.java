package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class BasesMin2024UpdateII implements Update {

	public static BasesMin2024UpdateII BASESMIN2024UPDATEII = new BasesMin2024UpdateII();

	private static final int DOMAIN = 0;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
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
		calendar.set(Calendar.YEAR, 2024);
		Date start2024Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))
		.and(SYSTEM_DATA.EXPRESSION.contains("1847.40"))) > 0;

		if ( upgraded ) 
			return;		
		
		
		
		UpdateConditionStep<SystemDataRecord> update2024BasesMin = 
		dslContext.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace( 
		DSL.replace(SYSTEM_DATA.EXPRESSION
		, "1759.50", "("+PROVISIONAL_BASES+" ? 1847.40 : 1759.50)")		// 1
		, "1459.20", "("+PROVISIONAL_BASES+" ? 1532.10 : 1459.20)")		// 2
		, "1269.30", "("+PROVISIONAL_BASES+" ? 1332.90 : 1269.30)")		// 3

		, "10.60", "("+PROVISIONAL_BASES+" ? 11.13 : 10.60)")			// 1
		, "8.79", "("+PROVISIONAL_BASES+" ? 9.23 : 8.79)")				// 2
		, "7.65", "("+PROVISIONAL_BASES+" ? 8.03 : 7.65)")				// 3
		, "7.59", "("+PROVISIONAL_BASES+" ? 7.97 : 7.59)")				// 4 ... 11
		)
		.where(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))
		.and(SYSTEM_DATA.EXPRESSION.notContains("1847.40"));
		
		// DOMAIN = 0 , GENERAL
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			update2024BasesMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
