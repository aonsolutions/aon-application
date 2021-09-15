package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class SMI2020UpdateRollback implements Update {

	public static final SMI2020UpdateRollback SMI2020UPDATEROLLBACK = new SMI2020UpdateRollback();

	private static final String SMI = "SMI";
	private static final String ROUND = "ROUND";
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	
	private SMI2020UpdateRollback() {
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
		calendar.set(Calendar.YEAR, 2020);
		
		LocalDate startDate2020 = new Date(calendar.getTimeInMillis()).toLocalDate();


		calendar.add(Calendar.YEAR, -1);
		LocalDate startDate2019 = new Date(calendar.getTimeInMillis()).toLocalDate();
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		LocalDate endDate2019 = new Date(calendar.getTimeInMillis()).toLocalDate();




		UpdateConditionStep<SystemDataRecord> updateBasesMinMonth = dslContext
				.update(SYSTEM_DATA)
				.set(SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION,String.format("%s(%s*14/12,2)", ROUND, SMI) ,  "1050.00"))
				.where(SYSTEM_DATA.DOMAIN.eq(0))
				.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
				.and(SYSTEM_DATA.START_DATE.eq(startDate2019));

		UpdateConditionStep<SystemDataRecord> updateBasesMinDay = dslContext
				.update(SYSTEM_DATA)
				.set(SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION,String.format("%s((%s*14/12)/30,2)", ROUND, SMI),  "35.00" ))
				.where(SYSTEM_DATA.DOMAIN.eq(0))
				.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
				.and(SYSTEM_DATA.START_DATE.eq(startDate2019));
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateBasesMinDay.execute();
			updateBasesMinMonth.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
