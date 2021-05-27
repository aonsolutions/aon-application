package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

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

import com.esferalia.aon.jooq.tables.SystemDeduction;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class Bases2019Update implements Update {

	public static Bases2019Update BASES2019UPDATE = new Bases2019Update();

	private static final String SMI = "SMI";
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_CGC_MAX = "BASE_CGC_MAX";
	private static final String BASE_CGP_MAX = "BASE_CGP_MAX";
	private static final String BASE_CGC_MIN_DAY = "BASE_CGC_MIN_DIA";
	private static final String BASE_CGC_MIN_MONTH = "BASE_CGC_MIN_MES";
	private static final String BASE_CGC_MAX_DAY = "BASE_CGC_MAX_DIA";
	private static final String BASE_CGC_MAX_MONTH = "BASE_CGC_MAX_MES";
	private static final String REDUCCION_CGC_E_02 = "REDUCCION_CGC_E_02";
	private static final String PORCENTAJE_CGC_E = "PORCENTAJE_CGC_E";
	
	private Bases2019Update() {
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
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);
		
		Date _2019StartDate = new Date(calendar.getTimeInMillis());


		calendar.add(Calendar.YEAR, -1);
		Date _2018StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date _2018EndDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;
		
		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MAX))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;
		
		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MAX))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_DAY))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_MONTH))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX_DAY))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MAX_MONTH))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(REDUCCION_CGC_E_02))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_CGC_E))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(SMI))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;


		if ( upgraded ) 
			return;

		// CLEAN OLD 2019
		DeleteConditionStep<SystemDataRecord> deleteGeneral = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));
		
		DeleteConditionStep<SystemDataRecord> deleteHome = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));

		DeleteConditionStep<SystemDataRecord> deleteAgrarian = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-107))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DAY, BASE_CGC_MIN_MONTH, BASE_CGC_MAX_DAY, BASE_CGC_MAX_MONTH, REDUCCION_CGC_E_02, PORCENTAJE_CGC_E))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));
		
		DeleteConditionStep<SystemDataRecord> deleteSMI = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SMI))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));

		// CLOSE 2018
		UpdateConditionStep<SystemDataRecord> updateGeneral = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2018EndDate)
		.where(SYSTEM_DATA.DOMAIN.in(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))
		;
		
		UpdateConditionStep<SystemDataRecord> updateHome = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2018EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))
		;

		UpdateConditionStep<SystemDataRecord> updateAgrarian = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2018EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(-107))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DAY, BASE_CGC_MIN_MONTH, BASE_CGC_MAX_DAY, BASE_CGC_MAX_MONTH, REDUCCION_CGC_E_02))
		.and(SYSTEM_DATA.END_DATE.isNull())
		;

		UpdateConditionStep<SystemDataRecord> updateSMI = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2018EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SMI))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))
		;

		// DOMAIN = 0 , GENERAL
		InsertSetMoreStep<SystemDataRecord> insertGeneral = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "ROUND([ "
		+"\"01\":(TIEMPO_COMPLETO ? 1199.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 7.22 * HORAS_NOMINA) ,"
		+"\"02\":(TIEMPO_COMPLETO ?  994.20 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.99 * HORAS_NOMINA) ,"
		+"\"03\":(TIEMPO_COMPLETO ?  864.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.21 * HORAS_NOMINA) ,"
		+"\"04\":(TIEMPO_COMPLETO ?  858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.17 * HORAS_NOMINA) ,"
		+"\"05\":(TIEMPO_COMPLETO ?  858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.17 * HORAS_NOMINA) ,"
		+"\"06\":(TIEMPO_COMPLETO ?  858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.17 * HORAS_NOMINA) ,"
		+"\"07\":(TIEMPO_COMPLETO ?  858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.17 * HORAS_NOMINA) ,"
		+"\"08\":(TIEMPO_COMPLETO ?   28.62 * DIAS_NOMINA : 5.17 * HORAS_NOMINA) ,"
		+"\"09\":(TIEMPO_COMPLETO ?   28.62 * DIAS_NOMINA : 5.17 * HORAS_NOMINA) ,"
		+"\"10\":(TIEMPO_COMPLETO ?   28.62 * DIAS_NOMINA : 5.17 * HORAS_NOMINA) ,"
		+"\"11\":(TIEMPO_COMPLETO ?   28.62 * DIAS_NOMINA : 5.17 * HORAS_NOMINA)] [GRUPO_COTIZACION] * 1.223, 2)" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "ROUND((TIEMPO_COMPLETO ? 858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.17 * HORAS_NOMINA) * 1.223, 2)" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "[ "
		+"\"01\":(4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"02\":(4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"03\":(4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"04\":(4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"05\":(4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"06\":(4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"07\":(4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)) ,"
		+"\"08\":(135.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) ,"
		+"\"09\":(135.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) ,"
		+"\"10\":(135.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA)) ,"
		+"\"11\":(135.67 * (DIAS_NOMINA > 30 ? 30 : DIAS_NOMINA))] [GRUPO_COTIZACION]"
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MAX)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "4070.10 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		// DOMAIN = -106 , EMPLEADAS DE HOGAR
		InsertSetMoreStep<SystemDataRecord> insertHome = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "($ in [ "
		+"[240.00,206.00], "
		+"[375.00,340.00], "
		+"[510.00,474.00], "
		+"[645.00,608.00], "
		+"[780.00,743.00], "
		+"[914.00,877.00], "
		+"[1050.00,1050.00], "
		+"[1144.00,1097.00], "
		+"[1294.00,1232.00], "
		+"[Double.MAX_VALUE,BASE_CGC_BRUTA] ] if $[0] >= BASE_CGC_BRUTA )[0][1]" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "($ in [ "
		+"[240.00,206.00], "
		+"[375.00,340.00], "
		+"[510.00,474.00], "
		+"[645.00,608.00], "
		+"[780.00,743.00], "
		+"[914.00,877.00], "
		+"[1050.00,1050.00], "
		+"[1144.00,1097.00], "
		+"[1294.00,1232.00], "
		+"[Double.MAX_VALUE,BASE_CGC_BRUTA] ] if $[0] >= BASE_CGC_BRUTA )[0][1]" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "($ in [ "
		+"[240.00,206.00], "
		+"[375.00,340.00], "
		+"[510.00,474.00], "
		+"[645.00,608.00], "
		+"[780.00,743.00], "
		+"[914.00,877.00], "
		+"[1050.00,1050.00], "
		+"[1144.00,1097.00], "
		+"[1294.00,1232.00], "
		+"[Double.MAX_VALUE,BASE_CGC_BRUTA] ] if $[0] >= BASE_CGC_BRUTA )[0][1]" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MAX)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "($ in [ "
		+"[240.00,206.00], "
		+"[375.00,340.00], "
		+"[510.00,474.00], "
		+"[645.00,608.00], "
		+"[780.00,743.00], "
		+"[914.00,877.00], "
		+"[1050.00,1050.00], "
		+"[1144.00,1097.00], "
		+"[1294.00,1232.00], "
		+"[Double.MAX_VALUE,BASE_CGC_BRUTA] ] if $[0] >= BASE_CGC_BRUTA )[0][1]" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		
		// DOMAIN = -107, AGRARIO
		InsertSetMoreStep<SystemDataRecord> insertAgrarian = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN_DAY)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "[ "
		+"\"01\":63.76, "
		+"\"02\":52.87, "
		+"\"03\":45.99, "
		+"\"04\":45.65, "
		+"\"05\":45.65, "
		+"\"06\":45.65, "
		+"\"07\":45.65, "
		+"\"08\":45.65, "
		+"\"09\":45.65, "
		+"\"10\":45.65, "
		+"\"11\":45.65][GRUPO_COTIZACION]  * JORNADAS_REALES " )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN_MONTH)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "ROUND([ "
		+"\"01\":1199.10,"
		+"\"02\":994.20 ,"
		+"\"03\":864.90 ,"
		+"\"04\":858.60 ,"
		+"\"05\":858.60 ,"
		+"\"06\":858.60 ,"
		+"\"07\":858.60 ,"
		+"\"08\":858.60 ,"
		+"\"09\":858.60 ,"
		+"\"10\":858.60 ,"
		+"\"11\":858.60][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 ) * 1.223, 2)" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX_DAY)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "176.96 * JORNADAS_REALES")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX_MONTH)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "4070.10 * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, REDUCCION_CGC_E_02)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION,
		"COTIZACION_MENSUAL ? "
		+ "((BASE_CGC <= 986.70) ? 7.20 : ((BASE_CGC <= 3803.70) ? (7.20 * ( 1 + (BASE_CGC - 986.70)/ BASE_CGC * 2.52 * 6.15 / 7.20)) : 0.00))"
		+ " : ((BASE_CGC / JORNADAS_REALES <= 42.90) ? 7.20 "
		+ " : ((BASE_CGC / JORNADAS_REALES <= 176.96)? (7.20 * ( 1 + (BASE_CGC / JORNADAS_REALES - 42.90) / ( BASE_CGC / JORNADAS_REALES ) * 2.52 * 6.15 / 7.20)) : 0.00)"
		+ ")"
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_CGC_E)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "(GRUPO_COTIZACION == \"01\") ? (23.60 - REDUCCION_CGC_E_01) : (19.10 - REDUCCION_CGC_E_02)")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		// SMI
		InsertSetMoreStep<SystemDataRecord> insertSMI = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, SMI)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "900.00" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		

		// WARNING
		InsertSetMoreStep<SystemDataRecord> insertCgcMinWarningMsg = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, "BASE_CGC_MIN_WARN")
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.EXPRESSION,  
		"\"<div>Base m&iacute;nima provisional.</div>"
		+"<div>Incrementada en el porcentaje experimentado para el año 2019 por el Salario M&iacute;nimo.</div>"
		+"<div>En el entorno de un 22 por ciento.</div>"
		+"<div>&nbsp;</div>"
		+"<div class='aon-text-right'>Disculpe las molestias, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\""
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		InsertSetMoreStep<SystemDeductionRecord> insertCgcMinWarningDeduction = 
		dslContext
		.insertInto(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.DOMAIN, 0)
		.set(SYSTEM_DEDUCTION.END_DATE, (Date) null)
		.set(SYSTEM_DEDUCTION.START_DATE, _2019StartDate)
		.set(SYSTEM_DEDUCTION.EXPRESSION,
		"( BASE_CGC_BRUTA < BASE_CGC ) ? HIDE(BASE_CGC_MIN_WARN) : HIDE();"
		)
		
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			deleteGeneral.execute();
			deleteHome.execute();
			deleteAgrarian.execute();
			deleteSMI.execute();
			
			updateGeneral.execute();
			updateHome.execute();
			updateAgrarian.execute();
			updateSMI.execute();
			
			insertGeneral.execute();
			insertHome.execute();
			insertAgrarian.execute();
			insertSMI.execute();
			
			insertCgcMinWarningMsg.execute();
			insertCgcMinWarningDeduction.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
