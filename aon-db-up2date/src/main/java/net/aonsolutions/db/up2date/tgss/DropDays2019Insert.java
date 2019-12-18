package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class DropDays2019Insert implements Update {
	
	public static final DropDays2019Insert DROPDAYS2019INSERT = new DropDays2019Insert();
	
	private DropDays2019Insert() {
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
		
		// START_DATE 01/01/2019
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2018);
		
		Date _2018StartDate = new Date(calendar.getTimeInMillis());
		
		// DELETE @{CAUSA_INACTIVIDAD} ¿POR QUE ESTABA EN LA BD?
		dslContext.delete(SYSTEM_PAYMENT)
			.where(SYSTEM_PAYMENT.DESCRIPTION.eq("@{CAUSA_INACTIVIDAD}"))
			.execute();
		
		
		boolean upgraded = dslContext.fetchCount(
				dslContext.select().from(SYSTEM_PAYMENT)
					.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
					.and(SYSTEM_PAYMENT.START_DATE.eq(_2018StartDate))
					.and(SYSTEM_PAYMENT.DESCRIPTION.eq("DIAS DE AUSENCIA"))
				) == 1;

		// IF ALREADY EXISTS
				
		if ( upgraded )
			return;
		
		// DOMAIN = 0, DROPDAYS
		
		InsertSetMoreStep<SystemPaymentRecord> insertSystemPayment = dslContext.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte) 0)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, (Integer) null)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "DIAS DE AUSENCIA")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/DIAS_AUSENCIA * 0.00/**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "BASE_CGC_MIN")
			.set(SYSTEM_PAYMENT.START_DATE, _2018StartDate)
			.set(SYSTEM_PAYMENT.MONTH, (Byte) null)
			.set(SYSTEM_PAYMENT.END_DATE, (Date) null)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0);
			
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insertSystemPayment.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
