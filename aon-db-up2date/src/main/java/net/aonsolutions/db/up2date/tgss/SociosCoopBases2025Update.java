package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class SociosCoopBases2025Update implements Update {

	public static SociosCoopBases2025Update SOCIOSCOOPBASES2025UPDATE = new SociosCoopBases2025Update();

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
		calendar.set(Calendar.YEAR, 2025);
		Date start2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_PAYMENT.START_DATE.eq(start2025Date))) > 0;

		if ( upgraded ) 
			return;		
		
		

		// DOMAIN = 0 , GENERAL
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN)
			.set(SYSTEM_PAYMENT.TYPE, (byte)0)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0)
			.set(SYSTEM_PAYMENT.START_DATE, start2025Date)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "Bases mínimas de cotización respecto de los socios de cooperativas de trabajo asociado, en los supuestos de contrato a tiempo parcial.")
			.set(SYSTEM_PAYMENT.EXPRESSION, "if(COEFICIENTE_PARCIALIDAD < 1.00){"
					+ "BASE_CGC_MIN "
					+ "=(["
					+"\"01\":868.2,"
					+"\"02\":639.2,"
					+"\"03\":556.8][GRUPO_COTIZACION] or 552.6);" 
					+ "BASE_CGC_MAX=BASE_CGC_MIN;" 
					+ "BASE_CGP_MIN=BASE_CGC_MIN;" 
					+ "BASE_CGP_MAX=BASE_CGC_MIN;" 
					+ "}; "
					+ "HIDE();")
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
