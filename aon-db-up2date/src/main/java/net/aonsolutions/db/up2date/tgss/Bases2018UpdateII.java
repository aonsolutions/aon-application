package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Bases2018UpdateII implements Update {

	public static Bases2018UpdateII BASES2018UPDATEII = new Bases2018UpdateII();

	private static final String BASE_CGC_MAX = "BASE_CGC_MAX";
	private static final String BASE_CGP_MAX = "BASE_CGP_MAX";
	private static final String BASE_CGC_MAX_DAY = "BASE_CGC_MAX_DIA";
	private static final String BASE_CGC_MAX_MONTH = "BASE_CGC_MAX_MES";
	private static final String REDUCCION_CGC_E_02 = "REDUCCION_CGC_E_02";
	
	private Bases2018UpdateII() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		
		// 2018/08/01
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.YEAR, 2018);
		
		
		Date _2018AugustStartDate = new Date(calendar.getTimeInMillis());


		calendar.set(Calendar.YEAR, 2017);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		Date _2017StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.YEAR, 2018);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		Date _2018JulyEndDate = new Date(calendar.getTimeInMillis());
		
		// General 
		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(_2018AugustStartDate))) == 1;
		
		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MAX))
			.and(SYSTEM_DATA.START_DATE.eq(_2018AugustStartDate))) == 1;
		
		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX_DAY))
			.and(SYSTEM_DATA.START_DATE.eq(_2018AugustStartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX_MONTH))
			.and(SYSTEM_DATA.START_DATE.eq(_2018AugustStartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(REDUCCION_CGC_E_02))
			.and(SYSTEM_DATA.START_DATE.eq(_2018AugustStartDate))) == 1;

		//if ( upgraded ) 
		//	return;

		// CLEAN OLD 2018
		DeleteConditionStep<SystemDataRecord> deleteGeneral = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(_2018AugustStartDate));
		
		DeleteConditionStep<SystemDataRecord> deleteAgrarian = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-107))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_DAY, BASE_CGC_MAX_MONTH, REDUCCION_CGC_E_02))
		.and(SYSTEM_DATA.START_DATE.eq(_2018AugustStartDate));
		
		// CLOSE 2017 
		UpdateConditionStep<SystemDataRecord> updateGeneral = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2018JulyEndDate)
		.where(SYSTEM_DATA.DOMAIN.in(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate));

		UpdateConditionStep<SystemDataRecord> updateAgrarian = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2018JulyEndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(-107))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MAX_DAY, BASE_CGC_MAX_MONTH, REDUCCION_CGC_E_02))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;

		// DOMAIN = 0 , GENERAL
		InsertSetMoreStep<SystemDataRecord> insertGeneral = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX)
		.set(SYSTEM_DATA.START_DATE, _2018AugustStartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "[ "
		+"\"01\":(3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"02\":(3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"03\":(3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"04\":(3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"05\":(3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"06\":(3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"07\":(3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"08\":(126.79 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) ,"
		+"\"09\":(126.79 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) ,"
		+"\"10\":(126.79 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) ,"
		+"\"11\":(126.79 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))] [GRUPO_COTIZACION]"
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MAX)
		.set(SYSTEM_DATA.START_DATE, _2018AugustStartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "3803.70 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		
		// DOMAIN = -107, AGRARIO
		InsertSetMoreStep<SystemDataRecord> insertAgrarian = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX_DAY)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018AugustStartDate)
		.set(SYSTEM_DATA.EXPRESSION, "165.36 * JORNADAS_REALES")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX_MONTH)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018AugustStartDate)
		.set(SYSTEM_DATA.EXPRESSION, "3803.70 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, REDUCCION_CGC_E_02)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018AugustStartDate)
		.set(SYSTEM_DATA.EXPRESSION,
		"COTIZACION_MENSUAL ? "
		+ "((BASE_CGC <= 986.70) ? 7.11 : ((BASE_CGC <= 3803.70) ? (7.11 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 7.11)) : 0.00))"
		+ " : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 7.11 "
		+ " : ((BASE_CGC / JORNADAS_REALES <= 165.36)? (7.11 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 7.11)) : 0.00)"
		+ ")"
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			deleteGeneral.execute();
			deleteAgrarian.execute();
			
			updateGeneral.execute();
			updateAgrarian.execute();
			
			insertGeneral.execute();
			insertAgrarian.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
