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

public class AgriculturalBases2024Update implements Update {

	public static AgriculturalBases2024Update AGRICULTURALBASES2024UPDATE = new AgriculturalBases2024Update();

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
		calendar.set(Calendar.YEAR, 2024);
		
		Date start2024Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2023);
		Date end2023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);
		Date start2023Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2023BasesCgcMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2023Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES, BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))
		;
		
		UpdateConditionStep<SystemDataRecord> close2023BasesCgcMax =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2023Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES, BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))
		;

		UpdateConditionStep<SystemDataRecord> close2023BasesCgpMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2023Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_MES, BASE_CGP_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2024BaseCgcMinMes = 
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
		, "1759.50", "1847.40")		// 1
		, "1459.20", "1532.10")		// 2
		, "1269.30", "1332.90")		// 3
		, "1260.00", "1323.00")		// 4,5,6,7,8,9,10,11
		,	 
		DSL.date(start2024Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2024BaseCgcMaxMes = 
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
		, "4495.50","4720.50")
		,	 
		DSL.date(start2024Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date)))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2024BaseCgcMinDia = 
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
		, "76.50", "80.32") 	// 1 
		, "63.44", "66.61")		// 2
		, "55.19", "57.95")		// 3
		, "54.78", "57.52")		// 4,5,6,7,8,9,10,11
		,	 
		DSL.date(start2024Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2024BaseCgcMaxDia = 
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
		, "195.46", "205.24") 	
		,	 
		DSL.date(start2024Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date)))
		;


		InsertOnDuplicateStep<SystemDataRecord> insert2024BasesCgpMin = 
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
		, "1260.00", "1323.00")	//
		, "7.59", "7.97") 		//  
		,	 
		DSL.date(start2024Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_DIA, BASE_CGP_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date)))
		;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2023BasesCgcMin.execute();
			close2023BasesCgcMax.execute();
			close2023BasesCgpMin.execute();
			
			insert2024BaseCgcMinMes.execute();
			insert2024BaseCgcMaxMes.execute();
			insert2024BaseCgcMinDia.execute();
			insert2024BaseCgcMaxDia.execute();
			insert2024BasesCgpMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
