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

public class AgriculturalBases2025Update implements Update {

	public static AgriculturalBases2025Update AGRICULTURALBASES2025UPDATE = new AgriculturalBases2025Update();

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
		calendar.set(Calendar.YEAR, 2025);
		
		Date start2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2024);
		Date end2024Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2024);
		Date start2024Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2024BasesCgcMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2024Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES, BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))
		;
		
		UpdateConditionStep<SystemDataRecord> close2024BasesCgcMax =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2024Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES, BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))
		;

		UpdateConditionStep<SystemDataRecord> close2024BasesCgpMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2024Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_MES, BASE_CGP_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2025BaseCgcMinMes = 
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
		, "1847.40",	"1929.00")		// 1
		, "1532.10", 	"1599.60")		// 2
		, "1332.90",	"1391.70")		// 3
		, "1323.00", 	"1381.20")		// 4 ... 11
		,	 
		DSL.date(start2025Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2025BaseCgcMaxMes = 
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
		, "4720.50","4909.50")
		,	 
		DSL.date(start2025Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date)))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2025BaseCgcMinDia = 
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
		, "80.32", "83.37") 	// 1 
		, "66.61", "69.55")		// 2
		, "57.95", "60.51")		// 3
		, "57.52", "60.05")		// 4,5,6,7,8,9,10,11
		,	 
		DSL.date(start2025Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2025BaseCgcMaxDia = 
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
		, "205.24", "213.46") 	
		,	 
		DSL.date(start2025Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date)))
		;


		InsertOnDuplicateStep<SystemDataRecord> insert2025BasesCgpMin = 
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
		, "1323.00", "1381.20")	//
		, "7.97", "8.32") 		//  
		,	 
		DSL.date(start2025Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_DIA, BASE_CGP_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date)))
		;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2024BasesCgcMin.execute();
			close2024BasesCgcMax.execute();
			close2024BasesCgpMin.execute();
			
			insert2025BaseCgcMinMes.execute();
			insert2025BaseCgcMaxMes.execute();
			insert2025BaseCgcMinDia.execute();
			insert2025BaseCgcMaxDia.execute();
			insert2025BasesCgpMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
