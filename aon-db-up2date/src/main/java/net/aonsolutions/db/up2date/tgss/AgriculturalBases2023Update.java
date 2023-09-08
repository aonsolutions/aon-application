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

public class AgriculturalBases2023Update implements Update {

	public static AgriculturalBases2023Update AGRICULTURALBASES2023UPDATE = new AgriculturalBases2023Update();

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
		calendar.set(Calendar.YEAR, 2023);
		
		Date start2023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2022);
		Date end2022Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		Date start2022Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2022BasesCgcMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2022Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES, BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;
		
		UpdateConditionStep<SystemDataRecord> close2022BasesCgcMax =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2022Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES, BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;

		UpdateConditionStep<SystemDataRecord> close2022BasesCgpMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2022Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_MES, BASE_CGP_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2023BaseCgcMinMes = 
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
		, "1629.30", "1759.50") 	// 1 
		, "1351.20", "1459.20")		// 2
		, "1175.40", "1269.30")		// 3
		, "1166.70", "1260.00")		// 4,5,6,7,8,9,10,11
		,	 
		DSL.date(start2023Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2023BaseCgcMaxMes = 
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
		, "4139.40", "4495.50") 	
		,	 
		DSL.date(start2023Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date)))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2023BaseCgcMinDia = 
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
		, "70.84", "76.50") 		// 1 
		, "58.75", "63.44")		// 2
		, "51.10", "55.19")		// 3
		, "50.73", "54.78")		// 4,5,6,7,8,9,10,11
		,	 
		DSL.date(start2023Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2023BaseCgcMaxDia = 
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
		, "176.96", "195.46") 	
		,	 
		DSL.date(start2023Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date)))
		;


		InsertOnDuplicateStep<SystemDataRecord> insert2023BasesCgpMin = 
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
		, "1166.70", "1260.00")		//
		, "7.03", "7.59") 		//  
		,	 
		DSL.date(start2023Date),
		DSL.castNull(SYSTEM_DATA.END_DATE)
		)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN_DIA, BASE_CGP_MIN_MES))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date)))
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2022BasesCgcMin.execute();
			close2022BasesCgcMax.execute();
			close2022BasesCgpMin.execute();
			
			insert2023BaseCgcMinMes.execute();
			insert2023BaseCgcMaxMes.execute();
			insert2023BaseCgcMinDia.execute();
			insert2023BaseCgcMaxDia.execute();
			insert2023BasesCgpMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
