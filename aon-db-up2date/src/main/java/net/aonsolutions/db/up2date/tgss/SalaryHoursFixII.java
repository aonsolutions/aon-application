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

public class SalaryHoursFixII implements Update {

	public static SalaryHoursFixII SALARYHOURSFIXII = new SalaryHoursFixII();

	private static final String SALARY_HOURS = "HORAS_NOMINA";
	
	private SalaryHoursFixII() {
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
		.and(SYSTEM_DATA.NAME.eq(SALARY_HOURS))
		.and(SYSTEM_DATA.EXPRESSION.contains("BASE_CGC_MIN/BASE_CGC_MIN_HORA"))) == 0;

		if ( upgraded ) 
			return;

		UpdateConditionStep<SystemDataRecord> updateSalaryHours = 
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, "MAX(1,FLOOR(MIN(HORAS_TRABAJADAS, BASE_CGC/BASE_CGC_MIN_HORA)))"
		) 
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SALARY_HOURS));
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateSalaryHours.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
