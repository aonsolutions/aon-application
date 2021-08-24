package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.Field;
import org.jooq.InsertOnDuplicateStep;
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

public class BaseCgcMin2019Fix implements Update {

	public static BaseCgcMin2019Fix BASECGCMIN2019FIX= new BaseCgcMin2019Fix();

	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	
	private BaseCgcMin2019Fix() {
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

		String  baseCgcMinExpression ="[ "
				+ "\"01\":(MAX(8.33, (POR_HORAS() ? 8.83 * HORAS_NOMINA : 1466.40 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) ,"
				+ "\"02\":(MAX(7.32, (POR_HORAS() ? 7.32 * HORAS_NOMINA : 1215.90 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) ,"
				+ "\"03\":(MAX(6.37, (POR_HORAS() ? 6.37 * HORAS_NOMINA : 1057.80 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) ,"
				+ "\"04\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) ,"
				+ "\"05\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) ,"
				+ "\"06\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) ,"
				+ "\"07\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))) ,"
				+ "\"08\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)))) ,"
				+ "\"09\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)))) ,"
				+ "\"10\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD)))) ,"
				+ "\"11\":(MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : (MODALIDAD_MENSUAL ? 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD : 35.00 * DIAS_NOMINA * COEFICIENTE_PARCIALIDAD))))"
				+ "] [GRUPO_COTIZACION]";
		
		UpdateConditionStep<SystemDataRecord> updateBaseCgcMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, baseCgcMinExpression )
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));
		;
		
		String baseCgpMinExpression = "MAX(6.33, (POR_HORAS() ? 6.33 * HORAS_NOMINA : 1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30) * COEFICIENTE_PARCIALIDAD))";
		
		UpdateConditionStep<SystemDataRecord> updateBaseCgpMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, baseCgpMinExpression )
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.in(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));
		;

		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateBaseCgcMin.execute();
			updateBaseCgpMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
