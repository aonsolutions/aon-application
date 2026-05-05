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

public class AgriculturalBases2026Update implements Update {

	public static final  AgriculturalBases2026Update AGRICULTURALBASES2026UPDATE = new AgriculturalBases2026Update();

	private static final int DOMAIN = -107;
	private static final String BASE_CGC_MIN_MES = "BASE_CGC_MIN_MES";
	private static final String BASE_CGC_MIN_DIA = "BASE_CGC_MIN_DIA";
	
	private static final String BASE_CGC_MAX_MES = "BASE_CGC_MAX_MES";
	private static final String BASE_CGC_MAX_DIA = "BASE_CGC_MAX_DIA";

	private static final String BASE_CGP_MIN_MES = "BASE_CGP_MIN_MES";
	private static final String BASE_CGP_MIN_DIA = "BASE_CGP_MIN_DIA";
	
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
		calendar.set(Calendar.YEAR, 2026);
		
		Date start2026Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2026Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2025);
		Date end2025Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);
		Date start2025Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2025BasesCgcMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2025Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES, BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date))
		;
		
		UpdateConditionStep<SystemDataRecord> close2025BasesCgcMax =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2025Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES, BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date))
		;

		UpdateConditionStep<SystemDataRecord> close2025BasesCgpMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2025Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_MES, BASE_CGP_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2026BaseCgcMinMes = 
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
		, "1929.00", "1989.30")		// 1
		, "1599.60", "1649.70")		// 2
		, "1391.70", "1435.20")		// 3
		, "1381.20", "1424.40")		// 4 ... 11
		,	 
		DSL.date(start2026Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2026BaseCgcMaxMes = 
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
		DSL.replace(SYSTEM_DATA.EXPRESSION
		, "4909.50", "5101.20")
		,	 
		DSL.date(start2026Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date)))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2026BaseCgcMinDia = 
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
		, "83.37", "96.49") 	// 1 
		, "69.55", "71.73")		// 2
		, "60.51", "62.40")		// 3
		, "60.05", "61.93")		// 4,5,6,7,8,9,10,11
		,	 
		DSL.date(start2026Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2026BaseCgcMaxDia = 
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
		DSL.replace(SYSTEM_DATA.EXPRESSION
		, "213.46", "221.79") 	
		,	 
		DSL.date(start2026Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date)))
		;


		InsertOnDuplicateStep<SystemDataRecord> insert2026BasesCgpMin = 
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
		, "1381.20", "1424.40"	)		//
		, 	 "8.32", 	"8.58"	) 		//  
		,	 
		DSL.date(start2026Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_DIA, BASE_CGP_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date)))
		;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2025BasesCgcMin.execute();
			close2025BasesCgcMax.execute();
			close2025BasesCgpMin.execute();
			
			insert2026BaseCgcMinMes.execute();
			insert2026BaseCgcMaxMes.execute();
			insert2026BaseCgcMinDia.execute();
			insert2026BaseCgcMaxDia.execute();
			insert2026BasesCgpMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
