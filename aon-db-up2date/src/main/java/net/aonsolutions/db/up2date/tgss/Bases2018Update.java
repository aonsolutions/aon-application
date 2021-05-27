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

public class Bases2018Update implements Update {

	public static Bases2018Update BASES2018UPDATE = new Bases2018Update();

	private static final String IPREM = "IPREM";
	private static final String SMI = "SMI";
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_CGC_MIN_DAY = "BASE_CGC_MIN_DIA";
	private static final String BASE_CGC_MIN_MONTH = "BASE_CGC_MIN_MES";
	
	private Bases2018Update() {
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
		calendar.set(Calendar.YEAR, 2018);
		
		Date _2018StartDate = new Date(calendar.getTimeInMillis());


		calendar.add(Calendar.YEAR, -1);
		Date _2017StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date _2017EndDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;
		
		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
			.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
			.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_DAY))
			.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.in(-107))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN_MONTH))
			.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(SMI))
			.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(IPREM))
			.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded ) 
			return;

		// CLEAN OLD 2018
		DeleteConditionStep<SystemDataRecord> deleteGeneral = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate));
		
		DeleteConditionStep<SystemDataRecord> deleteHome = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate));

		DeleteConditionStep<SystemDataRecord> deleteAgrarian = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-107))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DAY, BASE_CGC_MIN_MONTH))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate));
		
		DeleteConditionStep<SystemDataRecord> deleteSMI = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SMI))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate));
		
		DeleteConditionStep<SystemDataRecord> deleteIPREM = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(IPREM))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate));

		// CLOSE 2017
		UpdateConditionStep<SystemDataRecord> updateGeneral = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2017EndDate)
		.where(SYSTEM_DATA.DOMAIN.in(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;
		
		UpdateConditionStep<SystemDataRecord> updateHome = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2017EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;

		UpdateConditionStep<SystemDataRecord> updateAgrarian = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2017EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(-107))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN_DAY, BASE_CGC_MIN_MONTH))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;

		UpdateConditionStep<SystemDataRecord> updateSMI = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2017EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SMI))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;

		UpdateConditionStep<SystemDataRecord> updateIPREM = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2017EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(IPREM))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;

		// DOMAIN = 0 , GENERAL
		InsertSetMoreStep<SystemDataRecord> insertGeneral = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "[ "
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
		+"\"11\":(TIEMPO_COMPLETO ?   28.62 * DIAS_NOMINA : 5.17 * HORAS_NOMINA)] [GRUPO_COTIZACION]" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "TIEMPO_COMPLETO ? 858.60 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) : 5.17 * HORAS_NOMINA" 
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
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "($ in [ "
		+"[196.15,167.74], "
		+"[306.40,277.51], "
		+"[416.80,387.29], "
		+"[527.10,497.08], "
		+"[637.40,606.86], "
		+"[746.90,716.65], "
		+"[858.60,858.60], "
		+"[Double.MAX_VALUE,896.94] ] if $[0] >= BASE_CGC_BRUTA )[0][1]" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		
		// DOMAIN = -107, AGRARIO
		InsertSetMoreStep<SystemDataRecord> insertAgrarian = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN_DAY)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "[ "
		+"\"01\":52.13, "
		+"\"02\":43.23, "
		+"\"03\":37.60, "
		+"\"04\":37.33, "
		+"\"05\":37.33, "
		+"\"06\":37.33, "
		+"\"07\":37.33, "
		+"\"08\":37.33, "
		+"\"09\":37.33, "
		+"\"10\":37.33, "
		+"\"11\":37.33][GRUPO_COTIZACION]  * JORNADAS_REALES " )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -107)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN_MONTH)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "[ "
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
		+"\"11\":858.60][GRUPO_COTIZACION] * ( DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30 )" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		// SMI
		InsertSetMoreStep<SystemDataRecord> insertSMI = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, SMI)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "735.90" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		
		// IPREM
		InsertSetMoreStep<SystemDataRecord> insertIPREM = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, IPREM)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "537.84 " )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			deleteGeneral.execute();
			deleteHome.execute();
			deleteAgrarian.execute();
			deleteSMI.execute();
			deleteIPREM.execute();
			
			updateGeneral.execute();
			updateHome.execute();
			updateAgrarian.execute();
			updateSMI.execute();
			updateIPREM.execute();
			
			insertGeneral.execute();
			insertHome.execute();
			insertAgrarian.execute();
			insertSMI.execute();
			insertIPREM.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
