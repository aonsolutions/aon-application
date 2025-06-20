package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TrainingExcessDeductionsITIMSFix implements Update {

	private static final int DOMAIN = -101;

	public static final TrainingExcessDeductionsITIMSFix TRAININGEXCESSDEDUCTIONSITIMSFIX = new TrainingExcessDeductionsITIMSFix();

	private TrainingExcessDeductionsITIMSFix() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2024);

		Date start2024Date = new Date(calendar.getTimeInMillis());


		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.eq("IT_E"))
			.and(SYSTEM_COST.EXPRESSION.contains("BASE_EXCESO"))
			.and(SYSTEM_COST.START_DATE.eq(start2024Date))) > 0;


		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN,DOMAIN)
			.set(SYSTEM_COST.CODE , "IT_E")
			.set(SYSTEM_COST.TYPE , (byte)1) // 1 = PROFESSIONAL_CONTINGENCY
			.set(SYSTEM_COST.DESCRIPTION,"IT" )
			.set(SYSTEM_COST.START_DATE,start2024Date)
			.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * (isdef PORCENTAJE_IT ? PORCENTAJE_IT : (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT)))/100" )
			.newRecord()
			.set(SYSTEM_COST.DOMAIN,DOMAIN)
			.set(SYSTEM_COST.CODE , "IMS_E")
			.set(SYSTEM_COST.TYPE , (byte)1) // 1 = PROFESSIONAL_CONTINGENCY
			.set(SYSTEM_COST.DESCRIPTION,"IMS" )
			.set(SYSTEM_COST.START_DATE,start2024Date)
			.set(SYSTEM_COST.EXPRESSION,"BASE_EXCESO * (isdef PORCENTAJE_IMS ? PORCENTAJE_IMS : (PORCENTAJE_IMS=( isdef OCUPACION ? OCUPACION_IMS[OCUPACION] : TARIFA_IMS)))/100" )
			.execute();
			;

			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
