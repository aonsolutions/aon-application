package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class SociosCoopBases2026Update implements Update {

	public static SociosCoopBases2026Update SOCIOSCOOPBASES2026UPDATE = new SociosCoopBases2026Update();

	private static final int DOMAIN = -1;
	
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2026);
		Date start2026Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2025);
		Date start2025Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date end2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_PAYMENT.START_DATE.eq(start2026Date))) > 0;

		if ( upgraded ) 
			return;		
		
		

		// DOMAIN = 0 , GENERAL
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.END_DATE, end2025Date)
			.where(SYSTEM_PAYMENT.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_PAYMENT.START_DATE.eq(start2025Date))
			.execute();

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN)
			.set(SYSTEM_PAYMENT.TYPE, (byte)0)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0)
			.set(SYSTEM_PAYMENT.START_DATE, start2026Date)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "Bases mínimas de cotización respecto de los socios de cooperativas de trabajo asociado, en los supuestos de contrato a tiempo parcial.")
			.set(SYSTEM_PAYMENT.EXPRESSION, "if(COEFICIENTE_PARCIALIDAD < 1.00){"
					+ "BASE_CGC_MIN "
					+ "=((["
					+"\"01\":895.20,"
					+"\"02\":660.00,"
					+"\"03\":574.20][GRUPO_COTIZACION] or 569.70) "
					+ "* (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/DIAS_MES)"
					+ ");" 
					+ "BASE_CGP_MIN=BASE_CGC_MIN;" 
					+ "}; "
					+ "HIDE();")
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
