package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SalaryData;

import net.aonsolutions.db.up2date.Update;

public class SalaryHours2022Fix implements Update {
	
	private static final SalaryData BASE_CGP = SALARY_DATA.as("base_cgp");
	private static final SalaryData HORAS_NOMINA = SALARY_DATA.as("horas_nomina");
	private static final SalaryData GRUPO_COTIZACION = SALARY_DATA.as("grupo_cotizacion");
	
	public static final SalaryHours2022Fix SALARYHOURS2022FIX  = new SalaryHours2022Fix();

	
	private SalaryHours2022Fix() {
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
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.YEAR, 2022);
		
		Date startMarch2022Date = new Date(calendar.getTimeInMillis());
		

		Map<String, Double> basesCgcMinHour = new HashMap<>();
		
		basesCgcMinHour.put("01",9.82);
		basesCgcMinHour.put("02",8.14);
		basesCgcMinHour.put("03",7.08);
		basesCgcMinHour.put("04",7.03);
		basesCgcMinHour.put("05",7.03);
		basesCgcMinHour.put("06",7.03);
		basesCgcMinHour.put("07",7.03);
		basesCgcMinHour.put("08",7.03);
		basesCgcMinHour.put("09",7.03);
		basesCgcMinHour.put("10",7.03);
		basesCgcMinHour.put("11",7.03);

		dslContext.transaction( (config) -> {
			
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			for ( String  grupoCotizacion : new String [] {
					"01", 
					"02", 
					"03", 
					"04", 
					"05", 
					"06", 
					"07", 
					"08", 
					"09", 
					"10", 
					"11"}) {
				
				dslContext
				.select()
				.from(BASE_CGP)
				
				.innerJoin(HORAS_NOMINA)
				.on(HORAS_NOMINA.NAME.eq("HORAS_NOMINA") 
				.and(BASE_CGP.SALARY.eq(HORAS_NOMINA.SALARY))
				.and(BASE_CGP.START_DATE.eq(HORAS_NOMINA.START_DATE)))

				.innerJoin(GRUPO_COTIZACION)
				.on(GRUPO_COTIZACION.NAME.eq("GRUPO_COTIZACION") 
				.and(GRUPO_COTIZACION.EXPRESSION.eq(grupoCotizacion))
				.and(BASE_CGP.SALARY.eq(GRUPO_COTIZACION.SALARY))
				.and(BASE_CGP.START_DATE.eq(GRUPO_COTIZACION.START_DATE)))

				.innerJoin(SALARY).on(SALARY.ID.eq(BASE_CGP.SALARY))
				
				.where(BASE_CGP.NAME.eq("BASE_CGP"))
				.and(BASE_CGP.START_DATE.ge(startMarch2022Date))
				.and(BASE_CGP.EXPRESSION.lt(HORAS_NOMINA.EXPRESSION.mul(basesCgcMinHour.get(grupoCotizacion))))
				.fetchStream()
				.forEach( r -> {
					
					double baseCgc = Double.parseDouble(r.get(BASE_CGP.EXPRESSION));
					double baseCgcMinHour = basesCgcMinHour.get(grupoCotizacion);

					double newHorasNomina = Math.floor(baseCgc / baseCgcMinHour);
					newHorasNomina = Math.floor(newHorasNomina * 100.00) / 100.00; 
					
					System.out.println( r.get(SALARY.ID) + "-." + r.get(SALARY.EMPLOYEE_NAME) + " [" + r.get(GRUPO_COTIZACION.EXPRESSION) + "] : " + r.get(BASE_CGP.EXPRESSION)  + " < " + basesCgcMinHour.get(grupoCotizacion) + " * " + r.get(HORAS_NOMINA.EXPRESSION) + "( " + newHorasNomina + " )");

					dslContext.update(SALARY_DATA).set(SALARY_DATA.EXPRESSION, Double.toString(newHorasNomina)).where(SALARY_DATA.ID.eq(r.get(HORAS_NOMINA.ID))).execute();
				});
				
			}
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}
	
}
