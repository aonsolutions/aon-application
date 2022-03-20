package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class SalaryHoursUpdateDaily implements Update {

	public static SalaryHoursUpdateDaily SALARYHOURSUPDATEDAILY = new SalaryHoursUpdateDaily();

	private static final String SALARY_HOURS = "HORAS_NOMINA";
	
	private SalaryHoursUpdateDaily() {
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
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		
		Date startSeptember2021Date = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.YEAR, 2021);
		Date endAugust2021Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);
		Date start2019Date = new Date(calendar.getTimeInMillis());
		

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SALARY_HOURS))
		.and(SYSTEM_DATA.EXPRESSION.contains("BASE_CGC_MIN"))) == 1;

		if ( upgraded ) 
			return;

		
		UpdateConditionStep<SystemDataRecord> updateBaseCgcMin = 
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, "MAX(1,FLOOR(MIN(( POR_HORAS() ? HORAS_TRABAJADAS : 10000) , BASE_CGC_MIN/BASE_CGC_MIN_HORA)))"
		) 
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SALARY_HOURS));
		
		UpdateConditionStep<SystemDataRecord> updatePorHoras = 
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION, "POR_HORAS", "FALSO")) 
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.in("BASE_CGC_MIN", "BASE_CGP_MIN"));

		 
		
		InsertSetMoreStep<SystemDataRecord> insertBaseCgcMinHora = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, "BASE_CGC_MIN_HORA")
		.set(SYSTEM_DATA.START_DATE, start2019Date)
		.set(SYSTEM_DATA.END_DATE, endAugust2021Date)
		.set(SYSTEM_DATA.EXPRESSION, 
		"["
		+"\"01\":8.83,"
		+"\"02\":7.32,"
		+"\"03\":6.37,"
		+"\"04\":6.33,"
		+"\"05\":6.33,"
		+"\"06\":6.33,"
		+"\"07\":6.33,"
		+"\"08\":6.33,"
		+"\"09\":6.33,"
		+"\"10\":6.33,"
		+"\"11\":6.33][GRUPO_COTIZACION]" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, "BASE_CGC_MIN_HORA")
		.set(SYSTEM_DATA.START_DATE, startSeptember2021Date)
		.set(SYSTEM_DATA.END_DATE, (Date)null)
		.set(SYSTEM_DATA.EXPRESSION, 
		"["
		+"\"01\":9.47,"
		+"\"02\":7.85,"
		+"\"03\":6.83,"
		+"\"04\":6.78,"
		+"\"05\":6.78,"
		+"\"06\":6.78,"
		+"\"07\":6.78,"
		+"\"08\":6.78,"
		+"\"09\":6.78,"
		+"\"10\":6.78,"
		+"\"11\":6.78][GRUPO_COTIZACION]" 
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updatePorHoras.execute();
			updateBaseCgcMin.execute();
			insertBaseCgcMinHora.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
