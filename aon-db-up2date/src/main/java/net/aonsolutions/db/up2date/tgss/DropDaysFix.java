package net.aonsolutions.db.up2date.tgss;

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

import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class DropDaysFix implements Update {
	
	public static final DropDaysFix DROPDAYSFIX = new DropDaysFix();
	
	private DropDaysFix() {
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
		
		
		
		boolean upgraded = dslContext.fetchCount(
				dslContext.select().from(SYSTEM_PAYMENT)
					.where(SYSTEM_PAYMENT.DOMAIN.eq(0)
					.and(SYSTEM_PAYMENT.START_DATE.eq(_2018StartDate))
					.and(SYSTEM_PAYMENT.DESCRIPTION.eq("DIAS DE AUSENCIA"))
					.and(SYSTEM_PAYMENT.QUOTE_EXPRESSION.startsWith("/*fixBaseCgcMin*/"))
					)
					
				) == 1;

		// IF ALREADY EXISTS
				
		if ( upgraded )
			return;
		
		// DOMAIN = 0, DROPDAYS
		
		UpdateConditionStep<SystemPaymentRecord> updateSystemPayment = dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/TOTAL_DEVENGADO;DIAS_AUSENCIA * 0.00/**/")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.START_DATE.eq(_2018StartDate))
			.and(SYSTEM_PAYMENT.DESCRIPTION.eq("DIAS DE AUSENCIA"));
			
		
		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateSystemPayment.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
