package net.aonsolutions.db.up2date.payroll;

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

public class SystemOffInsert implements Update {

	public static final SystemOffInsert SYSTEM_OFF_INSERT = new SystemOffInsert();
	
	private SystemOffInsert() {
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
		calendar.set(Calendar.YEAR, 2010);
		
		Date _2010StartDate = new Date(calendar.getTimeInMillis());

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");


			dslContext
			.delete(SYSTEM_PAYMENT)
			.where(SYSTEM_PAYMENT.EXPRESSION.like("%DIAS_INACTIVIDAD%") )
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0 )
			.set(SYSTEM_PAYMENT.TYPE, (byte) 0 )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{CAUSA_INACTIVIDAD}" )
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "BASE_CGC_MIN" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/isdef DIAS_INACTIVIDAD ? 0.00 : HIDE()/**/" )
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
