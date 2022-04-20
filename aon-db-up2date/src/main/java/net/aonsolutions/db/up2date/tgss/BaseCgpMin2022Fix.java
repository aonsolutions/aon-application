package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class BaseCgpMin2022Fix implements Update {

	public static BaseCgpMin2022Fix BASECGPMIN2022FIX = new BaseCgpMin2022Fix();

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	
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
		.where(SYSTEM_DATA.DOMAIN.eq(-105))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		) > 0;

		//...BASE_CGC_MIN...
		if ( upgraded ) 
			return;

		UpdateConditionStep<SystemDataRecord> baseCgpMin02022Update = 
		dslContext.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, 
		"MAX(7.03, ((COEFICIENTE_PARCIALIDAD < 1.00) ? BASE_CGC_MIN : 1166.70 * DIAS_NOMINA/DIAS_MES))")
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;
		
		InsertSetMoreStep<SystemDataRecord> baseCgpMin1052022Insert = 
		dslContext.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN,-105)
		.set(SYSTEM_DATA.NAME,BASE_CGP_MIN)
		.set(SYSTEM_DATA.START_DATE,start2022Date)
		.set(SYSTEM_DATA.EXPRESSION,"MAX(7.03, 1166.70 * DIAS_NOMINA/DIAS_MES * COEFICIENTE_PARCIALIDAD)")
		;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			baseCgpMin02022Update.execute();
			baseCgpMin1052022Insert.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
