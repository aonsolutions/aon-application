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

public class TrainningBases2024Update implements Update {

	public static final TrainningBases2024Update TRAINNINGBASES2024UPDATE = new TrainningBases2024Update();

	private static final int DOMAIN = -101;
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
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);
		Date start2023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date end2023Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2023CgcBasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2023Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.ge(start2023Date))
		;
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		Date start2022Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date end2022Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2022CgpBasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2022Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		.and(SYSTEM_DATA.END_DATE.isNull())
		.and(SYSTEM_DATA.EXPRESSION.contains("1166.70"))
		;

		UpdateConditionStep<SystemDataRecord> close2023CgpBasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2023Date)
		.set(SYSTEM_DATA.START_DATE, start2023Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.ge(start2022Date))
		.and(SYSTEM_DATA.END_DATE.isNull())
		.and(SYSTEM_DATA.EXPRESSION.contains("1260.00"))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2024Bases = 
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
		, "1759.50", "1847.40")		// 1
		, "1459.20", "1532.10")		// 2
		, "1269.30", "1332.90")		// 3
		, "1260.00", "1323.00")		// 4,5,6,7
		,   "42.00", "44.10")		// 8,9,10,11

		, "10.60", "11.13")			// 1
		, "8.79", "9.23")			// 2
		, "7.65", "8.03")			// 3
		, "7.59", "7.97") 			// 4,5,6,7,8,9,10,11
		,	 
		DSL.date(start2024Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN, BASE_CGC_MIN))
		.and(SYSTEM_DATA.END_DATE.eq(end2023Date)))
		;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2022CgpBasesMin.execute();
			close2023CgpBasesMin.execute();
			close2023CgcBasesMin.execute();
			
			insert2024Bases.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
