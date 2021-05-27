package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
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

public class AgriculturalITInsert implements Update {

	public static final AgriculturalITInsert AGRICULTURALITINSERT = new AgriculturalITInsert();
	
	private AgriculturalITInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);

		Date _2010StartDate = new Date(calendar.getTimeInMillis());

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Integer prestItConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PREST_IT"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		
		Integer mtndadConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("MTNAD"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		Integer pagoDirectoConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PAGO_DIRECTO"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		
		boolean	upgraded =
				dslContext.fetchCount(
				dslContext.select()
				.from(SYSTEM_PAYMENT)
				.where(SYSTEM_PAYMENT.DOMAIN.eq(-107))
				) == 3;
		
		if ( upgraded )
			return;
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(SYSTEM_PAYMENT)
			.where(SYSTEM_PAYMENT.DOMAIN.eq(-107))
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, -107)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0 )
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.EXPRESSION, "HIDE()")
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId)
			.execute()
			;
			
			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, -107)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0 )
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.EXPRESSION, "HIDE()")
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, mtndadConceptId)
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, -107)
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0 )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/0.00 * DIAS_IT/**/")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA")
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_IT} DÍAS DE IT PAGO DIRECTO")
			.execute()
			;


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
