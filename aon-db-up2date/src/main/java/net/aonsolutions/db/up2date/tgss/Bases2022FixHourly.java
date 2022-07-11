package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Bases2022FixHourly implements Update {

	public static Bases2022FixHourly BASES2022FIXHOURLY = new Bases2022FixHourly();

	private static final int DOMAIN = 0;
	
	private static final String HOURLY_BASE = "BASE_HORARIA";
	
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String SALARY_HOURS = "HORAS_NOMINA";
	
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
		calendar.set(Calendar.YEAR, 2022);
		Date start2022Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(HOURLY_BASE))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))) > 0;

		if ( upgraded ) 
			return;

		InsertOnDuplicateStep<SystemDataRecord> insert2022Hourly = 
		dslContext
		.insertInto(SYSTEM_DATA) 
		.set(SYSTEM_DATA.DOMAIN,DOMAIN)
		.set(SYSTEM_DATA.NAME,HOURLY_BASE)
		.set(SYSTEM_DATA.EXPRESSION,"FALSO()")
		.set(SYSTEM_DATA.START_DATE, start2022Date);

		UpdateConditionStep<SystemDataRecord> bases2022FixHourly = 
		dslContext.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, DSL.replace(DSL.replace(SYSTEM_DATA.EXPRESSION,"FALSO()",HOURLY_BASE),"HORAS_NOMINA","HORAS_TRABAJADAS"))
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;

		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			bases2022FixHourly.execute();
			insert2022Hourly.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
