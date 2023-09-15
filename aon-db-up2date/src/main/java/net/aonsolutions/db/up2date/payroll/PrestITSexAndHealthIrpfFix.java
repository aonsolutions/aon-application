package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
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

public class PrestITSexAndHealthIrpfFix implements Update {

	public static final PrestITSexAndHealthIrpfFix PRESTITSEXANDHEALTHIRPFFIX = new PrestITSexAndHealthIrpfFix();
	
	private PrestITSexAndHealthIrpfFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Integer prestItConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PREST_IT"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		

		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2023);
		
		Date june2023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
		.and(SYSTEM_PAYMENT.START_DATE.eq(june2023Date))
		.and(SYSTEM_PAYMENT.IRPF_EXPRESSION.eq("_P"))
		) > 1;
		
		if ( upgraded ) 
		    return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P" )
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.START_DATE.eq(june2023Date))
			.execute()
			;
			
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
