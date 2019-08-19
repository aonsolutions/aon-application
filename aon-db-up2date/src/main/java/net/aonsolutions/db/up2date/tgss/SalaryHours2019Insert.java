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

public class SalaryHours2019Insert implements Update {

	public static SalaryHours2019Insert SALARYHOURS2019INSERT = new SalaryHours2019Insert();

	private static final String SALARY_HOURS = "HORAS_NOMINA";
	
	private SalaryHours2019Insert() {
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
		.and(SYSTEM_DATA.NAME.eq(SALARY_HOURS))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded ) 
			return;

		// DOMAIN = 0 , GENERAL
		InsertSetMoreStep<SystemDataRecord> insertGeneral = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, SALARY_HOURS)
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "ROUND([ "
		+"\"01\":(POR_HORAS() ? HORAS_TRABAJADAS : 1466.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 8.83 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"02\":(POR_HORAS() ? HORAS_TRABAJADAS : 1215.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 7.32 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"03\":(POR_HORAS() ? HORAS_TRABAJADAS : 1057.80 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.37 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"04\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"05\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"06\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"07\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"08\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"09\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"10\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD) ,"
		+"\"11\":(POR_HORAS() ? HORAS_TRABAJADAS : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) / 6.33 * COEFICIENTE_PARCIALIDAD)] [GRUPO_COTIZACION]" 
		+", 0)" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;


//		+"\"01\":(POR_HORAS() ? 8.83 * HORAS_NOMINA : 1466.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
//		+"\"02\":(POR_HORAS() ? 7.32 * HORAS_NOMINA : 1215.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
//		+"\"03\":(POR_HORAS() ? 6.37 * HORAS_NOMINA : 1057.80 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
//		+"\"04\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
//		+"\"05\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
//		+"\"06\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
//		+"\"07\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
//		+"\"08\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)) ,"
//		+"\"09\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)) ,"
//		+"\"10\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)) ,"
//		+"\"11\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD))] [GRUPO_COTIZACION]" 

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insertGeneral.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
