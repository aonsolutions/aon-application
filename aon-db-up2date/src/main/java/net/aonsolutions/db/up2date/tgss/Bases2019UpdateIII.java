package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Bases2019UpdateIII implements Update {

	public static Bases2019UpdateIII BASES2019UPDATEIII = new Bases2019UpdateIII();

	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	
	private Bases2019UpdateIII() {
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


		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("POR_HORAS"))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))) == 1;
		


		if ( upgraded ) 
			return;

		
		InsertSetMoreStep<SystemDataRecord> insertByHours = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, "POR_HORAS")
		.set(SYSTEM_DATA.START_DATE, _2019StartDate)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.EXPRESSION, "def () { UTILIZADA('HORAS_TRABAJADAS') }")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		UpdateConditionStep<SystemDataRecord> updateBaseCgcMin = dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, "[ "
		+"\"01\":(POR_HORAS() ? 8.83 * HORAS_NOMINA : 1466.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
		+"\"02\":(POR_HORAS() ? 7.32 * HORAS_NOMINA : 1215.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
		+"\"03\":(POR_HORAS() ? 6.37 * HORAS_NOMINA : 1057.80 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
		+"\"04\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
		+"\"05\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
		+"\"06\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
		+"\"07\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD) ,"
		+"\"08\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)) ,"
		+"\"09\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)) ,"
		+"\"10\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)) ,"
		+"\"11\":(POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD))] [GRUPO_COTIZACION]" 
		)
		.where(SYSTEM_DATA.DOMAIN.eq(0) )
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))
		;

		UpdateConditionStep<SystemDataRecord> updateBaseCgpMin = dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, "(POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD)") 
		.where(SYSTEM_DATA.DOMAIN.eq(0) )
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insertByHours.execute();
			updateBaseCgcMin.execute();
			updateBaseCgpMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
