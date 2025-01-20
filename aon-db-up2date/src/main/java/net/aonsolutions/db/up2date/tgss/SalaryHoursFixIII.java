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

public class SalaryHoursFixIII implements Update {

	public static SalaryHoursFixIII SALARYHOURSFIXIII = new SalaryHoursFixIII();

	private static final String SALARY_HOURS = "HORAS_NOMINA";
	
	private SalaryHoursFixIII() {
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

		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.END_DATE.isNull())
		.and(SYSTEM_DATA.NAME.eq(SALARY_HOURS))
		.and(SYSTEM_DATA.EXPRESSION.contains("MIN(BASE_CGC,BASE_CGP)"))) > 0;

		if ( upgraded ) 
			return;

		UpdateConditionStep<SystemDataRecord> updateSalaryHours = 
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, "MAX(1,FLOOR(MIN(HORAS_TRABAJADAS, MIN(BASE_CGC,BASE_CGP)/BASE_CGC_MIN_HORA)))"
		) 
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.END_DATE.isNull())
		.and(SYSTEM_DATA.NAME.eq(SALARY_HOURS));
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateSalaryHours.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
